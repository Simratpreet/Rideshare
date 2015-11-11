package com.simrat.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class Home extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    private TextView shareRide, money, interact, logo;
    SharedPreferences sharedPreferences;
    private GestureDetector gestureDetector;
    Animation fadeIn, move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                Intent i;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String token = sharedPreferences.getString("AccessToken", "");
                String authToken = sharedPreferences.getString("AuthToken", "");
                Log.d("Token", token);
                if (token == "" && authToken == "") {
                    i = new Intent(Home.this, MainActivity.class);
                } else {
                    i = new Intent(Home.this, SecondActivity.class);
                }
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void findViews() {
        shareRide = (TextView) findViewById(R.id.share_your_ride_textview);
        interact = (TextView) findViewById(R.id.interact_textview);
        logo = (TextView) findViewById(R.id.logoText);
        move = AnimationUtils.loadAnimation(getApplication(), R.anim.move);
        fadeIn = AnimationUtils.loadAnimation(getApplication(), R.anim.fade_in);
        fadeIn = AnimationUtils.loadAnimation(getApplication(), R.anim.fade_in);
        logo.startAnimation(fadeIn);

        logo.setTypeface(MyApplication.getSquada_one());
        shareRide.setTypeface(MyApplication.getPt_sans());
        interact.setTypeface(MyApplication.getPt_sans());
        money = (TextView) findViewById(R.id.text_money);
        money.setTypeface(MyApplication.getPt_sans());
    }
}
