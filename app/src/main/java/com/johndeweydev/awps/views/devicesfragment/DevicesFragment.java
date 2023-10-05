package com.johndeweydev.awps.views.devicesfragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentDevicesBinding;
import com.johndeweydev.awps.usbserial.UsbDeviceItem;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

import java.util.ArrayList;

public class DevicesFragment extends Fragment {
  private static FragmentDevicesBinding binding;
  private UsbSerialViewModel usbSerialViewModel;
  private DevicesRVAdapter devicesRVAdapter;
  private final BroadcastReceiver usbBroadcastReceiver;
  private static TerminalArgs terminalArgs = null;
  private static FragmentActivity fragmentActivity;
  private boolean usbBroadcastReceiverRegistered = false;
  public static void setTerminalArgs(TerminalArgs terminalArgs) {
    DevicesFragment.terminalArgs = terminalArgs;
  }

  public static void requestUsbPermission() {
    int deviceId = terminalArgs.getDeviceId();
    int portNum = terminalArgs.getPortNum();
    UsbSerialViewModel.setTheDriverOfDevice(deviceId, portNum);

    if (UsbSerialViewModel.hasUsbDevicePermission()) {
      navigateToTerminalFragment();
    } else {
      UsbSerialViewModel.requestUsbDeviceAccessPermission(
              fragmentActivity, UsbBroadcastReceiver.INTENT_ACTION_GRANT_USB);
    }

  }

  private static void navigateToTerminalFragment() {
    DevicesFragmentDirections.ActionDevicesFragmentToTerminalFragment action;
    action = DevicesFragmentDirections.actionDevicesFragmentToTerminalFragment(terminalArgs);
    Navigation.findNavController(binding.getRoot()).navigate(action);
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

    fragmentActivity = requireActivity();
    devicesRVAdapter = new DevicesRVAdapter();
    binding.recyclerViewDevices.setAdapter(devicesRVAdapter);
    binding.recyclerViewDevices.setLayoutManager(new LinearLayoutManager(requireContext()));

    final Observer<ArrayList<UsbDeviceItem>> deviceListObserver = this::handleUpdateFromLivedata;
    usbSerialViewModel.devicesList.observe(getViewLifecycleOwner(), deviceListObserver);
    findUsbDevices();

    binding.appBarDevices.setNavigationOnClickListener(v -> binding.drawerLayoutDevices.open());
    binding.appBarDevices.setOnMenuItemClickListener(this::topAppBarNavItemSelected);
    binding.navMenuViewTerminalViewPager.setNavigationItemSelectedListener(this::navItemSelected);
  }

  private void handleUpdateFromLivedata(ArrayList<UsbDeviceItem> usbDeviceItemList) {
    for (int i = 0; i < usbDeviceItemList.size(); i++) {
      devicesRVAdapter.appendData(usbDeviceItemList.get(i));
      devicesRVAdapter.notifyItemInserted(i);
    }
  }

  private void findUsbDevices() {
    int size = usbSerialViewModel.checkAvailableUsbDevices(requireActivity(), Context.USB_SERVICE);
    if (size == 0) {
      binding.textViewNoDevices.setVisibility(View.VISIBLE);
      Log.d("dev-log", "DevicesFragment.findUsbDevices: No devices connected");
    } else {
      binding.textViewNoDevices.setVisibility(View.GONE);
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

      // TODO: Navigate to settings

      return true;
    } else if (item.getItemId() == R.id.infoMenuNavItemsDevices) {
      binding.drawerLayoutDevices.close();

      // TODO: Navigate to information

      return true;
    }
    return false;
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "DevicesFragment.onResume: Fragment resumed");

    if (terminalArgs != null) {
      if (!UsbSerialViewModel.hasUsbDevicePermission()) {
        IntentFilter intentFilter = new IntentFilter(UsbBroadcastReceiver.INTENT_ACTION_GRANT_USB);
        requireActivity().registerReceiver(usbBroadcastReceiver, intentFilter);
        usbBroadcastReceiverRegistered = true;
        Log.d("dev-log", "DevicesFragment.onResume: Usb broadcast receiver registered");
      }
    }
  }

  @Override
  public void onPause() {
    if (terminalArgs != null) {
      if (!UsbSerialViewModel.hasUsbDevicePermission() && usbBroadcastReceiverRegistered) {
        requireActivity().unregisterReceiver(usbBroadcastReceiver);
        Log.d("dev-log", "DevicesFragment.onResume: Usb broadcast receiver unregistered");
        usbBroadcastReceiverRegistered = false;
      }
    }

    super.onPause();
    Log.d("dev-log", "DevicesFragment.onPause: Fragment paused");
  }
}