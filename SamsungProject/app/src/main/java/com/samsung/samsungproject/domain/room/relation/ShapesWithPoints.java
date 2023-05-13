package com.samsung.samsungproject.domain.room.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;

import java.util.List;

public class ShapesWithPoints {
    @Embedded
    public Shape shape;
    @Relation(
            parentColumn = "id",
            entityColumn = "shape_id"
    )
    public List<Point> pointList;
}
