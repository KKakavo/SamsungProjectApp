package com.samsung.samsungproject.domain.room.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.samsung.samsungproject.domain.room.dao.PointDao;
import com.samsung.samsungproject.domain.room.dao.ShapeDao;
import com.samsung.samsungproject.domain.room.dao.UserDao;
import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;

@Database(entities = {User.class, Shape.class, Point.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract ShapeDao shapeDao();

    public abstract PointDao pointDao();
}
