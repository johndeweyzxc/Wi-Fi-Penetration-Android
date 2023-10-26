package com.johndeweydev.awps.views.manualarmafragment;

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
import com.johndeweydev.awps.databinding.FragmentManualArmaBinding;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;

public class ManualArmaFragment extends Fragment {

  private FragmentManualArmaBinding binding;
  private ManualArmaArgs manualArmaArgs;
  private SessionViewModel sessionViewModel;
  private ManualArmaRVAdapter manualArmaRVAdapter;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    binding = FragmentManualArmaBinding.inflate(inflater, container, false);

    if (getArguments() == null) {
      throw new NullPointerException("getArguments is null");
    } else {
      Log.d("dev-log", "ManualArmaFragment.onCreateView: Initializing fragment args");
      initializeManualArmaFragmentArgs();
    }
    return binding.getRoot();
  }

  private void initializeManualArmaFragmentArgs() {
    ManualArmaFragmentArgs manualArmaFragmentArgs;
    manualArmaFragmentArgs = ManualArmaFragmentArgs.fromBundle(getArguments());
    manualArmaArgs = manualArmaFragmentArgs.getManualArmaArgs();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (manualArmaArgs == null) {
      throw new NullPointerException("terminalArgs is null");
    }

    binding.materialToolBarManualArma.setOnClickListener(v -> {
      Navigation.findNavController(binding.getRoot()).popBackStack();
    });

    binding.textInputEditTextMacAddressManualArma.setOnClickListener(v -> {

    });

    setupRecyclerViewAndObserverData();
    setupSerialInputErrorListener();
    setupSerialOutputErrorListener();
  }

  private void setupRecyclerViewAndObserverData() {

  }

  private void setupSerialInputErrorListener() {
    final Observer<String> serialInputErrorObserver = s -> {
      if (s == null) {
        return;
      }
      sessionViewModel.currentSerialInputError.setValue(null);
      Log.d("dev-log", "ManualArmaFragment.setupSerialInputErrorListener: " +
              "Error on user input");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error writing " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "ManualArmaFragment.setupSerialInputErrorListener: " +
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
      Log.d("dev-log", "ManualArmaFragment.setupSerialOutputErrorListener: " +
              "Error on serial output");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "ManualArmaFragment.setupSerialOutputErrorListener: " +
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
    Log.d("dev-log", "ManualArmaFragment.onResume: Fragment resumed");
    Log.d("dev-log", "ManualArmaFragment.onResume: Connecting to device");
    connectToDevice();
  }

  private void connectToDevice() {
    if (manualArmaArgs == null) {
      throw new NullPointerException("terminalArgs is null");
    }

    int deviceId = manualArmaArgs.getDeviceId();
    int portNum = manualArmaArgs.getPortNum();
    String result = sessionViewModel.connectToDevice(
            19200, 8, 1, UsbSerialPort.PARITY_NONE, deviceId, portNum);

    if (result.equals("Successfully connected") || result.equals("Already connected")) {

      Log.d("dev-log",
              "ManualArmaFragment.connectToDevice: Starting event read");
      sessionViewModel.startEventDrivenReadFromDevice();
    } else if (result.equals("Failed to connect")) {

      Log.d("dev-log", "ManualFragment.connectToDevice: Failed to connect to the device");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      Log.d("dev-log", "ManualArmaFragment.connectToDevice: " +
              "Popping all fragments but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    }
  }

  private void stopEventReadAndDisconnectFromDevice() {
    Log.d("dev-log", "ManualArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Stopping event read");
    sessionViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "ManualArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Disconnecting from the device");
    sessionViewModel.disconnectFromDevice();
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "ManualArmaFragment.onPause: Stopping event read");
    sessionViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "ManualArmaFragment.onPause: Disconnecting from the device");
    sessionViewModel.disconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "ManualArmaFragment.onPause: Fragment paused");
  }
}