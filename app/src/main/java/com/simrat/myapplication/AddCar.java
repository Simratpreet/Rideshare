package com.simrat.myapplication;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.Car;
import com.simrat.myapplication.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by simrat on 14/11/15.
 */
public class AddCar extends AppCompatActivity{

    private String DEBUG_TAG = this.getClass().getName().toString();
    private RideshareDbHelper dbHelper;
    private User user;
    JSONArray cars;
    JSONObject brand_list;
    JSONArray car;
    private String token;
    @Bind(R.id.rootView) ViewGroup group;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.brandSpinner) MaterialSpinner brandSpinner;
    @Bind(R.id.carSpinner) MaterialSpinner carSpinner;
    @Bind(R.id.seatText) TextView seatText;
    @Bind(R.id.regno) TextView regNo;



    public AddCar(){
        try {
            cars = new JSONArray();
            brand_list = new JSONObject();

            car = new JSONArray();
            car.put(new JSONObject().put("Alto800", "5"));
            car.put(new JSONObject().put("Wagon R", "5"));
            car.put(new JSONObject().put("Celerio", "5"));
            car.put(new JSONObject().put("Eeco", "7"));
            car.put(new JSONObject().put("Stingray", "5"));
            car.put(new JSONObject().put("Estilo", "5"));
            car.put(new JSONObject().put("Baleno", "5"));
            car.put(new JSONObject().put("Ritz", "5"));
            car.put(new JSONObject().put("Gypsy", "7"));
            car.put(new JSONObject().put("Swift", "5"));
            car.put(new JSONObject().put("Swift Dzire", "5"));
            car.put(new JSONObject().put("Ertiga", "7"));
            car.put(new JSONObject().put("Ciaz", "5"));
            car.put(new JSONObject().put("SCross", "5"));
            car.put(new JSONObject().put("Grand Vitara", "5"));
            car.put(new JSONObject().put("Omni", "8"));
            brand_list.put("Maruti Suzuki", car);

            car = new JSONArray();
            car.put(new JSONObject().put("i10", "5"));
            car.put(new JSONObject().put("Grand i10", "5"));
            car.put(new JSONObject().put("Eon", "5"));
            car.put(new JSONObject().put("Santro", "5"));
            car.put(new JSONObject().put("Getz", "5"));
            car.put(new JSONObject().put("Accent", "5"));
            car.put(new JSONObject().put("i20", "6"));
            car.put(new JSONObject().put("Xcent", "5"));
            car.put(new JSONObject().put("Verna", "5"));
            car.put(new JSONObject().put("Creta", "7"));
            car.put(new JSONObject().put("Elantra", "5"));
            car.put(new JSONObject().put("Sonata", "5"));
            car.put(new JSONObject().put("Santa Fe", "7"));
            brand_list.put("Hyundai", car);

            car = new JSONArray();
            car.put(new JSONObject().put("Brio", "5"));
            car.put(new JSONObject().put("Amaze", "5"));
            car.put(new JSONObject().put("Jazz", "5"));
            car.put(new JSONObject().put("City", "5"));
            car.put(new JSONObject().put("CRV", "7"));
            brand_list.put("Honda", car);

            cars.put(brand_list);

            Log.d(DEBUG_TAG, cars.toString());
        }catch (JSONException e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_car);
        new AddCar();
        ButterKnife.bind(this);

        setFont(group, MyApplication.getPt_sans());
        title.setText("Add Your Car");
        title.setTypeface(MyApplication.getPt_sans());
        mToolbar.setTitle("");

        token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("AuthToken", "");
        dbHelper = new RideshareDbHelper(getApplicationContext());
        user = dbHelper.getUser(token);

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
        String[] brand;
        Iterator<String> brands = brand_list.keys();
        brand = new String[brand_list.length()];
        for(int i=0;i<brand_list.length();i++){
            brand[i] = brands.next();
        }
        //Arrays.sort(brand);
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, brand);
        brandSpinner.setAdapter(brandAdapter);
        JSONArray jsonArray;
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String sel = brandSpinner.getSelectedItem().toString();
                    Log.d(DEBUG_TAG, sel);
                    JSONArray jsonArray = brand_list.getJSONArray(sel);
                    Log.d(DEBUG_TAG, jsonArray.toString());
                    String[] car = new String[jsonArray.length()];
                    for (int j = 0; j < jsonArray.length(); j++) {
                        car[j] = jsonArray.getString(j);
                        Log.d(DEBUG_TAG, car[j]);
                        JSONObject object = new JSONObject(car[j]);
                        Iterator<String> iterator = object.keys();
                        car[j] = iterator.next();
                        Log.d(DEBUG_TAG, car[j]);
                    }
                    //Arrays.sort(car);
                    ArrayAdapter<String> carAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, car);
                    carSpinner.setAdapter(carAdapter);
                } catch (JSONException e) {
                    Log.d(DEBUG_TAG, e.getMessage());
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    int pos = carSpinner.getSelectedItemPosition();
                    JSONArray jsonArray = brand_list.getJSONArray(brandSpinner.getSelectedItem().toString());
                    JSONObject object = jsonArray.getJSONObject(pos);
                    Log.d(DEBUG_TAG, jsonArray.toString());
                    Log.d(DEBUG_TAG, object.toString());
                    String seats = object.getString(carSpinner.getSelectedItem().toString());
                    Log.d(DEBUG_TAG, seats);
                    seatText.setText(seats);

                }catch (JSONException e){
                    Log.d(DEBUG_TAG, e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    @OnClick(R.id.addCar) public void addCar(){
        String brand = brandSpinner.getSelectedItem().toString();
        String car = carSpinner.getSelectedItem().toString();
        String car_name = brand + " " + car;
        String seats = seatText.getText().toString();
        String reg_no = regNo.getText().toString().toUpperCase();
        ProgressDialog pDialog = new ProgressDialog(AddCar.this);
        pDialog.setMessage("Adding Your Car ...");
        AddCarTask task = new AddCarTask(pDialog);
        task.execute(token, car_name, seats, reg_no);
    }
    private class AddCarTask extends AsyncTask<String, Void, Void>{
        private ProgressDialog progressDialog;
        private int responseCode;
        private String response = "";
        private String toast = "Invalid details.";

        public AddCarTask(ProgressDialog progressDialog){
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
            JSONObject car = new JSONObject();

            try{
                car.put("name", params[1]);
                car.put("seats", params[2]);
                car.put("registration_no", params[3]);
                holder.put("auth_token", params[0]);
                holder.put("car", car);

                url = new URL("https://shielded-earth-6986.herokuapp.com/cars");
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
                    Car _car = new Car(user.getId(), params[1], Integer.parseInt(params[2]), params[3]);
                    dbHelper.addCar(_car);
                    toast = "Added your car";

                }
                Log.d(DEBUG_TAG, response);
                urlConnection.disconnect();
            }catch (MalformedURLException e){
                //Log.d(DEBUG_TAG. e.getMessage());
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
            Toast.makeText(AddCar.this, toast, Toast.LENGTH_SHORT).show();

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