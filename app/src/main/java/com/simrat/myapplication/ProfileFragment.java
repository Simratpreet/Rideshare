package com.simrat.myapplication;


import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;

public class ProfileFragment extends Fragment {

    private TextView personName, gender, edu, work, location, prefs;
    private Switch music, smoke, drink;
    SharedPreferences sharedPreferences;
    private ImageView profilePic;
    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        findViews(view);
        return view;
    }
    private void findViews(View view){
        personName = (TextView) view.findViewById(R.id.person_name);
        personName.setText(sharedPreferences.getString("Name", ""));
        personName.setTypeface(MyApplication.getPt_sans());

        gender = (TextView) view.findViewById(R.id.genderText);
        edu = (TextView) view.findViewById(R.id.eduText);
        work = (TextView) view.findViewById(R.id.workText);
        location = (TextView) view.findViewById(R.id.locText);

        gender.setTypeface(MyApplication.getPt_sans());
        edu.setTypeface(MyApplication.getPt_sans());
        work.setTypeface(MyApplication.getPt_sans());
        location.setTypeface(MyApplication.getPt_sans());

        gender.setText(sharedPreferences.getString("Gender", "").substring(0,1).toUpperCase() +
                sharedPreferences.getString("Gender", "").substring(1) + ", " + sharedPreferences.getString("Age", "") + " years old");
        edu.setText(sharedPreferences.getString("Education", ""));
        work.setText(sharedPreferences.getString("Work", ""));
        location.setText(sharedPreferences.getString("Location", ""));
        String profilePicBitmap = sharedPreferences.getString("ProfilePic", "");
        byte[] decodedPic = Base64.decode(profilePicBitmap, Base64.DEFAULT);
        profilePic = (ImageView) view.findViewById(R.id.profile_pic);
        profilePic.setImageBitmap(BitmapFactory.decodeByteArray(decodedPic, 0, decodedPic.length));

        prefs = (TextView) view.findViewById(R.id.prefText);
        prefs.setTypeface(MyApplication.getPt_sans());
        music = (Switch) view.findViewById(R.id.music);
        smoke = (Switch) view.findViewById(R.id.smoke);
        drink = (Switch) view.findViewById(R.id.drink);
        music.setTypeface(MyApplication.getPt_sans());
        smoke.setTypeface(MyApplication.getPt_sans());
        drink.setTypeface(MyApplication.getPt_sans());

    }


}
