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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.Car;
import com.simrat.myapplication.model.Ride;

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
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by simrat on 1/11/15.
 */
public class PostRide extends AppCompatActivity {

    private String DEBUG_TAG = this.getClass().getName().toString();
    private SharedPreferences sharedPreferences;
    @Bind(R.id.publish_ride)
    Button publish_ride;
    @Bind(R.id.rootView)
    ViewGroup group;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.add_car_navigator)
    ImageView addCarNavigator;
    @Bind(R.id.car_select)
    Spinner carSelect;
    @Bind(R.id.price_per_seat) EditText pricePerSeat;
    @Bind(R.id.allowed_passengers) EditText allowedPassengers;
    private RideshareDbHelper dbHelper;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_ride);
        ButterKnife.bind(this);
        dbHelper = new RideshareDbHelper(getApplicationContext());
        findViews();
        token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AuthToken", "");

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

        ArrayAdapter<String> carsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dbHelper.myCars(dbHelper.getUser(token).getId()));
        carSelect.setAdapter(carsAdapter);

        addCarNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCar.class);
                startActivity(intent);
            }
        });

        publish_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishRide();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ArrayAdapter<String> carsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dbHelper.myCars(dbHelper.getUser(token).getId()));
        carSelect.setAdapter(carsAdapter);
    }

    private void findViews(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setFont(group, MyApplication.getPt_sans());
        title.setText("Post Ride");
        mToolbar.setTitle("");
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

    public void publishRide(){
        String source = sharedPreferences.getString("source", "");
        String destination = sharedPreferences.getString("destination", "");
        String time = sharedPreferences.getString("time", "");
        String ride_car = carSelect.getSelectedItem().toString();
        String price_per_seat = pricePerSeat.getText().toString();
        String allowed_passengers = allowedPassengers.getText().toString();
        if(source.isEmpty() || destination.isEmpty() || time.isEmpty() || ride_car.isEmpty() || price_per_seat.isEmpty() || allowed_passengers.isEmpty())
            Toast.makeText(getApplicationContext(), "Please fill all the details !", Toast.LENGTH_SHORT).show();
        else{
            ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Publishing Your Ride ...");
            PostRideTask task = new PostRideTask(pDialog);
            task.execute(token, source, destination, time, ride_car, price_per_seat, allowed_passengers);
        }
    }
    private class PostRideTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;
        private int responseCode;
        private String response = "";
        private String toast = "Invalid details.";

        public PostRideTask(ProgressDialog progressDialog){
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
            JSONObject ride = new JSONObject();
            dbHelper.getColumns();
            try{
                ride.put("source", params[1]);
                ride.put("destination", params[2]);
                ride.put("journey_datetime", params[3]);
                ride.put("price_per_seat", params[5]);
                ride.put("allowed_passengers", params[6]);
                holder.put("auth_token", params[0]);
                holder.put("car", params[4]);
                holder.put("ride", ride);

                url = new URL("https://shielded-earth-6986.herokuapp.com/rides");
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
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(holder.toString());
                bw.flush();
                bw.close();
                os.close();
                responseCode = urlConnection.getResponseCode();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while((line = br.readLine()) !=null) {
                    response += line;
                }
                Log.d(DEBUG_TAG, response);
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String name = params[4].substring(0, params[4].length() - 7);
                    String reg_no = params[4].substring(params[4].length() - 4);
                    Log.d(DEBUG_TAG, name + reg_no);
                    int car_id = dbHelper.getRideCar(Integer.toString(dbHelper.getUser(token).getId()), name, reg_no);
                    Log.d(DEBUG_TAG, Integer.toString(car_id));
                    Ride _ride = new Ride(dbHelper.getUser(token).getId(), car_id, params[1], params[2], params[3], params[5], params[6]);
                    dbHelper.addRide(_ride);
                    toast = "Uploaded your ride";
                }
                Log.d(DEBUG_TAG, response);
                urlConnection.disconnect();
            }catch (MalformedURLException e){

            }catch (IOException e){
                Log.d(DEBUG_TAG, e.getMessage());
            }catch (JSONException e){
                Log.d(DEBUG_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(PostRide.this, toast, Toast.LENGTH_SHORT).show();

        }
    }
}
