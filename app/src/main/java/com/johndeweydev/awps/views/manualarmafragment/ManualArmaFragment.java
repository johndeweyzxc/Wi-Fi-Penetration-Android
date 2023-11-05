package com.johndeweydev.awps.views.manualarmafragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.models.data.AccessPointData;
import com.johndeweydev.awps.models.data.DeviceConnectionParamData;
import com.johndeweydev.awps.databinding.FragmentManualArmaBinding;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;
import com.johndeweydev.awps.viewmodels.hashinfoviewmodel.HashInfoViewModel;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

public class ManualArmaFragment extends Fragment {

  private FragmentManualArmaBinding binding;
  private ManualArmaArgs manualArmaArgs = null;
  private SessionViewModel sessionViewModel;
  private HashInfoViewModel hashInfoViewModel;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    SessionRepoSerial sessionRepoSerial = new SessionRepoSerial();
    SessionViewModelFactory sessionViewModelFactory = new SessionViewModelFactory(
            sessionRepoSerial);
    sessionViewModel = new ViewModelProvider(this, sessionViewModelFactory).get(
            SessionViewModel.class);
    hashInfoViewModel = new ViewModelProvider(this).get(HashInfoViewModel.class);

    binding = FragmentManualArmaBinding.inflate(inflater, container, false);

    if (getArguments() == null) {
      Log.d("dev-log", "ManualArmaFragment.onCreateView: Get arguments is null");
    } else {
      Log.d("dev-log", "ManualArmaFragment.onCreateView: Initializing fragment args");
      ManualArmaFragmentArgs manualArmaFragmentArgs;
      manualArmaFragmentArgs = ManualArmaFragmentArgs.fromBundle(getArguments());
      manualArmaArgs = manualArmaFragmentArgs.getManualArmaArgs();
    }
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (manualArmaArgs == null) {
      Log.d("dev-log", "ManualArmaFragment.onViewCreated: Manual arma args is null");
      Navigation.findNavController(binding.getRoot()).popBackStack();
      return;
    }

    sessionViewModel.automaticAttack = false;
    sessionViewModel.selectedArmament = manualArmaArgs.getSelectedArmament();
    initializeMacAddressInput();

    binding.materialToolBarManualArma.setOnClickListener(v -> {
        NavController navController = Navigation.findNavController(binding.getRoot());
        navController.navigate(R.id.action_manualArmaFragment_to_exitModalBottomSheetDialog);
      }
    );
    binding.materialToolBarManualArma.setOnMenuItemClickListener(this::showDialogMenuOptions);
    binding.buttonStartManualArma.setEnabled(false);
    binding.buttonStartManualArma.setOnClickListener(v -> buttonPressedStartArma());
    binding.buttonCloseManualArma.setOnClickListener(v -> showDialogExit());

    ManualArmaRVAdapter manualArmaRVAdapter = setupRecyclerView();
    setupObservers(manualArmaRVAdapter);

    showDialogScanAccessPoints();
  }

  private void initializeMacAddressInput() {
    TextInputEditText macAddressInput = binding.textInputEditTextMacAddressManualArma;
    macAddressInput.requestFocus();

    if (Objects.requireNonNull(macAddressInput.getText()).length() != 12) {
      macAddressInput.setEnabled(false);
    }

    macAddressInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        binding.buttonStartManualArma.setEnabled(count == 12);
      }
      @Override
      public void afterTextChanged(Editable s) {
        binding.buttonStartManualArma.setEnabled(s.toString().length() == 12);
      }
    });
  }

  private boolean showDialogMenuOptions(MenuItem menuItem) {
    TextInputEditText macAddressInput = binding.textInputEditTextMacAddressManualArma;

    String[] choices = new String[]{
            "Find Targets",
            "Stop Attack",
            "Restart Launcher",
            "Clear Attack Logs",
            "Database",
            "More Information"
    };

    if (menuItem.getItemId() == R.id.moreOptionsManualArmaTopRightDropDown) {
      MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
      builder.setTitle("Options");
      builder.setItems(choices, (dialog, which) -> {
        switch (which) {
          case 0 ->
            // Find Targets
                  showDialogScanAccessPoints();
          case 1 -> {
            // Stop Attack
            if (sessionViewModel.attackOnGoing) {
              sessionViewModel.writeControlCodeStopRunningAttack();
            } else {
              Toast.makeText(getActivity(), "No ongoing attack", Toast.LENGTH_LONG).show();
            }
          }
          case 2 ->
            // Restart Launcher
                  sessionViewModel.writeControlCodeRestartLauncher();
          case 3 -> {
            // Clear Attack Logs
            // Clears the content in the recycler view of attack logs
            ManualArmaRVAdapter adapter = (ManualArmaRVAdapter)
                    binding.recyclerViewAttackLogsManualArma.getAdapter();
            if (adapter != null) {
              adapter.clearLogs();
            } else {
              Toast.makeText(requireActivity(), "Attack logs adapter is not set",
                      Toast.LENGTH_LONG).show();
            }
          }
          case 4 ->
            // Database
                  Navigation.findNavController(binding.getRoot()).navigate(
                          R.id.action_manualArmaFragment_to_hashesFragment);
          case 5 -> {
            // More Information
            dialog.dismiss();
            if (macAddressInput.getText() != null) {
              showDialogInformation(sessionViewModel.selectedArmament,
                      macAddressInput.getText().toString());
            } else {
              showDialogInformation(sessionViewModel.selectedArmament, "");
            }
          }
        }
      }).show();
      return true;
    }
    return false;
  }

  private void showDialogExit() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Exit Manual Attack");

    if (sessionViewModel.attackOnGoing) {
      builder.setMessage("You have an ongoing attack, Do you really want to exit manual attack?");
    } else {
      builder.setMessage("Do you really want to exit manual attack? You will be navigated back" +
              " to terminal");
    }
    builder.setPositiveButton("EXIT", (dialog, which) -> {
      if (sessionViewModel.attackOnGoing) {
        sessionViewModel.writeControlCodeDeactivationToLauncher();
        sessionViewModel.attackOnGoing = false;
      }
      Navigation.findNavController(binding.getRoot()).popBackStack();
    });
    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
    builder.show();
  }

  private void showDialogScanAccessPoints() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Scan Access Points")
            .setMessage("Do you want to scan for nearby access points?")
            .setPositiveButton("SCAN", (dialog, which) -> {
              sessionViewModel.userWantsToScanForAccessPoint = true;
              sessionViewModel.writeInstructionCodeForScanningDevicesToLauncher();
            })
            .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();
  }

  private void showDialogInformation(String attackType, String targetMacAddress) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Information");
    if (targetMacAddress.isEmpty()) {
      builder.setMessage("Using " + attackType + ", target is not specified");
    } else {
      builder.setMessage("Using " + attackType + ", target is " + targetMacAddress);
    }
    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
    builder.show();
  }

  private void buttonPressedStartArma() {
    View currentView = this.requireActivity().getCurrentFocus();
    if (currentView == null) {
      return;
    }

    InputMethodManager inputMethodManager = (InputMethodManager) requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);

    TextInputEditText macAddressInput = binding.textInputEditTextMacAddressManualArma;
    if (macAddressInput.getText() == null || macAddressInput.getText().toString().isEmpty()) {
      Toast.makeText(requireActivity(), "Mac address cannot be null or empty",
              Toast.LENGTH_SHORT).show();
      return;
    }

    sessionViewModel.writeInstructionCodeToLauncher(macAddressInput.getText().toString());
  }

  private ManualArmaRVAdapter setupRecyclerView() {
    ManualArmaRVAdapter manualArmaRVAdapter = new ManualArmaRVAdapter();
    LinearLayoutManager layout = new LinearLayoutManager(requireContext());
    layout.setStackFromEnd(true);
    binding.recyclerViewAttackLogsManualArma.setAdapter(manualArmaRVAdapter);
    binding.recyclerViewAttackLogsManualArma.setLayoutManager(layout);
    return manualArmaRVAdapter;
  }

  private void setupObservers(ManualArmaRVAdapter manualArmaRVAdapter) {
    // Append logs to the recycler view
    final Observer<String> attackLogsObserver = s -> {
      if (s == null) {
        return;
      }
      manualArmaRVAdapter.appendData(s);
      binding.recyclerViewAttackLogsManualArma.scrollToPosition(
              manualArmaRVAdapter.getItemCount() - 1);
    };
    sessionViewModel.currentAttackLog.observe(getViewLifecycleOwner(), attackLogsObserver);
    // Receive updates for any error cause by user input
    setupSerialInputErrorListener();
    // Receive updates for any error while reading data from the serial output
    setupSerialOutputErrorListener();

    // INITIALIZATION PHASE
    final Observer<String> launcherStartedObserver = s -> {
      if (s == null) {
        return;
      }
      // Everytime the launcher is started, always enable the 'start' and 'close' button
      binding.buttonStartManualArma.setEnabled(true);
      binding.buttonCloseManualArma.setEnabled(true);
      sessionViewModel.launcherActivateConfirmation.setValue(null);
    };
    sessionViewModel.launcherStarted.observe(getViewLifecycleOwner(), launcherStartedObserver);


    final Observer<String> armamentActivateConfirmationObserver = s -> {
      if (s == null) {
        return;
      }

      // Show a confirmation dialog on whether to activate the attack, this disables the 'close'
      // and 'start' button if the user activates the attack
      MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
      builder.setTitle("Armament Activate")
              .setMessage(s)
              .setPositiveButton("ACTIVATE", ((dialog, which) -> {
                sessionViewModel.writeControlCodeActivationToLauncher();
                binding.buttonStartManualArma.setEnabled(false);
                binding.buttonCloseManualArma.setEnabled(false);
              }))
              .setNegativeButton("CANCEL", (dialog, which) -> Objects.requireNonNull(
                      binding.textInputEditTextMacAddressManualArma.getText()
              ).clear()).show();
      sessionViewModel.launcherActivateConfirmation.setValue(null);
    };
    sessionViewModel.launcherActivateConfirmation.observe(getViewLifecycleOwner(),
            armamentActivateConfirmationObserver);

    // TARGET LOCKING PHASE
    final Observer<ArrayList<AccessPointData>> finishScanningObserver = targetList -> {
      if (targetList == null || targetList.isEmpty()) {
        return;
      }
      // Shows a dialog where the user can select a target access point
      showDialogForTargetSelection(targetList);
      sessionViewModel.userWantsToScanForAccessPoint = false;
      sessionViewModel.launcherFinishScanning.setValue(null);
    };
    sessionViewModel.launcherFinishScanning.observe(getViewLifecycleOwner(),
            finishScanningObserver);

    final Observer<String> accessPointNotFoundObserver = target -> {
      if (target == null) {
        return;
      }

      MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
      builder.setTitle("Target Not Found");
      builder.setMessage("The target access point with mac address of " + target + " is not " +
              "found. Do you want to find another target?");
      builder.setPositiveButton("NEW TARGET", (dialog, which) -> showDialogScanAccessPoints());
      builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
      builder.show();
      sessionViewModel.launcherAccessPointNotFound.setValue(null);
    };
    sessionViewModel.launcherAccessPointNotFound.observe(getViewLifecycleOwner(),
            accessPointNotFoundObserver);

    // EXECUTION PHASE
    final Observer<String> launcherMainTaskObserver = s -> {
      if (s == null) {
        return;
      }
      // Allow the user to close or deactivate the currently running attack by
      // enabling the 'close' button so the user can click it
      binding.buttonCloseManualArma.setEnabled(true);
    };
    sessionViewModel.launcherMainTaskCreated.observe(getViewLifecycleOwner(),
            launcherMainTaskObserver);


    // POST EXECUTION PHASE
    final Observer<String> launcherExecutionResultObserver = s -> {
      if (s == null) {
        return;
      }
      showDialogResult(s);
      binding.buttonStartManualArma.setEnabled(true);

    };
    sessionViewModel.launcherExecutionResult.observe(getViewLifecycleOwner(),
            launcherExecutionResultObserver);
  }

  public void showDialogResult(String result) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    if (result.equals("Failed")) {
      Toast.makeText(requireActivity(), "Failed to penetrate " +
              sessionViewModel.targetAccessPoint, Toast.LENGTH_LONG).show();
      sessionViewModel.writeControlCodeDeactivationToLauncher();
    } else if (result.equals("Success")) {

      // Save the information about the attack in the database
      hashInfoViewModel.addNewHashInfo(sessionViewModel.launcherExecutionResultData);

      sessionViewModel.launcherExecutionResultData = null;
      sessionViewModel.launcherExecutionResult.setValue(null);
      binding.textInputEditTextMacAddressManualArma.setText("");

      builder.setTitle("Successful Attack");
      builder.setMessage("Successfully penetrated " +
              sessionViewModel.targetAccessPoint + ", using " +
              sessionViewModel.selectedArmament +
              ". Do you want to find another target?");

      builder.setPositiveButton("NEW TARGET", (dialog, which) -> showDialogScanAccessPoints());
      builder.setNeutralButton("MORE INFO", (dialog, which) -> {

        // TODO: Show the attack result information via dialog

      }).show();
    }
  }

  private void showDialogForTargetSelection(ArrayList<AccessPointData> targetList) {
    String[] choicesSsid = new String[targetList.size()];
    String[] choicesMacAddress = new String[targetList.size()];

    final int[] checkedItem = {-1};
    for (int i = 0; i < targetList.size(); i++) {
      choicesSsid[i] = targetList.get(i).ssid();
      choicesMacAddress[i] = targetList.get(i).macAddress();
    }

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Select Target")
            .setPositiveButton("SELECT", (dialog, which) -> {
              if (checkedItem[0] == -1) {
                return;
              }
              checkedItem[0] = -1;
              dialog.dismiss();
              sessionViewModel.accessPointDataList.clear();
              binding.textInputEditTextMacAddressManualArma.setEnabled(true);
              binding.textInputEditTextMacAddressManualArma.requestFocus();
            })
            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
            .setSingleChoiceItems(choicesSsid, checkedItem[0], (dialog, which) -> {
              checkedItem[0] = which;
              binding.textInputEditTextMacAddressManualArma.setText(choicesMacAddress[which]);
              sessionViewModel.targetAccessPointSsid = choicesSsid[which];
              binding.buttonCloseManualArma.setEnabled(true);
              binding.buttonStartManualArma.setEnabled(true);
            }).show();
  }

  private void setupSerialInputErrorListener() {
    final Observer<String> serialInputErrorObserver = s -> {
      if (s == null) {
        return;
      }
      sessionViewModel.currentSerialInputError.setValue(null);
      Log.d("dev-log", "ManualArmaFragment.setupSerialInputErrorListener: " +
              "Error on serial input: " + s);
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error on serial input", Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "ManualArmaFragment.setupSerialInputErrorListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_manualArmaFragment_to_devicesFragment);
    };
    sessionViewModel.currentSerialInputError.observe(getViewLifecycleOwner(),
            serialInputErrorObserver);
  }

  private void setupSerialOutputErrorListener() {
    final Observer<String> serialOutputErrorObserver = s -> {
      if (s == null) {
        return;
      }
      sessionViewModel.currentSerialOutputError.setValue(null);
      Log.d("dev-log", "ManualArmaFragment.setupSerialOutputErrorListener: " +
              "Error on serial output: " + s);
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error on serial output ", Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "ManualArmaFragment.setupSerialOutputErrorListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_manualArmaFragment_to_devicesFragment);
    };
    sessionViewModel.currentSerialOutputError.observe(
            getViewLifecycleOwner(), serialOutputErrorObserver);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "ManualArmaFragment.onResume: Fragment resumed");
    Log.d("dev-log", "ManualArmaFragment.onResume: Connecting to device");
    connectToDevice();
    sessionViewModel.setLauncherEventHandler();
  }

  private void connectToDevice() {
    if (manualArmaArgs == null) {
      throw new NullPointerException("terminalArgs is null");
    }

    int deviceId = manualArmaArgs.getDeviceId();
    int portNum = manualArmaArgs.getPortNum();
    DeviceConnectionParamData deviceConnectionParamData = new DeviceConnectionParamData(
            19200, 8, 1, "PARITY_NONE", deviceId, portNum
    );
    String result = sessionViewModel.connectToDevice(deviceConnectionParamData);

    if (result.equals("Successfully connected") || result.equals("Already connected")) {

      Log.d("dev-log",
              "ManualArmaFragment.connectToDevice: Starting event read");
      sessionViewModel.startEventDrivenReadFromDevice();
    } else {
      Log.d("dev-log", "ManualArmaFragment.connectToDevice: " + result);
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      stopEventReadAndDisconnectFromDevice();

      Log.d("dev-log", "ManualArmaFragment.connectToDevice: " +
              "Popping all fragments but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_manualArmaFragment_to_devicesFragment);
    }
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "ManualArmaFragment.onPause: Fragment pausing");
    stopEventReadAndDisconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "ManualArmaFragment.onPause: Fragment paused");
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void stopEventReadAndDisconnectFromDevice() {
    Log.d("dev-log", "ManualArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Stopping event read");
    sessionViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "ManualArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Disconnecting from the device");
    sessionViewModel.disconnectFromDevice();
  }
}