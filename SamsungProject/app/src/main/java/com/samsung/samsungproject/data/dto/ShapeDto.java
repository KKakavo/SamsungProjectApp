package com.samsung.samsungproject.data.dto;

import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.domain.room.relation.ShapeWithPointsAndUser;

import java.util.List;
import java.util.stream.Collectors;

public class ShapeDto {
    private long id;
    private UserDto user;
    private List<PointDto> pointList;

    public ShapeDto(long id, UserDto userDto, List<PointDto> pointList) {
        this.id = id;
        this.user = user;
        this.pointList = pointList;
    }

    public static ShapeDto toDto(ShapeWithPointsAndUser shape){
        return new ShapeDto(shape.getShape().getId(),
                UserDto.toDto(shape.getUser()),
                shape.getPointList().stream().map(PointDto::toDto).collect(Collectors.toList()));
    }

    public static ShapeWithPointsAndUser toDomainObject(ShapeDto shapeDto) {
        return new ShapeWithPointsAndUser(new Shape(shapeDto.getId(), shapeDto.getUser().getId()),
                shapeDto.getPointList().stream().map(PointDto::toDomainObject).collect(Collectors.toList()),
                UserDto.toDomainObject(shapeDto.getUser()));
    }

    public long getId() {
        return id;
    }

    public UserDto getUser() {
        return user;
    }

    public List<PointDto> getPointList() {
        return pointList;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setPointList(List<PointDto> pointList) {
        this.pointList = pointList;
    }
}
