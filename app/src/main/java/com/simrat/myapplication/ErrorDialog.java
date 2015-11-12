package com.simrat.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by simrat on 12/11/15.
 */
public class ErrorDialog extends DialogFragment {
    private String err;

    public ErrorDialog(){

    }
    public void setError(){
        Bundle args = getArguments();
        this.err = args.getString("error", "");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.error_dialog, null);
        builder.setView(view);
        TextView error = (TextView) view.findViewById(R.id.errorText);
        error.setText(err);
        return builder.create();

    }
}
