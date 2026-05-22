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

public class AmbulanceManageRequestActivity extends AppCompatActivity {
    ListView ambulanceRequestListView;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    MyAdapter adapter;
    String id;
    boolean isRequestRunning = false;
    TextView totalrequest;
    String url = "https://ifathemalapp.com/apps/healthline/get_book_ambulance_data.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ambulance_manage_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        id = sharedPreferences.getString("id","");

        ambulanceRequestListView = findViewById(R.id.ambulanceBookRequestList);
        totalrequest = findViewById(R.id.totalrequest);

        adapter = new MyAdapter();
        ambulanceRequestListView.setAdapter(adapter);

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
                convertView = inflater.inflate(R.layout.ambulancebookrequestitem, parent, false);
            }

            TextView tvname = convertView.findViewById(R.id.tvname);
            TextView tvpickup = convertView.findViewById(R.id.tvpickup);
            TextView tvdestination = convertView.findViewById(R.id.tvdestination);
            TextView tvdate = convertView.findViewById(R.id.tvdate);
            TextView tvtime = convertView.findViewById(R.id.tvtime);
            Button btnaccept = convertView.findViewById(R.id.btnaccept);
            Button btndecline = convertView.findViewById(R.id.btndecline);
            TextView tvstatus = convertView.findViewById(R.id.tvstatus);
            TextView tvcontact = convertView.findViewById(R.id.tvcontact);
            Button btncontact = convertView.findViewById(R.id.btncontact);
            LinearLayout layoutcontact = convertView.findViewById(R.id.layoutcontact);
            Button btncomplete = convertView.findViewById(R.id.btncomplete);


            HashMap<String, String> hashMap = arrayList.get(position);


            String name = hashMap.get("patient_name");
            String pickup   = hashMap.get("pickup");
            String   destination   = hashMap.get("destination");
            String    date  = hashMap.get("date");
            String   time   = hashMap.get("time");
            String    ambulance_request_id  = hashMap.get("ambulance_request_id");
            String status = hashMap.get("status");
            String contactnumber = hashMap.get("contactnumber");

            tvname.setText(name);
            tvpickup.setText(pickup);
            tvdestination.setText(destination);
            tvdate.setText(date);
            tvtime.setText(time);
            tvstatus.setText(status.toUpperCase());

            btnaccept.setVisibility(View.VISIBLE);
            layoutcontact.setVisibility(View.GONE);
            btncontact.setVisibility(View.GONE);
            btncomplete.setVisibility(View.GONE);



            if (status.equalsIgnoreCase("accepted")) {

                btnaccept.setVisibility(View.GONE);
                layoutcontact.setVisibility(View.VISIBLE);
                btncontact.setVisibility(View.VISIBLE);
                btncomplete.setVisibility(View.VISIBLE);
                tvcontact.setText(contactnumber);

            } else if (status.equalsIgnoreCase("requested")) {

                btnaccept.setVisibility(View.VISIBLE);
                layoutcontact.setVisibility(View.GONE);
                btncontact.setVisibility(View.GONE);
                btncomplete.setVisibility(View.GONE);

            }





            btnaccept.setOnClickListener(v -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(AmbulanceManageRequestActivity.this);

                builder.setTitle("Confirm Booking Request");
                builder.setMessage("Are you sure you want to accept this booking request?");

                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    String requestId = ambulance_request_id;
                    updateAmbulanceRequestStatus(requestId, "accepted");

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            });

            btndecline.setOnClickListener(v -> {


                AlertDialog.Builder builder = new AlertDialog.Builder(AmbulanceManageRequestActivity.this);

                builder.setTitle("Reject Booking Request");
                builder.setMessage("Are you sure you want to decline this booking request?");

                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    String requestId = ambulance_request_id;
                    updateAmbulanceRequestStatus(requestId, "declined");

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            });


            btncomplete.setOnClickListener(v -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(AmbulanceManageRequestActivity.this);

                builder.setTitle("Complete Booking Request");
                builder.setMessage("Are you sure you want to mark it as completed?");

                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    String requestId = ambulance_request_id;
                    updateAmbulanceRequestStatus(requestId, "completed");

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            });


            btncontact.setOnClickListener(v -> {

                String phoneNumber = tvcontact.getText().toString().trim();

                if (!phoneNumber.isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);

                } else {
                    Toast.makeText(AmbulanceManageRequestActivity.this,
                            "No number found",
                            Toast.LENGTH_SHORT).show();
                }
            });



            return convertView;
        }
    }



    private void loadAmbulanceBookingRequest() {

        if (isRequestRunning) return;
        isRequestRunning = true;

        StringRequest request = new StringRequest(Request.Method.POST, url,

                response -> {

                    isRequestRunning = false;

                    Log.d("API_RESPONSE", response);

                    arrayList.clear();

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {

                            JSONArray array = jsonObject.getJSONArray("data");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject obj = array.getJSONObject(i);

                                HashMap<String, String> map = new HashMap<>();


                                map.put("ambulance_request_id", obj.getString("ambulance_request_id"));

                                map.put("patient_id", obj.getString("patient_id"));

                                map.put("ambulance_driver_id", obj.getString("ambulance_driver_id"));

                                map.put("patient_name", obj.getString("patient_name"));

                                map.put("date", obj.getString("date"));

                                map.put("time", obj.getString("time"));

                                map.put("pickup", obj.getString("pickup"));

                                map.put("destination", obj.getString("destination"));

                                map.put("status", obj.getString("status"));
                                map.put("contactnumber", obj.optString("contactnumber", "Not Found"));


                                arrayList.add(map);

                            }

                            adapter.notifyDataSetChanged();

                            totalrequest.setText("Total Request : " + arrayList.size());

                        }
                        else if(status.equals("error")){

                            Toast.makeText(
                                    AmbulanceManageRequestActivity.this,
                                    "Database connection error",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }else if(status.equals("empty")) {

                            Toast.makeText(
                                    AmbulanceManageRequestActivity.this,
                                    "No booking request found",
                                    Toast.LENGTH_SHORT
                            ).show();
                            totalrequest.setText("Total Request : NO Booking Request Found");


                        }
                        else {
                            Toast.makeText(
                                    AmbulanceManageRequestActivity.this,
                                    "Something is wrong",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(
                                AmbulanceManageRequestActivity.this,
                                "Parsing Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                },

                error -> {

                    isRequestRunning = false;

                    Toast.makeText(
                            AmbulanceManageRequestActivity.this,
                            "Network Error",
                            Toast.LENGTH_SHORT
                    ).show();

                    Log.e("API_ERROR", error.toString());

                }

        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();

                map.put("id", id);

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



    private void updateAmbulanceRequestStatus(String requestId, String statusValue) {

        String url = "https://ifathemalapp.com/apps/healthline/update_ambulance_booking_request_status.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    Log.d("UPDATE_RESPONSE", response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        String status = obj.getString("status");

                        if (status.equals("success")) {
                            Toast.makeText(this, "Request " + statusValue + " successfully", Toast.LENGTH_SHORT).show();


                            loadAmbulanceBookingRequest();

                        } else {
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    Log.e("UPDATE_ERROR", error.toString());
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();

                map.put("ambulance_request_id", requestId);
                map.put("status", statusValue);

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

}
