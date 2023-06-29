package com.samsung.samsungproject.data.db.dao.shape;

import com.samsung.samsungproject.domain.model.Shape;

import java.util.List;

public interface ShapeDao {
    long insert(Shape shape);
    void insertAll(List<Shape> shapeList);
    List<Shape> findAll();
    long getSize();
}
