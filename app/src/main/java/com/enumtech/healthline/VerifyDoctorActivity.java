package com.enumtech.healthline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerifyDoctorActivity extends AppCompatActivity {

    ListView listView;


    ArrayList<HashMap<String, String>> doctorList = new ArrayList<>();
    DoctorListAdapter adapter;
    String url = "https://ifathemalapp.com/apps/healthline/get_doctor_verification_request.php";

    boolean isRequestRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_doctor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);

        adapter = new DoctorListAdapter();
        listView.setAdapter(adapter);
        loadDoctors();
    }



    private void loadDoctors() {

        if (isRequestRunning) return;
        isRequestRunning = true;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    isRequestRunning = false;
                    Log.d("API_RESPONSE", response);

                    doctorList.clear();

                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() == 0) {
                            Toast.makeText(this, "No doctors request found", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            HashMap<String, String> map = new HashMap<>();

                            map.put("doctorid",obj.getString("doctorid"));
                            map.put("name", obj.optString("name", "N/A"));
                            map.put("speciality", obj.optString("speciality", "N/A"));
                            map.put("hospital", obj.optString("hospital", "N/A"));
                            map.put("bmdc", obj.optString("bmdc", "N/A"));


                            doctorList.add(map);
                        }

                        adapter.notifyDataSetChanged();

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

        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                1,
                1.0f
        ));

        Volley.newRequestQueue(this).add(request);
    }




    public class DoctorListAdapter extends BaseAdapter {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount() {
            return doctorList.size();
        }

        @Override
        public Object getItem(int position) {
            return doctorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("WrongViewCast")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.doctorverifyitem, parent, false);
            }


           TextView tvname = convertView.findViewById(R.id.tvName);
            TextView tvspeciality = convertView.findViewById(R.id.tvSpeciality);
            TextView tvhospital = convertView.findViewById(R.id.tvHospital);
            TextView tvbmdc = convertView.findViewById(R.id.tvbmdc);

            Button btndecline = convertView.findViewById(R.id.btndecline);
            Button btnverify = convertView.findViewById(R.id.btnverify);

            HashMap<String, String> model = doctorList.get(position);

            String doctorname = model.get("name").toUpperCase();




           tvname.setText(doctorname);
            tvspeciality.setText(model.get("speciality").toUpperCase());
            tvhospital.setText(model.get("hospital").toUpperCase());
            tvbmdc.setText("BMDC Reg. NO. : "+model.get("bmdc"));

            btnverify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestDoctorVerification(model.get("doctorid"),"marked");
                    loadDoctors();
                }
            });

            btndecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestDoctorVerification(model.get("doctorid"),"declined");
                    loadDoctors();
                }
            });




            return convertView;
        }


    }



    private void requestDoctorVerification(String id, String verify){

        String url =
                "https://ifathemalapp.com/apps/healthline/update_doctor_verification_status.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                response -> {

                    Log.d("VERIFY_RESPONSE", response);

                    try {

                        JSONObject jsonObject =
                                new JSONObject(response);

                        String status =
                                jsonObject.getString("status");

                        String message =
                                jsonObject.getString("message");

                        Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();

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

                map.put("user_id", id);
                map.put("verify", verify);

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