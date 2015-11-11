package com.simrat.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simrat on 11/11/15.
 */
public class GenderFragment extends DialogFragment {

    String gender;
    GenderDialogListerner listener;
    public GenderFragment(){

    }
    public interface GenderDialogListerner{
        public void onItemClick(String gender);
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (GenderDialogListerner) activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select your Gender");
        final String[] genderList = getResources().getStringArray(R.array.gender_values);
        builder.setItems(genderList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                gender = genderList[i];
                listener.onItemClick(gender);
            }
        });
        return builder.create();
    }
}
