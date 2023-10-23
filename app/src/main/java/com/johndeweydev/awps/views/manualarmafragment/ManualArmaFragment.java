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
import com.johndeweydev.awps.usbserial.UsbSerialStatus;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;
import com.johndeweydev.awps.viewmodels.usbserialviewmodel.UsbSerialViewModel;

public class ManualArmaFragment extends Fragment {

  private FragmentManualArmaBinding binding;
  private ManualArmaArgs manualArmaArgs;
  private SessionViewModel sessionViewModel;
  private UsbSerialViewModel usbSerialViewModel;
  private ManualArmaRVAdapter manualArmaRVAdapter;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);
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
    setupErrorWriteListener();
    setupErrorOnNewDataListener();
  }

  private void changeUiIfArmaIsReconnaissance() {

  }

  private void setupRecyclerViewAndObserverData() {

  }

  private void setupErrorWriteListener() {
    final Observer<String> writeErrorListener = s -> {
      if (s == null) {
        return;
      }
      usbSerialViewModel.currentErrorInput.setValue(null);
      Log.d("dev-log", "ManualArmaFragment.setupErrorWriteListener: " +
              "Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "ManualArmaFragment.setupErrorWriteListener: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Error writing " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "ManualArmaFragment.setupErrorWriteListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    };
    usbSerialViewModel.currentErrorInput.observe(getViewLifecycleOwner(), writeErrorListener);
  }

  private void setupErrorOnNewDataListener() {
    final Observer<String> onNewDataErrorListener = s -> {
      if (s == null) {
        return;
      }
      usbSerialViewModel.currentErrorOnNewData.setValue(null);
      Log.d("dev-log", "ManualArmaFragment.setupErrorOnNewDataListener: " +
              "Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "ManualArmaFragment.setupErrorOnNewDataListener: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "ManualArmaFragment.setupErrorOnNewDataListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    };
    usbSerialViewModel.currentErrorOnNewData.observe(
            getViewLifecycleOwner(), onNewDataErrorListener);
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
    UsbSerialStatus status = usbSerialViewModel.connectToDevice(
            19200, 8, 1, UsbSerialPort.PARITY_NONE, deviceId, portNum);

    if (status.equals(UsbSerialStatus.SUCCESSFULLY_CONNECTED)
            || status.equals(UsbSerialStatus.ALREADY_CONNECTED)
    ) {
      Log.d("dev-log",
              "ManualArmaFragment.connectToDevice: Starting event read");
      usbSerialViewModel.startEventDrivenReadFromDevice();
    } else if (status.equals(UsbSerialStatus.FAILED_TO_CONNECT)) {
      Log.d("dev-log", "ManualArmaFragment.connectToDevice: Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "ManualArmaFragment.connectToDevice: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      Log.d("dev-log", "ManualArmaFragment.connectToDevice: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    }
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "ManualArmaFragment.onPause: Stopping event read");
    usbSerialViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "ManualArmaFragment.onPause: Disconnecting from the device");
    usbSerialViewModel.disconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "ManualArmaFragment.onPause: Fragment paused");
  }
}