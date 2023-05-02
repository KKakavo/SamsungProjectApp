package com.samsung.samsungproject.feature.map.presentation;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {
    private final double DIFF = 0.000001;
    private LatLng[] crossingPoint(LatLng AStartPoint, LatLng AEndPoint, LatLng BStartPoint, LatLng BEndPoint) {
        double v = AEndPoint.latitude - AStartPoint.latitude;
        double w = AEndPoint.longitude - AStartPoint.longitude;
        double v2 = BEndPoint.latitude - BStartPoint.latitude;
        double w2 = BEndPoint.longitude - BStartPoint.longitude;
        double lenA = Math.sqrt(v * v + w * w);
        double lenB = Math.sqrt(v2 * v2 + w2 * w2);
        if(Math.abs(v/lenA - v2/lenB) < DIFF
                && Math.abs(w/lenA - w2/lenB) < DIFF)
            return null;
        double t2 = (-w * BStartPoint.latitude + w * AStartPoint.latitude + v * BStartPoint.longitude - v * AStartPoint.longitude) / (w * v2 - v * w2);
        double t = (BStartPoint.latitude - AStartPoint.latitude + v2 * t2) / v;
        if (t > 0 && t < 1 && t2 > 0 && t2 < 1) {
            return new LatLng[]{BStartPoint, new LatLng(BStartPoint.latitude + v2 * t2, BStartPoint.longitude + w2 * t2)};
        }

        return null;
    }
}
