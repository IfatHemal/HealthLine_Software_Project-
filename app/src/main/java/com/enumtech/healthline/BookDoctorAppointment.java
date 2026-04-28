package com.enumtech.healthline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class BookDoctorAppointment extends AppCompatActivity {

    EditText searchBar;
    Spinner hospitalSpinner, specialitySpinner;
    ListView listView;


    ArrayList<HashMap<String, String>> doctorList = new ArrayList<>();
    DoctorListAdapter adapter;

    String selectedHospital = "All";
    String selectedSpeciality = "All";
    String searchText = "";

    String url = "https://ifathemalapp.com/apps/healthline/doctorlist.php";

    boolean isRequestRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_doctor_appointment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchBar = findViewById(R.id.searchBar);
        hospitalSpinner = findViewById(R.id.hospitalSpinner);
        specialitySpinner = findViewById(R.id.specialitySpinner);
        listView = findViewById(R.id.listView);

        adapter = new DoctorListAdapter();
        listView.setAdapter(adapter);

        setupSpinners();

        loadDoctors();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                loadDoctors();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSpinners() {

        String[] hospitals = {
                "All",
                "Dhaka Medical College Hospital",
                "Square Hospital",
                "United Hospital",
                "Evercare Hospital",
                "Popular Medical Centre"
        };

        hospitalSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, hospitals));

        String[] specialities = {
                "All",
                "Cardiologist",
                "Dermatologist",
                "Neurologist",
                "Orthopedic",
                "Pediatrician",
                "Psychiatrist",
                "General Physician"
        };

        specialitySpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, specialities));

        hospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHospital = parent.getItemAtPosition(position).toString();
                loadDoctors();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        specialitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpeciality = parent.getItemAtPosition(position).toString();
                loadDoctors();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadDoctors() {

        if (isRequestRunning) return;
        isRequestRunning = true;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    isRequestRunning = false;
                    Log.d("API_RESPONSE", response);

                    doctorList.clear();

                    try {
                        JSONArray array = new JSONArray(response);

                        if (array.length() == 0) {
                            Toast.makeText(this, "No doctors found", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            HashMap<String, String> map = new HashMap<>();

                            map.put("doctorid",obj.getString("doctorid"));
                            map.put("name", obj.optString("name", "N/A"));
                            map.put("speciality", obj.optString("speciality", "N/A"));
                            map.put("hospital", obj.optString("hospital", "N/A"));
                            map.put("schedule", obj.optString("schedule", "N/A"));
                            map.put("fees", obj.optString("fees", "N/A"));
                            map.put("experience", obj.optString("experience", "N/A"));
                            map.put("rating", obj.optString("rating", "N/A"));
                            map.put("image",obj.getString("image"));
                            map.put("time", obj.optString("time"));

                            doctorList.add(map);
                        }

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
                map.put("hospital", selectedHospital);
                map.put("speciality", selectedSpeciality);
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

    public class DoctorListAdapter extends BaseAdapter {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @Override
        public int getCount() {
            return doctorList.size();
        }

        @Override
        public Object getItem(int position) {
            return doctorList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("WrongViewCast")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.doctoritem, parent, false);


                holder = new ViewHolder();

                holder.name = convertView.findViewById(R.id.tvName);
                holder.speciality = convertView.findViewById(R.id.tvSpeciality);
                holder.hospital = convertView.findViewById(R.id.tvHospital);
                holder.schedule = convertView.findViewById(R.id.tvSchedule);
                holder.fee = convertView.findViewById(R.id.tvFee);
                holder.experience = convertView.findViewById(R.id.tvExperience);
                holder.rating = convertView.findViewById(R.id.tvRating);
                holder.imgDoctor = convertView.findViewById(R.id.imgDoctor);
                holder.book = convertView.findViewById(R.id.book);
                holder.review = convertView.findViewById(R.id.review);



                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HashMap<String, String> model = doctorList.get(position);
            Picasso.get()
                    .load(model.get("image"))
                    .placeholder(R.drawable.default_profile_picture)
                    .error(R.drawable.default_profile_picture)
                    .into(holder.imgDoctor);

          String doctorname = model.get("name").toUpperCase();




            holder.name.setText(doctorname);
            holder.speciality.setText(model.get("speciality").toUpperCase());
            holder.hospital.setText(model.get("hospital").toUpperCase());
            holder.schedule.setText(model.get("schedule"));
            holder.fee.setText("Fee: " + model.get("fees") +" Taka");
            holder.experience.setText("Experience: " + model.get("experience") + " Years");
            holder.rating.setText("⭐ " + model.get("rating"));


            holder.book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BookingConfirmationActivity.doctorid = model.get("doctorid");
                    BookingConfirmationActivity.name = doctorname;
                    BookingConfirmationActivity.speciality= model.get("speciality").toUpperCase();
                    BookingConfirmationActivity.hospital = model.get("hospital").toUpperCase();
                    BookingConfirmationActivity.schedule = model.get("schedule");
                    BookingConfirmationActivity.fees = model.get("fees");
                    BookingConfirmationActivity.time = model.get("time");
                    startActivity(new Intent(BookDoctorAppointment.this,BookingConfirmationActivity.class));
                }
            });



            return convertView;
        }

        class ViewHolder {
            TextView name, speciality, hospital, schedule, fee, experience, rating;
            ImageView imgDoctor;

            Button review, book;
        }
    }
}