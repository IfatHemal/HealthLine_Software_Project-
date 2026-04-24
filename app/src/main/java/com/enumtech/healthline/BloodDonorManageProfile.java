package com.enumtech.healthline;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BloodDonorManageProfile extends AppCompatActivity {
    Spinner spinnerbloodgroup;
    TextView etname,etemail, btnsaveprofile, tvStatusBadge, tvbloodgrp, tvlocation, tvnumber, statuspoint;
    String id, selectedbloodgroup;
    ShapeableImageView profileimage;
    LinearLayout statusbadge;
    RelativeLayout btnActivate,btnDeactivate;
    EditText city,area,number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blood_donor_manage_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        spinnerbloodgroup = findViewById(R.id.spinnerbloodgroup);
        etemail = findViewById(R.id.etEmail);
        etname = findViewById(R.id.etName);
      profileimage = findViewById(R.id.profileimage);
      btnsaveprofile = findViewById(R.id.btnSaveProfile);
      tvStatusBadge = findViewById(R.id.tvStatusBadge);
      statusbadge = findViewById(R.id.statusbadge);
      btnActivate = findViewById(R.id.btnActivate);
      btnDeactivate = findViewById(R.id.btnDeactivate);
      city = findViewById(R.id.city);
      area = findViewById(R.id.area);
      number = findViewById(R.id.number);
      tvbloodgrp = findViewById(R.id.tvbloodgrp);
      tvlocation = findViewById(R.id.tvlocation);
      tvnumber = findViewById(R.id.tvnumber);
      statuspoint = findViewById(R.id.statuspoint);

        String[] bloodgroup = {
                "Select Blood Group",
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
        };
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, bloodgroup);
        bloodAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerbloodgroup.setAdapter(bloodAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String name = sharedPreferences.getString("name","");
        String email = sharedPreferences.getString("email", "");
        String role = sharedPreferences.getString("role","");
        String image = sharedPreferences.getString("image","");
        id = sharedPreferences.getString("id","");

        getBloodDonorRequest();

        etname.setText(name);
        etemail.setText(email);

        if (image != null && !image.isEmpty() && !image.equals("null")){

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture)
                    .into(profileimage);
        }


        btnsaveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedbloodgroup = spinnerbloodgroup.getSelectedItem().toString();



                if(selectedbloodgroup.equals("Select Blood Group")){
                    Toast.makeText(BloodDonorManageProfile.this,"Blood group must be selected",Toast.LENGTH_LONG).show();
                }
                else if(city.getText().toString().isEmpty()){
                    city.setError("City can't be empty");
                }
                else if (area.getText().toString().isEmpty()){
                    area.setError("Area can't be empty");
                }
                else if(number.getText().toString().isEmpty()){
                    number.setError("Number can't be empty");
                }
                else if(number.getText().toString().length()!=11){
                    number.setError("Enter a valid number!");
                }
                else{
                    String url ="https://ifathemalapp.com/apps/healthline/updateblooddonorprofile.php";
                    updateBloodDonorProfileRequest(url,"Deactive");
                }

            }
        });



        if(sharedPreferences.getString("status","").equalsIgnoreCase("active")){
            tvStatusBadge.setText("Active");
            statusbadge.setBackgroundResource(R.drawable.bg_status_active);
            tvStatusBadge.setTextColor(Color.parseColor("#16A34A"));
        }
        else if(sharedPreferences.getString("status","").equalsIgnoreCase("deactive")){
            tvStatusBadge.setText("Inactive");
            tvStatusBadge.setTextColor(Color.parseColor("#EF4444"));
            statusbadge.setBackgroundResource(R.drawable.bg_status_deactive);
        }




        btnActivate.setOnClickListener(v -> {

            if(tvbloodgrp.getText().toString().isEmpty()||tvlocation.getText().toString().isEmpty()||tvnumber.getText().toString().isEmpty()){
                Toast.makeText(BloodDonorManageProfile.this,"Set up your profile information first, then try again",Toast.LENGTH_LONG).show();
            }
            else {

                String url = "https://ifathemalapp.com/apps/healthline/update_blooddonor_activestatus.php";
                updateBloodDonorProfileRequest(url, "active");
                getBloodDonorRequest();

                Toast.makeText(this, "Your ID is now Activated", Toast.LENGTH_SHORT).show();
            }
        });


        btnDeactivate.setOnClickListener(v -> {


            String url = "https://ifathemalapp.com/apps/healthline/update_blooddonor_activestatus.php";
            updateBloodDonorProfileRequest(url,"deactive");
            getBloodDonorRequest();
            Toast.makeText(this, "Your ID is now Deactivated", Toast.LENGTH_SHORT).show();
        });



    }




    public void updateBloodDonorProfileRequest(String url, String status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                if(s.contains("Updated successfully")){
                    Toast.makeText(BloodDonorManageProfile.this,"Updated Successfully",Toast.LENGTH_LONG).show();
                    getBloodDonorRequest();
                } else if (s.contains("Inserted successfully")) {
                    Toast.makeText(BloodDonorManageProfile.this,"Inserted Successfully",Toast.LENGTH_LONG).show();
                    getBloodDonorRequest();
                } else {
                    Toast.makeText(BloodDonorManageProfile.this,"Can't update, there is something wrong",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(BloodDonorManageProfile.this,"Error: ",Toast.LENGTH_LONG).show();
                if(volleyError.networkResponse != null){
                    Log.e("VOLLEY_ERROR", new String(volleyError.networkResponse.data));
                } else {
                    Log.e("VOLLEY_ERROR", volleyError.toString());
                }

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> myMap = new HashMap<>();

                Log.d("DEBUG", "id=" + id);

                myMap.put("id", String.valueOf(id));
                myMap.put("blood_group", String.valueOf(selectedbloodgroup));
                myMap.put("location",String.valueOf(city.getText()+"-"+area.getText()));
                myMap.put("number",String.valueOf(number.getText()));
                myMap.put("activestatus",status);

                return myMap;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(BloodDonorManageProfile.this);
        queue.add(stringRequest);

    }


    public void getBloodDonorRequest(){

        StringRequest request = new StringRequest(Request.Method.POST, "https://ifathemalapp.com/apps/healthline/get_blood_donor.php",
                response -> {

                    try {


                        if(response.trim().startsWith("{")){
                            JSONObject obj = new JSONObject(response);

                            String status = obj.optString("status");

                            if(status.equals("empty")){
                                Toast.makeText(getApplicationContext(), "No donor data", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                        JSONArray array = new JSONArray(response);

                        if(array.length() == 0){
                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject obj = array.getJSONObject(0);

                        String blood = obj.optString("blood_group", "N/A");
                        String number = obj.optString("number", "N/A");
                        String location = obj.optString("location", "N/A");
                        String status = obj.optString("status", "N/A");
                        String last = obj.optString("last_donated", "Not Available");

                        tvnumber.setText(number);
                        tvbloodgrp.setText(blood);
                        tvlocation.setText(location);
                        if(status.equals("active")){
                            tvStatusBadge.setTextColor(Color.parseColor("#16A34A"));
                            statuspoint.setBackgroundResource(R.drawable.bg_btn_active);
                            statusbadge.setBackgroundResource(R.drawable.bg_status_active);
                        }
                        else{
                            tvStatusBadge.setTextColor(Color.parseColor("#EF4444"));
                            statuspoint.setBackgroundResource(R.drawable.bg_btn_deactive);
                            statusbadge.setBackgroundResource(R.drawable.bg_status_deactive);
                        }
                        tvStatusBadge.setText(status);

                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                })
                {

        @Override

        protected Map<String, String> getParams () {

            Map<String, String> map = new HashMap<>();

            map.put("user_id", id);

            return map;

        }

        };

        Volley.newRequestQueue(this).add(request);
    }


}