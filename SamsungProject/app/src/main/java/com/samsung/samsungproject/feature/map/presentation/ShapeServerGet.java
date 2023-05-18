package com.samsung.samsungproject.feature.map.presentation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.samsung.samsungproject.data.repository.ShapeRepository;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDao;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDaoSqlite;
import com.samsung.samsungproject.domain.model.Shape;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class ShapeServerGet implements Runnable{

    private final GoogleMap googleMap;
    private final ShapeDao shapeDao;
    private final Handler handler;

    public ShapeServerGet(GoogleMap googleMap, Context context, Handler handler) {
        this.googleMap = googleMap;
        shapeDao = new ShapeDaoSqlite(context);
        this.handler = handler;
        run();
    }

    @Override
    public void run() {
        try {
            Response<List<Shape>> response = ShapeRepository.getRecentShapes(shapeDao.getSize()).execute();
            if(response.isSuccessful()){

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
