package com.johndeweydev.awps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.johndeweydev.awps.models.api.bridge.Bridge;
import com.johndeweydev.awps.models.api.bridge.BridgeSingleton;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoDatabase;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoSingleton;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.models.repo.serial.terminalreposerial.TerminalRepoSerial;
import com.johndeweydev.awps.viewmodels.serial.terminalviewmodel.TerminalViewModel;
import com.johndeweydev.awps.viewmodels.serial.terminalviewmodel.TerminalViewModelFactory;

public class MainActivity extends AppCompatActivity {

  private String currentFragmentLabel;
  public final static int LOCATION_PERMISSION_REQUEST_CODE = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    LauncherSingleton.setUsbManager(usbManager);

    TerminalRepoSerial terminalRepoSerial = new TerminalRepoSerial();
    TerminalViewModelFactory terminalViewModelFactory = new TerminalViewModelFactory(
            terminalRepoSerial);
    new ViewModelProvider(this, terminalViewModelFactory).get(TerminalViewModel.class);

    setupBackPressedCallback();

    setContentView(R.layout.activity_main);
    fragmentChangeListener();

    HashInfoSingleton hashInfoSingleton = HashInfoSingleton.getInstance();
    HashInfoDatabase hashInfoDatabase = Room.databaseBuilder(getApplicationContext(),
            HashInfoDatabase.class, "awps_database").build();
    hashInfoSingleton.setHashInfoDatabase(hashInfoDatabase);

    BridgeSingleton bridgeSingleton = BridgeSingleton.getInstance();
    bridgeSingleton.setBridge(new Bridge());
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

  @Override
  public void onRequestPermissionsResult(
          int requestCode,
          @NonNull String[] permissions,
          @NonNull int[] grantResults
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Access to location is required", Toast.LENGTH_LONG)
                .show();

        // This either pops the manual arma fragment or the auto arma fragment, because those
        // fragment are the ones that ask for the GPS feature
        Navigation.findNavController(this, R.id.fragmentActivityMain)
                .popBackStack();
      } else {
        Log.d("dev-log", "MainActivity.onRequestPermissionsResult: Access to location " +
                "permission granted");
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
      if (resultCode == RESULT_OK) {
        Log.d("dev-log", "MainActivity.onActivityResult: Location turned on, request " +
                "for permission to access location");
        requestForLocationPermission();
      } else {
        Toast.makeText(this, "Location is required", Toast.LENGTH_LONG).show();

        // This either pops the manual arma fragment or the auto arma fragment, because those
        // fragment are the ones that ask for the GPS feature
        Navigation.findNavController(this, R.id.fragmentActivityMain)
                .popBackStack();
      }
    }
  }

  private void requestForLocationPermission() {
    int locationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION);

    if (locationPermission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(MainActivity.this,
              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              LOCATION_PERMISSION_REQUEST_CODE);
    }
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
    NavController navController = Navigation.findNavController(
            this, R.id.fragmentActivityMain);
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
    // TODO: Provide implementation
  }
}