package com.simrat.myapplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simrat.myapplication.data.RideshareDbHelper;
import com.simrat.myapplication.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by simrat on 11/11/15.
 */
public class UploadProfilePic extends AppCompatActivity {
    private static int RESULT_LOAD_IMG = 1;
    private Toolbar mToolbar;
    private TextView title, save;
    private ImageView profilePic;
    private Button choose;
    private String imgDecodableString;
    private String type, filename, bitmapEncode, token;
    private SharedPreferences sharedPreferences;
    private RideshareDbHelper dbHelper;
    private byte[] bitmapBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_pic);
        findViews();
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

    }
    private void findViews(){
        dbHelper = new RideshareDbHelper(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        title.setText("Update Profile Pic");
        title.setTypeface(MyApplication.getPt_sans());
        mToolbar.setTitle("");
        choose = (Button) findViewById(R.id.choose);
        save = (TextView) findViewById(R.id.save);
        choose.setTypeface(MyApplication.getPt_sans());
        save.setTypeface(MyApplication.getPt_sans());
        token = null;
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        String path = getApplicationContext().getFilesDir() + "/" + sharedPreferences.getString("AuthToken", "") + "dp.jpg";
        File file = new File(path);

        if(file.exists()){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                profilePic.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        else{
            path = getApplicationContext().getFilesDir() + "/" + sharedPreferences.getString("AuthToken", "") + "dp.png";
            file = new File(path);
            if(file.exists()){
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    profilePic.setImageBitmap(bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpUpload();
            }
        });
    }

    private void setUpUpload(){
        if(token != null){
            ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Saving ...");
            new ImageTask(pDialog).execute(token, bitmapEncode, type, filename);
        }
        else Toast.makeText(this, "Choose an image first", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Set the Image in ImageView after decoding the String
                profilePic.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

                Log.d("decstring", imgDecodableString);
                Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
                type = null;
                filename = null;
                String extension = MimeTypeMap.getFileExtensionFromUrl(imgDecodableString);
                if (extension != null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                Log.d("type", type);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                if(type == "image/jpeg") {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    filename = "dp.jpg";
                }
                else if(type == "image/png") {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    filename = "dp.png";
                }
                else{
                    Toast.makeText(this, "Please upload JPG/PNG image",Toast.LENGTH_LONG).show();
                    return;
                }
                bitmapBytes = outputStream.toByteArray();
                bitmapEncode = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
                Log.d("Image", bitmapEncode);
                Log.d("Imagelen", Integer.toString(bitmapEncode.length()));
                token = sharedPreferences.getString("AuthToken", "");
                Log.d("TOKEN", token);





            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
    private class ImageTask extends AsyncTask<String, Void, String>{

        private ProgressDialog progressDialog;
        String error;
        private int responseCode;

        public ImageTask(ProgressDialog progressDialog){
            this.progressDialog = progressDialog;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection;
            JSONObject user = new JSONObject();
            String response = "";
            JSONObject json = new JSONObject();


            try {
                json.put("success", false);
                //json.put("info", "Something went wrong.. Retry !!");
                user.put("auth_token", params[0]);
                user.put("pic", params[1]);
                user.put("content_type", params[2]);
                user.put("filename", params[3]);
                StringBuilder sb = new StringBuilder(user.toString());

                url = new URL("https://shielded-earth-6986.herokuapp.com/dp");

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
                bw.write(user.toString());
                bw.flush();
                bw.close();
                os.close();

                responseCode = urlConnection.getResponseCode();
                Log.d("Code", Integer.toString(responseCode));

                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while((line = br.readLine()) != null) {
                        response += line;

                    }
                    File file = new File(getFilesDir(), params[0] + params[3]);
                    FileOutputStream fileOutputStream;
                    if(file.exists()){
                        file.delete();
                    }
                    try{
                        byte[] decodedPic = Base64.decode(params[1], Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedPic, 0, decodedPic.length);
                        fileOutputStream = new FileOutputStream(file);
                        if(params[2] == "image/jpeg")
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        else bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    User u = dbHelper.getUser(params[0]);
                    sharedPreferences.edit().putString("ProfilePic", params[1]).commit();
                    json = new JSONObject(response);

                }
                else {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    while((line = br.readLine()) !=null) {
                        response += line;

                    }
                    error = response.toString();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
                urlConnection.disconnect();

            }catch (MalformedURLException e){

            }catch (IOException e){

            }catch (JSONException e){

            }
            Log.d("Response", response);
            return json.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.hide();
            if(responseCode == HttpURLConnection.HTTP_OK){
                ProfileFragment.setProfilePic();
                FragmentDrawer.setProfilePic();
            }
        }
    }
}