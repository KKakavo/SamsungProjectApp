package com.samsung.samsungproject.domain.model;


import java.util.List;


public class Shape {
    private final User user;
    private final int color;
    private final List<Point> pointList;

    public Shape(User user, List<Point> pointList, int color) {
        this.user = user;
        this.pointList = pointList;
        this.color = color;
    }

    public User getUser() {
        return user;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public int getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Shape{" +
                "user=" + user +
                ", pointList=" + pointList +
                ", color=" + color +
                '}';
    }
}