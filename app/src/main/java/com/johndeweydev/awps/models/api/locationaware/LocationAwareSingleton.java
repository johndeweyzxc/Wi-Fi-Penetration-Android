package com.johndeweydev.awps.models.api.locationaware;

import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.SettingsClient;

public class LocationAwareSingleton {

  private final LocationAware locationAware;
  private static LocationAwareSingleton instance;
  private static LocationManager locationManager;
  private static SettingsClient settingsClient;
  private static int locationPermission = -1;
  private static Geocoder geocoder;

  public LocationAwareSingleton() {
    locationAware = new LocationAware();
  }

  public LocationAware getLocationAware() {
    return locationAware;
  }

  public static synchronized LocationAwareSingleton getInstance() {
    if (instance == null) {
      instance = new LocationAwareSingleton();
    }
    return instance;
  }

  public static void setLocationManager(LocationManager aLocationManager) {
    locationManager = aLocationManager;
  }

  public static LocationManager getLocationManager() {
    if (locationManager != null) {
      return locationManager;
    } else {
      // Before using this method it is required to first create a new instance of LocationManager
      // and set the instance using setLocationManager
      Log.e("dev-log", "LocationAwareSingleton.getLocationManager: Instance of " +
              "LocationManager is not found in the singleton");
      return null;
    }
  }

  public static void setSettingsClient(SettingsClient aSettingsClient) {
    settingsClient = aSettingsClient;
  }

  public static SettingsClient getSettingsClient() {
    if (settingsClient != null) {
      return settingsClient;
    } else {
      // Before using this method it is required to first create a new instance of SettingsClient
      // and set the instance using setSettingsClient
      Log.e("dev-log", "LocationAwareSingleton.getSettingsClient: Instance of " +
              "SettingsClient is not found in the singleton");
      return null;
    }
  }

  public static void setLocationPermission(int aLocationPermission) {
    locationPermission = aLocationPermission;
  }

  public static void setGeocoder(Geocoder aGeocoder) {
    geocoder = aGeocoder;
  }

  public static Geocoder getGeocoder() {
    if (geocoder != null) {
      return geocoder;
    } else {
      // Before using this method it is required to first create a new instance of Geocoder
      // and set the instance using setGeocoder
      Log.e("dev-log", "LocationAwareSingleton.getGeocoder: Instance of " +
              "Geocoder is not found in the singleton");
      return null;
    }
  }

  public static boolean isGpsTurnedOn() {
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  public static boolean isLocationPermissionGranted() {
    return locationPermission == PackageManager.PERMISSION_GRANTED;
  }
}
