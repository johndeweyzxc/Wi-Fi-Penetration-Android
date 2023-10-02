package com.johndeweydev.himawhs;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.johndeweydev.himawhs.repository.DefaultMainRepository;
import com.johndeweydev.himawhs.viewmodels.DefaultMainViewModel;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DefaultMainRepository defaultMainRepository = new DefaultMainRepository();
    MainViewModelFactory mainViewModelFactory = new MainViewModelFactory(defaultMainRepository);
    new ViewModelProvider(this, mainViewModelFactory).get(DefaultMainViewModel.class);

    setContentView(R.layout.activity_main);
    fragmentChangeListener();
  }

  private void fragmentChangeListener() {
    NavController navController = Navigation.findNavController(
            this, R.id.fragmentContainerView);
    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
      String destLabel = (String) destination.getLabel();
      String logText = "fragmentChangeListener: Fragment destination changed to " + destLabel;

      Log.d("dev-log", logText);
      countFragmentOnTheStack();
    });
  }

  private void countFragmentOnTheStack() {
    Fragment navHostFragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragmentContainerView);
    assert navHostFragment != null;
    int backStackEntryCount = ((NavHostFragment) navHostFragment)
            .getChildFragmentManager().getBackStackEntryCount();

    String logText = "countFragmentOnTheStack: Current fragment back stack count is -> " +
            backStackEntryCount;

    Log.i("dev-log", logText);
  }
}