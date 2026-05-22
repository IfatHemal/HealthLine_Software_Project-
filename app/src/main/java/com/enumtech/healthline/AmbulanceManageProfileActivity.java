package com.enumtech.healthline;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
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

public class AmbulanceManageProfileActivity extends AppCompatActivity {

    TextView etname,etemail, btnsaveprofile, tvStatusBadge, tvlocation, tvnumber, statuspoint;
    TextView tvambulancename, tvambulancetype, tvambulancelicence, tvverification;
    String id;
    ShapeableImageView profileimage;
    LinearLayout statusbadge;
    RelativeLayout btnActivate,btnDeactivate,btnVerification;
    EditText city,area,number, etambulancename, etambulancetype, etambulancelicence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ambulance_manage_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etemail = findViewById(R.id.etEmail);
        etname = findViewById(R.id.etName);
        profileimage = findViewById(R.id.profileimage);
        btnsaveprofile = findViewById(R.id.btnSaveProfile);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        statusbadge = findViewById(R.id.statusbadge);
        btnActivate = findViewById(R.id.btnActivate);
        btnVerification = findViewById(R.id.btnVerification);
        tvverification = findViewById(R.id.tvverification);

        btnDeactivate = findViewById(R.id.btnDeactivate);
        city = findViewById(R.id.city);
        area = findViewById(R.id.area);
        number = findViewById(R.id.number);
        tvambulancelicence = findViewById(R.id.tvambulancelicence);
        tvambulancename = findViewById(R.id.tvambulancename);
        tvambulancetype = findViewById(R.id.tvambulancetype);
        etambulancelicence = findViewById(R.id.etabmulancelicence);
        etambulancename = findViewById(R.id.etabmulancename);
        etambulancetype = findViewById(R.id.etabmulancetype);

        tvlocation = findViewById(R.id.tvlocation);
        tvnumber = findViewById(R.id.tvnumber);
        statuspoint = findViewById(R.id.statuspoint);


        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String name = sharedPreferences.getString("name","");
        String email = sharedPreferences.getString("email", "");
        String role = sharedPreferences.getString("role","");
        String image = sharedPreferences.getString("image","");
        id = sharedPreferences.getString("id","");

        getAmbulanceDataRequest();

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

                if(city.getText().toString().isEmpty()){
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
                else if(etambulancelicence.getText().toString().isEmpty()){
                    etambulancelicence.setError("Ambulance licence number can't be empty!");
                } else if (etambulancename.getText().toString().isEmpty()) {
                    etambulancename.setError("Ambulance name can't be empty!");
                }
                else if(etambulancetype.getText().toString().isEmpty()){
                    etambulancetype.setError("Ambulance type must be defined");
                }
                else{
                    String url ="https://ifathemalapp.com/apps/healthline/updateambulanceprofile.php";
                    updateAmbulanceProfileRequest(url,"Deactive");
                }

            }
        });



        if(sharedPreferences.getString("status","").equalsIgnoreCase("active")){
            tvStatusBadge.setText("Activated");
            statusbadge.setBackgroundResource(R.drawable.bg_status_active);
            tvStatusBadge.setTextColor(Color.parseColor("#16A34A"));
        }
        else if(sharedPreferences.getString("status","").equalsIgnoreCase("deactive")){
            tvStatusBadge.setText("Deactivated");
            tvStatusBadge.setTextColor(Color.parseColor("#EF4444"));
            statusbadge.setBackgroundResource(R.drawable.bg_status_deactive);
        }


        btnVerification.setOnClickListener(v -> {
            requestAmbulanceVerification();
        });

        btnActivate.setOnClickListener(v -> {

            if(tvambulancelicence.getText().toString().isEmpty()||tvambulancename.getText().toString().isEmpty()||tvambulancetype.getText().toString().isEmpty()||tvlocation.getText().toString().isEmpty()||tvnumber.getText().toString().isEmpty()){
                Toast.makeText(AmbulanceManageProfileActivity.this,"Set up your profile information first, then try again",Toast.LENGTH_LONG).show();
            }
            else {

                String url = "https://ifathemalapp.com/apps/healthline/update_ambulance_active_status.php";
                updateAmbulanceProfileRequest(url, "activated");
                getAmbulanceDataRequest();

                Toast.makeText(this, "Your ID is now Activated", Toast.LENGTH_SHORT).show();
            }
        });


        btnDeactivate.setOnClickListener(v -> {


            String url = "https://ifathemalapp.com/apps/healthline/update_ambulance_active_status.php";
            updateAmbulanceProfileRequest(url,"deactivated");
            getAmbulanceDataRequest();
            Toast.makeText(this, "Your ID is now Deactivated", Toast.LENGTH_SHORT).show();
        });
    }


    public void updateAmbulanceProfileRequest(String url, String status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                if(s.contains("Updated successfully")){
                    Toast.makeText(AmbulanceManageProfileActivity.this,"Updated Successfully",Toast.LENGTH_SHORT).show();
                    getAmbulanceDataRequest();
                } else if (s.contains("Inserted successfully")) {
                    Toast.makeText(AmbulanceManageProfileActivity.this,"Saved Successfully",Toast.LENGTH_SHORT).show();
                    getAmbulanceDataRequest();
                }
                else if (s.equalsIgnoreCase("Insert failed:")) {
                    Toast.makeText(AmbulanceManageProfileActivity.this,"Insert Failed",Toast.LENGTH_LONG).show();
                    getAmbulanceDataRequest();
                }
                else {
                    Toast.makeText(AmbulanceManageProfileActivity.this,"Can't update, there is something wrong",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AmbulanceManageProfileActivity.this,"Error: ",Toast.LENGTH_LONG).show();
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

                myMap.put("id", String.valueOf(id));
                myMap.put("ambulancename",String.valueOf(etambulancename.getText()));
                myMap.put("ambulancetype",String.valueOf(etambulancetype.getText()));
                myMap.put("ambulancelicence",String.valueOf(etambulancelicence.getText()));
                myMap.put("location",String.valueOf(city.getText()+"-"+area.getText()));
                myMap.put("number",String.valueOf(number.getText()));
                myMap.put("activestatus",status);

                return myMap;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AmbulanceManageProfileActivity.this);
        queue.add(stringRequest);

    }



    public void getAmbulanceDataRequest(){

        StringRequest request = new StringRequest(Request.Method.POST, "https://ifathemalapp.com/apps/healthline/get_ambulance_driver.php",
                response -> {

                    try {


                        if(response.trim().startsWith("{")){
                            JSONObject obj = new JSONObject(response);

                            String status = obj.optString("status");

                            if(status.equals("empty")){
                                Toast.makeText(getApplicationContext(), "No ambulance drivers data found", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                        JSONArray array = new JSONArray(response);

                        if(array.length() == 0){
                            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject obj = array.getJSONObject(0);

                        String number = obj.optString("number", "N/A");
                        String location = obj.optString("location", "N/A");
                        String status = obj.optString("status", "N/A");
                        String ambulancename = obj.optString("ambulance_name", "N/A");
                        String ambulancetype = obj.optString("ambulance_type", "N/A");
                        String ambulancelicence = obj.optString("ambulance_licence", "N/A");


                        tvnumber.setText(number);
                        tvlocation.setText(location);
                        tvambulancelicence.setText(ambulancelicence);
                        tvambulancename.setText(ambulancename);
                        tvambulancetype.setText(ambulancetype);

                        if(status.equals("activated")){
                            tvStatusBadge.setTextColor(Color.parseColor("#16A34A"));
                            statuspoint.setBackgroundResource(R.drawable.bg_btn_active);
                            statusbadge.setBackgroundResource(R.drawable.bg_status_active);
                        }
                        else{
                            tvStatusBadge.setTextColor(Color.parseColor("#EF4444"));
                            statuspoint.setBackgroundResource(R.drawable.bg_btn_deactive);
                            statusbadge.setBackgroundResource(R.drawable.bg_status_deactive);
                        }
                        tvStatusBadge.setText(status.toUpperCase());

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

    private void requestAmbulanceVerification(){

        String url =
                "https://ifathemalapp.com/apps/healthline/request_ambulance_verification.php";

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

                        if(status.equals("success")){

                            tvverification.setText(
                                    "Verification Requested"
                            );

                            btnVerification.setEnabled(false);
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

                map.put("user_id", id);

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