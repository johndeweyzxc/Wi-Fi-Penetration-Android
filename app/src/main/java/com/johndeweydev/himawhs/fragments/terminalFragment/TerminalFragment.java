package com.johndeweydev.himawhs.fragments.terminalFragment;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.johndeweydev.himawhs.BuildConfig;
import com.johndeweydev.himawhs.CustomProber;
import com.johndeweydev.himawhs.R;
import com.johndeweydev.himawhs.databinding.FragmentTerminalBinding;
import com.johndeweydev.himawhs.models.SerialOutputModel;
import com.johndeweydev.himawhs.models.TerminalArgsModel;
import com.johndeweydev.himawhs.viewmodels.DefaultMainViewModel;

public class TerminalFragment extends Fragment {

  private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
  private FragmentTerminalBinding binding;
  private DefaultMainViewModel defaultMainViewModel;
  private TerminalAdapter terminalAdapter;
  private TerminalArgsModel args = null;
  private final BroadcastReceiver broadcastReceiver;
  private enum UsbPermission { Unknown, Requested, Granted, Denied }
  private UsbPermission usbPermission = UsbPermission.Unknown;
  private boolean isConnected = false;
  private UsbManager usbManager;
  private UsbSerialDriver usbSerialDriver;

  public class UsbBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
        usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                ? UsbPermission.Granted : UsbPermission.Denied;
        if (usbPermission == UsbPermission.Granted) {
          Log.d("dev-log", "TerminalFragment: Usb device permission granted");
          identifyDevice();
          requestDevicePermissionOrConnect();
        } else {
          Log.d("dev-log", "TerminalFragment: Usb device permission denied");
        }
      }
    }
  }

  public TerminalFragment() {
    broadcastReceiver = new UsbBroadcastReceiver();
  }

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    defaultMainViewModel = new ViewModelProvider(requireActivity()).get(DefaultMainViewModel.class);
    TerminalFragmentArgs terminalFragmentArgs = null;

    if (getArguments() != null) {
      try {
        terminalFragmentArgs = TerminalFragmentArgs.fromBundle(getArguments());
      } catch (IllegalArgumentException e) {
        Log.d("dev-log", "onCreateView: " + e.getMessage());
      }
      if (terminalFragmentArgs != null) {
        args = terminalFragmentArgs.getTerminalArgs();
      }
    }
    binding = FragmentTerminalBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (args != null) {
      Toast.makeText(requireActivity(), Integer.toString(args.getBaudRate()),
              Toast.LENGTH_SHORT).show();

      terminalAdapter = new TerminalAdapter();
      binding.recyclerViewTerminal.setAdapter(terminalAdapter);
      LinearLayoutManager layout = new LinearLayoutManager(requireContext());
      layout.setStackFromEnd(true);
      binding.recyclerViewTerminal.setLayoutManager(layout);

      final Observer<SerialOutputModel> serialOutputObserver;
      serialOutputObserver = this::handleNewSerialOutputFromLiveData;
      defaultMainViewModel.currentSerialMessage.observe(
              getViewLifecycleOwner(), serialOutputObserver);
    }

    binding.topAppBarTerminal.setOnClickListener(v -> binding.drawerLayoutTerminal.open());
    binding.navMenuViewTerminal.setNavigationItemSelectedListener(this::navItemSelected);
    binding.commandExecuteTerminal.setOnClickListener(v -> writeDataToDevice());
    binding.readSerialOutputTerminal.setOnClickListener(v -> readDataFromDevice());
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  @Override
  public void onResume() {
    super.onResume();

    requireActivity().registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));
    if(usbPermission == UsbPermission.Unknown || usbPermission == UsbPermission.Granted) {
      if (args != null && !isConnected) {
        identifyDevice();
        requestDevicePermissionOrConnect();
      }
    }
  }

  @Override
  public void onPause() {
    if (isConnected) {
      disconnectFromDevice();
      defaultMainViewModel.stopEventDrivenReadFromDevice();
    }

    requireActivity().unregisterReceiver(broadcastReceiver);
    super.onPause();
  }

  private void identifyDevice() {
    UsbDevice device = null;
    usbManager = (UsbManager) requireActivity().getSystemService(Context.USB_SERVICE);

    for(UsbDevice v : usbManager.getDeviceList().values()) {
      if(v.getDeviceId() == args.getDeviceId())
        device = v;
    }

    if(device == null) {
      Log.d("dev-log", "connectToDevice: Device not found");
      return;
    }

    usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(device);
    if(usbSerialDriver == null) {
      usbSerialDriver = CustomProber.getCustomProber().probeDevice(device);
    }

    if(usbSerialDriver == null) {
      Log.d("dev-log", "connectToDevice: Driver not found for device");
      return;
    }

    if(usbSerialDriver.getPorts().size() < args.getPortNum()) {
      Log.d("dev-log", "connectToDevice: Port not found for driver");
    }
  }

  private void requestDevicePermissionOrConnect() {

    if (usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(
            usbSerialDriver.getDevice())
    ) {
      usbPermission = UsbPermission.Requested;
      int flags = PendingIntent.FLAG_MUTABLE;
      PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(
              requireActivity(), 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
      usbManager.requestPermission(usbSerialDriver.getDevice(), usbPermissionIntent);
    } else {
      isConnected = defaultMainViewModel.connectToDevice(usbSerialDriver, usbManager,
              args.getPortNum(), args.getBaudRate());
      if (isConnected) {
        defaultMainViewModel.startEventDrivenReadFromDevice();
      }
    }
  }

  private void disconnectFromDevice() {
    isConnected = defaultMainViewModel.disconnectFromDevice();
  }

  private boolean navItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.devicesMenuNavItem) {
      binding.drawerLayoutTerminal.close();
      Navigation.findNavController(binding.getRoot())
              .navigate(R.id.action_terminalFragment_to_devicesFragment);
      return true;
    } else if (item.getItemId() == R.id.terminalMenuNavItem) {
      binding.drawerLayoutTerminal.close();
      return true;
    }
    return false;
  }

  private void writeDataToDevice() {
    if (args == null) {
      Toast.makeText(requireActivity(), "No device found", Toast.LENGTH_SHORT).show();
    } else {
      if (binding.commandExecuteTerminal.getText().length() != 0) {
        defaultMainViewModel.writeDataToDevice(binding.commandInputTerminal.getText().toString());
      }
    }
  }

  private void readDataFromDevice() {
    defaultMainViewModel.readDataFromDevice();
  }

  private void handleNewSerialOutputFromLiveData(SerialOutputModel serialOutputModel) {
    // TODO: Implement this
  }
}