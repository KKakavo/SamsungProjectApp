package com.samsung.samsungproject.feature.map.presentation;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapHelper {
    private final GoogleMap googleMap;
    private Polyline polyline;
    private List<LatLng> polylinePoints;
    private final static double DIFF = 0.000001;

    public MapHelper(GoogleMap googleMap) {
        this.googleMap = googleMap;
        polylinePoints = new ArrayList<>();
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
    public static Shape polygonToShape(PolygonOptions polygon, User user){
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
}
