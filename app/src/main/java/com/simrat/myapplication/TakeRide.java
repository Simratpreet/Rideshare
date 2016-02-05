package com.simrat.myapplication;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.Ride;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TakeRide extends Fragment {

    public static final String TAG = "SampleActivityBase";
    private String DEBUG_TAG = this.getClass().getName().toString();
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private Calendar now;
    private DatePickerDialog.OnDateSetListener date;
    private TimePickerDialog.OnTimeSetListener time;
    private TextView fromText,toText,selectText;
    private AutoCompleteTextView mAutocompleteViewSource, mAutocompleteViewDest;
    private EditText datetime;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(21.000000, 78.000000), new LatLng(21.000000, 78.000000));
    private Button next;
    private RideshareDbHelper dbHelper;
    String token;

    public TakeRide() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        findViews(view);

        dbHelper = new RideshareDbHelper(getContext());
        token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("AuthToken", "");
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteViewSource.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteViewDest.setOnItemClickListener(mAutocompleteClickListener);


        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getContext(), android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_INDIA, null);
        mAutocompleteViewSource.setAdapter(mAdapter);
        mAutocompleteViewDest.setAdapter(mAdapter);

        //Code to set up date time Dialogs
        setUpDateTimeText();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void findViews(View view){
        fromText = (TextView) view.findViewById(R.id.from_text);
        toText = (TextView) view.findViewById(R.id.to_text);
        selectText = (TextView) view.findViewById(R.id.selectdatetimetext);
        fromText.setTypeface(MyApplication.getPt_sans());
        toText.setTypeface(MyApplication.getPt_sans());
        selectText.setTypeface(MyApplication.getPt_sans());
        mAutocompleteViewSource = (AutoCompleteTextView)
                view.findViewById(R.id.autocomplete_places_source);
        mAutocompleteViewDest = (AutoCompleteTextView) view.findViewById(R.id.autocomplete_places_destination);
        datetime = (EditText) view.findViewById(R.id.datetime);
        datetime.setTypeface(MyApplication.getPt_sans());
        mAutocompleteViewSource.setTypeface(MyApplication.getPt_sans());
        mAutocompleteViewDest.setTypeface(MyApplication.getPt_sans());
        next = (Button) view.findViewById(R.id.next);
        next.setText("Search your Ride");
        next.setTypeface(MyApplication.getPt_sans());
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String from = mAutocompleteViewSource.getText().toString();
                String to = mAutocompleteViewDest.getText().toString();
                final String journey_datetime = datetime.getText().toString();
                if(from.isEmpty() || to.isEmpty() || journey_datetime.isEmpty()){
                    Toast.makeText(getContext(), "Please fill the details !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d(DEBUG_TAG, from + " " + to + " " + journey_datetime);
                    final ProgressDialog pDialog = new ProgressDialog(getContext());
                    pDialog.setMessage("Searching Your Ride ...");
                    pDialog.show();
                    SearchRideTask task = new SearchRideTask(pDialog);
                    String url = "https://shielded-earth-6986.herokuapp.com/search_rides";
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    StringRequest request = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d(DEBUG_TAG, response);
                                    pDialog.hide();
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", error.getMessage());
                                    pDialog.hide();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("source", from);
                            params.put("journey_datetime", journey_datetime);
                            params.put("auth_token", token);
                            return params;
                        }
                    };
                    queue.add(request);
                    //task.execute(token, from, to, journey_datetime);
                }
            }
        });
    }
    private class SearchRideTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;
        private int responseCode;
        private String response = "";
        private String toast = "Invalid details.";

        public SearchRideTask(ProgressDialog progressDialog){
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
            dbHelper.getColumns();
            try{

                url = new URL("https://shielded-earth-6986.herokuapp.com/rides");
                Log.d(DEBUG_TAG, url.toString());
                String get_params = "source=" + params[1] + "destination=" + params[2] + "journey_datetime=" + params[3] +
                        "auth_token=" + params[0];
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(get_params.getBytes().length));
                urlConnection.setRequestProperty("Content-Language", "en-US");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                // Send request
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.writeBytes(get_params);
                wr.flush();
                wr.close();
                // Get Response
                InputStream is = urlConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                String responseStr = response.toString();
                Log.d(DEBUG_TAG, responseStr);
                urlConnection.disconnect();
            }catch (MalformedURLException e){

            }catch (IOException e){
                Log.d(DEBUG_TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

        }
    }
    private void setUpDateTimeText(){
        datetime.setKeyListener(null);
        datetime.setClickable(true);
        now = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, monthOfYear);
                now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                TimePickerDialog t = TimePickerDialog.newInstance(time, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                t.show(getActivity().getFragmentManager(), "TimePicker");
                String month = getMonth(monthOfYear);
                String date = dayOfMonth + " " + month + " " + year;
                datetime.setText(date);
            }
        };

        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                now.set(Calendar.HOUR_OF_DAY, hourOfDay);
                now.set(Calendar.MINUTE, minute);
                String min = Integer.toString(minute);
                if(minute < 10)
                    min = "0" + minute;
                int hours = hourOfDay % 12;
                String am_pm;
                if(hourOfDay < 12)
                    am_pm = "AM";
                else am_pm = "PM";
                String time = " - " + hours + " : " + min + " " + am_pm;
                datetime.append(time);
            }
        };
        datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog d = DatePickerDialog.newInstance(date, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                d.show(getActivity().getFragmentManager(), "DatePicker");

            }
        });
    }
    private String getMonth(int m) {
        switch (m) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";

            default:
                break;
        }
        return null;
    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
             */

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
             */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView

            places.release();
        }
    };
}
