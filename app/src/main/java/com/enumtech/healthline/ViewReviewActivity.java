package com.enumtech.healthline;

import android.content.Intent;
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

public class ViewReviewActivity extends AppCompatActivity {

    ListView viewReviewList;
    ArrayList<HashMap<String,String>> reviewList = new ArrayList<>();
    TextView docname, overallrating, totalreview,tvallreview;
    public static String doctorid, doctorName;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewReviewList = findViewById(R.id.reviewViewList);
        docname = findViewById(R.id.docname);
        overallrating = findViewById(R.id.overallrating);
        totalreview = findViewById(R.id.totalreview);
        tvallreview = findViewById(R.id.tvallreview);

        docname.setText(""+doctorName);


         myAdapter = new MyAdapter();
        viewReviewList.setAdapter(myAdapter);
        loadReviews(doctorid);

    }



    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return reviewList.size();
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

            if(convertView == null){

                convertView = LayoutInflater.from(ViewReviewActivity.this)

                        .inflate(R.layout.showreviewitem, parent, false);

            }


            TextView tvName = convertView.findViewById(R.id.tvname);
            TextView tvReview = convertView.findViewById(R.id.tvreview);
            TextView tvDate = convertView.findViewById(R.id.tvdate);
            TextView tvRating = convertView.findViewById(R.id.tvrating);

            HashMap<String,String> map = reviewList.get(position);

            tvName.setText("Reviewed By: " +map.get("patient_name"));

            tvReview.setText(map.get("review"));

            tvDate.setText("Date: "+map.get("date"));

            tvRating.setText(map.get("rating")+" ⭐️");



            return convertView;
        }
    }


    public void loadReviews(String doctorId){

        String url = "https://ifathemalapp.com/apps/healthline/get_reviews.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                response -> {

                    try {

                        JSONArray array = new JSONArray(response);


                        reviewList.clear();

                        double total = 0;

                        for(int i=0; i<array.length(); i++){

                            JSONObject obj = array.getJSONObject(i);

                            total += obj.optDouble("rating",0);

                            HashMap<String,String> map = new HashMap<>();

                            map.put("reviewid", obj.optString("reviewid"));

                            map.put("patient_name", obj.optString("patient_name"));

                            map.put("rating", obj.optString("rating"));

                            map.put("review", obj.optString("review"));

                            map.put("date", obj.optString("date"));

                            reviewList.add(map);

                        }

                        double avg = 0;

                        if(array.length() > 0){

                            avg = total / array.length();

                        }

                        overallrating.setText("Overall Rating: "+String.format("%.1f",avg)+" ⭐");

                        totalreview.setText("Total Review : "+reviewList.size());

                        if(reviewList.size()==0){

                            tvallreview.append(" : No Review Found");
                        }


                        myAdapter.notifyDataSetChanged();

                    } catch (Exception e){
                        e.printStackTrace();

                        Toast.makeText(
                                ViewReviewActivity.this,
                                "Parsing Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                },

                error -> {

                    Toast.makeText(
                            ViewReviewActivity.this,
                            "Server Error",
                            Toast.LENGTH_SHORT
                    ).show();

                }

        ){

            @Override
            protected Map<String, String> getParams(){

                Map<String,String> map = new HashMap<>();

                map.put("doctor_id", doctorId);

                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}