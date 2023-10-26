package com.johndeweydev.awps.views.autoarmafragment.autoarmascreens.dashboard;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentAutoArmaDashboardBinding;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;
import com.johndeweydev.awps.viewmodels.usbserialviewmodel.UsbSerialViewModel;

public class AutoArmaDashboardFragment extends Fragment {

  private FragmentAutoArmaDashboardBinding binding;
  private MaterialButton buttonStartStop;
  private UsbSerialViewModel usbSerialViewModel;
  private SessionViewModel sessionViewModel;
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);
    sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    binding = FragmentAutoArmaDashboardBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setStartStopAttackLogic();
    setupObserverData();
  }

  private void setupObserverData() {


  }

  private void setStartStopAttackLogic() {
    buttonStartStop = binding.buttonStopStartAutoArmaDashboard;

    Drawable startIcon = ResourcesCompat.getDrawable(
            getResources(), R.drawable.ic_start_24, null);
    Drawable stopIcon = ResourcesCompat.getDrawable(
            getResources(), R.drawable.ic_stop_24, null);
    int colorRed = ResourcesCompat.getColor(getResources(), R.color.primaryRed, null);
    int colorPrimary = ResourcesCompat.getColor(getResources(), R.color.primary, null);

    buttonStartStop.setOnClickListener(v -> {

      if (buttonStartStop.getText().equals("Start")) {
        buttonStartStop.setText("Stop");
        buttonStartStop.setIcon(stopIcon);
        buttonStartStop.setBackgroundColor(colorRed);
      } else if (buttonStartStop.getText().equals("Stop")) {
        buttonStartStop.setText("Start");
        buttonStartStop.setIcon(startIcon);
        buttonStartStop.setBackgroundColor(colorPrimary);
      }
    });
  }
}