package com.enumtech.healthline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAppointmentActivity extends AppCompatActivity {

    ListView appointmentsList;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

    String userId;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        appointmentsList = findViewById(R.id.appointmentsList);

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);

        userId = sharedPreferences.getString("id","");


        myAdapter = new MyAdapter();
        appointmentsList.setAdapter(myAdapter);
        getmyappointments();


    }

    public class MyAdapter extends BaseAdapter{

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

            if(convertView == null){

                convertView = LayoutInflater.from(MyAppointmentActivity.this)

                        .inflate(R.layout.appointmentitem, parent, false);

            }

            TextView tvname = convertView.findViewById(R.id.tvname);

            TextView tvspeciality = convertView.findViewById(R.id.tvSpeciality);

            TextView tvhospital = convertView.findViewById(R.id.tvHospital);

            TextView tvschedule = convertView.findViewById(R.id.tvSchedule);

            TextView tvserial = convertView.findViewById(R.id.tvSerial);

            TextView tvstatus = convertView.findViewById(R.id.tvstatus);
            TextView tvfees = convertView.findViewById(R.id.tvfees);
            TextView tvtime = convertView.findViewById(R.id.tvtime);

            Button btncancel = convertView.findViewById(R.id.btncancel);



            HashMap<String,String> map = arrayList.get(position);

            String appointment_id = map.get("appointment_id");

            tvname.setText(map.get("doctor_name"));

            tvspeciality.setText(map.get("speciality").toUpperCase());

            tvhospital.setText(map.get("hospital").toUpperCase());

            tvschedule.setText("Appointment Date: "+map.get("date"));

            tvserial.setText("Serial: "+map.get("serial"));
            tvfees.setText("Fee: "+map.get("fees")+" Taka");

            tvstatus.setText(map.get("status"));
            tvtime.setText("Appointment Time: "+map.get("start_time")+" - "+map.get("end_time"));


            btncancel.setOnClickListener(v -> {

                String appointmentId = map.get("appointment_id");

                new android.app.AlertDialog.Builder(MyAppointmentActivity.this)
                        .setTitle("Cancel Appointment")
                        .setMessage("Do you really want to cancel the appointment?")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            cancelAppointment(appointment_id);


                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            });

            return convertView;
        }
    }

    public void getmyappointments(){

        String url = "https://ifathemalapp.com/apps/healthline/getmyappointments.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        JSONArray array = new JSONArray(response);


                        arrayList.clear();

                        for(int i=0;i<array.length();i++){

                            JSONObject obj = array.getJSONObject(i);

                            HashMap<String,String> map = new HashMap<>();

                            map.put("appointment_id",obj.getString("appointment_id"));
                            map.put("doctor_name", obj.getString("doctor_name"));
                            map.put("speciality", obj.getString("speciality"));
                            map.put("hospital", obj.getString("hospital"));
                            map.put("date", obj.getString("appointment_date"));
                            map.put("serial", obj.getString("serial_no"));
                            map.put("status", obj.getString("status"));
                            map.put("fees",obj.getString("fees"));
                            map.put("start_time",obj.getString("start_time"));
                            map.put("end_time",obj.getString("end_time"));

                            arrayList.add(map);
                        }

                        myAdapter.notifyDataSetChanged();


                    } catch (Exception e){
                        e.printStackTrace();
                    }

                },
                error -> {
                    Toast.makeText(MyAppointmentActivity.this,"Volley Error",Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("patient_id", userId);
                return map;
            }
        };

        Volley.newRequestQueue(MyAppointmentActivity.this).add(request);

    }

    private void cancelAppointment(String appointment_id){

        String url = "https://ifathemalapp.com/apps/healthline/deletemyappointment.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {


            if(response.contains("Successfully")) {
                Toast.makeText(this, "Appointment Cancelled", Toast.LENGTH_SHORT).show();
                getmyappointments();
            }


                },
                error -> {
                    Toast.makeText(this, "Error cancelling", Toast.LENGTH_SHORT).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("appointment_id", appointment_id);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}