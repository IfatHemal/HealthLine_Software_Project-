package com.enumtech.healthline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_nav);


        sharedPreferences = getSharedPreferences("myApp",MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        if(email.length()<=0){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        else{

            String role = sharedPreferences.getString("role", "");

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(role.equalsIgnoreCase("patient")) {
                fragmentTransaction.replace(R.id.framelayout, new HomeFragment());
                fragmentTransaction.commit();
            }
            else if(role.equalsIgnoreCase("doctor")){
                fragmentTransaction.replace(R.id.framelayout, new DoctorHomeFragment());
                fragmentTransaction.commit();
            }
            else if(role.equalsIgnoreCase("blood donor")){
                fragmentTransaction.replace(R.id.framelayout, new BloodDonorHomeFragment());
                fragmentTransaction.commit();
            }

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    int item = menuItem.getItemId();


                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                    if(item == R.id.home){

                        if(role.equalsIgnoreCase("patient")) {
                            fragmentTransaction.replace(R.id.framelayout, new HomeFragment());
                            fragmentTransaction.commit();
                        }
                        else if(role.equalsIgnoreCase("doctor")){
                            fragmentTransaction.replace(R.id.framelayout, new DoctorHomeFragment());
                            fragmentTransaction.commit();
                        } else if (role.equalsIgnoreCase("blood donor")) {
                            fragmentTransaction.replace(R.id.framelayout, new BloodDonorHomeFragment());
                            fragmentTransaction.commit();
                        }
                    }
                    else if (item == R.id.profile) {
                        fragmentTransaction.replace(R.id.framelayout,new ProfileFragment());
                        fragmentTransaction.commit();
                    }
                    else if(item == R.id.notification){
                        bottomNavigationView.getOrCreateBadge(R.id.notification).clearNumber();
                        fragmentTransaction.replace(R.id.framelayout, new NotificationFragment());
                        fragmentTransaction.commit();
                    }



                    return true;
                }
            });







        }



    }
}