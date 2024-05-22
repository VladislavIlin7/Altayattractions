package com.example.altayattractions.service;


import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.altayattractions.R;
import com.example.altayattractions.domain.Place;
import com.example.altayattractions.domain.Places;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MapService implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static FusedLocationProviderClient fusedLocationProviderClient;
    public static SupportMapFragment supportMapFragment;
    final private SimpleLocation location;
    private final Context context;
    private final String pathToImageStorage = "gs://alaty-map.appspot.com";
    private final LatLng altaiCenter = new LatLng(53.3545, 82.7194);
    private final boolean mapReady;
    private GoogleMap googleMap;


    public MapService(Context context, SimpleLocation location) {
        this.context = context;
        this.location = location;
        this.mapReady = false;
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
//        if (this.mapReady) return;
//
//        this.mapReady = true;
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(altaiCenter, 5));

        double myLatitude = this.location.getLatitude();
        double myLongitude = this.location.getLongitude();

        for (Place p : Places.getPlaces()) {
            this.googleMap.addMarker(new MarkerOptions().position(new LatLng(p.getLatitude(), p.getLongitude())).title(p.getName()));
            p.setDistance(SimpleLocation.calculateDistance(myLatitude, myLongitude, p.getLatitude(), p.getLongitude()) / 1000);
        }


        MarkerOptions markerUser = new MarkerOptions().position(new LatLng(myLatitude, myLongitude))
                .title("userLocations")
                .icon(BitmapDescriptorFactory.defaultMarker(HUE_BLUE));

        this.googleMap.addMarker(markerUser);


        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getTitle().equals("userLocations")) {
                    Toast.makeText(context, "Ваше меcтоположение", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Place place = Places.getPlaceByName(marker.getTitle());
                BottomSheetDialog dialog = new BottomSheetDialog(context);

                dialog.setContentView(R.layout.dialog_fragment);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                TextView item_distance = dialog.getWindow().findViewById(R.id.item_distance);
                TextView item_name = dialog.getWindow().findViewById(R.id.item_name);
                TextView item_address = dialog.getWindow().findViewById(R.id.item_address);
                TextView item_info = dialog.getWindow().findViewById(R.id.item_info);
                ImageView imageView = dialog.getWindow().findViewById(R.id.item_image);

                item_distance.setText(String.format("%.1f", place.getDistance()) + "km");
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
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
