package com.johndeweydev.himawhs;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.johndeweydev.himawhs.repository.UsbSerialRepository;
import com.johndeweydev.himawhs.viewmodels.UsbSerialViewModelFactory;
import com.johndeweydev.himawhs.viewmodels.UsbSerialViewModel;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    UsbSerialRepository usbSerialRepository = new UsbSerialRepository();
    UsbSerialViewModelFactory usbSerialViewModelFactory = new UsbSerialViewModelFactory(
            usbSerialRepository
    );
    new ViewModelProvider(this, usbSerialViewModelFactory).get(UsbSerialViewModel.class);

    setContentView(R.layout.activity_main);
    fragmentChangeListener();
  }

  private void fragmentChangeListener() {
    NavController navController = Navigation.findNavController(
            this, R.id.fragmentContainerView);
    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
      String destLabel = (String) destination.getLabel();
      String logText = "MainActivity.fragmentChangeListener: " +
              "Fragment destination changed to " + destLabel;

      Log.d("dev-log", logText);
      countFragmentOnTheStack();
    });
  }

  private void countFragmentOnTheStack() {
    Fragment navHostFragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragmentContainerView);
    assert navHostFragment != null;
    int backStackEntryCount = navHostFragment.getChildFragmentManager().getBackStackEntryCount();

    String logText = "MainActivity.countFragmentOnTheStack: " +
            "Current fragment back stack count is -> " + backStackEntryCount;

    Log.i("dev-log", logText);
  }
}