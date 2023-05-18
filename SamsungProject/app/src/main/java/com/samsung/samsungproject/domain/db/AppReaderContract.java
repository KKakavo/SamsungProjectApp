package com.samsung.samsungproject.domain.db;

public class AppReaderContract {
    public AppReaderContract() {
    }

    public static final String DATABASE_NAME = "AppDatabase.db";

    public static final int DATABASE_VERSION = 1;
    public static final String LOG_TAG = "AppDatabase";

    public static class ShapeEntry {
        public static final String TABLE_NAME = "shapes";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_COLOR = "color";
    }
    public static class UserEntry{
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NICKNAME = "nickname";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_ROLE = "role";


    }
    public static class PointEntry{
        public static final String TABLE_NAME = "points";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_SHAPE_ID = "shape_id";
    }
}
