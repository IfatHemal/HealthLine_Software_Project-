package com.enumtech.healthline;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BloodDonorList extends AppCompatActivity {

    ListView bloodDonorListview;
    Spinner spinnerbloodgroup;
    MyAdapter adapter;
    String selectedBloodGroup = "All Blood Group";
    EditText etsearchlocation;
    String searchText = "";
    boolean isRequestRunning = false;

    TextView tvdonorcount;
    ArrayList<HashMap<String,String>> bloodDonorlist = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blood_donor_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bloodDonorListview = findViewById(R.id.bloodDonorListview);
        spinnerbloodgroup = findViewById(R.id.spinnerBloodGroup);
        etsearchlocation = findViewById(R.id.etSearchLocation);
        tvdonorcount = findViewById(R.id.tvDonorCount);

        adapter = new MyAdapter();
        bloodDonorListview.setAdapter(adapter);


        loadBloodDonor();



        etsearchlocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                loadBloodDonor();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });



        String[] bloodgroup = {
                "All Blood Group",
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
        };
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, bloodgroup);
        bloodAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerbloodgroup.setAdapter(bloodAdapter);


        spinnerbloodgroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBloodGroup = parent.getItemAtPosition(position).toString();
                loadBloodDonor();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });




    }




    private void loadBloodDonor() {

        String url = "https://ifathemalapp.com/apps/healthline/blood_donor_list.php";
        if (isRequestRunning) return;
        isRequestRunning = true;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    isRequestRunning = false;
                    Log.d("API_RESPONSE", response);

                    bloodDonorlist.clear();

                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() == 0) {
                            Toast.makeText(this, "No Blood Donor found", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            HashMap<String, String> map = new HashMap<>();

                            map.put("blood_donor_id",obj.getString("blood_donor_id"));
                            map.put("name", obj.optString("name", "N/A"));
                            map.put("blood_group", obj.optString("blood_group", "N/A"));
                            map.put("location", obj.optString("location", "N/A"));
                            map.put("number", obj.optString("number", "N/A"));
                            map.put("last_donated", obj.optString("last_donated", "N/A"));

                            bloodDonorlist.add(map);
                        }

                        int count = bloodDonorlist.size();
                        tvdonorcount.setText("Found "+count+" Donors");
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    isRequestRunning = false;
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", String.valueOf(error));
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("blood_group", selectedBloodGroup);
                map.put("search", searchText);
                return map;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                1,
                1.0f
        ));

        Volley.newRequestQueue(this).add(request);
    }



    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount() {
            return bloodDonorlist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.blood_donor_item, parent, false);
            }

            TextView tvdonorname = convertView.findViewById(R.id.tvDonorName);
            TextView tvbloodgroup = convertView.findViewById(R.id.tvBloodGroup);
            TextView tvlocation = convertView.findViewById(R.id.tvLocation);
            TextView tvphone = convertView.findViewById(R.id.tvPhone);
            TextView tvlastdonation = convertView.findViewById(R.id.tvLastDonation);
            LinearLayout btncontactdonor = convertView.findViewById(R.id.btnContactDonor);


            HashMap<String, String> hashMap = bloodDonorlist.get(position);


            String blood_donor_id = hashMap.get("blood_donor_id");
            String name = hashMap.get("name");
            String bloodgroup = hashMap.get("blood_group");
            String location = hashMap.get("location");
            String number = hashMap.get("number");
            String last_donated = hashMap.get("last_donated");

            tvdonorname.setText(name);
            tvbloodgroup.setText(bloodgroup);
            tvlocation.setText(location);
            tvphone.setText(number);
            tvlastdonation.setText("Last Donated: "+last_donated);




            return convertView;
        }
    }
}