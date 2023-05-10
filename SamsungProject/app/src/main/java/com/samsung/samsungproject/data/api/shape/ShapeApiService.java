package com.samsung.samsungproject.data.api.shape;

import com.samsung.samsungproject.data.api.RetrofitService;

public class ShapeApiService {
    private static ShapeApi shapeApi;

    private static ShapeApi create(){
        return RetrofitService.getInstance().create(ShapeApi.class);
    }

    public static ShapeApi getInstance(){
        if(shapeApi == null) shapeApi = create();
        return shapeApi;
    }
}
