package com.enumtech.healthline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ProfileFragment extends Fragment {

    TextView tvlogout, tvname, tvrole, tvemail;
    String userid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View profileview = inflater.inflate(R.layout.fragment_profile, container, false);

        tvlogout = profileview.findViewById(R.id.tvlogout);
        tvname = profileview.findViewById(R.id.tvname);
        tvrole = profileview.findViewById(R.id.tvrole);
        tvemail = profileview.findViewById(R.id.tvemail);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        tvname.setText(sharedPreferences.getString("name","").toUpperCase());
        tvemail.setText(sharedPreferences.getString("email",""));
        tvrole.setText(sharedPreferences.getString("role","").toUpperCase()+" PROFILE");
        userid = sharedPreferences.getString("id","");


        tvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout",((dialog, which) ->{
                            editor.clear();
                            editor.apply();
                            startActivity(new Intent(getActivity(),MainActivity.class));
                            getActivity().finish();
                            } ))
                        .setNegativeButton("No",(dialog, which) -> {
                            dialog.dismiss();
                            } )
                        .show();

            }
        });


        return profileview;
    }

}