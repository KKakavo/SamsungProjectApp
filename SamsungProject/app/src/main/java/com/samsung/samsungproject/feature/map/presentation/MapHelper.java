package com.samsung.samsungproject.feature.map.presentation;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    /*public void createPolygon(int startPoint,LatLng crossingPoint) {
        List<LatLng> polygonPoints = new ArrayList<>(polylinePoints.subList(startPoint, polylinePoints.size() - 1));
        List<Point> points = new ArrayList<>();
        for (LatLng point : polygonPoints)
            points.add(new Point(point.latitude, point.longitude));
        saveShape(new Shape(user, points));
        polylinePoints = new LinkedList<>(polylinePoints.subList(0, startPoint - 1));
        googleMap.addPolygon(new PolygonOptions()
                .addAll(polygonPoints)
                .strokeWidth(20)
                .strokeColor(Color.YELLOW)
                .fillColor(Color.YELLOW));

    }

    public void TracePolyline() {
        polyline.remove();
        polyline = googleMap.addPolyline(new PolylineOptions()
                .addAll(polylinePoints)
                .width(20)
                .color(Color.RED)
                .startCap(new RoundCap())
                .endCap(new RoundCap()));
    }*/
}
