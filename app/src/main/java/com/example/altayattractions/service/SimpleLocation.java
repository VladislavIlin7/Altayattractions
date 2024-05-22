package com.example.altayattractions.service;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;

import java.util.Random;

public class SimpleLocation {
    private static final String PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER;
    private static final String PROVIDER_FINE = LocationManager.GPS_PROVIDER;
    private static final String PROVIDER_FINE_PASSIVE = LocationManager.PASSIVE_PROVIDER;
    private static final long INTERVAL_DEFAULT = 10 * 60 * 1000;
    private static final float KILOMETER_TO_METER = 1000.0f;
    private static final float LATITUDE_TO_KILOMETER = 111.133f;
    private static final float LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE = 111.320f;

    private static final Random mRandom = new Random();
    private static final double SQUARE_ROOT_TWO = Math.sqrt(2);
    private static Location mCachedPosition;
    private final LocationManager mLocationManager;
    private final boolean mRequireFine;
    private final boolean mPassive;
    private final long mInterval;
    private final boolean mRequireNewLocation;
    private int mBlurRadius;
    private LocationListener mLocationListener;
    private Location mPosition;
    private Listener mListener;

    public SimpleLocation(final Context context) {
        this(context, false);
    }

    public SimpleLocation(final Context context, final boolean requireFine) {
        this(context, requireFine, false);
    }

    public SimpleLocation(final Context context, final boolean requireFine, final boolean passive) {
        this(context, requireFine, passive, INTERVAL_DEFAULT);
    }

    public SimpleLocation(final Context context, final boolean requireFine, final boolean passive, final long interval) {
        this(context, requireFine, passive, interval, false);
    }

    public SimpleLocation(final Context context, final boolean requireFine, final boolean passive, final long interval, final boolean requireNewLocation) {
        mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mRequireFine = requireFine;
        mPassive = passive;
        mInterval = interval;
        mRequireNewLocation = requireNewLocation;

        if (!mRequireNewLocation) {
            mPosition = getCachedPosition();
            cachePosition();
        }
    }

    private static int calculateRandomOffset(final int radius) {
        return mRandom.nextInt((radius + 1) * 2) - radius;
    }

    public static void openSettings(final Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    public static double latitudeToKilometer(double latitude) {
        return latitude * LATITUDE_TO_KILOMETER;
    }

    public static double latitudeToMeter(double latitude) {
        return latitudeToKilometer(latitude) * KILOMETER_TO_METER;
    }

    public static double meterToLatitude(double meter) {
        return meter / latitudeToMeter(1.0f);
    }

    public static double longitudeToKilometer(double longitude, double latitude) {
        return longitude * LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE * Math.cos(Math.toRadians(latitude));
    }

    public static double longitudeToMeter(double longitude, double latitude) {
        return longitudeToKilometer(longitude, latitude) * KILOMETER_TO_METER;
    }

    public static double meterToLongitude(double meter, double latitude) {
        return meter / longitudeToMeter(1.0f, latitude);
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

        if (!mRequireNewLocation) {
            mPosition = getCachedPosition();
        }

        mLocationListener = createLocationListener();
    }

    public void endUpdates() {
        if (mLocationListener != null) {
            mLocationManager.removeUpdates(mLocationListener);
            mLocationListener = null;
        }
    }

    private Location blurWithRadius(final Location originalLocation) {
        if (mBlurRadius <= 0) {
            return originalLocation;
        } else {
            Location newLocation = new Location(originalLocation);

            double blurMeterLong = calculateRandomOffset(mBlurRadius) / SQUARE_ROOT_TWO;
            double blurMeterLat = calculateRandomOffset(mBlurRadius) / SQUARE_ROOT_TWO;

            newLocation.setLongitude(newLocation.getLongitude() + meterToLongitude(blurMeterLong, newLocation.getLatitude()));
            newLocation.setLatitude(newLocation.getLatitude() + meterToLatitude(blurMeterLat));

            return newLocation;
        }
    }

    public double getLatitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(mPosition);
            return position.getLatitude();
        }
    }

    public double getLongitude() {
        if (mPosition == null) {
            return 0.0f;
        } else {
            Location position = blurWithRadius(mPosition);
            return position.getLongitude();
        }
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                mPosition = location;
                cachePosition();

                if (mListener != null) {
                    mListener.onPositionChanged();
                }
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
        return getProviderName(mRequireFine);
    }

    private String getProviderName(final boolean requireFine) {
        if (requireFine) {
            if (mPassive) {
                return PROVIDER_FINE_PASSIVE;
            } else {
                return PROVIDER_FINE;
            }
        } else {
            if (hasLocationEnabled(PROVIDER_COARSE)) {
                if (mPassive) {
                    throw new RuntimeException("There is no passive provider for the coarse location");
                } else {
                    return PROVIDER_COARSE;
                }
            } else {
                if (hasLocationEnabled(PROVIDER_FINE) || hasLocationEnabled(PROVIDER_FINE_PASSIVE)) {
                    return getProviderName(true);
                } else {
                    return PROVIDER_COARSE;
                }
            }
        }
    }

    private Location getCachedPosition() {
        if (mCachedPosition != null) {
            return mCachedPosition;
        } else {
            try {
                return mLocationManager.getLastKnownLocation(getProviderName());
            } catch (Exception e) {
                return null;
            }
        }
    }

    private void cachePosition() {
        if (mPosition != null) {
            mCachedPosition = mPosition;
        }
    }

    public interface Listener {
        void onPositionChanged();

    }

    public static class Point implements Parcelable {

        public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {

            @Override
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            @Override
            public Point[] newArray(int size) {
                return new Point[size];
            }

        };
        public final double latitude;
        public final double longitude;

        public Point(double lat, double lon) {
            latitude = lat;
            longitude = lon;
        }

        private Point(Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
        }

        @Override
        public String toString() {
            return "(" + latitude + ", " + longitude + ")";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }

    }

}