package com.enumtech.healthline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    TextView tvSignup ;
    EditText etEmail,etPassword;
    Button btLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvSignup = findViewById(R.id.tvSignup);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btLogin = findViewById(R.id.btnLogin);


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(email.isEmpty()||password.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Fillup all the field",Toast.LENGTH_SHORT).show();
                }
                else {

                    String url = "https://ifathemalapp.com/apps/healthline/login.php";
                    stringRequest(url);
                }

            }
        });



        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

    }



    public void stringRequest(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(s.equalsIgnoreCase("no account")){
                    Toast.makeText(LoginActivity.this,"There is no account on this email, try signup first",Toast.LENGTH_LONG).show();
                }
                else if(s.equalsIgnoreCase("password incorrect")){
                    Toast.makeText(LoginActivity.this,"Incorrect Password!, try again.",Toast.LENGTH_LONG).show();
                    etPassword.setError("Enter correct password");
                }
                else{
                    SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email",etEmail.getText().toString());
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(LoginActivity.this,"Error: ",Toast.LENGTH_LONG).show();
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
                myMap.put("email",etEmail.getText().toString());
                myMap.put("password",etPassword.getText().toString());


                return myMap;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(stringRequest);

    }
}