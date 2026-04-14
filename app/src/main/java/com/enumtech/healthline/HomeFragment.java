package com.enumtech.healthline;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;


public class HomeFragment extends Fragment {


    HorizontalScrollView scrollView;
    Handler handler = new Handler();

    int scrollX = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View homeView = inflater.inflate(R.layout.fragment_home, container, false);


       scrollView = homeView.findViewById(R.id.scrollview);


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



        return  homeView;
    }







}