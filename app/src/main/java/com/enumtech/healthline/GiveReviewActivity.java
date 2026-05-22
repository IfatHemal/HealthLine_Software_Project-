package com.enumtech.healthline;

import android.content.Intent;
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

public class GiveReviewActivity extends AppCompatActivity {
    ListView appointmentsList;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

    String userId;
    GiveReviewActivity.MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_give_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        appointmentsList = findViewById(R.id.appointmentsList);

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);

        userId = sharedPreferences.getString("id","");


        myAdapter = new GiveReviewActivity.MyAdapter();
        appointmentsList.setAdapter(myAdapter);
        getmyappointments();
    }



    public class MyAdapter extends BaseAdapter {

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

                convertView = LayoutInflater.from(GiveReviewActivity.this)

                        .inflate(R.layout.reviewappointment, parent, false);

            }

            TextView tvname = convertView.findViewById(R.id.tvname);

            TextView tvspeciality = convertView.findViewById(R.id.tvSpeciality);

            TextView tvhospital = convertView.findViewById(R.id.tvHospital);

            TextView tvschedule = convertView.findViewById(R.id.tvSchedule);



            TextView tvfees = convertView.findViewById(R.id.tvfees);


            Button btnreview = convertView.findViewById(R.id.btnreview);



            HashMap<String,String> map = arrayList.get(position);

            String appointment_id = map.get("appointment_id");

            tvname.setText(map.get("doctor_name"));

            tvspeciality.setText(map.get("speciality").toUpperCase());

            tvhospital.setText(map.get("hospital").toUpperCase());

            tvschedule.setText("Appointment Date: "+map.get("date"));

            tvfees.setText("Fee: "+map.get("fees")+" Taka");



            btnreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SubmitReviewActivity.doctor= map.get("doctor_name");
                    SubmitReviewActivity.doctor_id = map.get("doctor_id");
                    SubmitReviewActivity.appointmentId = map.get("appointment_id");
                    startActivity(new Intent(GiveReviewActivity.this,SubmitReviewActivity.class));
                }
            });


            return convertView;
        }
    }

    public void getmyappointments(){

        String url = "https://ifathemalapp.com/apps/healthline/getcompletedappointment.php";

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
                            map.put("doctor_id", obj.optString("doctorid"));

                            arrayList.add(map);
                        }

                        myAdapter.notifyDataSetChanged();


                    } catch (Exception e){
                        e.printStackTrace();
                    }

                },
                error -> {
                    Toast.makeText(GiveReviewActivity.this,"Volley Error",Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<>();
                map.put("patient_id", userId);
                return map;
            }
        };

        Volley.newRequestQueue(GiveReviewActivity.this).add(request);

    }

}