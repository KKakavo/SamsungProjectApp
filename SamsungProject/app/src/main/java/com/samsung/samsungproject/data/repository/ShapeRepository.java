package com.samsung.samsungproject.data.repository;

import com.samsung.samsungproject.data.api.shape.ShapeApiService;
import com.samsung.samsungproject.data.dto.ShapeDto;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.room.relation.ShapeWithPointsAndUser;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;

public class ShapeRepository {
    public static Call<ShapeDto> saveShape(ShapeWithPointsAndUser shape){
        return ShapeApiService.getInstance().saveShape(ShapeDto.toDto(shape));
    }
    public static Call<List<ShapeDto>> saveAllShapes(List<ShapeWithPointsAndUser> shapeList){
        return ShapeApiService.getInstance().saveAllShapes(shapeList.stream().map(shape -> ShapeDto.toDto(shape)).collect(Collectors.toList()));
    }
    public static Call<List<ShapeDto>> getAllShapes(){
        return ShapeApiService.getInstance().getAllShapes();
    }
}
