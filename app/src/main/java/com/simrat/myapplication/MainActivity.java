package com.simrat.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.simrat.myapplication.MyApplication;

public class MainActivity extends Activity {

    private TextView shareRide, interact, info, money;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    AccessToken accessToken;
    Profile profile;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    SharedPreferences sharedPreferences;
    ImageView profilePic;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        context = getApplicationContext();


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        loginButton.setTypeface(MyApplication.getPt_sans());

        //loginButton.setReadPermissions("user_profile");
        float fbIconScale = 1.30F;
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_fb_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        loginButton.setCompoundDrawables(drawable, null, null, null);

        loginButton.setCompoundDrawablePadding(getApplication().getResources().
                getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        loginButton.setPadding(
                getApplication().getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_lr),
                getApplication().getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_top),
                getApplication().getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_lr),
                getApplication().getResources().getDimensionPixelSize(
                        R.dimen.fb_margin_override_bottom));



        final List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_about_me");
        permissions.add("user_birthday");
        permissions.add("user_education_history");
        permissions.add("user_location");
        permissions.add("user_work_history");
        loginButton.setReadPermissions(permissions);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };
        accessToken = AccessToken.getCurrentAccessToken();
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                accessToken = loginResult.getAccessToken();
                profile = Profile.getCurrentProfile();
                profile.getProfilePictureUri(80,80);
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPreferences.edit().putString("AccessToken", AccessToken.getCurrentAccessToken().toString()).commit();
                accessToken = AccessToken.getCurrentAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                try{
                                    String name = object.getString("first_name") + " " + object.getString("last_name");
                                    String birthday = object.getString("birthday");
                                    String gender = object.getString("gender");
                                    String education = object.getString("education");
                                    Log.d("Details", object.toString());
                                    sharedPreferences.edit().putString("Name", name).commit();
                                    Bitmap bitmap;
                                    try {
                                        bitmap = new ImageTask().execute(accessToken.getUserId().toString()).get();
                                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                        byte bitmapBytes[] = outputStream.toByteArray();
                                        String bitmapEncode = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                                        sharedPreferences.edit().putString("ProfilePic", bitmapEncode).commit();
                                        Log.d("s1", bitmapEncode);

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, about, bio, first_name, email, last_name, gender, link, work, location, education, birthday");
                request.setParameters(parameters);
                request.executeAsync();


                Intent i = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });


        setUpText();



    }
    private class ImageTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL imageUrl;
            Bitmap bitmap;

            try{
                imageUrl = new URL("https://graph.facebook.com/" + strings[0] + "/picture?type=large");
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

                return bitmap;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpText(){

        shareRide = (TextView) findViewById(R.id.share_your_ride_textview);
        interact = (TextView) findViewById(R.id.interact_textview);

        shareRide.setTypeface(MyApplication.getPt_sans());
        interact.setTypeface(MyApplication.getPt_sans());
        interact = (TextView) findViewById(R.id.text_interact);
        money = (TextView) findViewById(R.id.text_money);
        money.setTypeface(MyApplication.getPt_sans());
        loginButton.setText("Sign In With Facebook");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
