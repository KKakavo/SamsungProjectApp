package com.samsung.samsungproject.domain.db.dao.shape;

import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;

import java.util.List;

public interface ShapeDao {
    long insert(Shape shape);
    List<Shape> findAll();
    long getSize();
}
