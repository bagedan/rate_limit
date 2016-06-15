package com.agoda.controller.db.entity;

/**
 * Created by Tkachi on 6/15/2016.
 */
public class HotelEntity {
    private int hotelId;
    private String cityName;
    private String roomType;
    private double roomPrice;

    public int getHotelId() {
        return hotelId;
    }

    public HotelEntity setHotelId(int hotelId) {
        this.hotelId = hotelId;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public HotelEntity setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public String getRoomType() {
        return roomType;
    }

    public HotelEntity setRoomType(String roomType) {
        this.roomType = roomType;
        return this;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public HotelEntity setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
        return this;
    }

    @Override
    public String toString() {
        return "HotelEntity{" +
                "hotelId=" + hotelId +
                ", cityName='" + cityName + '\'' +
                ", roomType='" + roomType + '\'' +
                ", roomPrice=" + roomPrice +
                '}';
    }
}
