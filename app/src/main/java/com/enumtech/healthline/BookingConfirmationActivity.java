package com.enumtech.healthline;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class BookingConfirmationActivity extends AppCompatActivity {
    TextView tvname,tvspecialitiy,tvhospital,tvfees,time;
    Spinner selectDate;
    static String name =null, speciality= null,hospital = null,fees = null,schedule, doctorid=null;
    Button book;
    List<String> dates;
    String patientid, selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        patientid = sharedPreferences.getString("id","");



        selectDate = findViewById(R.id.selectDate);
        book = findViewById(R.id.book);
        tvname = findViewById(R.id.tvname);
        tvspecialitiy = findViewById(R.id.tvSpeciality);
        tvhospital = findViewById(R.id.tvHospital);
        tvfees = findViewById(R.id.tvFee);
        time = findViewById(R.id.time);




        if(schedule != null && !schedule.isEmpty()) {

            String[] rawDays = schedule.split(",");
            List<String> cleanDays = new ArrayList<>();

            for (String d : rawDays) {
                if(d != null){
                    d = d.trim().toLowerCase();


                    if(d.contains(" ")){
                        d = d.substring(0, d.indexOf(" "));
                    }

                    if(d.contains("(")){
                        d = d.substring(0, d.indexOf("("));
                    }

                    if(d.contains("-")){
                        d = d.substring(0, d.indexOf("-"));
                    }

                    cleanDays.add(d.trim());
                }
            }

            if(!cleanDays.isEmpty()) {

                dates = getNextAvailableDates(cleanDays.toArray(new String[0]));

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        dates
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectDate.setAdapter(adapter);
            }
        }

        selectDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                appointmentRequest();
            }
        });


        tvname.setText(name);
        tvspecialitiy.setText(speciality);
        tvhospital.setText(hospital);
        tvfees.setText("Fee: "+fees+" Taka");

    }



    private List<String> getNextAvailableDates(String[] doctorDays){

        List<String> result = new ArrayList<>();


        Set<String> availableDays = new HashSet<>();
        for(String d : doctorDays){
            availableDays.add(d.toLowerCase());
        }

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

        int count = 0;
        int safety = 0;

        while(count < 3 && safety < 30){

            String dayName = dayFormat.format(calendar.getTime()).toLowerCase();

            if(availableDays.contains(dayName)){
                result.add(dateFormat.format(calendar.getTime()));
                count++;
            }

            calendar.add(Calendar.DATE, 1);
            safety++;
        }


        return result;
    }

    public void appointmentRequest(){


        String url ="https://ifathemalapp.com/apps/healthline/appointment.php" ;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

            if(response.contains("success")) {
                Toast.makeText(BookingConfirmationActivity.this, "Appointment Booked Successfully", Toast.LENGTH_LONG).show();
                Log.d("APPOINTMENT", response);
            }
            if(response.contains("error")){
                Toast.makeText(BookingConfirmationActivity.this,"Input error",Toast.LENGTH_LONG).show();

            }

            if(response.contains("errors")){
                        Toast.makeText(BookingConfirmationActivity.this,"Database error",Toast.LENGTH_LONG).show();

                    }
                },
                error -> {
                    Toast.makeText(BookingConfirmationActivity.this,"Appointment Booking Failed",Toast.LENGTH_LONG).show();

                    Log.e("APPOINTMENT", error.toString());
                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();


                params.put("doctor_id", doctorid);
                params.put("patient_id", patientid);
                params.put("appointment_date", selectedDate);


                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(BookingConfirmationActivity.this);
        queue.add(request);


    }



}