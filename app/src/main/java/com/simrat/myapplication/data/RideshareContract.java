package com.simrat.myapplication.data;

import android.provider.BaseColumns;

/**
 * Created by simrat on 13/11/15.
 */
public final class RideshareContract {

    public RideshareContract(){}

    public static abstract class UserEntry implements BaseColumns{

        public static final String TABLE_NAME = "user";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_PROFILE_PIC = "profile_pic";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_MUSIC_LOVER = "music_lover";
        public static final String COLUMN_SMOKER = "smoker";
        public static final String COLUMN_DRINKER = "drinker";
    }
    public static abstract class CarEntry implements BaseColumns{
        public static final String TABLE_NAME = "car";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SEATS = "seats";
        public static final String COLUMN_REG_NO = "registration_no";
    }
    public static abstract class RideEntry implements BaseColumns{
        public static final String TABLE_NAME = "ride";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_DESTINATION = "destination";
        public static final String COLUMN_PRICE_PER_SEAT = "price_per_seat";
        public static final String COLUMN_CAR_ID = "car_id";
        public static final String COLUMN_JOURNEY_DATETIME = "journey_datetime";
        public static final String COLUMN_ALLOWED_PASS = "allowed_passengers";
    }
}
