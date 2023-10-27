package com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted;

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

import com.johndeweydev.awps.databinding.FragmentTerminalBinding;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.viewmodels.terminalviewmodel.TerminalViewModel;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

public class TerminalFragment extends Fragment {

  private FragmentTerminalBinding binding;
  private TerminalViewModel terminalViewModel;
  private TerminalRVAdapter terminalRVAdapter;
  private TerminalArgs terminalArgs = null;

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    terminalViewModel = new ViewModelProvider(requireActivity()).get(TerminalViewModel.class);
    binding = FragmentTerminalBinding.inflate(inflater, container, false);

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

    binding.editTextCommandWriteTerminal.setOnClickListener(v -> {
      String data = binding.editTextCommandInputTerminal.getText().toString();
      writeDataToDevice(data);
    });
  }

  private void setupRecyclerViewAndObserveData() {
    terminalRVAdapter = new TerminalRVAdapter();
    binding.recyclerViewTerminal.setAdapter(terminalRVAdapter);
    LinearLayoutManager layout = new LinearLayoutManager(requireContext());
    layout.setStackFromEnd(true);
    binding.recyclerViewTerminal.setLayoutManager(layout);

    final Observer<LauncherOutputData> serialOutputObserver;
    serialOutputObserver = this::handleNewSerialOutputFromLiveData;
    terminalViewModel.currentMessageFormatted.observe(
            getViewLifecycleOwner(), serialOutputObserver
    );
  }

  private void handleNewSerialOutputFromLiveData(LauncherOutputData launcherOutputData) {
    terminalRVAdapter.appendData(launcherOutputData);
    binding.recyclerViewTerminal.scrollToPosition(terminalRVAdapter.getItemCount() - 1);
  }

  private void writeDataToDevice(String data) {
    if (data.length() > 1) {
      terminalViewModel.writeDataToDevice(data);
    }
  }
}