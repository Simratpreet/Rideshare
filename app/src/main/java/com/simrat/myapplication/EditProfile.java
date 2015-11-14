package com.simrat.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by simrat on 14/11/15.
 */
public class EditProfile extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView title;
    private EditText firstname, lastname, email, phone, gender, city, age;
    private SharedPreferences sharedPreferences;
    private RideshareDbHelper dbHelper;
    private Switch music, smoke, drink;
    private TextView save;
    private String token;
    private String DEBUG_TAG = this.getClass().getName().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        findViews();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        //findViewById(R.id.pencil).setVisibility(View.INVISIBLE);
        token = sharedPreferences.getString("AuthToken", "");
        dbHelper = new RideshareDbHelper(getApplicationContext());
        User user = dbHelper.getUser(token);
        firstname = (EditText) findViewById(R.id.firstnameText);
        lastname = (EditText) findViewById(R.id.lastnameText);
        email = (EditText) findViewById(R.id.emailText);
        phone = (EditText) findViewById(R.id.phoneText);
        gender = (EditText) findViewById(R.id.genderText);
        city = (EditText) findViewById(R.id.cityText);
        age = (EditText) findViewById(R.id.ageText);
        music = (Switch) findViewById(R.id.music);
        smoke = (Switch) findViewById(R.id.smoke);
        drink = (Switch) findViewById(R.id.drink);
        save = (TextView) findViewById(R.id.save);

        firstname.setText(user.getFirst_name());
        lastname.setText(user.getLast_name());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        gender.setText(user.getGender());
        city.setText(user.getCity());
        Log.d(DEBUG_TAG, user.getDrink());
        if(user.getAge() != 0)
            age.setText(Integer.toString(user.getAge()));
        if(user.getMusic().contentEquals("true"))
            music.setChecked(true);
        if(user.getSmoke().contentEquals("true"))
            smoke.setChecked(true);
        if(user.getDrink().contentEquals("true"))
            drink.setChecked(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpEdit();
            }
        });
    }
    private void setUpEdit(){
        String first_name = firstname.getText().toString();
        String last_name = lastname.getText().toString();
        String _email = email.getText().toString();
        String _phone = phone.getText().toString();
        String _gender = gender.getText().toString();
        String _city = city.getText().toString();
        int _age = Integer.parseInt(age.getText().toString());
        String _music = "false";
        if(music.isChecked())
            _music = "true";
        String _smoke = "false";
        if(smoke.isChecked())
            _smoke = "true";
        String _drink = "false";
        if(drink.isChecked())
            _drink = "true";


        ProgressDialog pDialog = new ProgressDialog(EditProfile.this);
        pDialog.setMessage("Saving Please Wait ...");
        EditTask task = new EditTask(pDialog);
        task.execute(token, first_name, last_name, _email, _phone, _gender,
                _city, Integer.toString(_age), _music, _smoke, _drink);
    }
    private class EditTask extends AsyncTask<String, Void, Void>{

        private ProgressDialog progressDialog;
        private int responseCode;
        private String error;

        public EditTask(ProgressDialog progressDialog){
            this.progressDialog = progressDialog;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection;
            JSONObject holder = new JSONObject();
            JSONObject _user = new JSONObject();
            String response = "";
            JSONObject json = new JSONObject();


            try {
                holder.put("auth_token", params[0]);
                _user.put("first_name", params[1]);
                _user.put("last_name", params[2]);
                _user.put("email", params[3]);
                _user.put("phone", params[4]);
                _user.put("gender", params[5]);
                _user.put("city", params[6]);
                _user.put("age", params[7]);
                _user.put("music_lover", params[8]);
                _user.put("smoker", params[9]);
                _user.put("drinker", params[10]);
                holder.put("user", _user);

                url = new URL("https://shielded-earth-6986.herokuapp.com/edit_profile");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                bw.write(holder.toString());
                bw.flush();
                bw.close();
                os.close();

                responseCode = urlConnection.getResponseCode();
                Log.d(DEBUG_TAG, Integer.toString(responseCode));
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while((line = br.readLine()) !=null) {
                        response += line;

                    }
                    json = new JSONObject(response);

                    sharedPreferences.edit().putString("Name", params[1] + " " + params[2]).commit();
                    User user = new User();
                    user.setFirst_name(params[1]);
                    user.setLast_name(params[2]);
                    user.setEmail(params[3]);
                    user.setPhone(params[4]);
                    user.setGender(params[5]);
                    user.setCity(params[6]);
                    user.setAge(Integer.parseInt(params[7]));
                    user.setMusic(params[8]);
                    user.setSmoke(params[9]);
                    user.setDrink(params[10]);
                    dbHelper.updateUser(token, user);

                }
                else{
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while((line = br.readLine()) !=null) {
                        response += line;
                        Log.d(DEBUG_TAG, line);
                    }
                    error = response.toString();
                    Log.d(DEBUG_TAG, error);

                }
                urlConnection.disconnect();
            }catch (MalformedURLException e){

            }catch (IOException e){

            }catch (JSONException e){

            }
            Log.d(DEBUG_TAG, response);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.hide();
            if(responseCode == HttpURLConnection.HTTP_OK){
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }
            else {
                ErrorDialog dialog = new ErrorDialog();
                Bundle args = new Bundle();
                args.putString("error", "Invalid Details");
                dialog.setArguments(args);
                dialog.setError();
                dialog.show(getSupportFragmentManager(), "ErrorDialog");
            }
        }
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
