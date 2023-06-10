package com.samsung.samsungproject.data.db.dao.point;

import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;

import java.util.List;

public interface PointDao {
    long insert(Point point, long shape_id);
    List<Point> findAllByShapeId(long shape_id);
}
