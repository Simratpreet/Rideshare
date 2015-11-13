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
    public static final int DATABASE_VERSION = 1;
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
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
//    private void deleteProfilePic(SQLiteDatabase sqLiteDatabase){
//        sqLiteDatabase.execSQL();
//    }

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
        cursor.close();
        return user;
    }

}
