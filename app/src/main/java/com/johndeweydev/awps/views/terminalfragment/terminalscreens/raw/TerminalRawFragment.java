package com.johndeweydev.awps.views.terminalfragment.terminalscreens.raw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.johndeweydev.awps.databinding.FragmentTerminalRawBinding;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.viewmodels.terminalviewmodel.TerminalViewModel;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

public class TerminalRawFragment extends Fragment {

  private FragmentTerminalRawBinding binding;
  private TerminalViewModel terminalViewModel;
  private TerminalRawRVAdapter terminalRawRVAdapter;
  private TerminalArgs terminalArgs;

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    terminalViewModel = new ViewModelProvider(requireActivity()).get(TerminalViewModel.class);
    binding = FragmentTerminalRawBinding.inflate(inflater, container, false);

    Bundle argsFromBundle = getArguments();

    if (argsFromBundle == null) {
      throw new NullPointerException("getArguments is null");
    } else {
      initializeTerminalFragmentArgsFromBundle(argsFromBundle);
    }

    return binding.getRoot();
  }

  private void initializeTerminalFragmentArgsFromBundle(Bundle argsFromBundle) {
    terminalArgs = new TerminalArgs(
            argsFromBundle.getInt("deviceId"),
            argsFromBundle.getInt("portNum"),
            argsFromBundle.getInt("baudRate")
    );
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (terminalArgs == null) {
      throw new NullPointerException("terminalArgs is null");
    }

    setupRecyclerViewAndObserveData();

    binding.buttonCommandWriteTerminalRaw.setOnClickListener(v -> {
      String data = binding.editTextCommandInputTerminalRaw.getText().toString();
      writeDataToDevice(data);
    });
  }

  private void setupRecyclerViewAndObserveData() {
    terminalRawRVAdapter = new TerminalRawRVAdapter();
    binding.recyclerViewTerminalRaw.setAdapter(terminalRawRVAdapter);
    LinearLayoutManager layout = new LinearLayoutManager(requireContext());
    layout.setStackFromEnd(true);
    binding.recyclerViewTerminalRaw.setLayoutManager(layout);

    final Observer<LauncherOutputData> serialOutputItemObserver;
    serialOutputItemObserver = this::handleNewSerialOutputFromLiveData;
    terminalViewModel.currentMessageRaw.observe(
            getViewLifecycleOwner(), serialOutputItemObserver);
  }

  private void handleNewSerialOutputFromLiveData(LauncherOutputData launcherOutputData) {
    terminalRawRVAdapter.appendData(launcherOutputData);
    binding.recyclerViewTerminalRaw.scrollToPosition(terminalRawRVAdapter.getItemCount() - 1);
  }

  private void writeDataToDevice(String data) {
    if (data.length() > 1) {
      terminalViewModel.writeDataToDevice(data);
    }
  }
}
