package com.samsung.samsungproject.domain.db.dao.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.samsung.samsungproject.domain.db.AppDbOpenHelper;
import com.samsung.samsungproject.domain.db.AppReaderContract;
import com.samsung.samsungproject.domain.db.dao.user.UserDao;
import com.samsung.samsungproject.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDaoSqlite implements UserDao {
    private final AppDbOpenHelper openHelper;

    public UserDaoSqlite(Context context) {
        this.openHelper = new AppDbOpenHelper(context);
    }

    @Override
    public long insert(User user) {
        SQLiteDatabase database = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(
                AppReaderContract.UserEntry.COLUMN_EMAIL, user.getEmail());
        contentValues.put(
                AppReaderContract.UserEntry.COLUMN_NICKNAME, user.getNickname());
        contentValues.put(
                AppReaderContract.UserEntry.COLUMN_PASSWORD, user.getPassword());
        contentValues.put(
                AppReaderContract.UserEntry.COLUMN_ROLE, user.getRole());
        contentValues.put(
                AppReaderContract.UserEntry.COLUMN_SCORE, user.getScore());
        long index = database.insert(
                AppReaderContract.UserEntry.TABLE_NAME,
                null,
                contentValues
        );
        database.close();
        return index;
    }

    @Override
    public List<User> findAll() {
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(
                AppReaderContract.UserEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        List<User> userList = new ArrayList<>();
        if(cursor.moveToFirst()){
            int id = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_ID);
            int email = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_EMAIL);
            int nickname = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_NICKNAME);
            int password = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_PASSWORD);
            int role = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_ROLE);
            int score = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_SCORE);

            do {
                User user = new User(
                        cursor.getLong(id),
                        cursor.getString(email),
                        cursor.getString(nickname),
                        cursor.getString(password),
                        cursor.getString(role),
                        cursor.getInt(score)
                );
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return userList;
    }

    @Override
    public User findById(long id) {
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(
                AppReaderContract.UserEntry.TABLE_NAME,
                null,
                AppReaderContract.UserEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
        User user = null;
        if(cursor.moveToFirst()){
            int ID = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_ID);
            int email = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_EMAIL);
            int nickname = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_NICKNAME);
            int password = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_PASSWORD);
            int role = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_ROLE);
            int score = cursor.getColumnIndex(AppReaderContract.UserEntry.COLUMN_SCORE);
            user = new User(
                    cursor.getLong(ID),
                    cursor.getString(email),
                    cursor.getString(nickname),
                    cursor.getString(password),
                    cursor.getString(role),
                    cursor.getInt(score)
            );
        }
        cursor.close();
        database.close();
        return user;
    }

    @Override
    public long update(long id, User user) {
        return 0;
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase database = openHelper.getWritableDatabase();
        database.delete(AppReaderContract.UserEntry.TABLE_NAME,
                null,
                null);
    }


}
