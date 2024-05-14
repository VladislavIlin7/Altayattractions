package com.example.altayattractions.domain;


//public class Place {
//
//
//    private final String name;
//    private final String pathToImage;
//    private final String address;
//    private final String informations;
//    private final LatLng latLng;
//
//    public Place(String name, String pathToImage, String address, String informations, double latitude, double longitude){
//
//        this.name = name;
//        this.pathToImage = pathToImage;
//        this.address = address;
//        this.informations = informations;
//        this.latLng = latLng;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getPathToImage() {
//        return pathToImage;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public String getInformations() {
//        return informations;
//    }
//
//    public LatLng getLatLng() {
//        return latLng;
//    }
//}
public class Place {
    private String name;
    private String pathToImage;
    private String address;
    private String informations;
    private double latitude;
    private double longitude;

    public Place(String name, String pathToImage, String address, String informations, double latitude, double longitude){

        this.name = name;
        this.pathToImage = pathToImage;
        this.address = address;
        this.informations = informations;
        this.latitude = latitude;
        this.longitude = longitude;
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

}

