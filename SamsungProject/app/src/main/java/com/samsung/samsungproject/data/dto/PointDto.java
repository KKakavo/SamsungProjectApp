package com.samsung.samsungproject.data.dto;

import com.samsung.samsungproject.domain.model.Point;

public class PointDto {
    private long id;
    private double latitude;
    private double longitude;

    public PointDto(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static PointDto toDto(Point point){
        return new PointDto(point.getId(),
                point.getLatitude(),
                point.getLongitude());
    }

    public static Point toDomainObject(PointDto pointDto){
        return new Point(pointDto.getId(),
                pointDto.getLatitude(),
                pointDto.getLongitude());
    }

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
