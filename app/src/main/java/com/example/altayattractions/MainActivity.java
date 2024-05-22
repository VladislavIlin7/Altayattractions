package com.example.altayattractions;


import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.altayattractions.data.PlacesLoader;
import com.example.altayattractions.databinding.ActivityMainBinding;
import com.example.altayattractions.interfaces.IOperationHandler;
import com.example.altayattractions.service.MapFragment;
import com.example.altayattractions.service.PlaceFragment;
import com.example.altayattractions.service.SimpleLocation;


public class MainActivity extends AppCompatActivity implements IOperationHandler {

    ActivityMainBinding binding;
    PlaceFragment placeFragment;
    MapFragment mapFragment;
    private SimpleLocation location;


    public void onCompleted() {
        replaceFragment(this.getPlace());
    }

    @Override
    protected void onResume() {
        super.onResume();

        location.beginUpdates();
    }

    @Override
    protected void onPause() {
        location.endUpdates();

        super.onPause();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.location = new SimpleLocation(this);
        if (!this.location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.toString()) {
                case "Map":
                    replaceFragment(this.getMap());
                    break;
                case "List":
                    replaceFragment(this.getPlace());
                    break;
            }

            return true;
        });


        PlacesLoader.load(this);
    }

    private PlaceFragment getPlace() {
        if (this.placeFragment == null) {
            this.placeFragment = new PlaceFragment(this.location);
        }

        return this.placeFragment;
    }


    private MapFragment getMap() {
        if (this.mapFragment == null) {
            this.mapFragment = new MapFragment(this, this.location);
        }

        return this.mapFragment;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
