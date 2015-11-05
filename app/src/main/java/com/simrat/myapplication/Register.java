package com.simrat.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Register extends Activity {

    private TextView registerText, nameText, emailText, passText, phoneText, loginText;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        findViews();
    }
    private void findViews(){
        registerText = (TextView) findViewById(R.id.registerText);
        nameText = (TextView) findViewById(R.id.nameText);
        emailText = (TextView) findViewById(R.id.emailText);
        passText = (TextView) findViewById(R.id.passText);
        phoneText = (TextView) findViewById(R.id.phoneText);
        loginText = (TextView) findViewById(R.id.loginText);
        registerButton = (Button) findViewById(R.id.register_button);

        registerText.setTypeface(MyApplication.getPt_sans());
        nameText.setTypeface(MyApplication.getPt_sans());
        emailText.setTypeface(MyApplication.getPt_sans());
        passText.setTypeface(MyApplication.getPt_sans());
        phoneText.setTypeface(MyApplication.getPt_sans());
        loginText.setTypeface(MyApplication.getPt_sans());
        registerButton.setTypeface(MyApplication.getPt_sans());

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });
    }

}
