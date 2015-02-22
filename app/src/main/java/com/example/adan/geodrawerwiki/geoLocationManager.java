package com.example.adan.geodrawerwiki;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adan on 3/1/15.
 */
public class geoLocationManager {

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long MEASURE_TIME = ONE_MIN;
    private static final long POLLING_FREQ = 1000 * 10;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
    public static final float MIN_DISTANCE = 1000.0f;

    // Current best location estimate
    private Location mBestReading = null;

    // Reference to the LocationManager and LocationListener
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private MapsActivity mParent = null;

    public geoLocationManager(MapsActivity parent)
    {
        mParent = parent;
        initializeLocalization();
    }

    public void onResume()
    {
        if (null == mBestReading
                || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
                || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN)
        {

            // Register for network location updates
            if (null != mLocationManager
                    .getProvider(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, POLLING_FREQ,
                        MIN_DISTANCE, mLocationListener);
            }

            // Register for GPS location updates
            if (null != mLocationManager
                    .getProvider(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, POLLING_FREQ,
                        MIN_DISTANCE, mLocationListener);
            }

            // Schedule a runnable to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                @Override
                public void run() {

                    Log.i("geoWiki", "location updates cancelled");

                    mLocationManager.removeUpdates(mLocationListener);

                    if (mBestReading == null) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mParent.getApplicationContext(), mParent.getResources().getString(R.string.gps_error), Toast.LENGTH_SHORT).show();

                            }
                        };

                        mParent.runOnUiThread(runnable);
                    }
                }
            }, MEASURE_TIME, TimeUnit.MILLISECONDS);
        }
    }

    public void onPause()
    {
        mLocationManager.removeUpdates(mLocationListener);
    }

    public Location getLocation()
    {
        return mBestReading;
    }

    private  void initializeLocalization()
    {
        // Acquire reference to the LocationManager
        if (null == (mLocationManager = (LocationManager)mParent.getSystemService(Context.LOCATION_SERVICE)))
            mParent.finish();

        // Get best last location measurement
        mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (mBestReading == null)
                    mBestReading = location;
                else {
                    if (mBestReading.distanceTo(location) > MIN_DISTANCE)
                        mBestReading = location;
                    else
                        return;
                }

                mParent.onLocationChanged(mBestReading);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //TODO: completar
            }

            @Override
            public void onProviderEnabled(String provider) {
                //TODO: completar
            }

            @Override
            public void onProviderDisabled(String provider) {
                //TODO: completar
            }
        };
    }


    // Get the last known location from all providers
    // return best reading that is as accurate as minAccuracy and
    // was taken no longer then minAge milliseconds ago. If none,
    // return null.

    private Location bestLastKnownLocation(float minAccuracy, long maxAge) {

        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestAge = Long.MIN_VALUE;

        List<String> matchingProviders = mLocationManager.getAllProviders();

        for (String provider : matchingProviders) {

            Location location = mLocationManager.getLastKnownLocation(provider);

            if (location != null) {

                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if (accuracy < bestAccuracy) {

                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestAge = time;

                }
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy) {
//                || (System.currentTimeMillis() - bestAge) > maxAge) {
            return null;
        } else {
            return bestResult;
        }
    }

}
