package com.johndeweydev.awps.views.devicesfragment;

import static com.johndeweydev.awps.usbserial.UsbSerialStatus.NO_USB_PERMISSION;
import static com.johndeweydev.awps.usbserial.UsbSerialStatus.SUCCESSFULLY_CONNECTED;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.johndeweydev.awps.BuildConfig;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentDevicesBinding;
import com.johndeweydev.awps.repository.UsbDeviceModel;
import com.johndeweydev.awps.usbserial.UsbSerialMainSingleton;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

import java.util.ArrayList;

public class DevicesFragment extends Fragment {

  public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
  private FragmentDevicesBinding binding;
  private UsbSerialViewModel usbSerialViewModel;
  private DevicesRVAdapter devicesRVAdapter;
  private TerminalArgs terminalArgs = null;
  private final BroadcastReceiver usbBroadcastReceiver;
  private boolean usbBroadcastReceiverRegistered = false;
  private boolean usbDevicePermissionGranted = false;

  private class UsbBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
          Log.d("dev-log", "DevicesFragment.onPermissionGrant.checkPermission: " +
                  "Usb device permission granted");
          navigateToTerminalFragment();
          usbDevicePermissionGranted = true;
        } else {
          Log.d("dev-log", "DevicesFragment.onPermissionDenied: " +
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
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    DevicesRVAdapter.RVAdapterCallback onDeviceClickCallback;
    onDeviceClickCallback = terminalArgs -> {
      this.terminalArgs = terminalArgs;
      isUsbDevicePermissionGranted();
    };

    devicesRVAdapter = new DevicesRVAdapter(onDeviceClickCallback);
    binding.recyclerViewDevices.setAdapter(devicesRVAdapter);
    binding.recyclerViewDevices.setLayoutManager(new LinearLayoutManager(requireContext()));

    final Observer<ArrayList<UsbDeviceModel>> deviceListObserver = this::handleUpdateFromLivedata;
    usbSerialViewModel.devicesList.observe(getViewLifecycleOwner(), deviceListObserver);
    findUsbDevices();

    binding.materialToolBarDevices.setNavigationOnClickListener(
            v -> binding.drawerLayoutDevices.open());
    binding.materialToolBarDevices.setOnMenuItemClickListener(this::topAppBarNavItemSelected);
    binding.navigationViewTerminalMain.setNavigationItemSelectedListener(
            this::navItemSelected);
  }

  private void isUsbDevicePermissionGranted() {
    int deviceId = terminalArgs.getDeviceId();
    int portNum = terminalArgs.getPortNum();

    UsbSerialStatus result = usbSerialViewModel.connectToDevice(
            19200, 8, 1, UsbSerialPort.PARITY_NONE, deviceId, portNum
    );

    if (result.equals(NO_USB_PERMISSION)) {
      requestUsbDevicePermission();
    } else if (result.equals(SUCCESSFULLY_CONNECTED)) {
      navigateToTerminalFragment();
    }
  }

  private void requestUsbDevicePermission() {
    int flags = PendingIntent.FLAG_MUTABLE;
    PendingIntent pendingIntent;
    pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0,
            new Intent(INTENT_ACTION_GRANT_USB), flags
    );
    UsbSerialMainSingleton.getUsbManager().requestPermission(
            UsbSerialMainSingleton.getInstance()
                    .getUsbSerialMain()
                    .getUsbSerialDriver()
                    .getDevice(),
            pendingIntent
    );
  }

  private void navigateToTerminalFragment() {
    DevicesFragmentDirections.ActionDevicesFragmentToTerminalFragment action;
    action = DevicesFragmentDirections.actionDevicesFragmentToTerminalFragment(terminalArgs);
    Navigation.findNavController(binding.getRoot()).navigate(action);
  }

  private void handleUpdateFromLivedata(ArrayList<UsbDeviceModel> usbDeviceModelList) {
    for (int i = 0; i < usbDeviceModelList.size(); i++) {
      devicesRVAdapter.appendData(usbDeviceModelList.get(i));
      devicesRVAdapter.notifyItemInserted(i);
    }
  }

  private void findUsbDevices() {
    int size = usbSerialViewModel.checkAvailableUsbDevices();
    if (size == 0) {
      binding.textViewNoConnectedDevices.setVisibility(View.VISIBLE);
      Log.d("dev-log", "DevicesFragment.findUsbDevices: No devices connected");
    } else {
      binding.textViewNoConnectedDevices.setVisibility(View.GONE);
    }
  }


  private boolean topAppBarNavItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.refreshMenuTopAppBarDevice) {
      findUsbDevices();
      return true;
    }
    return false;
  }

  private boolean navItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.settingsMenuNavItemDevices) {
      binding.drawerLayoutDevices.close();

      // TODO: Navigate to settings fragment

      return true;
    } else if (item.getItemId() == R.id.infoMenuNavItemsDevices) {
      binding.drawerLayoutDevices.close();

      // TODO: Navigate to information fragment

      return true;
    }
    return false;
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "DevicesFragment.onResume: Fragment resumed");
    registerUsbBroadcastReceiver();
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  private void registerUsbBroadcastReceiver() {
    if (terminalArgs != null) {
      if (!usbDevicePermissionGranted) {
        IntentFilter intentFilter = new IntentFilter(INTENT_ACTION_GRANT_USB);
        requireActivity().registerReceiver(usbBroadcastReceiver, intentFilter);
        usbBroadcastReceiverRegistered = true;
        Log.d("dev-log", "DevicesFragment.registerUsbBroadcastReceiver: " +
                "Usb broadcast receiver registered");
      }
    }
  }

  @Override
  public void onPause() {
    unregisterUsbBroadcastReceiver();
    super.onPause();
    Log.d("dev-log", "DevicesFragment.onPause: Fragment paused");
  }

  private void unregisterUsbBroadcastReceiver() {
    if (terminalArgs != null) {
      if (usbBroadcastReceiverRegistered && !usbDevicePermissionGranted) {
        requireActivity().unregisterReceiver(usbBroadcastReceiver);
        Log.d("dev-log", "DevicesFragment.unregisterUsbBroadcastReceiver: " +
                "Usb broadcast receiver unregistered");
        usbBroadcastReceiverRegistered = false;
      }
    }
  }
}