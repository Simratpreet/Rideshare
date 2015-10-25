package com.simrat.myapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by simrat on 22/10/15.
 */
public class ProfileDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "profile.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PROFILE = "profile";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_PROFILE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_FIRST_NAME
            + " text not null," + COLUMN_LAST_NAME + "text not null);";

    public ProfileDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ProfileDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }
}
