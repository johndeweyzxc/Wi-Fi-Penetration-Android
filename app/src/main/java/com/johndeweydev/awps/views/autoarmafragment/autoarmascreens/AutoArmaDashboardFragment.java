package com.johndeweydev.awps.views.autoarmafragment.autoarmascreens;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentAutoArmaDashboardBinding;

public class AutoArmaDashboardFragment extends Fragment {

  private FragmentAutoArmaDashboardBinding binding;
  private MaterialButton buttonStartStop;
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentAutoArmaDashboardBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
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