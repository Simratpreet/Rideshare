package com.simrat.myapplication;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by simrat on 14/11/15.
 */
public class AddCar extends AppCompatActivity{

    private String DEBUG_TAG = this.getClass().getName().toString();
    JSONArray cars;
    JSONObject brand_list;
    JSONArray car;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.brandSpinner) MaterialSpinner brandSpinner;
    @Bind(R.id.carSpinner) MaterialSpinner carSpinner;
    @Bind(R.id.seatText) TextView seatText;

    public AddCar(){
        try {
            cars = new JSONArray();
            brand_list = new JSONObject();

            car = new JSONArray();
            car.put(new JSONObject().put("Alto", "5"));
            car.put(new JSONObject().put("Wagon R", "5"));
            car.put(new JSONObject().put("Celerio", "5"));
            brand_list.put("Maruti Suzuki", car);

            car = new JSONArray();
            car.put(new JSONObject().put("i10", "5"));
            car.put(new JSONObject().put("Grand i10", "5"));
            car.put(new JSONObject().put("i20", "6"));
            brand_list.put("Hyundai", car);

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

        title.setText("Add Your Car");
        title.setTypeface(MyApplication.getPt_sans());
        mToolbar.setTitle("");

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
                    String sel = carSpinner.getSelectedItem().toString();
                    JSONArray jsonArray = brand_list.getJSONArray(brandSpinner.getSelectedItem().toString());
                    String[] car = new String[jsonArray.length()];
                    for (int j = 0; j < jsonArray.length(); j++) {
                        car[j] = jsonArray.getString(j);
                        Log.d(DEBUG_TAG, car[j]);
                        JSONObject object = new JSONObject(car[j]);
                        String seats = object.getString(sel);
                        Log.d(DEBUG_TAG, seats);
                        seatText.setText(seats);
                    }

                }catch (JSONException e){
                    Log.d(DEBUG_TAG, e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}