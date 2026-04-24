package com.enumtech.healthline;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class BloodDonorHomeFragment extends Fragment {


    LinearLayout manageprofile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View blooddonorView = inflater.inflate(R.layout.fragment_blood_donor_home, container, false);

        manageprofile = blooddonorView.findViewById(R.id.manageprofile);
        manageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity( new Intent(getActivity(),BloodDonorManageProfile.class));
            }
        });
        return blooddonorView;
    }
}