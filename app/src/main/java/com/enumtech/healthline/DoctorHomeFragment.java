package com.enumtech.healthline;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


public class DoctorHomeFragment extends Fragment {


    LinearLayout manageprofile;
    HorizontalScrollView scrollView;
    Handler handler = new Handler();
    int scrollX = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View doctorHomeView = inflater.inflate(R.layout.fragment_doctor_home, container, false);


        scrollView = doctorHomeView.findViewById(R.id.scrollview);
        manageprofile = doctorHomeView.findViewById(R.id.manageprofile);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int maxScroll = scrollView.getChildAt(0).getWidth() / 2;
                scrollX += 5;

                if (scrollX >= maxScroll) {
                    scrollX = 0;
                }
                scrollView.scrollTo(scrollX, 0);
                handler.postDelayed(this, 30);
            }
        };

        scrollView.postDelayed(() -> {
            handler.post(runnable);
        }, 20);


        manageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),DoctorManageProfile.class));
            }
        });










        return doctorHomeView;
    }
}