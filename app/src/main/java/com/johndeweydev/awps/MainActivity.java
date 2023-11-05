package com.johndeweydev.awps;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoDatabase;
import com.johndeweydev.awps.models.api.hashinfo.HashInfoSingleton;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.models.repo.serial.terminalreposerial.TerminalRepoSerial;
import com.johndeweydev.awps.viewmodels.terminalviewmodel.TerminalViewModel;
import com.johndeweydev.awps.viewmodels.terminalviewmodel.TerminalViewModelFactory;

public class MainActivity extends AppCompatActivity {

  private String currentFragmentLabel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    LauncherSingleton.setUsbManager(usbManager);

    TerminalRepoSerial terminalRepoSerial = new TerminalRepoSerial();
    TerminalViewModelFactory terminalViewModelFactory = new TerminalViewModelFactory(
            terminalRepoSerial);
    new ViewModelProvider(this, terminalViewModelFactory).get(TerminalViewModel.class);

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

    setContentView(R.layout.activity_main);
    fragmentChangeListener();

    HashInfoSingleton hashInfoSingleton = HashInfoSingleton.getInstance();
    HashInfoDatabase hashInfoDatabase = Room.databaseBuilder(getApplicationContext(),
            HashInfoDatabase.class, "awps_database").build();
    hashInfoSingleton.setHashInfoDatabase(hashInfoDatabase);

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