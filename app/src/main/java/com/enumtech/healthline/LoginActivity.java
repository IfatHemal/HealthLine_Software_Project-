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

import org.json.JSONException;
import org.json.JSONObject;

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
                    loginRequest(url,email,password);
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



    public void loginRequest(String url,String email,String password) {


            StringRequest request = new StringRequest(Request.Method.POST, url,

                    response -> {

                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.getString("status").equals("success")) {

                                String id = obj.getString("id");
                                String name = obj.getString("name");
                                String role = obj.getString("role");

                                SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id", id);
                                editor.putString("name", name);
                                editor.putString("email", email);
                                editor.putString("role", role);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Welcome " + name, Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            }

                        } catch (JSONException e) {

                            if (response.equals("password incorrect")) {
                                Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                            } else if (response.equals("no account")) {
                                Toast.makeText(LoginActivity.this, "No Account Found", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(LoginActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                    },

                    error -> Toast.makeText(LoginActivity.this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()

            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();

                    map.put("email", email);
                    map.put("password", password);

                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }

}