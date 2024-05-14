package com.example.altayattractions.service;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.altayattractions.R;
import com.example.altayattractions.db.DataBase;
import com.example.altayattractions.domain.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class MapService implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    private final Context context;
    private final String pathToImageStorage = "gs://alaty-map.appspot.com";
    private final LatLng altaiCenter = new LatLng(53.3545, 82.7194);


    public MapService(Context context) {
        this.context = context;
    }
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Toast.makeText(context, latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Toast.makeText(context, "Long " + latLng.latitude + " " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }







    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(altaiCenter, 5));
        for (Place p : DataBase.getPlaces()) {
            googleMap.addMarker(new MarkerOptions().position( new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName()));

        }



        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getTitle().equals("userLocations")) {
                    Toast.makeText(context, "Ваше метоположение", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Place place = DataBase.getPlaceByName(marker.getTitle());
                BottomSheetDialog dialog = new BottomSheetDialog(context);

                dialog.setContentView(R.layout.dialog_fragment);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                TextView item_name = dialog.getWindow().findViewById(R.id.item_name);
                TextView item_address = dialog.getWindow().findViewById(R.id.item_address);
                TextView item_info = dialog.getWindow().findViewById(R.id.item_info);
                ImageView imageView = dialog.getWindow().findViewById(R.id.item_image);

                item_name.setText(place.getName());
                item_address.setText(place.getAddress());
                item_info.setText(place.getInformations());

                FirebaseApp.initializeApp(context);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(pathToImageStorage);
                StorageReference reference = firebaseStorage.getReference(place.getPathToImage());
                Glide.with(context).load(reference).into(imageView);

                return false;
            }
        });

        googleMap.getUiSettings().setZoomControlsEnabled(true);


    }
}
