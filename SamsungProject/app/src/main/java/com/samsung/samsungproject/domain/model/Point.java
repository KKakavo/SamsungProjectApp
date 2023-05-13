package com.samsung.samsungproject.domain.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Point {
    @PrimaryKey
    private long id;
    private double latitude;
    private double longitude;
    private long shape_id;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
