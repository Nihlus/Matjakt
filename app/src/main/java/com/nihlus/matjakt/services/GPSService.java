package com.nihlus.matjakt.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service
{
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int TEN_SECONDS = 1000 * 10;
    private static final int TEN_METERS = 10;

    private final IBinder binder = new GPSBinder();

    private Location currentLocation;

    private LocationManager locationManager;
    private final LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            if (isBetterLocation(location))
            {
                currentLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled(String provider)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {

        }
    };

    public class GPSBinder extends Binder
    {
        public GPSService getService()
        {
            return GPSService.this;
        }
    }

    // The service is created
    @Override
    public void onCreate()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
    }

    // The service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), TEN_SECONDS, TEN_METERS, locationListener);
        return START_NOT_STICKY;
    }

    // The service is stopped and then destroyed
    @Override
    public void onDestroy()
    {
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }


    public Location getCurrentLocation()
    {
        if (currentLocation == null)
        {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        }

        return currentLocation;
    }

    private boolean isBetterLocation(Location NewLocation)
    {
        if (currentLocation == null)
        {
            return true;
        }

        // Check if the new fix is newer or older
        long timeDelta = NewLocation.getTime() - currentLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If more than two minutes have elapsed, the new location is more likely to be accurate
        if (isSignificantlyNewer)
        {
            return true;
        }

        // If the new location is more than two minutes older, it has to be worse
        if (isSignificantlyOlder)
        {
            return false;
        }

        // Check if the new fix is more accurate
        int accuracyDelta = (int)(NewLocation.getAccuracy() - currentLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the locations are from the same provider
        boolean isFromSameProvider = isSameProvider(NewLocation.getProvider(), currentLocation.getProvider());

        // Determine the location quality
        if (isMoreAccurate)
        {
            return true;
        }
        else if (isNewer && !isLessAccurate)
        {
            return true;
        }
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
        {
            return true;
        }

        return false;
    }

    private boolean isSameProvider(String A, String B)
    {
        if (A == null)
        {
            return B == null;
        }

        return A.equals(B);
    }
}
