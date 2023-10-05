package com.johndeweydev.awps.views.terminalfragment.terminalscreens.raw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.johndeweydev.awps.databinding.FragmentTerminalRawBinding;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted.TerminalRVAdapter;

public class TerminalRawFragment extends Fragment {

  private FragmentTerminalRawBinding binding;
  private UsbSerialViewModel usbSerialViewModel;
  private TerminalArgs terminalArgs;

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
      Log.d("dev-log", "TerminalRawFragment.onCreateView: No arguments found");
    } else {
      initializeTerminalFragmentArgsFromBundle(argsFromBundle);
    }

    binding = FragmentTerminalRawBinding.inflate(inflater, container, false);
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
      Log.d("dev-log", "TerminalRawFragment.initializeTerminalFragmentArgsFromBundle: " +
              e.getMessage());
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (terminalArgs != null) {
      setupRecyclerViewAndObserveData();
    }

    binding.commandExecuteTerminalRaw.setOnClickListener(v -> writeDataToDevice());
    binding.readSerialOutputTerminalRaw.setOnClickListener(v -> readDataFromDevice());
  }

  private void setupRecyclerViewAndObserveData() {
    TerminalRVAdapter terminalRVAdapter = new TerminalRVAdapter();
    binding.recyclerViewTerminalRaw.setAdapter(terminalRVAdapter);
    LinearLayoutManager layout = new LinearLayoutManager(requireContext());
    layout.setStackFromEnd(true);
    binding.recyclerViewTerminalRaw.setLayoutManager(layout);

    // TODO: Setup observer for raw data
  }

  private void writeDataToDevice() {
    if (terminalArgs == null) {
      Toast.makeText(requireActivity(), "Device not connected", Toast.LENGTH_SHORT).show();
    } else {
      int textLengthOfCommandInput = binding.commandExecuteTerminalRaw.getText().length();
      if (textLengthOfCommandInput != 0) {
        usbSerialViewModel.writeDataToDevice(binding.commandInputTerminalRaw.getText().toString());
      } else {
        Toast.makeText(requireActivity(), "Empty command", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void readDataFromDevice() {
    // TODO: Read raw data
  }
}
