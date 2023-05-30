package com.samsung.samsungproject.data.repository;

import com.samsung.samsungproject.data.api.shape.ShapeApiService;
import com.samsung.samsungproject.domain.model.Shape;

import java.util.List;

import retrofit2.Call;

public class ShapeRepository {
    public static long controlSum;
    public static Call<Shape> saveShape(Shape shape, long score){
        return ShapeApiService.getInstance().saveShape(shape, score);
    }
    public static Call<List<Shape>> saveAllShapes(List<Shape> shapeList, long score){
        return ShapeApiService.getInstance().saveAllShapes(shapeList, score);
    }
    public static Call<List<Shape>> getAllShapes(){
        return ShapeApiService.getInstance().getAllShapes();
    }
    public static Call<List<Shape>> getRecentShapes(long id){
        return ShapeApiService.getInstance().getRecentShapes(id, controlSum);
    }
}
