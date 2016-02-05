package com.simrat.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.simrat.myapplication.data.RideshareContract.*;
import com.simrat.myapplication.model.Car;
import com.simrat.myapplication.model.Ride;
import com.simrat.myapplication.model.User;

import java.util.ArrayList;

/**
 * Created by simrat on 13/11/15.
 */
public class RideshareDbHelper extends SQLiteOpenHelper {

    private String DEBUG_TAG = this.getClass().getName().toString();
    private SQLiteDatabase db;
    public static final int DATABASE_VERSION = 6;
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
        String add_age = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_AGE + " INTEGER ";
        String add_music_lover = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_MUSIC_LOVER + " TEXT ";
        String add_smoker = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_SMOKER + " TEXT ";
        String add_drinker = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_DRINKER + " TEXT ";
        sqLiteDatabase.execSQL(add_age);
        sqLiteDatabase.execSQL(add_music_lover);
        sqLiteDatabase.execSQL((add_smoker));
        sqLiteDatabase.execSQL(add_drinker);
        String SQL_CREATE_CAR_TABLE = "CREATE TABLE " + CarEntry.TABLE_NAME + " (" +
                CarEntry.COLUMN_NAME + " TEXT, " +
                CarEntry.COLUMN_REG_NO + " TEXT, " +
                CarEntry.COLUMN_SEATS + " INTEGER, " +
                CarEntry.COLUMN_USER_ID + " INTEGER)";
        sqLiteDatabase.execSQL(SQL_CREATE_CAR_TABLE);

        String SQL_CREATE_RIDE_TABLE = "CREATE TABLE " + RideEntry.TABLE_NAME + " (" +
                RideEntry.COLUMN_USER_ID + " INTEGER, " +
                RideEntry.COLUMN_CAR_ID + " INTEGER, " +
                RideEntry.COLUMN_SOURCE + " TEXT, " +
                RideEntry.COLUMN_DESTINATION + " TEXT, " +
                RideEntry.COLUMN_JOURNEY_DATETIME + " TEXT, " +
                RideEntry.COLUMN_PRICE_PER_SEAT + " TEXT, " +
                RideEntry.COLUMN_ALLOWED_PASS + " TEXT) ";
        sqLiteDatabase.execSQL(SQL_CREATE_RIDE_TABLE);

//        String add_id_to_car = "ALTER TABLE " + CarEntry.TABLE_NAME + " ADD COLUMN " + CarEntry._ID + " INTEGER PRIMARY KEY ";
//        String add_id_to_ride = "ALTER TABLE " + RideEntry.TABLE_NAME + " ADD COLUMN " + RideEntry._ID + " INTEGER PRIMARY KEY ";
//
//        sqLiteDatabase.execSQL(add_id_to_car);
//        sqLiteDatabase.execSQL(add_id_to_ride);

        String drop_cars = "DROP TABLE " + CarEntry.TABLE_NAME;
        String drop_rides = "DROP TABLE " + RideEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(drop_cars);
        sqLiteDatabase.execSQL(drop_rides);
        String SQL_CREATE_CAR_TABLE_PRIMARY = "CREATE TABLE " + CarEntry.TABLE_NAME + " (" +
                CarEntry._ID + " INTEGER PRIMARY KEY, " +
                CarEntry.COLUMN_NAME + " TEXT, " +
                CarEntry.COLUMN_REG_NO + " TEXT, " +
                CarEntry.COLUMN_SEATS + " INTEGER, " +
                CarEntry.COLUMN_USER_ID + " INTEGER)";

        String SQL_CREATE_RIDE_TABLE_PRIMARY = "CREATE TABLE " + RideEntry.TABLE_NAME + " (" +
                RideEntry._ID + " INTEGER PRIMARY KEY, " +
                RideEntry.COLUMN_USER_ID + " INTEGER, " +
                RideEntry.COLUMN_CAR_ID + " INTEGER, " +
                RideEntry.COLUMN_SOURCE + " TEXT, " +
                RideEntry.COLUMN_DESTINATION + " TEXT, " +
                RideEntry.COLUMN_JOURNEY_DATETIME + " TEXT, " +
                RideEntry.COLUMN_PRICE_PER_SEAT + " TEXT, " +
                RideEntry.COLUMN_ALLOWED_PASS + " TEXT) ";
        sqLiteDatabase.execSQL(SQL_CREATE_CAR_TABLE_PRIMARY);
        sqLiteDatabase.execSQL(SQL_CREATE_RIDE_TABLE_PRIMARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String add_age = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_AGE + " INTEGER ";
        String add_music_lover = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_MUSIC_LOVER + " TEXT ";
        String add_smoker = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_SMOKER + " TEXT ";
        String add_drinker = "ALTER TABLE " + UserEntry.TABLE_NAME + " ADD COLUMN " + UserEntry.COLUMN_DRINKER + " TEXT ";
        String SQL_CREATE_CAR_TABLE = "CREATE TABLE " + CarEntry.TABLE_NAME + " (" +
                CarEntry.COLUMN_NAME + " TEXT, " +
                CarEntry.COLUMN_REG_NO + " TEXT, " +
                CarEntry.COLUMN_SEATS + " INTEGER, " +
                CarEntry.COLUMN_USER_ID + " INTEGER)";

        String SQL_CREATE_RIDE_TABLE = "CREATE TABLE " + RideEntry.TABLE_NAME + " (" +
                RideEntry.COLUMN_USER_ID + " INTEGER, " +
                RideEntry.COLUMN_CAR_ID + " INTEGER, " +
                RideEntry.COLUMN_SOURCE + " TEXT, " +
                RideEntry.COLUMN_DESTINATION + " TEXT, " +
                RideEntry.COLUMN_JOURNEY_DATETIME + " TEXT, " +
                RideEntry.COLUMN_PRICE_PER_SEAT + " TEXT, " +
                RideEntry.COLUMN_ALLOWED_PASS + " TEXT) ";

        String drop_cars = "DROP TABLE " + CarEntry.TABLE_NAME;
        String drop_rides = "DROP TABLE " + RideEntry.TABLE_NAME;

        String SQL_CREATE_CAR_TABLE_PRIMARY = "CREATE TABLE " + CarEntry.TABLE_NAME + " (" +
                CarEntry._ID + " INTEGER PRIMARY KEY, " +
                CarEntry.COLUMN_NAME + " TEXT, " +
                CarEntry.COLUMN_REG_NO + " TEXT, " +
                CarEntry.COLUMN_SEATS + " INTEGER, " +
                CarEntry.COLUMN_USER_ID + " INTEGER)";

        String SQL_CREATE_RIDE_TABLE_PRIMARY = "CREATE TABLE " + RideEntry.TABLE_NAME + " (" +
                RideEntry._ID + " INTEGER PRIMARY KEY, " +
                RideEntry.COLUMN_USER_ID + " INTEGER, " +
                RideEntry.COLUMN_CAR_ID + " INTEGER, " +
                RideEntry.COLUMN_SOURCE + " TEXT, " +
                RideEntry.COLUMN_DESTINATION + " TEXT, " +
                RideEntry.COLUMN_JOURNEY_DATETIME + " TEXT, " +
                RideEntry.COLUMN_PRICE_PER_SEAT + " TEXT, " +
                RideEntry.COLUMN_ALLOWED_PASS + " TEXT) ";

//        String add_id_to_car = "ALTER TABLE " + CarEntry.TABLE_NAME + " ADD COLUMN " + CarEntry._ID + " INTEGER PRIMARY KEY ";
//        String add_id_to_ride = "ALTER TABLE " + RideEntry.TABLE_NAME + " ADD COLUMN " + RideEntry._ID + " INTEGER PRIMARY KEY ";

        if(oldVersion < 2){
            sqLiteDatabase.execSQL(add_age);
            sqLiteDatabase.execSQL(add_music_lover);
            sqLiteDatabase.execSQL((add_smoker));
            sqLiteDatabase.execSQL(add_drinker);
        }
        if(oldVersion < 3){
            sqLiteDatabase.execSQL(SQL_CREATE_CAR_TABLE);
        }
        if(oldVersion < 4){
            sqLiteDatabase.execSQL(SQL_CREATE_RIDE_TABLE);
        }
        if(oldVersion < 5){
//            sqLiteDatabase.execSQL(add_id_to_car);
//            sqLiteDatabase.execSQL(add_id_to_ride);
        }
        if(oldVersion < 6){
            sqLiteDatabase.execSQL(drop_cars);
            sqLiteDatabase.execSQL(drop_rides);
            sqLiteDatabase.execSQL(SQL_CREATE_CAR_TABLE_PRIMARY);
            sqLiteDatabase.execSQL(SQL_CREATE_RIDE_TABLE_PRIMARY);
        }
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
        user.setId(cursor.getInt(cursor.getColumnIndex(UserEntry._ID)));
        user.setAge(cursor.getInt(cursor.getColumnIndex(UserEntry.COLUMN_AGE)));
        user.setMusic(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_MUSIC_LOVER)));
        user.setSmoke(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_SMOKER)));
        user.setDrink(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_DRINKER)));
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
        Cursor cursor = db.query(RideEntry.TABLE_NAME, null, null, null, null, null, null);
        String[] column_names = cursor.getColumnNames();
//        for(int i=0; i<column_names.length; i++){
//            Log.d(DEBUG_TAG, column_names[i]);
//            Log.d(DEBUG_TAG, Integer.toString(db.getVersion()));
//        }
    }
    public void addCar(Car car){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CarEntry.COLUMN_USER_ID, car.getUser_id());
        contentValues.put(CarEntry.COLUMN_NAME, car.getName());
        contentValues.put(CarEntry.COLUMN_SEATS, car.getSeats());
        contentValues.put(CarEntry.COLUMN_REG_NO, car.getReg_no());
        long rowId;
        rowId = db.insert(CarEntry.TABLE_NAME, null, contentValues);
        Log.d(DEBUG_TAG, Long.toString(rowId));
    }
    public Car getCar(int user_id){
        db = this.getReadableDatabase();
        Cursor cursor = db.query(CarEntry.TABLE_NAME, null, " USER_ID = ? ", new String[] { Integer.toString(user_id) },
                null, null, null);
        Car car = new Car(cursor.getInt(cursor.getColumnIndex(CarEntry.COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndex(CarEntry.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(CarEntry.COLUMN_SEATS)),
                cursor.getString(cursor.getColumnIndex(CarEntry.COLUMN_REG_NO)));
        car.setId(cursor.getInt(cursor.getColumnIndex(CarEntry._ID)));
        cursor.close();
        return car;
    }
    public int getRideCar(String user_id, String name, String reg_no){
        db = this.getReadableDatabase();
        Log.d(DEBUG_TAG, user_id + " " + name);
        Cursor cursor = db.rawQuery("Select * from car where user_id = ? and name = ? and registration_no like ?", new String[] { user_id, name, "%" + reg_no });
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(CarEntry._ID));
    }
    public ArrayList<String> myCars(int user_id){
        ArrayList<String> cars = new ArrayList<String>();
        db = this.getReadableDatabase();
        Cursor cursor = db.query(CarEntry.TABLE_NAME, new String[] { CarEntry.COLUMN_NAME, CarEntry.COLUMN_REG_NO }, " USER_ID = ? ", new String[] { Integer.toString(user_id) },
                null, null, null);

        if(cursor != null){
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                String temp = cursor.getString(cursor.getColumnIndex(CarEntry.COLUMN_REG_NO));
                cars.add(cursor.getString(cursor.getColumnIndex(CarEntry.COLUMN_NAME)) + " - " +
                        temp.substring(temp.length() - 4));;
            }
            return cars;
        }
        else{
            return null;
        }
    }
    public void addRide(Ride ride){
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RideEntry.COLUMN_USER_ID, ride.getUser_id());
        contentValues.put(RideEntry.COLUMN_CAR_ID, ride.getCar_id());
        contentValues.put(RideEntry.COLUMN_SOURCE, ride.getSource());
        contentValues.put(RideEntry.COLUMN_DESTINATION, ride.getDestination());
        contentValues.put(RideEntry.COLUMN_JOURNEY_DATETIME, ride.getDatetime());
        contentValues.put(RideEntry.COLUMN_PRICE_PER_SEAT, ride.getPrice());
        contentValues.put(RideEntry.COLUMN_ALLOWED_PASS, ride.getAllowed_pass());
        long rowId;
        rowId = db.insert(RideEntry.TABLE_NAME, null, contentValues);
        Log.d(DEBUG_TAG, Long.toString(rowId));
    }

}
