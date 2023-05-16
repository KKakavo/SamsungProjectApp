package com.samsung.samsungproject.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;


@Entity(tableName = "shapes")
public class Shape {
    @PrimaryKey
    private long id;
    @ColumnInfo(name = "user_id")
    private long user_id;

    public void setId(long id) {
        this.id = id;
    }

    public Shape(long id, long user_id) {
        this.id = id;
        this.user_id = user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getId() {
        return id;
    }

    public long getUser_id() {
        return user_id;
    }
}