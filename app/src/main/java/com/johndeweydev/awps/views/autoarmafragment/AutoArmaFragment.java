package com.johndeweydev.awps.views.autoarmafragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentAutoArmaBinding;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;

public class AutoArmaFragment extends Fragment {

  private FragmentAutoArmaBinding binding;
  private AutoArmaArgs autoArmaArgs = null;
  private SessionViewModel sessionViewModel;


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    binding = FragmentAutoArmaBinding.inflate(inflater, container, false);

    if (getArguments() == null) {
      Log.d("dev-log", "AutoArmaFragment.onCreateView: Get arguments is null");
    } else {
      Log.d("dev-log", "AutoArmaFragment.onCreateView: Initializing fragment args");
      AutoArmaFragmentArgs autoArmaFragmentArgs;
      autoArmaFragmentArgs = AutoArmaFragmentArgs.fromBundle(getArguments());
      autoArmaArgs = autoArmaFragmentArgs.getAutoArmaArgs();
    }
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (autoArmaArgs == null) {
      Log.d("dev-log", "AutoArmaFragment.onViewCreated: Auto arma args is null");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
      return;
    }

    sessionViewModel.selectedArmament = autoArmaArgs.getSelectedArmament();

    binding.appBarLayoutAutoArma.setOnClickListener(v ->
            Navigation.findNavController(binding.getRoot()).popBackStack());

    setupSerialInputErrorListener();
    setupSerialOutputErrorListener();
  }

  private void setupSerialInputErrorListener() {
    final Observer<String> serialInputErrorObserver = s -> {
      if (s == null) {
        return;
      }
      sessionViewModel.currentSerialInputError.setValue(null);
      Log.d("dev-log", "AutoArmaFragment.setupSerialInputErrorListener: " +
              "Error on user input");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error writing " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "AutoArmaFragment.setupSerialInputErrorListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    };
    sessionViewModel.currentSerialInputError.observe(getViewLifecycleOwner(),
            serialInputErrorObserver);
  }

  private void setupSerialOutputErrorListener() {
    final Observer<String> serialOutputErrorObserver = s -> {
      if (s == null) {
        return;
      }
      sessionViewModel.currentSerialOutputError.setValue(null);
      Log.d("dev-log", "AutoArmaFragment.setupSerialOutputErrorListener: " +
              "Error on serial output");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "AutoArmaFragment.setupSerialOutputErrorListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    };
    sessionViewModel.currentSerialOutputError.observe(
            getViewLifecycleOwner(), serialOutputErrorObserver);

  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "AutoArmaFragment.onResume: Fragment resumed");
    Log.d("dev-log", "AutoArmaFragment.onResume: Connecting to device");
    connectToDevice();
  }

  private void connectToDevice() {
    int deviceId = autoArmaArgs.getDeviceId();
    int portNum = autoArmaArgs.getPortNum();
    String result = sessionViewModel.connectToDevice(
            19200, 8, 1, UsbSerialPort.PARITY_NONE, deviceId, portNum);

    if (result.equals("Successfully connected") || result.equals("Already connected")) {

      Log.d("dev-log",
              "AutoArmaFragment.connectToDevice: Starting event read");
      sessionViewModel.startEventDrivenReadFromDevice();
    } else if (result.equals("Failed to connect")) {

      Log.d("dev-log", "AutoArmaFragment.connectToDevice: " +
              "Failed to connect to the device");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      Log.d("dev-log", "AutoArmaFragment.connectToDevice: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    }
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "AutoArmaFragment.onPause: Fragment pausing");
    stopEventReadAndDisconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "AutoArmaFragment.onPause: Fragment paused");
  }

  private void stopEventReadAndDisconnectFromDevice() {
    Log.d("dev-log", "AutoArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Stopping event read");
    sessionViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "AutoArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Disconnecting from the device");
    sessionViewModel.disconnectFromDevice();
  }
}