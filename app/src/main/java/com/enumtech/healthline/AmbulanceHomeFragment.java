package com.enumtech.healthline;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class AmbulanceHomeFragment extends Fragment {

    LinearLayout  manageprofile, manageRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View ambulanceView= inflater.inflate(R.layout.fragment_ambulance_home, container, false);


        manageprofile = ambulanceView.findViewById(R.id.manageprofile);
        manageRequest = ambulanceView.findViewById(R.id.manageRequest);


        manageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(),AmbulanceManageProfileActivity.class));
            }
        });


        manageRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(getActivity(),AmbulanceManageRequestActivity.class));
            }
        });

        return ambulanceView;
    }
}