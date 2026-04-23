package com.enumtech.healthline;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackSerialActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    MyAdapter adapter;

    String userid;
    TextView noappointment;

    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_serial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listTrack);
        noappointment = findViewById(R.id.noappointment);

        SharedPreferences sp = getSharedPreferences("myApp", MODE_PRIVATE);
        userid = sp.getString("id", "");

        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        loadTodayAppointments();

        runnable = new Runnable() {
            @Override
            public void run() {
                loadTodayAppointments();
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void loadTodayAppointments() {

        String url = "https://ifathemalapp.com/apps/healthline/get_current_serial.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {

                        if(response.equals("notfound")){
                            noappointment.setVisibility(View.VISIBLE);
                            list.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        JSONArray array = new JSONArray(response);

                        list.clear();

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject obj = array.getJSONObject(i);

                            HashMap<String, String> map = new HashMap<>();

                            map.put("id", obj.optString("appointment_id"));
                            map.put("serial", obj.optString("serial_no"));
                            map.put("status", obj.optString("status"));
                            map.put("doctor", obj.optString("doctor_name"));
                            map.put("speciality", obj.optString("speciality"));
                            map.put("hospital", obj.optString("hospital"));
                            map.put("date", obj.optString("appointment_date"));
                            map.put("start_time", obj.optString("start_time"));
                            map.put("end_time", obj.optString("end_time"));
                            map.put("current", obj.optString("current_serial"));

                            list.add(map);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Load Failed", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", userid);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(TrackSerialActivity.this)
                        .inflate(R.layout.currentappointmentitem, parent, false);
            }

            HashMap<String, String> map = list.get(position);

            ((TextView) convertView.findViewById(R.id.tvDoctor)).setText(map.get("doctor"));
            ((TextView) convertView.findViewById(R.id.tvSpeciality)).setText(map.get("speciality").toUpperCase());
            ((TextView) convertView.findViewById(R.id.tvHospital)).setText(map.get("hospital").toUpperCase());
            ((TextView) convertView.findViewById(R.id.tvDate)).setText("Appointment Date: "+map.get("date"));
            ((TextView) convertView.findViewById(R.id.tvTime)).setText("Appointment Time: "+map.get("start_time")+" - "+map.get("end_time"));
            ((TextView) convertView.findViewById(R.id.tvSerial)).setText("Serial: " + map.get("serial"));
            ((TextView) convertView.findViewById(R.id.tvStatus)).setText("Status: " + map.get("status"));

            TextView tvCurrent = convertView.findViewById(R.id.tvCurrent);

            String current = map.get("current");

            if (current == null || current.equals("0")) {
                tvCurrent.setText("Consultation didn't started yet, starts at " + map.get("start_time"));
            } else {
                tvCurrent.setText("Currently serving serial: " + current);
            }

            return convertView;
        }
    }
}