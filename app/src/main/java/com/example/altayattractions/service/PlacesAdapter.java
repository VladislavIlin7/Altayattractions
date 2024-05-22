package com.example.altayattractions.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.altayattractions.R;
import com.example.altayattractions.domain.Place;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PlacesAdapter extends ArrayAdapter<Place> {

    private final Context context;
    private final SimpleLocation location;

    private final String pathToImageStorage = "gs://alaty-map.appspot.com";

    public PlacesAdapter(@NonNull Context context, @NonNull Place[] places, @NonNull SimpleLocation location) {
        super(context, R.layout.item, places);
        this.context = context;
        this.location = location;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Place place = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        }

        if (place.getDistance() < 0.01) {
            place.setDistance(SimpleLocation.calculateDistance(location.getLatitude(), location.getLongitude(), place.getLatitude(), place.getLongitude()) / 1000);
        }


        if (place.getDistance() > 0.01) {
            TextView distance = view.findViewById(R.id.distanceList);
            distance.setText(String.format("%.1f", place.getDistance()) + " км");
        }


        TextView name = view.findViewById(R.id.item_name);
        TextView address = view.findViewById(R.id.item_address);
        ImageView imageView = view.findViewById(R.id.avatar);


        name.setText(place.getName());
        address.setText(place.getAddress());

        FirebaseApp.initializeApp(context);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(pathToImageStorage);
        StorageReference reference = firebaseStorage.getReference(place.getPathToImage());
        Glide.with(context).load(reference).into(imageView);

        return view;
    }

}