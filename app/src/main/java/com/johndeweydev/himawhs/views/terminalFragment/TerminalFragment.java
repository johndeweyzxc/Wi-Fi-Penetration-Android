package com.johndeweydev.himawhs.views.terminalFragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.johndeweydev.himawhs.BuildConfig;
import com.johndeweydev.himawhs.R;
import com.johndeweydev.himawhs.databinding.FragmentTerminalBinding;
import com.johndeweydev.himawhs.usbserial.UsbSerialOutputItem;
import com.johndeweydev.himawhs.viewmodels.UsbSerialViewModel;

public class TerminalFragment extends Fragment {

  private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
  private FragmentTerminalBinding binding;
  private UsbSerialViewModel usbSerialViewModel;
  private TerminalArgs args = null;
  private final BroadcastReceiver broadcastReceiver;
  private boolean usbPermissionDeniedByUser = false;
  private boolean usbBroadcastReceiverUnregistered = false;

  public class UsbBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
        if (usbSerialViewModel.hasUsbDevicePermission()) {
          Log.d("dev-log", "TerminalFragment.UsbBroadcastReceiver.checkPermission: " +
                  "Usb device permission granted");
        } else {
          Log.d("dev-log", "TerminalFragment.UsbBroadcastReceiver.checkPermission: " +
                  "Usb device permission denied by user");
          usbPermissionDeniedByUser = true;
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
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);

    if (getArguments() == null) {
      Log.d("dev-log", "TerminalFragment.onCreateView: No arguments found");
    } else {
      initializeTerminalFragmentArgs();
    }

    binding = FragmentTerminalBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  private void initializeTerminalFragmentArgs() {
    TerminalFragmentArgs terminalFragmentArgs = null;

    try {
      terminalFragmentArgs = TerminalFragmentArgs.fromBundle(getArguments());
    } catch (IllegalArgumentException e) {
      Log.d("dev-log", "onCreateView: " + e.getMessage());
    }
    if (terminalFragmentArgs != null) {
      args = terminalFragmentArgs.getTerminalArgs();
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (args != null) {
      setupRecyclerViewAndObserveData();
    }
    binding.topAppBarTerminal.setOnClickListener(v -> binding.drawerLayoutTerminal.open());
    binding.navMenuViewTerminal.setNavigationItemSelectedListener(this::navItemSelected);
    binding.commandExecuteTerminal.setOnClickListener(v -> writeDataToDevice());
    binding.readSerialOutputTerminal.setOnClickListener(v -> readDataFromDevice());
  }

  private void setupRecyclerViewAndObserveData() {
    Toast.makeText(requireActivity(), Integer.toString(args.getBaudRate()),
            Toast.LENGTH_SHORT
    ).show();

    TerminalAdapter terminalAdapter = new TerminalAdapter();
    binding.recyclerViewTerminal.setAdapter(terminalAdapter);
    LinearLayoutManager layout = new LinearLayoutManager(requireContext());
    layout.setStackFromEnd(true);
    binding.recyclerViewTerminal.setLayoutManager(layout);

    final Observer<UsbSerialOutputItem> serialOutputObserver;
    serialOutputObserver = this::handleNewSerialOutputFromLiveData;
    usbSerialViewModel.currentSerialMessage.observe(
            getViewLifecycleOwner(), serialOutputObserver
    );
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
      Toast.makeText(requireActivity(), "Device not connected", Toast.LENGTH_SHORT).show();
    } else {
      int textLengthOfCommandInput = binding.commandExecuteTerminal.getText().length();
      Log.d("dev-log", "Input length: " + textLengthOfCommandInput);
      if (textLengthOfCommandInput != 0) {
        usbSerialViewModel.writeDataToDevice(binding.commandInputTerminal.getText().toString());
      } else {
        Toast.makeText(requireActivity(), "Empty command", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void readDataFromDevice() {
    if (args == null) {
      Toast.makeText(requireActivity(), "Device not connected", Toast.LENGTH_SHORT).show();
    } else {
      usbSerialViewModel.readDataFromDevice();
    }
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "TerminalFragment.onResume: Fragment resumed");

    if (usbPermissionDeniedByUser) {
      Navigation.findNavController(binding.getRoot()).popBackStack();
      return;
    }

    if (args != null) {
      int deviceId = args.getDeviceId();
      int portNum = args.getPortNum();
      usbSerialViewModel.setTheDriverOfDevice(deviceId, portNum);

      if (usbSerialViewModel.hasUsbDevicePermission()) {
        int baudRate = args.getBaudRate();
        usbSerialViewModel.connectToDevice(portNum, baudRate);
      } else {

        Log.d("dev-log", "TerminalFragment.onResume: Requesting usb device permission");
        IntentFilter intentFilter = new IntentFilter(INTENT_ACTION_GRANT_USB);
        requireActivity().registerReceiver(broadcastReceiver, intentFilter);
        usbBroadcastReceiverUnregistered = false;
        usbSerialViewModel.requestUsbDeviceAccessPermission(
                requireActivity(), INTENT_ACTION_GRANT_USB
        );
      }
    }

  }

  @Override
  public void onPause() {

    if (args != null) {
      if (usbSerialViewModel.hasUsbDevicePermission()) {
        usbSerialViewModel.disconnectFromDevice();
        usbSerialViewModel.stopEventDrivenReadFromDevice();
      } else {
        unregisterUsbPermissionReceiver();
      }
    }
    super.onPause();
    Log.d("dev-log", "TerminalFragment.onPause: Fragment paused");
  }

  private void unregisterUsbPermissionReceiver() {
    if (!usbBroadcastReceiverUnregistered) {
      requireActivity().unregisterReceiver(broadcastReceiver);
      usbBroadcastReceiverUnregistered = true;
    }
  }

  private void handleNewSerialOutputFromLiveData(UsbSerialOutputItem usbSerialOutputItem) {
    // TODO: Implement this
  }
}