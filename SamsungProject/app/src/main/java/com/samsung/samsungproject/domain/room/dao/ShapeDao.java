package com.samsung.samsungproject.domain.room.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.room.relation.ShapeWithPointsAndUser;

import java.util.List;

@Dao
public interface ShapeDao {
    @Transaction
    @Query("SELECT * FROM shapes")
    public List<ShapeWithPointsAndUser> getAllShapes();

    @Transaction
    @Insert
    public void insertShape(Shape shape);
}
