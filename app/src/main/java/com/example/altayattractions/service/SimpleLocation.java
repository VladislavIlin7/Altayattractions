package com.example.altayattractions.service;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

public class SimpleLocation {
    private final LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mPosition;

    public SimpleLocation(final Context context) {
        mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mPosition = getLastLocation();
    }

    public static void openSettings(final Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

    public boolean hasLocationEnabled() {
        return hasLocationEnabled(getProviderName());
    }

    private boolean hasLocationEnabled(final String providerName) {
        try {
            return mLocationManager.isProviderEnabled(providerName);
        } catch (Exception e) {
            return false;
        }
    }

    public void beginUpdates() {
        if (mLocationListener != null) {
            endUpdates();
        }

        mPosition = getLastLocation();

        mLocationListener = createLocationListener();
    }

    public void endUpdates() {
        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationListener = null;
        }
    }
    
    public double getLatitude() {
        if (mPosition == null) {
            return 0.0f;
        }

        return mPosition.getLatitude();
    }

    public double getLongitude() {
        if (mPosition == null) {
            return 0.0f;
        }

        return mPosition.getLongitude();
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                mPosition = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    private String getProviderName() {
        return LocationManager.NETWORK_PROVIDER;
    }

    private Location getLastLocation() {
        if (mLocationManager == null)
            return null;

        try {
            return mLocationManager.getLastKnownLocation(getProviderName());
        } catch (Exception e) {
            return null;
        }
    }
}