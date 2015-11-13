package com.simrat.myapplication;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.User;

/**
 * Created by simrat on 14/11/15.
 */
public class EditProfile extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView title;
    private EditText firstname, lastname, email, phone, gender, city;
    private SharedPreferences sharedPreferences;
    private RideshareDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        findViews();

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
    }
    private void findViews(){
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.rootView);
        setFont(viewGroup, MyApplication.getPt_sans());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        title.setText("Edit Profile");
        title.setTypeface(MyApplication.getPt_sans());
        mToolbar.setTitle("");
        findViewById(R.id.pencil).setVisibility(View.INVISIBLE);
        String token = sharedPreferences.getString("AuthToken", "");
        dbHelper = new RideshareDbHelper(getApplicationContext());
        User user = dbHelper.getUser(token);
        firstname = (EditText) findViewById(R.id.firstnameText);
        lastname = (EditText) findViewById(R.id.lastnameText);
        email = (EditText) findViewById(R.id.emailText);
        phone = (EditText) findViewById(R.id.phoneText);
        gender = (EditText) findViewById(R.id.genderText);
        city = (EditText) findViewById(R.id.cityText);
        firstname.setText(user.getFirst_name());
        lastname.setText(user.getLast_name());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        gender.setText(user.getGender());
        city.setText(user.getCity());
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
