package com.simrat.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.support.v4.app.FragmentActivity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.simrat.myapplication.MyApplication;

public class MainActivity extends FragmentActivity {

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
    private String error;

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
                Log.d("Login Token", accessToken.toString());
                //profile = Profile.getCurrentProfile();
                //profile.getProfilePictureUri(80, 80);
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPreferences.edit().putString("AccessToken", AccessToken.getCurrentAccessToken().toString()).commit();
                accessToken = AccessToken.getCurrentAccessToken();
                Log.d("Current Token", accessToken.toString());
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
//                                    if(object.getString("birthday") != null)
//                                        birthday = object.getString("birthday");
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
//                                    if(object.getString("education") != null) {
//                                        education = object.getString("education");
//                                        JSONArray arr = new JSONArray(education);
//                                        JSONObject ob = arr.getJSONObject(0);
//                                        education = ob.getJSONObject("school").getString("name");
//                                        Log.d("Education", education);
//                                        sharedPreferences.edit().putString("Education", education).commit();
//                                    }
//                                    if(object.getString("work") != null){
//                                        String work = object.getString("work");
//                                        JSONArray arr = new JSONArray(work);
//                                        JSONObject ob = arr.getJSONObject(0);
//                                        work = ob.getJSONObject("position").getString("name") + " " + ob.getJSONObject("employer").getString("name");
//                                        Log.d("Work", work);
//                                        sharedPreferences.edit().putString("Work", work).commit();
//                                    }
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
                                        Log.d("Encoding", bitmapEncode);
//                                        Log.d("s1", bitmapEncode);

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


                //Bundle extras = new Bundle();
                //extras.putString("fb_dp", sharedPreferences.getString("ProfilePic", ""));
                Intent i = new Intent(MainActivity.this, SecondActivity.class);
                //i.putExtras(extras);
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
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpLogin();
            }
        });
    }
    private void setUpLogin(){
        String email = emailText.getText().toString();
        String password = passText.getText().toString();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(!isValid(email, password))
            return;
        else {
            ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading Please Wait ...");
            LoginTask loginTask = new LoginTask(pDialog);
            loginTask.execute(email, password);
        }
    }
    private boolean isValid(String email, String password){

        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            error = "Email must be like abc@example.com";
            emailText.setError(error);
            return false;
        }
        if(password.length() < 8 ){
            error = "Password must be at least 8 characters long";
            passText.setError(error);
            return false;
        }
        return true;
    }
    private class LoginTask extends AsyncTask<String,Void,JSONObject>{
        ProgressDialog progressDialog;
        private String login_error;
        private int responseCode;
        public LoginTask(ProgressDialog progressDialog){
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection;
            JSONObject holder = new JSONObject();
            JSONObject user = new JSONObject();
            String response = "";
            JSONObject json = new JSONObject();


            try {
                json.put("success", false);
                //json.put("info", "Something went wrong.. Retry !!");
                user.put("email", params[0]);
                user.put("password", params[1]);
                holder.put("user",user);
                StringBuilder sb = new StringBuilder(holder.toString());

                url = new URL("https://shielded-earth-6986.herokuapp.com/users/sign_in");

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
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                bw.write(holder.toString());
                bw.flush();
                bw.close();
                os.close();

                responseCode = urlConnection.getResponseCode();
                Log.d("Code", Integer.toString(responseCode));

                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while((line = br.readLine()) !=null) {
                        response += line;

                    }


                    json = new JSONObject(response);
                    sharedPreferences.edit().putString("Name", json.getJSONObject("data").getString("first_name") + " " +
                            json.getJSONObject("data").getString("last_name")).commit();
                    sharedPreferences.edit().putString("AuthToken", json.getJSONObject("data").getString("auth_token")).commit();
                    Log.d("LoginAuthToken", json.getJSONObject("data").getString("auth_token"));
                    Log.d("LoginAuth", sharedPreferences.getString("AuthToken", ""));
                }
                else {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while((line = br.readLine()) !=null) {
                        response += line;

                    }
                    login_error = response.toString();
                    Toast.makeText(getApplicationContext(), login_error, Toast.LENGTH_SHORT).show();
                }
                urlConnection.disconnect();
            }catch (MalformedURLException e){

            }catch (IOException e){

            }catch (JSONException e){

            }
            Log.d("Response", response);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.hide();
            if(responseCode == HttpURLConnection.HTTP_OK){
                Intent i = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(i);
                finish();
            }
            else {
                ErrorDialog dialog = new ErrorDialog();
                Bundle args = new Bundle();
                args.putString("error", "Invalid Email or Password.");
                dialog.setArguments(args);
                Log.d("ARgs", args.getString("error", ""));
                dialog.setError();
                dialog.show(getSupportFragmentManager(), "ErrorDialog");
            }

        }
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