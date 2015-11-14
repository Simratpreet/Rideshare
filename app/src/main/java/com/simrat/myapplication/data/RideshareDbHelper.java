package com.simrat.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.simrat.myapplication.data.UserContract.*;
import com.simrat.myapplication.model.User;

/**
 * Created by simrat on 13/11/15.
 */
public class RideshareDbHelper extends SQLiteOpenHelper {

    private String DEBUG_TAG = this.getClass().getName().toString();
    private SQLiteDatabase db;
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "rideshare.db";

    public RideshareDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY, " +
                UserEntry.COLUMN_TOKEN + " TEXT UNIQUE NOT NULL, " +
                UserEntry.COLUMN_FIRST_NAME + " TEXT, " +
                UserEntry.COLUMN_LAST_NAME + " TEXT, " +
                UserEntry.COLUMN_EMAIL + " TEXT, " +
                UserEntry.COLUMN_PHONE + " TEXT, " +
                UserEntry.COLUMN_GENDER + " TEXT, " +
                UserEntry.COLUMN_CITY + " TEXT, " +
                UserEntry.COLUMN_PROFILE_PIC + " BLOB) ";
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String add_age = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_AGE + " INTEGER ";
        String add_music_lover = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_MUSIC_LOVER + " TEXT ";
        String add_smoker = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_SMOKER + " TEXT ";
        String add_drinker = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_DRINKER + " TEXT ";

//        if(oldVersion == 1) {
//            onCreate(sqLiteDatabase);
//        }
//        if(oldVersion >= 1 && newVersion == 2){
//            sqLiteDatabase.execSQL(add_age);
//            sqLiteDatabase.execSQL(add_music_lover);
//            sqLiteDatabase.execSQL(add_smoker);
//            sqLiteDatabase.execSQL(add_drinker);
//        }
        Log.d(DEBUG_TAG, Integer.toString(oldVersion));
        Log.d(DEBUG_TAG, Integer.toString(newVersion));

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
    public void addUser(User user){

        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COLUMN_EMAIL, user.getEmail());
        contentValues.put(UserEntry.COLUMN_FIRST_NAME, user.getFirst_name());
        contentValues.put(UserEntry.COLUMN_LAST_NAME, user.getLast_name());
        contentValues.put(UserEntry.COLUMN_PHONE, user.getPhone());
        contentValues.put(UserEntry.COLUMN_TOKEN, user.getToken());
        contentValues.put(UserEntry.COLUMN_GENDER, user.getGender());
        contentValues.put(UserEntry.COLUMN_CITY, user.getCity());

        long rowId;
        rowId = db.insert(UserEntry.TABLE_NAME, null, contentValues);
        Log.d(DEBUG_TAG, Long.toString(rowId));
    }
    public User getUser(String token){

        db = this.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null, " TOKEN = ? ", new String[] { token }, null, null, null);
        cursor.moveToFirst();
        User user = new User(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_LAST_NAME)),
                cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_PHONE)),
                cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_GENDER)),
                cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_CITY)));
        user.setAge(cursor.getInt(cursor.getColumnIndex(UserEntry.COLUMN_AGE)));
        user.setMusic(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_MUSIC_LOVER)));
        user.setSmoke(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_SMOKER)));
        user.setDrink(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_DRINKER)));
        Log.d(DEBUG_TAG, Integer.toString(cursor.getInt(cursor.getColumnIndex(UserEntry.COLUMN_AGE))));
        //Log.d(DEBUG_TAG, cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_DRINKER)));
        Log.d(DEBUG_TAG, user.getDrink());
        cursor.close();
        return user;
    }
    public void updateUser(String token, User user){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserEntry.COLUMN_EMAIL, user.getEmail());
        contentValues.put(UserEntry.COLUMN_FIRST_NAME, user.getFirst_name());
        contentValues.put(UserEntry.COLUMN_LAST_NAME, user.getLast_name());
        contentValues.put(UserEntry.COLUMN_PHONE, user.getPhone());
        contentValues.put(UserEntry.COLUMN_GENDER, user.getGender());
        contentValues.put(UserEntry.COLUMN_CITY, user.getCity());
        contentValues.put(UserEntry.COLUMN_AGE, user.getAge());
        contentValues.put(UserEntry.COLUMN_MUSIC_LOVER, user.getMusic());
        contentValues.put(UserEntry.COLUMN_SMOKER, user.getSmoke());
        contentValues.put(UserEntry.COLUMN_DRINKER, user.getDrink());

        long rowId;
        rowId = db.update(UserEntry.TABLE_NAME, contentValues, " TOKEN = ? ", new String[] { token });
        Log.d(DEBUG_TAG, Long.toString(rowId));
    }
    public void getColumns(){
        db = this.getReadableDatabase();
        Cursor cursor = db.query(UserEntry.TABLE_NAME, null, null, null, null, null, null);
        String[] column_names = cursor.getColumnNames();
        for(int i=0; i<column_names.length; i++){
            Log.d(DEBUG_TAG, column_names[i]);
            Log.d(DEBUG_TAG, Integer.toString(db.getVersion()));
        }
    }

}
