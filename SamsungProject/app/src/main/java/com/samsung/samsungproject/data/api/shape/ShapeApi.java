package com.samsung.samsungproject.data.api.shape;

import com.samsung.samsungproject.data.dto.ShapeDto;
import com.samsung.samsungproject.domain.model.Shape;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ShapeApi {
    @POST("shape")
    Call<ShapeDto> saveShape(@Body ShapeDto shape);
    @POST("shape/all")
    Call<List<ShapeDto>> saveAllShapes(@Body List<ShapeDto> shape);
    @GET("shape/all")
    Call<List<ShapeDto>> getAllShapes();
}
