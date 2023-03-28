package com.samsung.samsungproject.domain;

import com.google.android.gms.maps.model.LatLng;

public class Picture {
    private final long id;
    private final LatLng coordinates;
    private final String path;

    public Picture(long id, LatLng coordinates, String path) {
        this.id = id;
        this.coordinates = coordinates;
        this.path = path;
    }
}
