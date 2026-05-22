package com.enumtech.healthline;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookAmbulanceActivity extends AppCompatActivity {
    ListView ambulanceListView;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    MyAdapter adapter;
    String id;
    boolean isRequestRunning = false;
    EditText searchBar, etpickuplocation, etdestinationlocation, etcontactnumber;
    String searchText = "";
    String url = "https://ifathemalapp.com/apps/healthline/ambulancelist.php";
    Button dateBtn;
    Button timeBtn;
    String  ambulancedriverid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_ambulance);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchBar = findViewById(R.id.searchBar);
        etdestinationlocation = findViewById(R.id.etdestinationlocation);
        etpickuplocation = findViewById(R.id.etpickuplocation);
        etcontactnumber = findViewById(R.id.etcontactnumber);

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        id = sharedPreferences.getString("id","");
        ambulanceListView = findViewById(R.id.ambulanceList);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                loadAmbulance();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });


        adapter = new MyAdapter();
        ambulanceListView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(ambulanceListView);


        loadAmbulance();




        dateBtn = findViewById(R.id.dateBtn);

        dateBtn.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    BookAmbulanceActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {

                        String formattedDate = selectedYear + "-"
                                + String.format("%02d", (selectedMonth + 1))
                                + "-"
                                + String.format("%02d", selectedDay);

                        dateBtn.setText(formattedDate);

                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            datePickerDialog.show();

        });




        timeBtn = findViewById(R.id.timeBtn);

        timeBtn.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    BookAmbulanceActivity.this,
                    (view, selectedHour, selectedMinute) -> {

                        String amPm;

                        int hour12;

                        if (selectedHour == 0) {
                            hour12 = 12;
                            amPm = "AM";
                        } else if (selectedHour < 12) {
                            hour12 = selectedHour;
                            amPm = "AM";
                        } else if (selectedHour == 12) {
                            hour12 = 12;
                            amPm = "PM";
                        } else {
                            hour12 = selectedHour - 12;
                            amPm = "PM";
                        }

                        String formattedTime = String.format(
                                "%02d:%02d %s",
                                hour12,
                                selectedMinute,
                                amPm
                        );

                        timeBtn.setText(formattedTime);

                    },
                    hour,
                    minute,
                    false
            );

            timePickerDialog.show();

        });




    }



    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.ambulanceitem, parent, false);
            }

            TextView tvambulancename = convertView.findViewById(R.id.tvambulancename);
            TextView tvambulancelicence = convertView.findViewById(R.id.tvambulancelicence);
            TextView tvambulancetype = convertView.findViewById(R.id.tvambulancetype);
            TextView tvlocation = convertView.findViewById(R.id.tvlocation);
            TextView tvdrivername = convertView.findViewById(R.id.tvdrivername);
            Button btnRequest = convertView.findViewById(R.id.btnRequest);


            HashMap<String, String> hashMap = arrayList.get(position);

            tvambulancelicence.setText(hashMap.get("ambulance_licence"));
            tvambulancename.setText(hashMap.get("ambulance_name"));
            tvambulancetype.setText(hashMap.get("ambulance_type"));

            tvlocation.setText("🌍   "+hashMap.get("location"));
            tvdrivername.setText("👤   "+hashMap.get("drivername"));


            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ambulancedriverid = hashMap.get("ambulancedriverid");

                    if(etpickuplocation.getText().toString().isEmpty()){
                        etpickuplocation.setError("Pickup location must be defined!");
                        Toast.makeText(BookAmbulanceActivity.this,"Pickup location can't be empty",Toast.LENGTH_SHORT).show();
                    }
                    if(etdestinationlocation.getText().toString().isEmpty()){
                        etdestinationlocation.setError("Destination must be defined");
                        Toast.makeText(BookAmbulanceActivity.this,"Destination can't be empty",Toast.LENGTH_SHORT).show();
                    }
                    if(dateBtn.getText().toString().equals("Select Date")){
                        Toast.makeText(BookAmbulanceActivity.this,"Date must be selected!",Toast.LENGTH_SHORT).show();
                    }
                    if(timeBtn.getText().toString().equals("Select Time")){

                        Toast.makeText(BookAmbulanceActivity.this,"Time must be selected!",Toast.LENGTH_SHORT).show();
                    }
                    if(etcontactnumber.getText().toString().isEmpty()){
                        etcontactnumber.setError("Enter a valid contact number!");
                        Toast.makeText(BookAmbulanceActivity.this,"Contact number can't be empty",Toast.LENGTH_SHORT).show();
                    }

                    if(!etdestinationlocation.getText().toString().isEmpty() &&

                            !etpickuplocation.getText().toString().isEmpty() &&

                            !dateBtn.getText().toString().equals("Select Date") &&

                            !timeBtn.getText().toString().equals("Select Time") &&
                            !etcontactnumber.getText().toString().isEmpty()
                    ){

                        AlertDialog.Builder builder = new AlertDialog.Builder(BookAmbulanceActivity.this);

                        builder.setTitle("Confirm Booking Request");
                        builder.setMessage("Are you sure you want to request for booking?");

                        builder.setCancelable(false);

                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            dialog.dismiss();
                            ambulanceBookingRequest();

                        });

                        builder.setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
            });




            return convertView;
        }
    }




    private void loadAmbulance() {

        if (isRequestRunning) return;
        isRequestRunning = true;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    isRequestRunning = false;
                    Log.d("API_RESPONSE", response);

                    arrayList.clear();

                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() == 0) {
                            Toast.makeText(this, "No ambulance found", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            HashMap<String, String> map = new HashMap<>();

                            map.put("ambulancedriverid",obj.getString("ambulance_driver_id"));
                            map.put("drivername", obj.optString("name", "N/A"));
                            map.put("location", obj.optString("location", "N/A"));
                            map.put("number", obj.optString("number", "N/A"));
                            map.put("ambulance_name", obj.optString("ambulance_name", "N/A"));
                            map.put("ambulance_licence", obj.optString("ambulance_licence", "N/A"));
                            map.put("ambulance_type", obj.optString("ambulance_type", "N/A"));


                            arrayList.add(map);
                        }

                        adapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(ambulanceListView);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    isRequestRunning = false;
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", String.valueOf(error));
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("search", searchText);
                return map;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                1,
                1.0f
        ));

        Volley.newRequestQueue(this).add(request);
    }





    public void ambulanceBookingRequest(){


        String url ="https://ifathemalapp.com/apps/healthline/ambulance_booking_request.php" ;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    String status;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                        status = obj.getString("status");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                    if(status.equals("success")) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setTitle("Request Sent Successfully");
                        builder.setMessage("You will find your request in your My BookingRequest section");

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                        });

                        builder.setCancelable(false);

                        builder.show();
                    }
                    if(status.equals("error")){
                        Toast.makeText(BookAmbulanceActivity.this,"Request failed",Toast.LENGTH_LONG).show();

                    }

                    if(status.equals("errors")){
                        Toast.makeText(BookAmbulanceActivity.this,"Database connection error",Toast.LENGTH_SHORT).show();

                    }
                },
                error -> {
                    Toast.makeText(BookAmbulanceActivity.this,"Network connection failed",Toast.LENGTH_SHORT).show();

                    Log.e("APPOINTMENT", error.toString());
                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();


                params.put("ambulance_driver_id", ambulancedriverid);
                params.put("patient_id", id);
                params.put("date", dateBtn.getText().toString());
                params.put("time", timeBtn.getText().toString());
                params.put("pickup", etpickuplocation.getText().toString());
                params.put("destination", etdestinationlocation.getText().toString());
                params.put("contactnumber", etcontactnumber.getText().toString());


                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(BookAmbulanceActivity.this);
        queue.add(request);


    }









    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {

            return;

        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);

        listView.requestLayout();

    }


}