package com.samsung.samsungproject.data.repository;

import com.samsung.samsungproject.data.api.shape.ShapeApiService;
import com.samsung.samsungproject.domain.model.Shape;

import java.util.List;

import retrofit2.Call;

public class ShapeRepository {
    public static Call<Shape> saveShape(Shape shape){
        return ShapeApiService.getInstance().saveShape(shape);
    }
    public static Call<List<Shape>> saveAllShapes(List<Shape> shapeList){
        return ShapeApiService.getInstance().saveAllShapes(shapeList);
    }
    public static Call<List<Shape>> getAllShapes(){
        return ShapeApiService.getInstance().getAllShapes();
    }
}
