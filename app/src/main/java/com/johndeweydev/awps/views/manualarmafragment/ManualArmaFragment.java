package com.johndeweydev.awps.views.manualarmafragment;

import static com.johndeweydev.awps.AppConstants.BAUD_RATE;
import static com.johndeweydev.awps.AppConstants.DATA_BITS;
import static com.johndeweydev.awps.AppConstants.PARITY_NONE;
import static com.johndeweydev.awps.AppConstants.STOP_BITS;

import android.content.Context;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentManualArmaBinding;
import com.johndeweydev.awps.models.data.AccessPointData;
import com.johndeweydev.awps.models.data.DeviceConnectionParamData;
import com.johndeweydev.awps.models.data.HashInfoEntity;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;
import com.johndeweydev.awps.viewmodels.hashinfoviewmodel.HashInfoViewModel;
import com.johndeweydev.awps.viewmodels.serial.sessionviewmodel.SessionViewModel;
import com.johndeweydev.awps.viewmodels.serial.sessionviewmodel.SessionViewModelFactory;

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

    checkLocationSettings();

    sessionViewModel.automaticAttack = false;
    sessionViewModel.selectedArmament = manualArmaArgs.getSelectedArmament();

    initializeMacAddressInput();
    binding.materialToolBarManualArma.setOnClickListener(v ->
            showDialogAskUserToExitOfThisFragment());
    binding.materialToolBarManualArma.setOnMenuItemClickListener(this::showDialogMenuOptionsToUser);
    binding.buttonStartManualArma.setEnabled(false);
    binding.buttonStartManualArma.setOnClickListener(v -> buttonPressed());

    ManualArmaRVAdapter manualArmaRVAdapter = setupRecyclerView();
    setupObservers(manualArmaRVAdapter);
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

  private void buttonPressed() {
    if (sessionViewModel.attackOnGoing) {
      showDialogAskUserToStopAttack();
      return;
    }

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

  private void checkLocationSettings() {
    LocationRequest locationRequest = new LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(2000)
            .build();

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);

    SettingsClient client = LocationServices.getSettingsClient(requireActivity());
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

    task.addOnFailureListener(requireActivity(), e -> {
      if (e instanceof ResolvableApiException) {
        // Location settings are not satisfied, but this can be fixed
        // by showing the user a dialog.

        try {
          // Show the dialog by calling startResolutionForResult(),
          // and check the result in onActivityResult().
          ResolvableApiException resolvable = (ResolvableApiException) e;
          resolvable.startResolutionForResult(requireActivity(),
                  LocationSettingsStatusCodes.RESOLUTION_REQUIRED);
        } catch (IntentSender.SendIntentException sendEx) {
          Log.w("dev-log", "ManualArmaFragment.checkLocationSettings: " + sendEx.getMessage());
        }
      }
    });
  }

  private ManualArmaRVAdapter setupRecyclerView() {
    ManualArmaRVAdapter manualArmaRVAdapter = new ManualArmaRVAdapter();
    LinearLayoutManager layout = new LinearLayoutManager(requireActivity());
    layout.setStackFromEnd(true);
    binding.recyclerViewAttackLogsManualArma.setAdapter(manualArmaRVAdapter);
    binding.recyclerViewAttackLogsManualArma.setLayoutManager(layout);
    return manualArmaRVAdapter;
  }

  private void setupObservers(ManualArmaRVAdapter manualArmaRVAdapter) {
    final Observer<String> attackLogsObserver = log -> {
      if (log == null) {
        return;
      }
      manualArmaRVAdapter.appendData(log);
      binding.recyclerViewAttackLogsManualArma.scrollToPosition(
              manualArmaRVAdapter.getItemCount() - 1);
    };
    sessionViewModel.currentAttackLog.observe(getViewLifecycleOwner(), attackLogsObserver);
    setupSerialInputErrorListener();
    setupSerialOutputErrorListener();

    // INITIALIZATION PHASE
    final Observer<String> launcherStartedObserver = started -> {
      if (started == null) {
        return;
      }
      TextInputEditText macAddressInput = binding.textInputEditTextMacAddressManualArma;
      boolean isTargetSet = Objects.requireNonNull(macAddressInput.getText()).length() == 12;
      binding.buttonStartManualArma.setEnabled(isTargetSet);
    };
    sessionViewModel.launcherStarted.observe(getViewLifecycleOwner(), launcherStartedObserver);

    final Observer<String> armamentActivateConfirmationObserver = confirmationMessage -> {
      if (confirmationMessage == null) {
        return;
      }
      sessionViewModel.launcherActivateConfirmation.setValue(null);
      showDialogAskUserToActivateTheAttack(confirmationMessage);
    };
    sessionViewModel.launcherActivateConfirmation.observe(getViewLifecycleOwner(),
            armamentActivateConfirmationObserver);

    // TARGET LOCKING PHASE
    final Observer<ArrayList<AccessPointData>> finishScanningObserver = targetList -> {
      if (targetList == null || targetList.isEmpty()) {
        return;
      }
      sessionViewModel.launcherFinishScanning.setValue(null);
      showDialogToShowUserOfAvailableTargets(targetList);
      sessionViewModel.userWantsToScanForAccessPoint = false;
    };
    sessionViewModel.launcherFinishScanning.observe(getViewLifecycleOwner(),
            finishScanningObserver);

    final Observer<String> accessPointNotFoundObserver = target -> {
      if (target == null) {
        return;
      }
      sessionViewModel.launcherAccessPointNotFound.setValue(null);
      showDialogTellUserTargetNotFound(target);
    };
    sessionViewModel.launcherAccessPointNotFound.observe(getViewLifecycleOwner(),
            accessPointNotFoundObserver);

    // EXECUTION PHASE
    final Observer<String> launcherMainTaskCreatedObserver = taskCreationEvent -> {
      if (taskCreationEvent == null) {
        return;
      }
      sessionViewModel.launcherMainTaskCreated.setValue(null);
      Drawable stop = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_btn_stop_24);
      binding.buttonStartManualArma.setEnabled(true);
      binding.buttonStartManualArma.setText(R.string.stop_manual_arma);
      binding.buttonStartManualArma.setIcon(stop);
    };
    sessionViewModel.launcherMainTaskCreated.observe(getViewLifecycleOwner(),
            launcherMainTaskCreatedObserver);

    // POST EXECUTION PHASE
    final Observer<String> launcherExecutionResultObserver = executionResult -> {
      if (executionResult == null) {
        return;
      }
      sessionViewModel.launcherExecutionResult.setValue(null);
      showDialogTellUserAboutTheResultOfAttack(executionResult);
      Drawable start = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_btn_start_24);
      binding.buttonStartManualArma.setText(R.string.start_manual_arma);
      binding.buttonStartManualArma.setIcon(start);
    };
    sessionViewModel.launcherExecutionResult.observe(getViewLifecycleOwner(),
            launcherExecutionResultObserver);

    final Observer<String> launcherDeauthStoppedObserver = deautherStopped -> {
      if (deautherStopped == null) {
        return;
      }
      sessionViewModel.launcherDeauthStopped.setValue(null);
      Drawable start = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_btn_start_24);
      binding.buttonStartManualArma.setText(R.string.start_manual_arma);
      binding.buttonStartManualArma.setIcon(start);
    };
    sessionViewModel.launcherDeauthStopped.observe(getViewLifecycleOwner(),
            launcherDeauthStoppedObserver);
  }

  private void showDialogAskUserToExitOfThisFragment() {
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

  private boolean showDialogMenuOptionsToUser(MenuItem menuItem) {
    TextInputEditText macAddressInput = binding.textInputEditTextMacAddressManualArma;

    String[] choices = getResources().getStringArray(R.array.dialog_options_manual_arma);

    String findTargets = getResources().getString(R.string.find_targets);
    String change_attack_type = getResources().getString(R.string.change_attack_type);
    String clearAttackLogs = getResources().getString(R.string.clear_attack_logs);
    String restartLauncher = getResources().getString(R.string.restart_launcher);
    String database = getResources().getString(R.string.database);
    String moreInfo = getResources().getString(R.string.more_info);
    String settings = getResources().getString(R.string.settings);

    if (menuItem.getItemId() != R.id.moreOptionsManualArmaTopRightDialogMenu) {
      return false;
    }

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Menu Options");
    builder.setItems(choices, (dialog, which) -> {

      if (choices[which].equals(findTargets)) {

        sessionViewModel.userWantsToScanForAccessPoint = true;
        sessionViewModel.writeInstructionCodeForScanningDevicesToLauncher();
      } else if (choices[which].equals(change_attack_type)) {

        showDialogAskUserToSelectAttackType();
      } else if (choices[which].equals(clearAttackLogs)) {

        ManualArmaRVAdapter adapter = (ManualArmaRVAdapter)
                binding.recyclerViewAttackLogsManualArma.getAdapter();
        if (adapter != null) {
          adapter.clearLogs();
        } else {
          Toast.makeText(requireActivity(), "Attack logs adapter is not set",
                  Toast.LENGTH_LONG).show();
        }
      } else if (choices[which].equals(restartLauncher)) {

        sessionViewModel.writeControlCodeRestartLauncher();
      } else if (choices[which].equals(database)) {

        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_manualArmaFragment_to_hashesFragment);
      } else if (choices[which].equals(moreInfo)) {

        dialog.dismiss();
        if (macAddressInput.getText() != null) {
          showDialogToUserAboutConfiguredAttack(sessionViewModel.selectedArmament,
                  sessionViewModel.targetAccessPointSsid);
        } else {
          showDialogToUserAboutConfiguredAttack(sessionViewModel.selectedArmament,
                  "");
        }
      } else if (choices[which].equals(settings)) {
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_manualArmaFragment_to_settingsFragment);
      }
    }).show();

    return true;
  }

  private void showDialogAskUserToSelectAttackType() {
    final String[] choices = getResources().getStringArray(
            R.array.dialog_options_select_attack_manual_arma);

    final int[] checkedItem = {-1};

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Select attack type");
    builder.setPositiveButton("SELECT", (dialog, which) -> {
      if (checkedItem[0] == -1) return;

      checkedItem[0] = -1;
      Toast.makeText(getActivity(), "Changed attack type to " +
              sessionViewModel.selectedArmament, Toast.LENGTH_LONG).show();
    });
    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
    builder.setSingleChoiceItems(choices, checkedItem[0], (dialog, which) -> {
      checkedItem[0] = which;
      sessionViewModel.selectedArmament = choices[which];
    }).show();
  }

  private void showDialogAskUserToActivateTheAttack(String message) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Armament Activate")
            .setMessage(message)
            .setPositiveButton("ACTIVATE", ((dialog, which) -> {
              sessionViewModel.writeControlCodeActivationToLauncher();
              binding.buttonStartManualArma.setEnabled(false);
            }))
            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).show();
  }

  private void showDialogToUserAboutConfiguredAttack(String attackType, String targetMacAddress) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Information");
    if (targetMacAddress == null || targetMacAddress.isEmpty()) {
      builder.setMessage("Using " + attackType + ", target is not specified");
    } else {
      builder.setMessage("Using " + attackType + ", target is " + targetMacAddress);
    }
    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
    builder.show();
  }

  private void showDialogTellUserTargetNotFound(String target) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Target Not Found");
    builder.setMessage("The target access point " + target + " is not found. Do you want to find " +
            "another target?");
    builder.setPositiveButton("NEW TARGET", (dialog, which) -> {

      sessionViewModel.userWantsToScanForAccessPoint = true;
      sessionViewModel.writeInstructionCodeForScanningDevicesToLauncher();
    });
    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
    builder.show();
  }

  private void showDialogAskUserToStopAttack() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Armament Deactivate");
    builder.setMessage("Do you want to stop or deactivate the currently running attack?");
    builder.setPositiveButton("DEACTIVATE", (dialog, which) ->
            sessionViewModel.writeControlCodeDeactivationToLauncher());
    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).show();
  }


  private void showDialogTellUserAboutTheResultOfAttack(String result) {
    // Ensure the GPS is turned on because when saving hashes in the database the app will ask for
    // the current location of the user
    checkLocationSettings();

    if (result.equals("Failed")) {

      MaterialAlertDialogBuilder builderFailed = new MaterialAlertDialogBuilder(requireActivity());
      builderFailed.setTitle("Failed Attack");
      builderFailed.setMessage("Failed to penetrate " + sessionViewModel.targetAccessPointSsid +
              " using " + sessionViewModel.selectedArmament + " Do you want to SCAN for new target " +
              "or CHANGE the attack type?");
      builderFailed.setPositiveButton("SCAN", (dialog, which) -> {

        sessionViewModel.userWantsToScanForAccessPoint = true;
        sessionViewModel.writeInstructionCodeForScanningDevicesToLauncher();
      });
      builderFailed.setNeutralButton("CHANGE", (dialog, which) ->
              showDialogAskUserToSelectAttackType());
      builderFailed.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).show();

    } else if (result.equals("Success")) {
      MaterialAlertDialogBuilder builderSuccess = new MaterialAlertDialogBuilder(requireActivity());

      // Save result in the database
      HashInfoEntity copyOfLauncherExecutionResultData;
      copyOfLauncherExecutionResultData = sessionViewModel.launcherExecutionResultData;
      hashInfoViewModel.addNewHashInfo(copyOfLauncherExecutionResultData);

      sessionViewModel.launcherExecutionResultData = null;
      sessionViewModel.launcherExecutionResult.setValue(null);
      binding.textInputEditTextMacAddressManualArma.setText("");

      builderSuccess.setTitle("Successful Attack");
      builderSuccess.setMessage("Successfully penetrated " +
              sessionViewModel.targetAccessPointSsid + " using " +
              sessionViewModel.selectedArmament +
              ". Do you want to find another target?");

      builderSuccess.setPositiveButton("NEW TARGET", (dialog, which) -> {

        sessionViewModel.userWantsToScanForAccessPoint = true;
        sessionViewModel.writeInstructionCodeForScanningDevicesToLauncher();
      });
      builderSuccess.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).show();
    }
  }

  private void showDialogToShowUserOfAvailableTargets(ArrayList<AccessPointData> targetList) {
    String[] choicesSsid = new String[targetList.size()];
    String[] choicesMacAddress = new String[targetList.size()];

    final int[] checkedItem = {-1};
    for (int i = 0; i < targetList.size(); i++) {
      choicesSsid[i] = targetList.get(i).ssid();
      choicesMacAddress[i] = targetList.get(i).macAddress();
    }

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Select Target");
    builder.setPositiveButton("SELECT", (dialog, which) -> {
      if (checkedItem[0] == -1) {
        return;
      }
      checkedItem[0] = -1;
      dialog.dismiss();
      sessionViewModel.accessPointDataList.clear();
      binding.textInputEditTextMacAddressManualArma.setEnabled(true);
      binding.textInputEditTextMacAddressManualArma.requestFocus();
    });
    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
    builder.setSingleChoiceItems(choicesSsid, checkedItem[0], (dialog, which) -> {
      checkedItem[0] = which;
      binding.textInputEditTextMacAddressManualArma.setText(choicesMacAddress[which]);
      sessionViewModel.targetAccessPointSsid = choicesSsid[which];
      binding.buttonStartManualArma.setEnabled(true);
    }).show();
  }

  private void setupSerialInputErrorListener() {
    final Observer<String> serialInputErrorObserver = inputError -> {
      if (inputError == null) {
        return;
      }
      sessionViewModel.currentSerialInputError.setValue(null);
      Log.d("dev-log", "ManualArmaFragment.setupSerialInputErrorListener: " +
              "Error on serial input: " + inputError);
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
    final Observer<String> serialOutputErrorObserver = outputError -> {
      if (outputError == null) {
        return;
      }
      sessionViewModel.currentSerialOutputError.setValue(null);
      Log.d("dev-log", "ManualArmaFragment.setupSerialOutputErrorListener: " +
              "Error on serial output: " + outputError);
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
            BAUD_RATE, DATA_BITS, STOP_BITS, PARITY_NONE, deviceId, portNum);
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
    sessionViewModel.currentAttackLog.setValue(null);
    Log.d("dev-log", "ManualArmaFragment.onPause: Fragment pausing");
    stopEventReadAndDisconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "ManualArmaFragment.onPause: Fragment paused");
  }

  @Override
  public void onDestroyView() {
    sessionViewModel.attackLogNumber = 0;
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