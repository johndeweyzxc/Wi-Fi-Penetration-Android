package com.johndeweydev.awps;

import static com.johndeweydev.awps.AppConstants.NOTIFICATION_CHANNEL_ID;
import static com.johndeweydev.awps.AppConstants.NOTIFICATION_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.johndeweydev.awps.models.api.bridge.Bridge;
import com.johndeweydev.awps.models.api.bridge.BridgeSingleton;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoDatabase;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoSingleton;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.models.api.locationaware.LocationAware;
import com.johndeweydev.awps.models.api.locationaware.LocationAwareSingleton;
import com.johndeweydev.awps.models.repo.serial.terminalreposerial.TerminalRepoSerial;
import com.johndeweydev.awps.viewmodels.serial.terminalviewmodel.TerminalViewModel;
import com.johndeweydev.awps.viewmodels.serial.terminalviewmodel.TerminalViewModelFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationAware.GpsSettingsListener {

  private String currentFragmentLabel;
  private LocationAwareSingleton locationAwareSingleton;
  private boolean isSecondStageFinish = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    createNotificationChannel();

    // Set up USB serial
    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    LauncherSingleton.setUsbManager(usbManager);

    TerminalRepoSerial terminalRepoSerial = new TerminalRepoSerial();
    TerminalViewModelFactory terminalViewModelFactory = new TerminalViewModelFactory(
            terminalRepoSerial);
    new ViewModelProvider(this, terminalViewModelFactory).get(TerminalViewModel.class);

    // Set up Database
    HashInfoSingleton hashInfoSingleton = HashInfoSingleton.getInstance();
    HashInfoDatabase hashInfoDatabase = Room.databaseBuilder(getApplicationContext(),
            HashInfoDatabase.class, "awps_database").build();
    hashInfoSingleton.setHashInfoDatabase(hashInfoDatabase);

    // Set up Network
    BridgeSingleton bridgeSingleton = BridgeSingleton.getInstance();
    bridgeSingleton.setBridge(new Bridge());

    // Set up GPS
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    SettingsClient client = LocationServices.getSettingsClient(this);
    int locationPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION);
    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    locationAwareSingleton = LocationAwareSingleton.getInstance();
    locationAwareSingleton.getLocationAware().setGpsSettingsListener(this);
    LocationAwareSingleton.setLocationManager(locationManager);
    LocationAwareSingleton.setSettingsClient(client);
    LocationAwareSingleton.setLocationPermission(locationPermission);
    LocationAwareSingleton.setGeocoder(geocoder);

    // First stage
    if (!LocationAwareSingleton.isGpsTurnedOn()) {
      // GPS is turned off
      showDialogAskUserToTurnOnGps();
    } else {
      // GPS is turned on
      locationIsTurnedOnByUser();
    }
  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "Location Updates";
      String description = "Display the current location using GPS";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;

      NotificationChannel channel = new NotificationChannel(
              NOTIFICATION_CHANNEL_ID, name, importance);
      channel.setDescription(description);

      // Register the channel with the system
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(channel);
      }
    }
  }

  @Override
  public void onGpsLocationChanged(double latitude, double longitude) {
    String strLat = Double.toString(latitude);
    String strLon = Double.toString(longitude);
    String address = getAddressFromCoordinates(latitude, longitude);
    showLocationUpdateViaNotification(strLat, strLon, address);
  }

  private void showLocationUpdateViaNotification(
          String latitude, String longitude, String address) {

    NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(
            "Lat: " + latitude + "\nLon: " + longitude + "\nAddress: " + address);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
            NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif_location_24)
            .setContentTitle("Location Update")
            .setStyle(style)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    // Build the notification and display it
    NotificationManager notificationManager = (NotificationManager) getSystemService(
            Context.NOTIFICATION_SERVICE);
    notificationManager.notify(NOTIFICATION_ID, builder.build());
  }

  private String getAddressFromCoordinates(double latitude, double longitude) {
    Geocoder geocoder = LocationAwareSingleton.getGeocoder();
    List<Address> addresses = null;
    try {
      assert geocoder != null;
      addresses = geocoder.getFromLocation(latitude, longitude, 1);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (addresses == null) {
      Log.d("dev-log", "MainActivity.getAddressFromCoordinates: Addresses is " +
              "null");
      return "None";
    } else {
      return addresses.get(0).getAddressLine(0);
    }
  }

  @Override
  public void onGpsEnabled() {
    Log.d("dev-log", "MainActivity.onGpsEnabled: GPS is enabled");
  }

  @Override
  public void onGpsDisabled() {
    Log.d("dev-log", "MainActivity.onGpsDisabled: GPS is disabled");
    showDialogAskUserToTurnOnGps();
  }

  private void showDialogAskUserToTurnOnGps() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
    builder.setTitle("Turn on GPS");
    builder.setMessage("You need to turn on GPS in order for this to work");
    builder.setPositiveButton("TURN ON", (dialog, which) -> {
      Task<LocationSettingsResponse> task = locationAwareSingleton
              .getLocationAware().askUserToTurnOnGps();
      setUpSettingsClientTaskListener(task);
    });
    builder.setNegativeButton("CANCEL", (dialog, which) -> {
      Toast.makeText(this, "GPS is required", Toast.LENGTH_LONG).show();
      finish();
    });
    builder.setOnCancelListener(dialog -> finish());
    builder.show();
  }

  private void setUpSettingsClientTaskListener(Task<LocationSettingsResponse> task) {
    task.addOnFailureListener(this, e -> {
      if (e instanceof ResolvableApiException) {
        // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
        try {
          // Show the dialog by calling startResolutionForResult(), and check the result in
          // onActivityResult().
          ResolvableApiException resolvable = (ResolvableApiException) e;
          resolvable.startResolutionForResult(this,
                  AppConstants.LOCATION_PERMISSION_REQUEST_CODE);
        } catch (IntentSender.SendIntentException sendEx) {
          Log.w("dev-log", "MainActivity.checkLocationSettings: " + sendEx.getMessage());
        }
      }
    });
  }

  @Override
  @SuppressLint("MissingPermission")
  public void onRequestPermissionsResult(
          int requestCode,
          @NonNull String[] permissions,
          @NonNull int[] grantResults
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == AppConstants.LOCATION_PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        // User denied permission
        Toast.makeText(this,
                "Access to location is required", Toast.LENGTH_LONG).show();
        finish();
      } else {
        // User granted permission
        Log.d("dev-log", "MainActivity.onRequestPermissionsResult: " +
                "Location turned on and permission granted");
        launchSecondStage();
      }
    }
  }

  @Override
  @SuppressLint("MissingPermission")
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == AppConstants.LOCATION_PERMISSION_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        // User accepted request to turn on GPS
        locationIsTurnedOnByUser();
      } else {
        // User declined request to turn on GPS
        Toast.makeText(this, "GPS is required!", Toast.LENGTH_LONG).show();
        finish();
      }
    }
  }

  private void locationIsTurnedOnByUser() {
    if (LocationAwareSingleton.isLocationPermissionGranted()) {
      // GPS is turned on and permission is granted
      Log.d("dev-log", "MainActivity.locationIsTurnedOnByUser: GPS is turned on and " +
              "permission is granted");
      launchSecondStage();
    } else {
      // GPS is turned on however permission is not granted
      Log.d("dev-log", "MainActivity.locationIsTurnedOnByUser: GPS is turned on " +
              "however permission is not granted");
      ActivityCompat.requestPermissions(MainActivity.this,
              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              AppConstants.LOCATION_PERMISSION_REQUEST_CODE);
    }
  }

  private void launchSecondStage() {
    // Second stage
    if (!isSecondStageFinish) {
      LocationAwareSingleton
              .getInstance()
              .getLocationAware()
              .gpsIsOnSetUpLocationUpdateListener();

      setupBackPressedCallback();
      setContentView(R.layout.activity_main);
      isSecondStageFinish = true;
      fragmentChangeListener();
    }
  }

  public void requestUsbDevicePermission() {
    int flags = PendingIntent.FLAG_MUTABLE;
    PendingIntent pendingIntent;
    pendingIntent = PendingIntent.getBroadcast(this, 0,
            new Intent(AppConstants.INTENT_ACTION_GRANT_USB), flags
    );
    LauncherSingleton.getUsbManager().requestPermission(
            LauncherSingleton.getInstance()
                    .getLauncher()
                    .getUsbSerialDriver()
                    .getDevice(),
            pendingIntent
    );
  }

  private void setupBackPressedCallback() {
    // When the user presses the back button on their device, it will ask user if it really wants
    // to exit on its current screen
    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        switch (currentFragmentLabel) {
          case "devices_fragment" -> showExitDialogInDevicesFragment();
          case "terminal_fragment" -> showExitDialogInTerminalFragment();
          case "manual_arma_fragment" -> showExitDialogInManualArmaFragment();
          case "auto_arma_fragment" -> showExitDialogInAutoArmaFragment();
          case "hashes_fragment" -> showExitDialogInHashesFragment();
        }
      }
    };
    this.getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
  }

  private void fragmentChangeListener() {
    NavController navController = Navigation.findNavController(
            this, R.id.fragmentActivityMain);
    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
      String destLabel = (String) destination.getLabel();
      currentFragmentLabel = destLabel;
      Log.d("dev-log", "MainActivity.fragmentChangeListener: Current fragment is "
              + destLabel);
      countFragmentOnTheStack();
    });
  }

  private void countFragmentOnTheStack() {
    Fragment navHostFragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragmentActivityMain);
    assert navHostFragment != null;
    int backStackEntryCount = navHostFragment.getChildFragmentManager().getBackStackEntryCount();
    Log.i("dev-log", "MainActivity.countFragmentOnTheStack: " +
            backStackEntryCount + " fragment in the stack");
  }

  private void showExitDialogInDevicesFragment() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
      builder.setTitle("Exit App")
              .setMessage("You pressed the back button, do you want to leave the app?")
              .setPositiveButton("YES", (dialog, which) -> this.finish())
              .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();
  }

  private void showExitDialogInTerminalFragment() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
    builder.setTitle("Exit Terminal")
            .setMessage("You pressed the back button, do you want to leave the terminal?")
            .setPositiveButton("YES", (dialog, which) -> {
              NavController navController = Navigation.findNavController(
                      this, R.id.fragmentActivityMain);
              navController.popBackStack();
            })
            .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();
  }

  private void showExitDialogInManualArmaFragment() {
    NavController navController = Navigation.findNavController(this,
            R.id.fragmentActivityMain);
    navController.navigate(R.id.action_manualArmaFragment_to_exitModalBottomSheetDialog);
  }

  private void showExitDialogInHashesFragment() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
    builder.setTitle("Exit Database")
            .setMessage("You pressed the back button, do you want to leave the database?")
            .setPositiveButton("YES", (dialog, which) -> {
              NavController navController = Navigation.findNavController(
                      this, R.id.fragmentActivityMain);
              navController.popBackStack();
            })
            .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();
  }

  private void showExitDialogInAutoArmaFragment() {
    NavController navController = Navigation.findNavController(this,
            R.id.fragmentActivityMain);
    navController.navigate(R.id.action_autoArmaFragment_to_exitModalBottomSheetDialog);
  }

  @Override
  protected void onDestroy() {
    isSecondStageFinish = false;
    super.onDestroy();
  }
}