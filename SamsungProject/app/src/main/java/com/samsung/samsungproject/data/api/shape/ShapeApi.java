package com.samsung.samsungproject.data.api.shape;

import com.samsung.samsungproject.domain.model.Shape;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ShapeApi {
    @POST("shape")
    Call<Shape> saveShape(@Body Shape shape, @Query("score") long score);
    @POST("shape/all")
    Call<List<Shape>> saveAllShapes(@Body List<Shape> shape, @Query("score") long score);
    @GET("shape/all")
    Call<List<Shape>> getAllShapes();

    @GET("shape/recent")
    Call<List<Shape>> getRecentShapes(@Query("id") long id, @Query("control_sum") long controlSum);
    @GET("shape/sum")
    Call<Long> getControlSum();
}
