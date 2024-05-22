package com.example.altayattractions.service;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {
    final private Context context;
    final private SimpleLocation location;
    private MapService mapService;

    public MapFragment(Context context, SimpleLocation location) {
        this.context = context;
        this.location = location;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mapService = new MapService(this.context, this.location);
        getMapAsync(this.mapService);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mapService.onMapReady(googleMap);
    }
}