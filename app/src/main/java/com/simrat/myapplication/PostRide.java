package com.simrat.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by simrat on 1/11/15.
 */
public class PostRide extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    @Bind(R.id.go) Button go;
    @Bind(R.id.source) TextView source;
    @Bind(R.id.destination) TextView destination;
    @Bind(R.id.rootView) ViewGroup group;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.title) TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_ride);
        ButterKnife.bind(this);
        findViews();

        HashMap<String, String> ride_details;

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        else
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            ride_details = (HashMap<String, String>) bundle.getSerializable("RideFields");
            for(Map.Entry<String,String> entry : ride_details.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d(key, value);
            }
        }

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostRide.this, AddCar.class);
                startActivity(i);
                finish();
            }
        });

    }
    private void findViews(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setFont(group, MyApplication.getPt_sans());
        title.setText("Post Ride");
        mToolbar.setTitle("");
        source.setText(sharedPreferences.getString("source", ""));
        destination.setText(sharedPreferences.getString("destination", ""));
    }
    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof EditText || v instanceof Button) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof ViewGroup)
                setFont((ViewGroup) v, font);
        }
    }
}
