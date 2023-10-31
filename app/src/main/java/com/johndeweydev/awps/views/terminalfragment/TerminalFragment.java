package com.johndeweydev.awps.views.terminalfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.data.DeviceConnectionParamData;
import com.johndeweydev.awps.data.LauncherOutputData;
import com.johndeweydev.awps.databinding.FragmentTerminalBinding;
import com.johndeweydev.awps.viewmodels.terminalviewmodel.TerminalViewModel;
import com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs;
import com.johndeweydev.awps.views.manualarmafragment.ManualArmaArgs;

import java.util.Objects;

public class TerminalFragment extends Fragment {

  private FragmentTerminalBinding binding;
  private String selectedArmament;
  private TerminalViewModel terminalViewModel;
  private TerminalArgs terminalArgs = null;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    terminalViewModel = new ViewModelProvider(requireActivity()).get(TerminalViewModel.class);
    binding = FragmentTerminalBinding.inflate(inflater, container, false);

    if (getArguments() == null) {
      Log.d("dev-log", "TerminalFragment.onCreateView: Get arguments is null");
    } else {
      Log.d("dev-log", "TerminalFragment.onCreateView: Initializing fragment args");
      TerminalFragmentArgs terminalFragmentArgs;
      if (getArguments().isEmpty()) {
        Log.w("dev-log", "TerminalFragment.onCreateView: Terminal argument is missing, " +
                "using data in the view model");
        terminalArgs = new TerminalArgs(
                terminalViewModel.deviceIdFromTerminalArgs,
                terminalViewModel.portNumFromTerminalArgs,
                terminalViewModel.baudRateFromTerminalArgs
        );
      } else {
        Log.d("dev-log", "TerminalFragment.onCreateView: Getting terminal argument " +
                "from bundle");
        terminalFragmentArgs = TerminalFragmentArgs.fromBundle(getArguments());
        terminalArgs = terminalFragmentArgs.getTerminalArgs();

        terminalViewModel.deviceIdFromTerminalArgs = terminalArgs.getDeviceId();
        terminalViewModel.portNumFromTerminalArgs = terminalArgs.getPortNum();
        terminalViewModel.baudRateFromTerminalArgs = terminalArgs.getBaudRate();
      }
    }
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (terminalArgs == null) {
      Log.d("dev-log", "TerminalFragment.onViewCreated: Terminal args is null");
      Navigation.findNavController(binding.getRoot()).popBackStack();
      return;
    }

    binding.materialToolBarTerminal.setNavigationOnClickListener(v ->
            binding.drawerLayoutTerminal.open()
    );
    binding.materialToolBarTerminal.setOnMenuItemClickListener(menuItem -> {
      String[] choices = new String[]{"Restart Launcher", "More Information"};

      if (menuItem.getItemId() == R.id.moreOptionsTerminalTopRightDropDown) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Options")
                .setItems(choices, (dialog, which) -> {
                  if (which == 0) {
                    terminalViewModel.writeControlCodeRestartLauncher();
                    dialog.dismiss();
                  } else if (which == 1) {
                    // TODO: Show dialog that shows information about the state of the terminal
                  }
                }).show();
      }
      return false;
    });
    binding.navigationViewTerminal.setNavigationItemSelectedListener(
            this::navItemSelected
    );

    binding.buttonCreateCommandTerminal.setOnClickListener(v -> {
      View dialogCommandInput = LayoutInflater.from(requireContext()).inflate(
              R.layout.dialog_command_input, null);
      TextInputEditText textInputEditTextDialogCommandInput = dialogCommandInput.findViewById(
              R.id.textInputEditTextDialogCommandInput);

      textInputEditTextDialogCommandInput.requestFocus();

      MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(
              requireContext())
              .setTitle("Command Instruction Input")
              .setMessage("Enter command instruction code that will be sent to the launcher module")
              .setView(dialogCommandInput)
              .setPositiveButton("SEND", (dialog, which) ->
                      terminalViewModel.writeDataToDevice(Objects.requireNonNull(
                      textInputEditTextDialogCommandInput.getText()
                      ).toString()))
              .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

      AlertDialog dialog = materialAlertDialogBuilder.create();
      if (dialog.getWindow() != null) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
      }
      dialog.show();
    });

    TerminalRVAdapter terminalRVAdapter = setupRecyclerView();
    setupObservers(terminalRVAdapter);
  }

  private TerminalRVAdapter setupRecyclerView() {
    TerminalRVAdapter terminalRVAdapter = new TerminalRVAdapter();
    LinearLayoutManager layout = new LinearLayoutManager(requireContext());
    layout.setStackFromEnd(true);
    binding.recyclerViewLogsTerminal.setAdapter(terminalRVAdapter);
    binding.recyclerViewLogsTerminal.setLayoutManager(layout);
    return terminalRVAdapter;
  }

  private void setupObservers(TerminalRVAdapter terminalRVAdapter) {

    // If triggered, it will append new terminal logs to the recycler view
    final Observer<LauncherOutputData> currentSerialOutputObserver = s -> {
      if (s == null) {
        return;
      }
      terminalRVAdapter.appendNewTerminalLog(s);
      binding.recyclerViewLogsTerminal.scrollToPosition(terminalRVAdapter.getItemCount() - 1);
    };
    terminalViewModel.currentSerialOutputRaw.observe(getViewLifecycleOwner(),
            currentSerialOutputObserver);
    terminalViewModel.currentSerialOutput.observe(getViewLifecycleOwner(),
            currentSerialOutputObserver);

    setupSerialInputErrorListener();
    setupSerialOutputErrorListener();
  }

  private void setupSerialInputErrorListener() {
    final Observer<String> writeErrorListener = s -> {
      if (s == null) {
        return;
      }
      terminalViewModel.currentSerialInputError.setValue(null);
      Log.d("dev-log", "TerminalFragment.setupSerialInputErrorListener: " +
              "Error on user input");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error writing " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "TerminalFragment.setupSerialInputErrorListener: " +
              "Popping this fragment off the back stack");
      Navigation.findNavController(binding.getRoot()).popBackStack();
    };
    terminalViewModel.currentSerialInputError.observe(getViewLifecycleOwner(), writeErrorListener);
  }

  private void setupSerialOutputErrorListener() {
    final Observer<String> onNewDataErrorListener = s -> {
      if (s == null) {
        return;
      }
      terminalViewModel.currentSerialOutputError.setValue(null);
      Log.d("dev-log", "TerminalFragment.setupSerialOutputErrorListener: " +
              "Error on serial output");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "TerminalFragment.setupSerialOutputErrorListener: " +
              "Popping this fragment off the back stack");
      Navigation.findNavController(binding.getRoot()).popBackStack();
    };
    terminalViewModel.currentSerialOutputError.observe(
            getViewLifecycleOwner(), onNewDataErrorListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "TerminalFragment.onResume: Fragment resumed");
    Log.d("dev-log", "TerminalFragment.onResume: Connecting to device");
    connectToDevice();
    terminalViewModel.setLauncherEventHandler();
  }

  private void connectToDevice() {
    int deviceId = terminalArgs.getDeviceId();
    int portNum = terminalArgs.getPortNum();
    DeviceConnectionParamData deviceConnectionParamData = new DeviceConnectionParamData(
            19200, 8, 1, "PARITY_NONE", deviceId, portNum
    );
    String result = terminalViewModel.connectToDevice(deviceConnectionParamData);

    if (result.equals("Successfully connected") || result.equals("Already connected")) {

      Log.d("dev-log",
              "TerminalFragment.connectToDevice: Starting event read");
      terminalViewModel.startEventDrivenReadFromDevice();
    } else {
      Log.d("dev-log", "TerminalFragment.connectToDevice: " + result);
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      stopEventReadAndDisconnectFromDevice();

      Log.d("dev-log", "TerminalFragment.connectToDevice: " +
              "Popping this fragment off the back stack");
      Navigation.findNavController(binding.getRoot()).popBackStack();
    }
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "TerminalFragment.onPause: Fragment pausing");
    stopEventReadAndDisconnectFromDevice();

    super.onPause();
    Log.d("dev-log", "TerminalFragment.onPause: Fragment paused");
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void stopEventReadAndDisconnectFromDevice() {
    Log.d("dev-log", "TerminalFragment.stopEventReadAndDisconnectFromDevice: " +
            "Stopping event read");
    terminalViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "TerminalFragment.stopEventReadAndDisconnectFromDevice: " +
            "Disconnecting from the device");
    terminalViewModel.disconnectFromDevice();
  }

  private boolean navItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.automaticAttackMain) {

      binding.drawerLayoutTerminal.close();
      showAttackTypeDialogSelector(true);
      return true;
    } else if (item.getItemId() == R.id.manualAttackMain) {

      binding.drawerLayoutTerminal.close();
      showAttackTypeDialogSelector(false);
      return true;
    } else if (item.getItemId() == R.id.settingsMenuNavItemTerminalMain) {
      binding.drawerLayoutTerminal.close();

      // TODO: Navigate to settings

      return true;
    } else if (item.getItemId() == R.id.infoMenuNavItemsTerminalMain) {
      binding.drawerLayoutTerminal.close();

      // TODO: Navigate to info

      return true;
    }
    return false;
  }

  private void showAttackTypeDialogSelector(boolean automaticAttack) {
    final String[] choices = new String[]{"PMKID Based Attack", "MIC Based Attack", "Deauther"};

    final int[] checkedItem = {-1};

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Select attack type")
            .setPositiveButton("CONFIRM", (dialog, which) -> {
              if (checkedItem[0] == -1) {
                return;
              }
              checkedItem[0] = -1;
              if (automaticAttack) {
                navigateToAutoArmaFragment();
              } else {
                navigateToManualArmaFragment();
              }
            })
            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
            .setSingleChoiceItems(choices, checkedItem[0], (dialog, which) -> {
              checkedItem[0] = which;
              selectedArmament = choices[which];
            }).show();
  }

  private void navigateToAutoArmaFragment() {
    Log.d("dev-log", "TerminalFragment.showAttackTypeDialogSelector: " +
            "Navigating to auto arma fragment");

    AutoArmaArgs autoArmaArgs = new AutoArmaArgs(
            terminalArgs.getDeviceId(),
            terminalArgs.getPortNum(),
            terminalArgs.getBaudRate(),
            selectedArmament);

    TerminalFragmentDirections.ActionTerminalFragmentToAutoArmaFragment action;
    action = TerminalFragmentDirections.actionTerminalFragmentToAutoArmaFragment(
            autoArmaArgs);
    Navigation.findNavController(binding.getRoot()).navigate(action);
  }

  private void navigateToManualArmaFragment() {
    Log.d("dev-log", "TerminalFragment.showAttackTypeDialogSelector: " +
            "Navigating to manual arma fragment");
    ManualArmaArgs manualArmaArgs = new ManualArmaArgs(
            terminalArgs.getDeviceId(),
            terminalArgs.getPortNum(),
            terminalArgs.getBaudRate(),
            selectedArmament);

    TerminalFragmentDirections.ActionTerminalFragmentToManualArmaFragment action;
    action = TerminalFragmentDirections.actionTerminalFragmentToManualArmaFragment(
            manualArmaArgs);
    Navigation.findNavController(binding.getRoot()).navigate(action);
  }
}