package com.samsung.samsungproject.feature.map.presentation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDao;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDaoSqlite;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.stream.Collectors;

public class ShapeDbThread implements Runnable
{
    private final ShapeDao shapeDao;
    private final GoogleMap googleMap;
    private final Handler handler;

    public ShapeDbThread(Context context, GoogleMap googleMap, Handler handler) {
        this.shapeDao = new ShapeDaoSqlite(context);
        this.googleMap = googleMap;
        this.handler = handler;
    }

    @Override
    public void run() {
        //shapeDao.findAll().stream().map(MapHelper::shapeToPolygon).forEach(polygonOptions -> handler.sendMessage(new Message().setData(new Bundle().putParcelable("options", polygonOptions))));

        System.out.println("lol");
    }
}
