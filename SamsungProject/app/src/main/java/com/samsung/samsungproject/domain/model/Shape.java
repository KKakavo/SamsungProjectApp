package com.samsung.samsungproject.domain.model;

import java.util.List;

public class Shape {
    private User user;
    private List<Point> pointList;

    public Shape(User user, List<Point> pointList) {
        this.user = user;
        this.pointList = pointList;
    }
}
