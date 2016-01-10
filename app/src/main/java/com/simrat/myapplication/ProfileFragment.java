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
    @Bind(R.id.edit_profile_button) Button edit_profile;
    private TextView title;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        ButterKnife.bind(this, view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        findViews(view);
        return view;
    }
    private void findViews(View view){
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.rootView);
        setFont(viewGroup, MyApplication.getPt_sans());
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
        if(user.getMusic() != null && user.getMusic().contentEquals("true"))
            music.setChecked(true);
        if(user.getSmoke() != null && user.getSmoke().contentEquals("true"))
            smoke.setChecked(true);
        if(user.getDrink() != null && user.getDrink().contentEquals("true"))
            drink.setChecked(true);

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

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                startActivity(intent);
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
    }
}
