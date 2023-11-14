package com.johndeweydev.awps.models.api.locationaware;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class LocationAware implements LocationListener {

  public interface GpsSettingsListener {
    void onGpsEnabled();
    void onGpsDisabled();
    void onGpsLocationChanged(double latitude, double longitude);
  }

  private double latitude;
  private double longitude;
  private GpsSettingsListener gpsSettingsListener;

  public LocationAware() {
    Log.w("dev-log", "LocationAware: Created new instance of LocationAware");
  }

  public void setGpsSettingsListener(GpsSettingsListener gpsSettingsListener) {
    this.gpsSettingsListener = gpsSettingsListener;
  }

  public Task<LocationSettingsResponse> askUserToTurnOnGps() {
    LocationRequest locationRequest = new LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(2000)
            .build();

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);

    SettingsClient client = LocationAwareSingleton.getSettingsClient();
    assert client != null;
    return client.checkLocationSettings(builder.build());
  }

  @SuppressLint("MissingPermission")
  public void gpsIsOnSetUpLocationUpdateListener() {
    Objects.requireNonNull(LocationAwareSingleton.getLocationManager())
            .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, this);
  }

  @Override
  public void onLocationChanged(Location location) {
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    gpsSettingsListener.onGpsLocationChanged(latitude, longitude);
  }

  @Override
  public void onProviderDisabled(@NonNull String provider) {
    gpsSettingsListener.onGpsDisabled();
  }

  @Override
  public void onProviderEnabled(@NonNull String provider) {
    gpsSettingsListener.onGpsEnabled();
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

}
