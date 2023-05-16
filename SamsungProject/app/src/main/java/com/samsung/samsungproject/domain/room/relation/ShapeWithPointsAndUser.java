package com.samsung.samsungproject.domain.room.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;

import java.util.List;


public class ShapeWithPointsAndUser {
    @Embedded
    private Shape shape;
    @Relation(parentColumn = "id",
            entityColumn = "shape_id",
            entity = Point.class)
    private List<Point> pointList;

    @Embedded(prefix = "us_")
    private User user;
    public ShapeWithPointsAndUser(Shape shape, List<Point> pointList, User user) {
        this.shape = shape;
        this.pointList = pointList;
        this.user = user;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shape getShape() {
        return shape;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public User getUser() {
        return user;
    }
}
