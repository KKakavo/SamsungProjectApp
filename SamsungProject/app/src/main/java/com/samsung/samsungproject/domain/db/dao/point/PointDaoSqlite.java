package com.samsung.samsungproject.domain.db.dao.point;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.samsungproject.domain.db.AppDbOpenHelper;
import com.samsung.samsungproject.domain.db.AppReaderContract;
import com.samsung.samsungproject.domain.model.Point;

import java.util.ArrayList;
import java.util.List;

public class PointDaoSqlite implements PointDao{
    private final AppDbOpenHelper openHelper;

    public PointDaoSqlite(Context context) {
        this.openHelper = new AppDbOpenHelper(context);
    }

    @Override
    public long insert(Point point, long shape_id) {
        SQLiteDatabase database = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppReaderContract.PointEntry.COLUMN_LATITUDE, point.getLatitude());
        contentValues.put(AppReaderContract.PointEntry.COLUMN_LONGITUDE, point.getLongitude());
        contentValues.put(AppReaderContract.PointEntry.COLUMN_SHAPE_ID, shape_id);
        long index = database.insert(AppReaderContract.PointEntry.TABLE_NAME,
                null,
                contentValues);
        database.close();
        return index;
    }

    @Override
    public List<Point> findAllByShapeId(long shape_id) {
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(
                AppReaderContract.PointEntry.TABLE_NAME,
                null,
                AppReaderContract.PointEntry.COLUMN_SHAPE_ID + " = ?",
                new String[]{String.valueOf(shape_id)},
                null,
                null,
                null
        );
        List<Point> pointList = new ArrayList<>();
        if(cursor.moveToFirst()){
            int latitude = cursor.getColumnIndex(AppReaderContract.PointEntry.COLUMN_LATITUDE);
            int longitude = cursor.getColumnIndex(AppReaderContract.PointEntry.COLUMN_LONGITUDE);
            do{
                Point point = new Point(
                        cursor.getDouble(latitude),
                        cursor.getDouble(longitude)
                );
                pointList.add(point);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return pointList;
    }
}
