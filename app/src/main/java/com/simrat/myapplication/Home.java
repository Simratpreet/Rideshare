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
import android.widget.TextView;


public class Home extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    SharedPreferences sharedPreferences;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                Intent i;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String token = sharedPreferences.getString("AccessToken", "");
                Log.d("Token", token);
                if(token == "") {
                     i = new Intent(Home.this, MainActivity.class);
                }
                else {
                    i = new Intent(Home.this, SecondActivity.class);
                }
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (gestureDetector.onTouchEvent(event)) {
//            return true;
//        }
//        return super.onTouchEvent(event);
//    }
//
//    private void onLeftSwipe() {
//        // Do something
//        Intent i;
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String token = sharedPreferences.getString("AccessToken", "");
//        Log.d("Token", token);
//        if(token == "") {
//            i = new Intent(Home.this, MainActivity.class);
//        }
//        else {
//            i = new Intent(Home.this, SecondActivity.class);
//        }
//        startActivity(i);
//
//        // close this activity
//        finish();
//    }
//
//    private void onRightSwipe() {
//        // Do something
//    }
//
//    private class SwipeGestureDetector
//            extends GestureDetector.SimpleOnGestureListener {
//        // Swipe properties, you can change it to make the swipe
//        // longer or shorter and speed
//        private static final int SWIPE_MIN_DISTANCE = 120;
//        private static final int SWIPE_MAX_OFF_PATH = 200;
//        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2,
//                               float velocityX, float velocityY) {
//            try {
//                float diffAbs = Math.abs(e1.getY() - e2.getY());
//                float diff = e1.getX() - e2.getX();
//
//                if (diffAbs > SWIPE_MAX_OFF_PATH)
//                    return false;
//
//                // Left swipe
//                if (diff > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Home.this.onLeftSwipe();
//
//                    // Right swipe
//                } else if (-diff > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Home.this.onRightSwipe();
//                }
//            } catch (Exception e) {
//                Log.e("YourActivity", "Error on gestures");
//            }
//            return false;
//        }
//    }
}

