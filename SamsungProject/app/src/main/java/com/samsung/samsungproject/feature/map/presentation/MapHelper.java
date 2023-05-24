package com.samsung.samsungproject.feature.map.presentation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.samsung.samsungproject.data.repository.ShapeRepository;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDao;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDaoSqlite;
import com.samsung.samsungproject.domain.db.dao.user.UserDao;
import com.samsung.samsungproject.domain.db.dao.user.UserDaoSqlite;
import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapHelper {
    private final GoogleMap googleMap;
    private final ShapeDao shapeDao;
    private final static double DIFF = 0.000001;

    public MapHelper(Context context, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.shapeDao = new ShapeDaoSqlite(context);
    }

    public static LatLng crossingPoint(LatLng AStartPoint, LatLng AEndPoint, LatLng BStartPoint, LatLng BEndPoint) {
        double v = AEndPoint.latitude - AStartPoint.latitude;
        double w = AEndPoint.longitude - AStartPoint.longitude;
        double v2 = BEndPoint.latitude - BStartPoint.latitude;
        double w2 = BEndPoint.longitude - BStartPoint.longitude;
        double lenA = Math.sqrt(v * v + w * w);
        double lenB = Math.sqrt(v2 * v2 + w2 * w2);
        if (Math.abs(v / lenA - v2 / lenB) < DIFF
                && Math.abs(w / lenA - w2 / lenB) < DIFF)
            return null;
        double t2 = (-w * BStartPoint.latitude + w * AStartPoint.latitude + v * BStartPoint.longitude - v * AStartPoint.longitude) / (w * v2 - v * w2);
        double t = (BStartPoint.latitude - AStartPoint.latitude + v2 * t2) / v;
        if (t > 0 && t < 1 && t2 > 0 && t2 < 1) {
            return new LatLng(BStartPoint.latitude + v2 * t2, BStartPoint.longitude + w2 * t2);
        }

        return null;
    }

    public static LatLng pointToLatLng(Point point){
        return new LatLng(point.getLatitude(), point.getLongitude());
    }
    public static Point latLngToPoint(LatLng latLng){
        return new Point(latLng.latitude, latLng.longitude);
    }
    public static Shape polygonToShape(Polygon polygon, User user){
        return new Shape(user,
                polygon.getPoints().stream().map(MapHelper::latLngToPoint).collect(Collectors.toList()),
                polygon.getFillColor());
    }
    public static PolygonOptions shapeToPolygon(Shape shape){
        return new PolygonOptions()
                .addAll(shape.getPointList().stream().map(MapHelper::pointToLatLng).collect(Collectors.toList()))
                .fillColor(shape.getColor())
                .strokeWidth(20)
                .strokeColor(shape.getColor());
    }
   /* public static void downloadRecentShapes(){
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return ;
            }
        });
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                ShapeRepository.getRecentShapes()
            }
        });
    }*/
}
