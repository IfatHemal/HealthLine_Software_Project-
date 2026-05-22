package com.enumtech.healthline;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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

import java.util.HashMap;
import java.util.Map;

public class SubmitReviewActivity extends AppCompatActivity {
    String userId;
    public static String doctor, doctor_id, appointmentId;
    TextView doctorname;
    EditText etReview;
    RatingBar ratingBar;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        doctorname = findViewById(R.id.tvDoctor);
        etReview = findViewById(R.id.etReview);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmit = findViewById(R.id.btnSubmit);

        doctorname.setText(""+doctor);


        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);

        userId = sharedPreferences.getString("id","");


        btnSubmit.setOnClickListener(v -> {

            float rating = ratingBar.getRating();

            String review = etReview.getText().toString();

            if(rating == 0){

                Toast.makeText(this,"Please give rating",Toast.LENGTH_SHORT).show();

                return;

            }

            submitReview(rating, review);

        });
    }


    private void submitReview(float rating, String review){

        String url = "https://ifathemalapp.com/apps/healthline/submit_review.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,

                response -> {

                if(response.contains("Review submitted")) {
                    Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_LONG).show();
                }
                else if(response.contains("Already reviewed")){
                    Toast.makeText(this, "Already reviewed", Toast.LENGTH_LONG).show();
                }
                else if(response.contains("Failed")){
                    Toast.makeText(this, "Review submission failed!", Toast.LENGTH_LONG).show();
                }
                else if(response.contains("connection failed")){
                    Toast.makeText(this, "Database connection failed!", Toast.LENGTH_LONG).show();
                }


                },

                error -> {

                    Toast.makeText(this,"Volley Error",Toast.LENGTH_SHORT).show();

                }){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> map = new HashMap<>();

                map.put("doctor_id", doctor_id);
                map.put("patient_id", userId);
                map.put("appointment_id", appointmentId);
                map.put("rating", String.valueOf(rating));
                map.put("review", review);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}