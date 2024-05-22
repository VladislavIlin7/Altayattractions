package com.example.altayattractions.domain;


public class Place {
    private final String pathToImage;
    private final String address;
    private final String informations;
    private final double latitude;
    private final double longitude;
    private String name;
    private double distance;

    public Place(String name, String pathToImage, String address, String informations, double latitude, double longitude) {
        this.name = name;
        this.pathToImage = pathToImage;
        this.address = address;
        this.informations = informations;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public String getAddress() {
        return address;
    }

    public String getInformations() {
        return informations;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}

