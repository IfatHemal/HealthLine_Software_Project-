package com.enumtech.healthline;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class UpdateSerialActivity extends AppCompatActivity {
    ListView listView;

    ArrayList<HashMap<String,String>> list = new ArrayList<>();

    MyAdapter adapter;

    String userid;
    TextView tvnoappointment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_serial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);

        userid = sharedPreferences.getString("id","");


            listView = findViewById(R.id.listAppointments);
            tvnoappointment = findViewById(R.id.tvnoappointment);

            adapter = new MyAdapter();

            listView.setAdapter(adapter);

            loadTodayAppointments();
    }

    private void loadTodayAppointments(){

        String url = "https://ifathemalapp.com/apps/healthline/get_today_appointments.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {

                        if(response.equals("notfound")){
                            tvnoappointment.setVisibility(View.VISIBLE);
                            list.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }


                            JSONArray array = new JSONArray(response);
                            list.clear();

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject obj = array.getJSONObject(i);

                                HashMap<String, String> map = new HashMap<>();

                                map.put("id", obj.getString("appointment_id"));
                                map.put("name", obj.getString("patient_name"));
                                map.put("serial", obj.getString("serial_no"));
                                map.put("status", obj.getString("status"));

                                list.add(map);
                            }

                            adapter.notifyDataSetChanged();


                    } catch (Exception e){}
                },
                error -> {}
        ){
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("user_id", userid);
                return map;
            }
        };

        Volley.newRequestQueue(UpdateSerialActivity.this).add(request);
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

            if(convertView == null){
                convertView = LayoutInflater.from(UpdateSerialActivity.this)
                        .inflate(R.layout.patientlistitem, parent, false);
            }

            TextView name = convertView.findViewById(R.id.tvName);
            TextView serial = convertView.findViewById(R.id.tvSerial);
            TextView status = convertView.findViewById(R.id.tvStatus);

            Button btnComplete = convertView.findViewById(R.id.btnComplete);
            Button btnSkip = convertView.findViewById(R.id.btnSkip);

            HashMap<String,String> map = list.get(position);

            name.setText("Name: " + map.get("name"));
            serial.setText("Serial: " + map.get("serial"));
            status.setText("Status: " + map.get("status"));

            if(map.get("status").equals("COMPLETED")){

                btnComplete.setEnabled(false);
                btnComplete.setAlpha(0.5f);
                btnSkip.setAlpha(0.5f);
                btnSkip.setEnabled(false);

            } else {

                btnComplete.setEnabled(true);
                btnSkip.setEnabled(true);
                btnComplete.setAlpha(1f);
                btnSkip.setAlpha(1f);

            }

            btnComplete.setOnClickListener(v -> {
                updateStatus(map.get("id"), "COMPLETED", position);
            });

            btnSkip.setOnClickListener(v -> {
                updateStatus(map.get("id"), "PENDING", position);
            });

            return convertView;
        }
    }

    private void updateStatus(String id, String status, int position){

        String url = "https://ifathemalapp.com/apps/healthline/update_status.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    list.get(position).put("status", status);
                    adapter.notifyDataSetChanged();

                },
                error -> {}
        ){
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("appointment_id", id);
                map.put("status", status);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}