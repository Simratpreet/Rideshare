package com.simrat.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends FragmentActivity implements GenderFragment.GenderDialogListerner{

    private EditText firstnameText, lastnameText,  emailText, passText, phoneText, genderText;
    private AutoCompleteTextView locationText;
    private TextView registerText, loginText;
    private Button registerButton;
    SharedPreferences sharedPreferences;
    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        findViews();

    }
    private void findViews(){
        registerText = (TextView) findViewById(R.id.registerText);
        firstnameText = (EditText) findViewById(R.id.firstnameText);
        lastnameText = (EditText) findViewById(R.id.lastnameText);
        emailText = (EditText) findViewById(R.id.emailText);
        passText = (EditText) findViewById(R.id.passText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        locationText = (AutoCompleteTextView) findViewById(R.id.locationText);
        loginText = (TextView) findViewById(R.id.loginText);
        genderText = (EditText) findViewById(R.id.genderText);
        registerButton = (Button) findViewById(R.id.register_button);


        registerText.setTypeface(MyApplication.getPt_sans());
        firstnameText.setTypeface(MyApplication.getPt_sans());
        lastnameText.setTypeface(MyApplication.getPt_sans());
        emailText.setTypeface(MyApplication.getPt_sans());
        passText.setTypeface(MyApplication.getPt_sans());
        locationText.setTypeface(MyApplication.getPt_sans());
        phoneText.setTypeface(MyApplication.getPt_sans());
        genderText.setTypeface(MyApplication.getPt_sans());
        loginText.setTypeface(MyApplication.getPt_sans());
        registerButton.setTypeface(MyApplication.getPt_sans());

        List<String> cities = new ArrayList<>(Arrays.asList(City.city));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cities);
        locationText.setAdapter(cityAdapter);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpRegistration();
            }
        });
        final GenderFragment genderFragment = new GenderFragment();
        genderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genderFragment.show(getSupportFragmentManager(), "gender");

            }
        });

    }
    @Override
    public void onItemClick(String gender) {
        genderText.setText(gender);
    }

    private void setUpRegistration(){


        String email = emailText.getText().toString();
        String first_name = firstnameText.getText().toString();
        String last_name = lastnameText.getText().toString();
        String phone = phoneText.getText().toString();
        String password = passText.getText().toString();
        String gender = genderText.getText().toString();
        String city = locationText.getText().toString();

        if(!isValid(email, first_name, last_name, phone, password, gender, city))
            return;
        else{
            ProgressDialog pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Registering Please Wait ...");
            SignUpTask task = new SignUpTask(pDialog);
            task.execute(email, first_name, last_name, phone, password, gender, city);
        }


    }
    private boolean isValid(String email, String first_name, String last_name, String phone, String password,
                            String gender, String city){
        if(first_name.length() < 2){
            error = "First Name must be at least 2 characters long";
            firstnameText.setError(error);
            return false;
        }
        if(last_name.length() < 2){
            error = "Last Name must be at least 2 characters long";
            lastnameText.setError(error);
            return false;
        }
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            error = "Email must be like abc@example.com";
            emailText.setError(error);
            return false;
        }
        String PHONE_PATTERN = "^[789]\\d{9}$";
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(phone);
        if(!matcher.matches()){
            error = "Invalid Mobile Number";
            phoneText.setError(error);
            return false;
        }
        if(gender == null){
            error = "Enter your gender please.";
            genderText.setError(error);
            return false;
        }
        if(city == null){
            error = "Enter your city please";
            locationText.setError(error);
            return false;
        }
        if(password.length() < 8 ){
            error = "Password must be at least 8 characters long";
            passText.setError(error);
            return false;
        }
        return true;
    }
    private class SignUpTask extends AsyncTask<String, Void, JSONObject>{
        ProgressDialog progressDialog;
        private String signup_error;

        public  SignUpTask(ProgressDialog progressDialog){
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String ... params) {
            URL url;
            HttpURLConnection urlConnection;
            JSONObject holder = new JSONObject();
            JSONObject user = new JSONObject();
            String response = "";
            JSONObject json = new JSONObject();


            try {
                json.put("success", false);
                //json.put("info", "Something went wrong.. Retry !!");
                user.put("email", params[0]);
                user.put("first_name", params[1]);
                user.put("last_name", params[2]);
                user.put("phone", params[3]);
                user.put("password", params[4]);
                user.put("gender", params[5]);
                user.put("city", params[6]);
                holder.put("user",user);
                StringBuilder sb = new StringBuilder(holder.toString());

                url = new URL("https://shielded-earth-6986.herokuapp.com/users");

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

                int responseCode = urlConnection.getResponseCode();
                Log.d("Code", Integer.toString(responseCode));

                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while((line = br.readLine()) !=null) {
                        response += line;

                    }

                    sharedPreferences.edit().putString("Name", params[1] + " " + params[2]).commit();
                    json = new JSONObject(response);
                    sharedPreferences.edit().putString("AuthToken", json.getJSONObject("data").getString("auth_token")).commit();
                    Log.d("AuthToken", json.getJSONObject("data").getString("auth_token"));
                    Log.d("Auth", sharedPreferences.getString("AuthToken", ""));
                }
                else{
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while((line = br.readLine()) !=null) {
                        response += line;

                    }
                    signup_error = response.toString();
                    Toast.makeText(getApplicationContext(), signup_error, Toast.LENGTH_SHORT);
                }

            }catch (MalformedURLException e){

            }catch (IOException e){

            }catch (JSONException e){

            }
            Log.d("Response", response);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            Intent i = new Intent(Register.this, SecondActivity.class);
            startActivity(i);
            finish();
        }
    }

}
