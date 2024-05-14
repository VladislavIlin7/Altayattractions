package com.example.altayattractions.db;


import com.google.android.gms.maps.model.LatLng;
import com.example.altayattractions.domain.Place;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;


public class DataBase {

//    private static final List<Place> places = Arrays.asList(
//            new Place(1, "Canada", "/Canada.jpg", "CanadaAddress", "info Canada", new LatLng(56.130366, -106.346771)),
//            new Place (2, "Japan", "/Japan.jpg", "JapanAddress", "info Japan", new LatLng(35.6895, 139.692)));

    private static final List<Place> places = new ArrayList<Place>();

    public static List<Place> getPlaces() {
        return places;

    }

    public static void add (Place place) {
        if (place != null)
            places.add(place);
    }


    public static Place getPlaceByName (String name) {
        for (Place p : places) {
            if (p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }
}