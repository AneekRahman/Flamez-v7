package com.flamez.app;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    // ---------------------------------------------------------------------------------------------

    ImageButton navHome, navGlobal, navMessage, navProfile, navCamera;

    FragmentManager fragmentManager;

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---------------------------------------------------------------------------------------------

        navHome = (ImageButton) findViewById(R.id.nav_home_btn);
        navGlobal = (ImageButton) findViewById(R.id.nav_global_btn);
        navMessage = (ImageButton) findViewById(R.id.nav_message_btn);
        navProfile = (ImageButton) findViewById(R.id.nav_profile_btn);
        navCamera = (ImageButton) findViewById(R.id.nav_camera_btn);

        fragmentManager = getSupportFragmentManager();
        final HomeFragment homeFragment = new HomeFragment();
        final UserProfileFragment userProfileFragment = new UserProfileFragment();

        // ---------------------------------------------------------------------------------------------

        Fragment fragmentCheck = fragmentManager.findFragmentById(R.id.main_fragment_holder);

        if(fragmentCheck == null || !fragmentCheck.isAdded()){

            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_holder, homeFragment)
                    .commit();

        }

        // HOME

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_holder, homeFragment)
                        .commit();

                unselectButtons();
                navHome.setImageResource(R.drawable.nav_home_icon_selected);

            }
        });

        // GLOBAL

        navGlobal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unselectButtons();
                navGlobal.setImageResource(R.drawable.nav_global_icon_selected);

            }
        });

        // MESSAGE

        navMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unselectButtons();
                navMessage.setImageResource(R.drawable.nav_message_icon_selected);

            }
        });

        // PROFILE

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_holder, userProfileFragment)
                        .commit();

                unselectButtons();
                navProfile.setImageResource(R.drawable.nav_profile_icon_selected);

            }
        });

        // CAMERA

        navCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraActivityIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(cameraActivityIntent);

            }
        });

        // ---------------------------------------------------------------------------------------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    // ---------------------------------------------------------------------------------------------

    public void unselectButtons(){

        navHome.setImageResource(R.drawable.nav_home_icon_unselected);
        navGlobal.setImageResource(R.drawable.nav_global_icon_unselected);
        navMessage.setImageResource(R.drawable.nav_message_icon_unselected);
        navProfile.setImageResource(R.drawable.nav_profile_icon_unselected);

    }

}