package com.example.altayattractions.data;

import androidx.annotation.NonNull;

import com.example.altayattractions.domain.Place;
import com.example.altayattractions.domain.Places;
import com.example.altayattractions.interfaces.IOperationHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlacesLoader {

    public static void load(IOperationHandler loadCompleted) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseRef = FirebaseDatabase.
                        getInstance().getReference().child("places");
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Places.clear();

                        for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                            String name = placeSnapshot.child("name").getValue(String.class);
                            String pathToImage = placeSnapshot.child("pathToImage").getValue(String.class);
                            String address = placeSnapshot.child("address").getValue(String.class);
                            String informations = placeSnapshot.child("informations").getValue(String.class);
                            double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                            double longitude = placeSnapshot.child("longitude").getValue(Double.class);


                            Places.add(new Place(name, pathToImage, address, informations, latitude, longitude));
                        }

                        if (loadCompleted != null)
                            loadCompleted.onCompleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });
            }
        }).start();
    }
}
