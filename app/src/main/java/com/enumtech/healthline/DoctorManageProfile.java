package com.enumtech.healthline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DoctorManageProfile extends AppCompatActivity {

    Spinner spinnerHospital, spinnerSpeciality;
    EditText  etExperience;

    TextView btnSave, btnback ,etName, etEmail;
    RelativeLayout btnVerification, btnActivate, btnDeactivate;
    TextView tvStatusBadge;
    ImageView profileimage;

    String selectedHospital,selectedSpeciality,experience,id;
    LinearLayout statusbadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_manage_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerHospital = findViewById(R.id.spinnerHospital);
        spinnerSpeciality = findViewById(R.id.spinnerSpeciality);
        etName = findViewById(R.id.etName);
        etEmail  = findViewById(R.id.etEmail);
        etExperience  = findViewById(R.id.etExperience);
        btnSave  = findViewById(R.id.btnSaveProfile);
        btnVerification = findViewById(R.id.btnVerification);
        btnActivate  = findViewById(R.id.btnActivate);
        btnDeactivate = findViewById(R.id.btnDeactivate);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        btnback   = findViewById(R.id.btnback);
        profileimage = findViewById(R.id.profileimage);
        statusbadge = findViewById(R.id.statusbadge);


        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorManageProfile.this,MainActivity.class));
            }
        });
        String[] hospitals = {
                "Select Hospital",
                "Dhaka Medical College Hospital",
                "Square Hospital",
                "United Hospital",
                "Evercare Hospital",
                "Popular Medical Centre"
        };
        ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, hospitals);
        hospitalAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerHospital.setAdapter(hospitalAdapter);

        String[] specialities = {
                "Select Speciality",
                "Cardiologist",
                "Dermatologist",
                "Neurologist",
                "Orthopedic",
                "Pediatrician",
                "Psychiatrist",
                "General Physician"
        };
        ArrayAdapter<String> specialityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, specialities);
        specialityAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerSpeciality.setAdapter(specialityAdapter);



        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String name = sharedPreferences.getString("name","");
        String email = sharedPreferences.getString("email", "");
        String role = sharedPreferences.getString("role","");
        String image = sharedPreferences.getString("image","");
        id = sharedPreferences.getString("id","");

        etName.setText(name);
        etEmail.setText(email);

        if (image != null && !image.isEmpty() && !image.equals("null")){

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture)
                    .into(profileimage);
        }



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedHospital = spinnerHospital.getSelectedItem().toString().toLowerCase();
                selectedSpeciality = spinnerSpeciality.getSelectedItem().toString().toLowerCase();
                experience = etExperience.getText().toString();


                if(selectedHospital.equals("select hospital") || selectedSpeciality.equals("select speciality")){
                    Toast.makeText(DoctorManageProfile.this,"Hospital and Speciality must be selected",Toast.LENGTH_LONG).show();
                }
                else{
                    String url ="https://ifathemalapp.com/apps/healthline/updatedoctorprofile.php";
                    updateDoctorProfileRequest(url,"Deactive");
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


        btnVerification.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Verification request sent to admin!", Toast.LENGTH_SHORT).show();
        });


        btnActivate.setOnClickListener(v -> {

            tvStatusBadge.setText("Active");
            tvStatusBadge.setTextColor(Color.parseColor("#16A34A"));
            statusbadge.setBackgroundResource(R.drawable.bg_status_active);
            String url = "https://ifathemalapp.com/apps/healthline/updateactivestatus.php";
            updateDoctorProfileRequest(url,"active");
            editor.putString("status","active");
            editor.apply();

            Toast.makeText(this, "Your ID is now Active", Toast.LENGTH_SHORT).show();
        });


        btnDeactivate.setOnClickListener(v -> {

            tvStatusBadge.setText("Inactive");
            tvStatusBadge.setTextColor(Color.parseColor("#EF4444"));
           statusbadge.setBackgroundResource(R.drawable.bg_status_deactive);
            String url = "https://ifathemalapp.com/apps/healthline/updateactivestatus.php";
            updateDoctorProfileRequest(url,"deactive");
            editor.putString("status","deactive");
            editor.apply();
            Toast.makeText(this, "Your ID is now Inactive", Toast.LENGTH_SHORT).show();
        });

    }



    public void updateDoctorProfileRequest(String url, String status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                if(s.contains("Updated successfully")){
                    Toast.makeText(DoctorManageProfile.this,"Updated Successfully",Toast.LENGTH_LONG).show();
                } else if (s.contains("Inserted successfully")) {
                    Toast.makeText(DoctorManageProfile.this,"Inserted Successfully",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DoctorManageProfile.this,"Can't update, there is something wrong",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(DoctorManageProfile.this,"Error: ",Toast.LENGTH_LONG).show();
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
                myMap.put("hospital", String.valueOf(selectedHospital));
                myMap.put("speciality", String.valueOf(selectedSpeciality));
                myMap.put("experience", String.valueOf(experience));
                myMap.put("activestatus",status);

                return myMap;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(DoctorManageProfile.this);
        queue.add(stringRequest);

    }

}