package com.samsung.samsungproject.domain.db.dao.shape;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.samsungproject.domain.db.AppDbOpenHelper;
import com.samsung.samsungproject.domain.db.AppReaderContract;
import com.samsung.samsungproject.domain.db.dao.point.PointDao;
import com.samsung.samsungproject.domain.db.dao.point.PointDaoSqlite;
import com.samsung.samsungproject.domain.db.dao.user.UserDao;
import com.samsung.samsungproject.domain.db.dao.user.UserDaoSqlite;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class ShapeDaoSqlite implements ShapeDao{
    private final AppDbOpenHelper openHelper;
    private final PointDao pointDao;

    public ShapeDaoSqlite(Context context) {
        this.openHelper = new AppDbOpenHelper(context);
        pointDao = new PointDaoSqlite(context);
    }

    @Override
    public long insert(Shape shape) {
        SQLiteDatabase database = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AppReaderContract.ShapeEntry.COLUMN_COLOR, shape.getColor());
        contentValues.put(AppReaderContract.ShapeEntry.COLUMN_USER_ID, shape.getUser().getId());
        long index = database.insert(AppReaderContract.ShapeEntry.TABLE_NAME,
                null,
                contentValues);
        database.close();
        shape.getPointList().forEach(point -> pointDao.insert(point, index));
        return index;
    }

    @Override
    public List<Shape> findAll() {
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(
                AppReaderContract.ShapeEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        List<Shape> shapeList = new ArrayList<>();
        if(cursor.moveToFirst()){
            int id = cursor.getColumnIndex(AppReaderContract.ShapeEntry.COLUMN_ID);
            int color = cursor.getColumnIndex(AppReaderContract.ShapeEntry.COLUMN_COLOR);
            int user_id = cursor.getColumnIndex(AppReaderContract.ShapeEntry.COLUMN_USER_ID);
            do{
                Shape shape = new Shape(
                        null,
                        pointDao.findAllByShapeId(cursor.getLong(id)),
                        cursor.getInt(color));
                shapeList.add(shape);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shapeList;
    }

    @Override
    public long getSize() {
        SQLiteDatabase database = openHelper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(database, AppReaderContract.ShapeEntry.TABLE_NAME);
    }

}
