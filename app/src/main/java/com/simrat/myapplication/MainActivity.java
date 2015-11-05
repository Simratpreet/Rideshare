package com.simrat.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.simrat.myapplication.MyApplication;

public class MainActivity extends Activity {

    private TextView shareRide, interact, info, money, logo;
    private TextView loginText, emailText, passText, orText, registerText, title;
    private Button buttonLogin;
    private Toolbar mToolbar;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    AccessToken accessToken;
    Profile profile;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    SharedPreferences sharedPreferences;
    Animation fadeIn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());


        Intent i;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPreferences.getString("AccessToken", "");
        Log.d("Token", token);
        if(token != "") {
            i = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(i);
            finish();
        }


        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        setContentView(R.layout.login_layout);
        findViews();
        setUpDimen();

        context = getApplicationContext();
        callbackManager = CallbackManager.Factory.create();

        float fbIconScale = 1.30F;
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.facebook);
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
                Log.d("MOm", accessToken.toString());
                //profile = Profile.getCurrentProfile();
                //profile.getProfilePictureUri(80, 80);
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
                                    String name, birthday, gender, education;

                                try {
                                    name = object.getString("first_name") + " " + object.getString("last_name");
                                    if(object.getString("birthday") != null)
                                        birthday = object.getString("birthday");
                                    gender = object.getString("gender");
                                    sharedPreferences.edit().putString("Gender", gender).commit();
                                    Log.d("Gender", gender);

                                    if(object.getString("age_range") != null){
                                        JSONObject age = new JSONObject(object.getString("age_range"));
                                        sharedPreferences.edit().putString("Age", age.getString("min")).commit();
                                        Log.d("Age", age.getString("min"));
                                    }
                                    //Details: {"id":"185862431749702","name":"Simratpreet Singh","age_range":{"min":21},"first_name":"Simratpreet","email":"simrat@mhire.in","last_name":"Singh","gender":"male","link":"https:\/\/www.facebook.com\/app_scoped_user_id\/185862431749702\/",
                                      //      "work":[{"description":"Ruby On Rails Developer","employer":{"id":"1417363405233600","name":"mhire.in"},"location":{"id":"130646063637019","name":"Noida, India"},"position":{"id":"174691335892789","name":"Works at"},"start_date":"0000-00"}],
                                      // "location":{"id":"102161913158207","name":"Delhi, India"},
                                      // "education":[{"school":{"id":"143094462462512","name":"Maharaja Agrasen Institute of Technology"},"type":"College"}],"birthday":"12\/11\/1993"}
                                    if(object.getString("education") != null) {
                                        education = object.getString("education");
                                        JSONArray arr = new JSONArray(education);
                                        JSONObject ob = arr.getJSONObject(0);
                                        education = ob.getJSONObject("school").getString("name");
                                        Log.d("Education", education);
                                        sharedPreferences.edit().putString("Education", education).commit();
                                    }
                                    if(object.getString("work") != null){
                                        String work = object.getString("work");
                                        JSONArray arr = new JSONArray(work);
                                        JSONObject ob = arr.getJSONObject(0);
                                        work = ob.getJSONObject("position").getString("name") + " " + ob.getJSONObject("employer").getString("name");
                                        Log.d("Work", work);
                                        sharedPreferences.edit().putString("Work", work).commit();
                                    }
                                    Log.d("Details", object.toString());
                                    sharedPreferences.edit().putString("Name", name).commit();
                                    JSONObject location = new JSONObject(object.getString("location"));
                                    sharedPreferences.edit().putString("Location", location.getString("name")).commit();
                                    Bitmap bitmap;
                                    try {
                                        bitmap = new ImageTask().execute(accessToken.getUserId().toString()).get();
                                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                        byte bitmapBytes[] = outputStream.toByteArray();
                                        String bitmapEncode = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                                        sharedPreferences.edit().putString("ProfilePic", bitmapEncode).commit();
                                        Log.d("s1", bitmapEncode);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.d("Exception in graph", e.getMessage());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("JSONException in graph", e.getMessage());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, age_range, about, bio, first_name, email, last_name, gender, link, work, location, education, birthday");
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
        setupWindowAnimations();

    }
    private void findViews(){

        loginButton = (LoginButton) findViewById(R.id.fblogin_button);
        loginButton.setTypeface(MyApplication.getPt_sans());
        loginText = (TextView) findViewById(R.id.loginText);
        emailText = (TextView) findViewById(R.id.emailText);
        passText = (TextView) findViewById(R.id.passText);
        orText = (TextView) findViewById(R.id.orText);
        buttonLogin = (Button) findViewById(R.id.login_button);
        registerText = (TextView) findViewById(R.id.registerText);
        loginText.setTypeface(MyApplication.getPt_sans());
        emailText.setTypeface(MyApplication.getPt_sans());
        passText.setTypeface(MyApplication.getPt_sans());
        registerText.setTypeface(MyApplication.getPt_sans());
        orText.setTypeface(MyApplication.getPt_sans());
        buttonLogin.setTypeface(MyApplication.getPt_sans());

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Register.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void setUpDimen(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

    }
    @TargetApi(21)
    private void setupWindowAnimations() {
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        //getWindow().setExitTransition(slide);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
