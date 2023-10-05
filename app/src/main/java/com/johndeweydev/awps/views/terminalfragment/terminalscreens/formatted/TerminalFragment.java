package com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.johndeweydev.awps.databinding.FragmentTerminalBinding;
import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

public class TerminalFragment extends Fragment {

  private FragmentTerminalBinding binding;
  private UsbSerialViewModel usbSerialViewModel;
  private TerminalArgs terminalArgs = null;

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);

    Bundle argsFromBundle = getArguments();

    if (argsFromBundle == null) {
      Log.d("dev-log", "TerminalFragment.onCreateView: No arguments found");
    } else {
      initializeTerminalFragmentArgsFromBundle(argsFromBundle);
    }

    binding = FragmentTerminalBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  private void initializeTerminalFragmentArgsFromBundle(Bundle argsFromBundle) {

    try {
      terminalArgs = new TerminalArgs(
              argsFromBundle.getInt("deviceId"),
              argsFromBundle.getInt("portNum"),
              argsFromBundle.getInt("baudRate")
      );
    } catch (IllegalArgumentException e) {
      Log.d("dev-log", "TerminalFragment.initializeTerminalFragmentArgsFromBundle: " +
              e.getMessage());
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (terminalArgs != null) {
      setupRecyclerViewAndObserveData();
    }
    binding.commandExecuteTerminal.setOnClickListener(v -> writeDataToDevice());
    binding.readSerialOutputTerminal.setOnClickListener(v -> readDataFromDevice());
  }

  private void setupRecyclerViewAndObserveData() {
    TerminalRVAdapter terminalRVAdapter = new TerminalRVAdapter();
    binding.recyclerViewTerminal.setAdapter(terminalRVAdapter);
    LinearLayoutManager layout = new LinearLayoutManager(requireContext());
    layout.setStackFromEnd(true);
    binding.recyclerViewTerminal.setLayoutManager(layout);

    final Observer<UsbSerialOutputItem> serialOutputObserver;
    serialOutputObserver = this::handleNewSerialOutputFromLiveData;
    usbSerialViewModel.currentSerialMessage.observe(
            getViewLifecycleOwner(), serialOutputObserver
    );
  }

  private void writeDataToDevice() {
    if (terminalArgs == null) {
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
    if (terminalArgs == null) {
      Toast.makeText(requireActivity(), "Device not connected", Toast.LENGTH_SHORT).show();
    } else {
      usbSerialViewModel.readDataFromDevice();
    }
  }

  private void handleNewSerialOutputFromLiveData(UsbSerialOutputItem usbSerialOutputItem) {
    // TODO: Implement this
  }
}