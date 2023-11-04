package com.johndeweydev.awps.views.devicesfragment;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.johndeweydev.awps.BuildConfig;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.data.DeviceConnectionParamData;
import com.johndeweydev.awps.data.UsbDeviceData;
import com.johndeweydev.awps.databinding.FragmentDevicesBinding;
import com.johndeweydev.awps.models.api.launcher.LauncherSingleton;
import com.johndeweydev.awps.viewmodels.terminalviewmodel.TerminalViewModel;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

import java.util.ArrayList;

public class DevicesFragment extends Fragment {

  public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
  private FragmentDevicesBinding binding;
  private TerminalViewModel terminalViewModel;
  private TerminalArgs terminalArgs = null;
  private BroadcastReceiver usbBroadcastReceiver;
  private boolean usbBroadcastReceiverRegistered = false;
  private boolean usbDevicePermissionGranted = false;

  private class UsbBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
          Log.d("dev-log", "DevicesFragment.onReceive: " +
                  "Usb device permission granted");
          Log.d("dev-log", "DevicesFragment.onReceive: Navigating to terminal fragment");
          navigateToTerminalFragment();
          usbDevicePermissionGranted = true;
        } else {
          Log.d("dev-log", "DevicesFragment.onReceive: " +
                  "Usb device permission denied by user");
          usbDevicePermissionGranted = false;
        }
      }
    }
  }

  public DevicesFragment() {
    usbBroadcastReceiver = new UsbBroadcastReceiver();
  }

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    binding = FragmentDevicesBinding.inflate(inflater, container, false);
    terminalViewModel = new ViewModelProvider(requireActivity()).get(TerminalViewModel.class);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.materialToolBarDevices.setNavigationOnClickListener(v ->
            binding.drawerLayoutDevices.open()
    );
    binding.materialToolBarDevices.setOnMenuItemClickListener(menuItem -> {
      if (menuItem.getItemId() == R.id.refreshMenuTopAppBarDevice) {
        findUsbDevices();
        return true;
      }
      return false;
    });
    binding.navigationViewTerminalMain.setNavigationItemSelectedListener(menuItem -> {
      if (menuItem.getItemId() == R.id.databaseMenuNavItemDevices) {

        binding.drawerLayoutDevices.close();
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_devicesFragment_to_hashesFragment);
        return true;
      } else if (menuItem.getItemId() == R.id.settingsMenuNavItemDevices) {
        binding.drawerLayoutDevices.close();

        // TODO: Navigate to settings fragment

        return true;
      } else if (menuItem.getItemId() == R.id.infoMenuNavItemsDevices) {
        binding.drawerLayoutDevices.close();

        // TODO: Navigate to information fragment

        return true;
      }
      return false;
    });

    DevicesRVAdapter devicesRVAdapter = setupRecyclerView();
    setupObservers(devicesRVAdapter);

    findUsbDevices();
  }

  private DevicesRVAdapter setupRecyclerView() {
    DevicesRvAdapterEvent devicesRvAdapterEvent;
    devicesRvAdapterEvent = terminalArgs -> {
      this.terminalArgs = terminalArgs;
      isUsbDevicePermissionGranted();
    };

    DevicesRVAdapter devicesRVAdapter = new DevicesRVAdapter(devicesRvAdapterEvent);
    binding.recyclerViewDevices.setAdapter(devicesRVAdapter);
    binding.recyclerViewDevices.setLayoutManager(new LinearLayoutManager(requireContext()));
    return devicesRVAdapter;
  }

  private void setupObservers(DevicesRVAdapter devicesRVAdapter) {
    final Observer<ArrayList<UsbDeviceData>> deviceListObserver = deviceList -> {
      if (deviceList == null) {
        return;
      }
      for (int i = 0; i < deviceList.size(); i++) {
        devicesRVAdapter.appendData(deviceList.get(i));
        devicesRVAdapter.notifyItemInserted(i);
      }
    };
    terminalViewModel.devicesList.observe(getViewLifecycleOwner(), deviceListObserver);
  }

  private void isUsbDevicePermissionGranted() {
    int deviceId = terminalArgs.getDeviceId();
    int portNum = terminalArgs.getPortNum();

    Log.d("dev-log",
            "DevicesFragment.isUsbDevicePermissionGranted: Connecting to the device");
    DeviceConnectionParamData deviceConnectionParamData = new DeviceConnectionParamData(
            19200, 8, 1, "PARITY_NONE", deviceId, portNum
    );
    String result = terminalViewModel.connectToDevice(deviceConnectionParamData);

    if (result.equals("No usb permission")) {

      Log.d("dev-log",
              "DevicesFragment.isUsbDevicePermissionGranted: Requesting usb device permission");
      requestUsbDevicePermission();
    } else if (result.equals("Successfully connected") || result.equals("Already connected")) {

      Log.d("dev-log",
              "DevicesFragment.isUsbDevicePermissionGranted: Navigating to terminal fragment");
      navigateToTerminalFragment();
    }
  }

  private void navigateToTerminalFragment() {
    DevicesFragmentDirections.ActionDevicesFragmentToTerminalFragment action;
    action = DevicesFragmentDirections.actionDevicesFragmentToTerminalFragment(terminalArgs);
    Navigation.findNavController(binding.getRoot()).navigate(action);
  }

  private void requestUsbDevicePermission() {
    int flags = PendingIntent.FLAG_MUTABLE;
    PendingIntent pendingIntent;
    pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0,
            new Intent(INTENT_ACTION_GRANT_USB), flags
    );
    LauncherSingleton.getUsbManager().requestPermission(
            LauncherSingleton.getInstance()
                    .getLauncher()
                    .getUsbSerialDriver()
                    .getDevice(),
            pendingIntent
    );
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "DevicesFragment.onResume: Fragment resumed");
    Log.d("dev-log", "DevicesFragment.onResume: Registering usb broadcast receiver");
    registerUsbBroadcastReceiver();
    terminalViewModel.setLauncherEventHandler();
  }

  private void findUsbDevices() {
    int size = terminalViewModel.getAvailableDevices();
    if (size == 0) {
      binding.textViewNoConnectedDevices.setVisibility(View.VISIBLE);
      Log.d("dev-log", "DevicesFragment.findUsbDevices: No devices connected");
    } else {
      binding.textViewNoConnectedDevices.setVisibility(View.GONE);
    }
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  private void registerUsbBroadcastReceiver() {
    if (terminalArgs != null) {
      if (!usbDevicePermissionGranted) {
        IntentFilter intentFilter = new IntentFilter(INTENT_ACTION_GRANT_USB);
        requireActivity().registerReceiver(usbBroadcastReceiver, intentFilter);
        usbBroadcastReceiverRegistered = true;
        Log.d("dev-log", "DevicesFragment.registerUsbBroadcastReceiver: " +
                "Registered usb broadcast receiver");
      }
    }
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "DevicesFragment.onPause: Unregistering usb broadcast receiver");
    unregisterUsbBroadcastReceiver();
    super.onPause();
    Log.d("dev-log", "DevicesFragment.onPause: Fragment paused");
  }

  private void unregisterUsbBroadcastReceiver() {
    if (terminalArgs != null) {
      if (usbBroadcastReceiverRegistered && !usbDevicePermissionGranted) {
        requireActivity().unregisterReceiver(usbBroadcastReceiver);
        usbBroadcastReceiverRegistered = false;
        Log.d("dev-log", "DevicesFragment.unregisterUsbBroadcastReceiver: " +
                "Unregistered usb broadcast receiver");
      }
    }
  }

  @Override
  public void onDestroyView() {
    usbBroadcastReceiver = null;
    binding = null;
    super.onDestroyView();
  }
}