package com.simrat.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    private String DEBUG_TAG = this.getClass().getName().toString();
    private RideshareDbHelper dbHelper;
    private TextView personName, email, phone, gender, city, age;
    @Bind(R.id.music) Switch music;
    @Bind(R.id.smoke) Switch smoke;
    @Bind(R.id.drink) Switch drink;
    static SharedPreferences sharedPreferences;
    static private ImageView profilePic;
    private ImageView pencil;
    private TextView title;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().findViewById(R.id.pencil).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.pencil).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().findViewById(R.id.pencil).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        ButterKnife.bind(this, view);
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        findViews(view);
        return view;
    }
    private void findViews(View view){
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.rootView);
        setFont(viewGroup, MyApplication.getPt_sans());
        pencil = (ImageView) getActivity().findViewById(R.id.pencil);
        pencil.setVisibility(View.VISIBLE);
        title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("My Profile");
        String token = sharedPreferences.getString("AuthToken", "");
        dbHelper = new RideshareDbHelper(getContext());
        personName = (TextView) view.findViewById(R.id.person_name);
        personName.setText(sharedPreferences.getString("Name", "").toUpperCase());
        email = (TextView) view.findViewById(R.id.emailText);
        phone = (TextView) view.findViewById(R.id.phoneText);
        gender = (TextView) view.findViewById(R.id.genderText);
        city = (TextView) view.findViewById(R.id.cityText);
        age = (TextView) view.findViewById(R.id.ageText);

        profilePic = (ImageView) view.findViewById(R.id.profile_pic);
        Log.d(DEBUG_TAG, token);
        User user = dbHelper.getUser(token);
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        gender.setText(user.getGender());
        city.setText(user.getCity());
        if(user.getAge() != 0)
            age.setText(Integer.toString(user.getAge()));
        if(user.getMusic().contentEquals("true"))
            music.setChecked(true);
        if(user.getSmoke().contentEquals("true"))
            smoke.setChecked(true);
        if(user.getDrink().contentEquals("true"))
            drink.setChecked(true);
//        if(sharedPreferences.getString("Gender", "") != "")
//            gender.setText(sharedPreferences.getString("Gender", "").substring(0,1).toUpperCase() +
//                    sharedPreferences.getString("Gender", "").substring(1) + ", "
//                    + sharedPreferences.getString("Age", "") + " years old");
//
//        edu.setText(sharedPreferences.getString("Education", ""));
//        work.setText(sharedPreferences.getString("Work", ""));
//        location.setText(sharedPreferences.getString("Location", "")

        String path = getContext().getFilesDir() + "/" + token + "dp.jpg";
        File file = new File(path);

        if(file.exists()){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                profilePic.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        else{
            path = getContext().getFilesDir() + "/" + token + "dp.png";
            file = new File(path);
            if(file.exists()){
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    profilePic.setImageBitmap(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UploadProfilePic.class);
                startActivity(i);

            }
        });
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditProfile.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        dbHelper.getColumns();

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
    public static void setProfilePic(){
        String profilePicBitmap = sharedPreferences.getString("ProfilePic", "");
        byte[] decodedPic = Base64.decode(profilePicBitmap, Base64.DEFAULT);
        profilePic.setImageBitmap(BitmapFactory.decodeByteArray(decodedPic, 0, decodedPic.length));
    }

    @Override
    public void onPause() {
        super.onPause();
        pencil.setVisibility(View.INVISIBLE);
    }
}
