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
    public Point(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getId() {
        return id;
    }

    public long getShape_id() {
        return shape_id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setShape_id(long shape_id) {
        this.shape_id = shape_id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
