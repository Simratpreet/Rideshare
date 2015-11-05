package com.simrat.myapplication;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MyApplication extends Application {

    private static Typeface pt_sans, squada_one;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        pt_sans = Typeface.createFromAsset(getApplicationContext().getAssets(), "PT_Sans-Regular.ttf");
        squada_one = Typeface.createFromAsset(getApplicationContext().getAssets(), "SquadaOne-Regular.ttf");
        printKeyHash();
    }

    public static Typeface getPt_sans(){
        return pt_sans;
    }
    public static Typeface getSquada_one() { return squada_one; }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.simrat.myapplication", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("Hash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
