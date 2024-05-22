package com.example.altayattractions.domain;

import java.util.ArrayList;
import java.util.List;

public class Places {
    public static final List<Place> places = new ArrayList<Place>();

    public static Place[] getPlaces() {
        Place[] array = new Place[places.size()];
        places.toArray(array);
        return array;
    }

    public static void add(Place place) {
        if (place != null)
            places.add(place);
    }

    public static Place getPlaceByName(String name) {
        for (Place p : places) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
}