/*
 * Copyright (c) 2017. Fardev  (fardan.self@gmail.com)
 */

package org.sandec.wisatasmg.helper;

/**
 * Created by Gilang Ramadhan on 16/07/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

public class LocationFinder extends Service implements LocationListener {
    private float lat;
    private float lon;
    private Location location;
    private LocationManager locationManager;

    boolean gpsStatus = false;
    boolean networkStatus = false;
    boolean locationStatus = true;
    String provider;
    double latitude;
    double longitude;
    private final Context mContext;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public LocationFinder(Context context) {
        super();
        this.mContext = context;
        getLocation();
    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    public float getLatitude() {
        if ((location) != null) {
            lat = (float) (location.getLatitude());

        } else {
            lat = 0;
        }
        return lat;
    }

    public float getLongitude() {
        if (location != null) {
            lon = (float) (location.getLongitude());
        } else {
            lon = 0;
        }
        return lon;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private static final int REQUEST_LOCATION = 1;

    public Location getLocation() {

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsStatus && !networkStatus) {
            locationStatus = false;
        } else {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            } else {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                provider = locationManager.getBestProvider(criteria, true);


                locationManager.requestLocationUpdates(provider, 60 * 120000, 0, this);
                location = (locationManager.getLastKnownLocation(provider));
                if (location != null) {
                   // Toast.makeText(mContext, "location " + location, Toast.LENGTH_LONG).show();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }

            }
        }
        return location;

    }

    protected void enableLocation() {

    }

    public boolean locationavailable() {
        return locationStatus;
    }
}