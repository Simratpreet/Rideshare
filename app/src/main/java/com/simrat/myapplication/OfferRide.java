package com.simrat.myapplication;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class OfferRide extends Fragment {

    public static final String TAG = "SampleActivityBase";

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    Calendar now;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    private TextView fromText,toText,selectText;
    private AutoCompleteTextView mAutocompleteViewSource, mAutocompleteViewDest;
    private EditText datetime;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(21.000000, 78.000000), new LatLng(21.000000, 78.000000));

    public OfferRide() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_ride, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        fromText = (TextView) view.findViewById(R.id.from_text);
        toText = (TextView) view.findViewById(R.id.to_text);
        selectText = (TextView) view.findViewById(R.id.selectdatetimetext);
        fromText.setTypeface(MyApplication.getPt_sans());
        toText.setTypeface(MyApplication.getPt_sans());
        selectText.setTypeface(MyApplication.getPt_sans());
        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteViewSource = (AutoCompleteTextView)
                view.findViewById(R.id.autocomplete_places_source);
        mAutocompleteViewDest = (AutoCompleteTextView) view.findViewById(R.id.autocomplete_places_destination);
        datetime = (EditText) view.findViewById(R.id.datetime);
        datetime.setTypeface(MyApplication.getPt_sans());
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteViewSource.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteViewDest.setOnItemClickListener(mAutocompleteClickListener);

        mAutocompleteViewSource.setTypeface(MyApplication.getPt_sans());
        mAutocompleteViewDest.setTypeface(MyApplication.getPt_sans());

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getContext(), android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_INDIA, null);
        mAutocompleteViewSource.setAdapter(mAdapter);
        mAutocompleteViewDest.setAdapter(mAdapter);
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
//        new DatePickerDialog.OnDateChangedListener(){
//            @Override
//            public void onDateChanged() {
//                TimePickerDialog t = TimePickerDialog.newInstance(time, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
//                t.show(getActivity().getFragmentManager(), "TimePicker");
//            }
//        };
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
        return view;
    }
    private String getMonth(int m){
        switch (m){
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
            Log.i(TAG, "Autocomplete item selected: " + item.description);

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


    private void setUpDateTimeDialog(){


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



}
