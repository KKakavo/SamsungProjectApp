package com.samsung.samsungproject.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.samsung.samsungproject.data.api.shape.ShapeApi;
import com.samsung.samsungproject.data.api.shape.ShapeApiService;
import com.samsung.samsungproject.data.db.dao.shape.ShapeDao;
import com.samsung.samsungproject.data.db.dao.shape.ShapeDaoSqlite;
import com.samsung.samsungproject.data.repository.communication.RepositoryCallback;
import com.samsung.samsungproject.data.repository.communication.Result;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.map.presentation.MapUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class ShapeRepository {
    private long controlSum;
    private final ShapeApi api;
    private final ShapeDao dao;
    private final Executor executor;
    private final Handler handler;
    private final String LOG_TAG = "ShapeRepository";

    public ShapeRepository(Context context) {
        dao = ShapeDaoSqlite.getInstance(context);
        api = ShapeApiService.getInstance();
        executor = Executors.newFixedThreadPool(3);
        handler = new Handler(Looper.getMainLooper());
    }

    public void saveAllPolygons(List<PolygonOptions> polygonList,
                              User user,
                              RepositoryCallback<List<PolygonOptions>> callback) {
        executor.execute(() -> {
            if(polygonList.size() > 0) {
                long score = 0;
                for (PolygonOptions polygon : polygonList)
                    score += Math.round(SphericalUtil.computeArea(polygon.getPoints()));
                List<Shape> shapeList = polygonList.stream()
                        .map(polygon -> MapUtils.polygonToShape(polygon, user))
                        .collect(Collectors.toList());
                try {
                    Response<List<Shape>> response = api.saveAllShapes(shapeList, score).execute();
                    if (response.isSuccessful()) {
                        List<Shape> body = response.body();
                        dao.insertAll(body);
                        controlSum += body.size();
                        handler.post(() -> callback.onComplete(new Result.Success<>(
                                body.stream().map(MapUtils::shapeToPolygon).collect(Collectors.toList()))));
                    } else
                        throw new IOException("http code: " + response.code() + " in method saveAll");
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    Result result = new Result.Error<>(e, null);
                    handler.post(() -> callback.onComplete(result));
                }
            }
        });
    }

    public void getRecentPolygons(RepositoryCallback<List<PolygonOptions>> callback) {
        executor.execute(() -> {
            try {
                Response<List<Shape>> response = api.getRecentShapes(dao.getSize(), controlSum).execute();
                if(response.isSuccessful()){
                    List<Shape> body = response.body();
                    controlSum+= body.size();
                    handler.post(() -> callback.onComplete(new Result.Success<>(
                            body.stream().map(MapUtils::shapeToPolygon).collect(Collectors.toList()))));
                } else throw new IOException("http code: " + response.code() + " in method getRecentShapes");
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                Result result = new Result.Error<>(e, null);
                handler.post(() -> callback.onComplete(result));
            }
        });
    }

    public void getAllPolygons(RepositoryCallback<List<User>> callback){
        executor.execute(() -> {
            List<Shape> shapeList = dao.findAll();
            controlSum += shapeList.size();
            handler.post(() -> callback.onComplete(new Result.Success<>(
                    shapeList.stream().map(MapUtils::shapeToPolygon).collect(Collectors.toList()))));
        });
    }
}
