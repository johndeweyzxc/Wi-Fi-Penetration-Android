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
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentAutoArmaMainBinding;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;
import com.johndeweydev.awps.viewmodels.usbserialviewmodel.UsbSerialViewModel;
import com.johndeweydev.awps.views.autoarmafragment.autoarmascreens.dashboard.AutoArmaDashboardFragment;
import com.johndeweydev.awps.views.autoarmafragment.autoarmascreens.logs.AutoArmaLogsFragment;

import java.util.ArrayList;

public class AutoArmaMainFragment extends Fragment {

  private FragmentAutoArmaMainBinding binding;
  private SessionViewModel sessionViewModel;
  private UsbSerialViewModel usbSerialViewModel;
  private AutoArmaArgs autoArmaArgs;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);
    binding = FragmentAutoArmaMainBinding.inflate(inflater, container, false);

    if (getArguments() == null) {
      throw new NullPointerException("getArguments is null");
    } else {
      Log.d("dev-log", "AutoArmaMainFragment.onCreateView: Initializing fragment args");
      initializeAutoArmaMainFragmentArgs();
    }
    return binding.getRoot();
  }

  private void initializeAutoArmaMainFragmentArgs() {
    AutoArmaMainFragmentArgs autoArmaMainFragmentArgs;
    autoArmaMainFragmentArgs = AutoArmaMainFragmentArgs.fromBundle(getArguments());
    autoArmaArgs = autoArmaMainFragmentArgs.getAutoArmaArgs();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (autoArmaArgs == null) {
      // TODO: Replace NPE, instead show an error message and pop this fragment
      throw new NullPointerException("terminalArgs is null");
    }

    initializeViewPager();

    binding.appBarAutoArmaMain.setOnClickListener(v -> {
      Navigation.findNavController(binding.getRoot()).popBackStack();
    });

    setupErrorWriteListener();
    setupErrorOnNewDataListener();
  }

  private void initializeViewPager() {
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    AutoArmaDashboardFragment autoArmaDashboardFragment = new AutoArmaDashboardFragment();
    fragmentList.add(autoArmaDashboardFragment);

    AutoArmaLogsFragment autoArmaLogsFragment = new AutoArmaLogsFragment();
    fragmentList.add(autoArmaLogsFragment);

    FragmentStateAdapter adapter = new AutoArmaMainVPAdapter(
            fragmentList, getChildFragmentManager(), getLifecycle());

    binding.viewPagerAutoArmaViewPager.setAdapter(adapter);
  }

  private void setupErrorOnNewDataListener() {
    final Observer<String> onNewDataErrorListener = s -> {
      if (s == null) {
        return;
      }
      usbSerialViewModel.currentErrorOnNewData.setValue(null);
      Log.d("dev-log", "AutoArmaMainFragment.setupErrorOnNewDataListener: " +
              "Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "AutoArmaMainFragment.setupErrorOnNewDataListener: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "AutoArmaMainFragment.setupErrorOnNewDataListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    };
    usbSerialViewModel.currentErrorOnNewData.observe(
            getViewLifecycleOwner(), onNewDataErrorListener);

  }

  private void setupErrorWriteListener() {
    final Observer<String> writeErrorListener = s -> {
      if (s == null) {
        return;
      }
      usbSerialViewModel.currentErrorInput.setValue(null);
      Log.d("dev-log", "AutoArmaMainFragment.setupErrorWriteListener: " +
              "Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "AutoArmaMainFragment.setupErrorWriteListener: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Error writing " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "AutoArmaMainFragment.setupErrorWriteListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    };
    usbSerialViewModel.currentErrorInput.observe(getViewLifecycleOwner(), writeErrorListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "AutoArmaMainFragment.onResume: Fragment resumed");
    Log.d("dev-log", "AutoArmaMainFragment.onResume: Connecting to device");
    connectToDevice();
  }

  private void connectToDevice() {
    if (autoArmaArgs == null) {
      // TODO: Replace NPE, instead show an error message and pop this fragment
      throw new NullPointerException("terminalArgs is null");
    }

    int deviceId = autoArmaArgs.getDeviceId();
    int portNum = autoArmaArgs.getPortNum();
    UsbSerialStatus status = usbSerialViewModel.connectToDevice(
            19200, 8, 1, UsbSerialPort.PARITY_NONE, deviceId, portNum);

    if (status.equals(UsbSerialStatus.SUCCESSFULLY_CONNECTED)
            || status.equals(UsbSerialStatus.ALREADY_CONNECTED)
    ) {
      Log.d("dev-log",
              "AutoArmaMainFragment.connectToDevice: Starting event read");
      usbSerialViewModel.startEventDrivenReadFromDevice();
    } else if (status.equals(UsbSerialStatus.FAILED_TO_CONNECT)) {
      Log.d("dev-log", "AutoArmaMainFragment.connectToDevice: Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "AutoArmaMainFragment.connectToDevice: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      Log.d("dev-log", "AutoArmaMainFragment.connectToDevice: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaMainFragment_to_devicesFragment);
    }
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "AutoArmaMainFragment.onPause: Stopping event read");
    usbSerialViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "AutoArmaMainFragment.onPause: Disconnecting from the device");
    usbSerialViewModel.disconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "AutoArmaMainFragment.onPause: Fragment paused");
  }
}