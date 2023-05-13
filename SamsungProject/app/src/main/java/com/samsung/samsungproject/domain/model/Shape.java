package com.samsung.samsungproject.domain.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;


@Entity
public class Shape {
    @PrimaryKey
    private long id;
    //private User user;
    private long user_id;
    //private List<Point> pointList;

    public Shape(User user, List<Point> pointList) {
        this.user = user;
        this.pointList = pointList;
    }

    public User getUser() {
        return user;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }
}