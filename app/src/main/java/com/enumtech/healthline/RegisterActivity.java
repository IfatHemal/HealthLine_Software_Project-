package com.enumtech.healthline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Spinner spinnerRole;
    TextView tvLogin;
    EditText etFullName, etEmail, etPassword, etConfirmPassword;
    Button btnCreateAccount;
    String selectedRole,name,email,password,confirmpassword ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        spinnerRole = findViewById(R.id.spinnerRole);
        tvLogin = findViewById(R.id.tvLogin);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);




        String[] roles = {"Select Role","Patient", "Doctor", "Blood Donor", "Ambulance Driver"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, roles);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);


        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
               finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 selectedRole = spinnerRole.getSelectedItem().toString().toLowerCase();


                 name = etFullName.getText().toString();
                 email = etEmail.getText().toString();
                 password = etPassword.getText().toString();
                 confirmpassword = etConfirmPassword.getText().toString();



                 if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()){
                     Toast.makeText(RegisterActivity.this,"Please Enter All The Input Fields",Toast.LENGTH_LONG).show();
                 }
                 else {

                     if(selectedRole.equalsIgnoreCase("Select Role")){
                         Toast.makeText(RegisterActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
                     }
                     else if(!password.equals(confirmpassword)){
                         Toast.makeText(RegisterActivity.this,"Password doesn't matched",Toast.LENGTH_LONG).show();
                     }
                     else {
                         String url = "https://ifathemalapp.com/apps/healthline/signup.php";
                         stringRequest(url);
                         finish();
                     }
                 }
            }
        });



    }

    public void stringRequest(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {


                if(s.trim().equalsIgnoreCase("User already exist, change email and try again")){
                    Toast.makeText(RegisterActivity.this,"User already exist, try to login.",Toast.LENGTH_LONG).show();
                }
                else if(s.trim().equalsIgnoreCase("signup successful")){
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                }
                else {
                    Toast.makeText(RegisterActivity.this,"Signup failed",Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(RegisterActivity.this,"Error: ",Toast.LENGTH_LONG).show();
                if(volleyError.networkResponse != null){
                    Log.e("VOLLEY_ERROR", new String(volleyError.networkResponse.data));
                } else {
                    Log.e("VOLLEY_ERROR", volleyError.toString());
                }

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> myMap = new HashMap<String,String>();
                myMap.put("role",selectedRole);
                myMap.put("name",name);
                myMap.put("email",email);
                myMap.put("password",password);


                return myMap;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(stringRequest);

    }
}