package com.enumtech.healthline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAmbulanceRequestActivity extends AppCompatActivity {
    ListView bookingRequestListView;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    MyAdapter adapter;
    String id;
    boolean isRequestRunning = false;
    TextView totalrequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_ambulance_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        id = sharedPreferences.getString("id","");

        bookingRequestListView = findViewById(R.id.bookingRequestListView);
        totalrequest = findViewById(R.id.totalrequest);

        adapter = new MyAdapter();
       bookingRequestListView.setAdapter(adapter);

        loadAmbulanceBookingRequest();
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
                convertView = inflater.inflate(R.layout.myambulancerequestitem, parent, false);
            }


            TextView tvambulancename = convertView.findViewById(R.id.tvambulancename);
            TextView tvambulancelicence = convertView.findViewById(R.id.tvambulancelicence);
            TextView tvambulancetype = convertView.findViewById(R.id.tvambulancetype);
            TextView tvdrivername = convertView.findViewById(R.id.tvdrivername);
            TextView tvpickup = convertView.findViewById(R.id.tvpickup);
            TextView tvdestination = convertView.findViewById(R.id.tvdestination);
            TextView tvdate = convertView.findViewById(R.id.tvdate);
            TextView tvstatus = convertView.findViewById(R.id.tvstatus);
            TextView tvcontact = convertView.findViewById(R.id.tvcontact);
            Button btncontact = convertView.findViewById(R.id.btncontact);
            Button btncancel = convertView.findViewById(R.id.btncancel);
            LinearLayout layoutcontact = convertView.findViewById(R.id.layoutcontact);






             HashMap<String, String> map = arrayList.get(position);

            String status=   map.get("status");

            layoutcontact.setVisibility(View.GONE);
            btncontact.setVisibility(View.GONE);

            if(status != null && status.equalsIgnoreCase("accepted")){

                layoutcontact.setVisibility(View.VISIBLE);
                btncontact.setVisibility(View.VISIBLE);

                tvcontact.setText(map.get("number"));

                tvstatus.setText("ACCEPTED");

            }
            else if(status != null && status.equalsIgnoreCase("requested")){

                tvstatus.setText("PENDING");

            }
            else if(status != null && status.equalsIgnoreCase("declined")){

                tvstatus.setText("REJECTED");

            }

            tvambulancename.setText(map.get("ambulance_name"));

            tvambulancelicence.setText(map.get("ambulance_licence"));

            tvambulancetype.setText(map.get("ambulance_type"));
            
            tvdrivername.setText(map.get("driver_name"));

            tvpickup.setText(map.get("pickup"));

            tvdestination.setText(map.get("destination"));

            tvdate.setText(map.get("date"));
            String ambulance_request_id = map.get("ambulance_request_id");




            btncancel.setOnClickListener(v -> {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(MyAmbulanceRequestActivity.this);

                builder.setTitle("Cancel Booking");

                builder.setMessage(
                        "Are you sure you want to cancel this booking request?"
                );

                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (dialog, which) -> {

                    dialog.dismiss();

                    cancelBookingRequest(ambulance_request_id);
                    loadAmbulanceBookingRequest();

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });

                builder.show();

            });


            btncontact.setOnClickListener(v -> {

                String phoneNumber = tvcontact.getText().toString().trim();

                if (!phoneNumber.isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);

                } else {
                    Toast.makeText(MyAmbulanceRequestActivity.this,
                            "No number found",
                            Toast.LENGTH_SHORT).show();
                }
            });


            return convertView;
        }
    }







    private void loadAmbulanceBookingRequest() {

        String url = "https://ifathemalapp.com/apps/healthline/get_patient_ambulance_book_data.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,

                response -> {

                    Log.d("API_RESPONSE", response);

                    arrayList.clear();

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");

                        if(status.equals("success")){

                            JSONArray array = jsonObject.getJSONArray("data");

                            for(int i = 0; i < array.length(); i++){

                                JSONObject obj = array.getJSONObject(i);

                                HashMap<String,String> map = new HashMap<>();

                                map.put("ambulance_request_id", obj.getString("ambulance_request_id"));

                                map.put("ambulance_name", obj.getString("ambulance_name"));

                                map.put("ambulance_licence", obj.getString("ambulance_licence"));

                                map.put("ambulance_type", obj.getString("ambulance_type"));

                                map.put("number", obj.getString("number"));

                                map.put("driver_name", obj.getString("driver_name"));

                                map.put("pickup", obj.getString("pickup"));

                                map.put("destination", obj.getString("destination"));

                                map.put("date", obj.getString("date"));

                                map.put("status", obj.getString("status"));

                                map.put("number", obj.getString("number"));

                                arrayList.add(map);

                            }

                            adapter.notifyDataSetChanged();

                            totalrequest.setText(
                                    "Total Request : " + arrayList.size()
                            );

                        } else {

                            totalrequest.setText(
                                    "No Booking Request Found"
                            );

                            Toast.makeText(
                                   this,
                                    "No booking request found",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e){

                        e.printStackTrace();

                        Toast.makeText(
                               this,
                                "Parsing Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                },

                error -> {

                    error.printStackTrace();

                    Toast.makeText(
                            this,
                            "Network Error",
                            Toast.LENGTH_SHORT
                    ).show();
                }

        ){

            @Override
            protected Map<String, String> getParams() {

                Map<String,String> map = new HashMap<>();

                map.put("patient_id", id);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }



    private void cancelBookingRequest(String requestId){

        String url =
                "https://ifathemalapp.com/apps/healthline/delete_ambulance_booking_request.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                response -> {

                    Log.d("DELETE_RESPONSE", response);

                    try {

                        JSONObject jsonObject =
                                new JSONObject(response);

                        String status =
                                jsonObject.getString("status");

                        if(status.equals("success")){

                            Toast.makeText(
                                    this,
                                    "Booking request cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadAmbulanceBookingRequest();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Failed to cancel request",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e){

                        e.printStackTrace();

                        Toast.makeText(
                                this,
                                "Parsing Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                },

                error -> {

                    error.printStackTrace();

                    Toast.makeText(
                            this,
                            "Network Error",
                            Toast.LENGTH_SHORT
                    ).show();
                }

        ){

            @Override
            protected Map<String, String> getParams() {

                Map<String,String> map =
                        new HashMap<>();

                map.put(
                        "ambulance_request_id", requestId
                );

                return map;
            }
        };

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        10000,
                        1,
                        1.0f
                )
        );

        Volley.newRequestQueue(this).add(request);
    }

}