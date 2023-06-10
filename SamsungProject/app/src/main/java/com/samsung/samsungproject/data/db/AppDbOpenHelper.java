package com.samsung.samsungproject.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AppDbOpenHelper extends SQLiteOpenHelper {
    public AppDbOpenHelper(@Nullable Context context) {
        super(context,
                AppReaderContract.DATABASE_NAME,
                null,
                AppReaderContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + AppReaderContract.UserEntry.TABLE_NAME + " (" +
                        AppReaderContract.UserEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AppReaderContract.UserEntry.COLUMN_NICKNAME + " TEXT NOT NULL, " +
                        AppReaderContract.UserEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                        AppReaderContract.UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, " +
                        AppReaderContract.UserEntry.COLUMN_ROLE + " TEXT NOT NULL, " +
                        AppReaderContract.UserEntry.COLUMN_SCORE + " INTEGER NOT NULL );"
        );
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + AppReaderContract.ShapeEntry.TABLE_NAME + " (" +
                        AppReaderContract.ShapeEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AppReaderContract.ShapeEntry.COLUMN_COLOR + " INTEGER NOT NULL, " +
                        AppReaderContract.ShapeEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + AppReaderContract.ShapeEntry.COLUMN_USER_ID + ")"
                        + " REFERENCES " + AppReaderContract.UserEntry.TABLE_NAME +
                        " (" + AppReaderContract.UserEntry.COLUMN_ID + ") );"
        );
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + AppReaderContract.PointEntry.TABLE_NAME + " (" +
                        AppReaderContract.PointEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        AppReaderContract.PointEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                        AppReaderContract.PointEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                        AppReaderContract.PointEntry.COLUMN_SHAPE_ID + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + AppReaderContract.PointEntry.COLUMN_SHAPE_ID + ")"
                        + " REFERENCES " + AppReaderContract.ShapeEntry.TABLE_NAME +
                        " (" + AppReaderContract.ShapeEntry.COLUMN_ID + " ));"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                "DROP TABLE IF EXISTS " + AppReaderContract.PointEntry.TABLE_NAME + ";"
        );
        db.execSQL(
                "DROP TABLE IF EXISTS " + AppReaderContract.ShapeEntry.TABLE_NAME + ";"
        );
        db.execSQL(
                "DROP TABLE IF EXISTS " + AppReaderContract.UserEntry.TABLE_NAME + ";"
        );
        onCreate(db);
    }
}
