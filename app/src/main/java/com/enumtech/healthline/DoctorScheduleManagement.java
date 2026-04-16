package com.enumtech.healthline;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoctorScheduleManagement extends AppCompatActivity {

    CheckBox sat, sun, mon, tue, wed, thu, fri;
    TextView startTime, endTime;

    String start_time = "", end_time = "",id;
    ArrayList<String> selectedDays = new ArrayList<>();

    Button btnupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_schedule_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        btnupdate = findViewById(R.id.btnupdate);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);


        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        id = sharedPreferences.getString("id","");


        startTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                start_time = String.format("%02d:%02d", hourOfDay, minute1);
                startTime.setText(start_time);
            }, hour, minute, false);

            dialog.show();
        });



        endTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute12) -> {
                end_time = String.format("%02d:%02d", hourOfDay, minute12);
                endTime.setText(end_time);
            }, hour, minute, false);

            dialog.show();
        });





        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDays.clear();

                if(sat.isChecked()) selectedDays.add("Saturday");
                if(!sat.isChecked()) selectedDays.remove("Saturday");
                if(sun.isChecked()) selectedDays.add("Sunday");
                if(!sun.isChecked()) selectedDays.remove("Sunday");
                if(mon.isChecked()) selectedDays.add("Monday");
                if(!mon.isChecked()) selectedDays.remove("Monday");
                if(tue.isChecked()) selectedDays.add("Tuesday");
                if(!tue.isChecked()) selectedDays.remove("Tuesday");
                if(wed.isChecked()) selectedDays.add("Wednesday");
                if(!wed.isChecked()) selectedDays.remove("Wednesday");
                if(thu.isChecked()) selectedDays.add("Thursday");
                if (!thu.isChecked()) selectedDays.remove("Thursday");
                if(fri.isChecked()) selectedDays.add("Friday");
                if(!fri.isChecked()) selectedDays.remove("Friday");

                if(selectedDays.isEmpty()){
                    Toast.makeText(DoctorScheduleManagement.this,"Select at least one day",Toast.LENGTH_SHORT).show();
                }
                else if(start_time.isEmpty() || end_time.isEmpty()){

                    Toast.makeText(DoctorScheduleManagement.this,"Select starting and ending consultation time",Toast.LENGTH_SHORT).show();
                }
                else{

                    RequestQueue queue = Volley.newRequestQueue(DoctorScheduleManagement.this);
                    String url = "https://ifathemalapp.com/apps/healthline/schedule.php";

                    StringRequest request = new StringRequest(Request.Method.POST, url,
                            response -> {

                                if(response.contains("success")){
                                    Toast.makeText(DoctorScheduleManagement.this,"Schedule Updated Successfully",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DoctorScheduleManagement.this,response,Toast.LENGTH_SHORT).show();
                                }

                            },
                            error -> Log.e("VOLLEY", error.toString())
                    ){
                        @Override
                        protected Map<String, String> getParams() {

                            Map<String, String> params = new HashMap<>();

                            params.put("id", id);
                            params.put("start_time", start_time);
                            params.put("end_time", end_time);

                            // send as comma separated string
                            params.put("days", selectedDays.toString());

                            return params;
                        }
                    };


                    queue.add(request);

                }

            }
        });







    }
}