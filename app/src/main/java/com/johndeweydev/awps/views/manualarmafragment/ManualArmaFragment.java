package com.johndeweydev.awps.views.manualarmafragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.johndeweydev.awps.data.AccessPointData;
import com.johndeweydev.awps.data.DeviceConnectionParamData;
import com.johndeweydev.awps.databinding.FragmentManualArmaBinding;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModelFactory;

import java.util.ArrayList;
import java.util.Objects;

public class ManualArmaFragment extends Fragment {

  private FragmentManualArmaBinding binding;
  private ManualArmaArgs manualArmaArgs = null;
  private SessionViewModel sessionViewModel;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    SessionRepoSerial sessionRepoSerial = new SessionRepoSerial();
    SessionViewModelFactory sessionViewModelFactory = new SessionViewModelFactory(
            sessionRepoSerial);
    sessionViewModel = new ViewModelProvider(this, sessionViewModelFactory).get(
            SessionViewModel.class);

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
    binding.textInputEditTextMacAddressManualArma.requestFocus();

    binding.materialToolBarManualArma.setOnClickListener(v -> {
        NavController navController = Navigation.findNavController(binding.getRoot());
        navController.navigate(R.id.action_manualArmaFragment_to_exitModalBottomSheetDialog);
      }
    );

    binding.materialToolBarManualArma.setOnMenuItemClickListener(menuItem -> {
      String[] choices = new String[]{"Restart Launcher", "More Information"};

      if (menuItem.getItemId() == R.id.moreOptionsManualArmaTopRightDropDown) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Options")
                .setItems(choices, (dialog, which) -> {
                  if (which == 0) {
                    sessionViewModel.writeControlCodeRestartLauncher();
                    dialog.dismiss();
                  } else if (which == 1) {
                    TextInputEditText targetMacInput = binding
                            .textInputEditTextMacAddressManualArma;

                    dialog.dismiss();
                    if (targetMacInput.getText() != null) {
                      showDialogInformation(sessionViewModel.selectedArmament,
                              targetMacInput.getText().toString());
                    } else {
                      showDialogInformation(sessionViewModel.selectedArmament, "");
                    }
                  }

                }).show();
      }
      return false;
    });

    binding.buttonStartManualArma.setOnClickListener(v -> {
      View currentView = this.requireActivity().getCurrentFocus();
      if (currentView != null) {
        buttonPressedStartArma(currentView);
      }
    });

    binding.buttonCloseManualArma.setOnClickListener(v -> {
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
    });

    ManualArmaRVAdapter manualArmaRVAdapter = setupRecyclerView();
    setupObservers(manualArmaRVAdapter);

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

  private void buttonPressedStartArma(View view) {
    InputMethodManager inputMethodManager = (InputMethodManager) requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

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

    // Everytime the launcher is started, always enable the 'start' and 'close' button
    final Observer<String> launcherStartedObserver = s -> {
      if (s == null) {
        return;
      }
      binding.buttonStartManualArma.setEnabled(true);
      binding.buttonCloseManualArma.setEnabled(true);
    };
    sessionViewModel.launcherStarted.observe(getViewLifecycleOwner(), launcherStartedObserver);

    // Show a confirmation dialog on whether to activate the attack, this disables the 'close'
    // and 'start' button if the user activates the attack
    final Observer<String> armamentActivateConfirmationObserver = s -> {
      if (s == null) {
        return;
      }
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
    };
    sessionViewModel.launcherActivateConfirmation.observe(getViewLifecycleOwner(),
            armamentActivateConfirmationObserver);

    // Shows a dialog where the user can select a target access point
    final Observer<ArrayList<AccessPointData>> finishScanningObserver = targetList -> {
      if (targetList == null) {
        return;
      }
      showDialogForTargetSelection(targetList);
      sessionViewModel.userWantsToScanForAccessPoint = false;
      sessionViewModel.scannedAccessPoints.setValue(null);
      sessionViewModel.launcherFinishScanning.setValue(null);
    };
    sessionViewModel.launcherFinishScanning.observe(getViewLifecycleOwner(),
            finishScanningObserver);

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

    // Allow the user to close or deactivate the currently running attack by
    // enabling the 'close' button so the user can click it
    final Observer<String> launcherMainTaskObserver = s -> {
      if (s == null) {
        return;
      }
      binding.linearProgressIndicatorMainTaskIndicatorManualArma.setVisibility(View.VISIBLE);
      binding.buttonCloseManualArma.setEnabled(true);
    };
    sessionViewModel.launcherMainTaskCreated.observe(getViewLifecycleOwner(),
            launcherMainTaskObserver);

    // Send a deactivation request to the launcher which will make the launcher restart,
    // this also enables the 'start' button
    final Observer<String> launcherExecutionResultObserver = s -> {
      if (s == null) {
        return;
      }
      if (s.equals("Failed")) {
        sessionViewModel.writeControlCodeDeactivationToLauncher();
      } else if (s.equals("Success")) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Successful Attack")
                .setMessage("Successfully penetrated " +
                        sessionViewModel.targetAccessPoint + ", using " +
                        sessionViewModel.selectedArmament)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
      }
      binding.linearProgressIndicatorMainTaskIndicatorManualArma.setVisibility(View.INVISIBLE);
      binding.buttonStartManualArma.setEnabled(true);

    };
    sessionViewModel.launcherExecutionResult.observe(getViewLifecycleOwner(),
            launcherExecutionResultObserver);

    // Receive updates for any error cause by user input
    setupSerialInputErrorListener();
    // Receive updates for any error while reading data from the serial output
    setupSerialOutputErrorListener();
  }

  private void showDialogForTargetSelection(ArrayList<AccessPointData> targetList) {
    String[] choicesSsid = new String[targetList.size()];
    String[] choicesMacAddress = new String[targetList.size()];

    final int[] checkedItem = {-1};
    for (int i = 0; i < targetList.size(); i++) {
      choicesSsid[i] = targetList.get(i).getSsid();
      choicesMacAddress[i] = targetList.get(i).getMacAddress();
    }

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Select Target")
            .setPositiveButton("SELECT", (dialog, which) -> {
              if (checkedItem[0] == -1) {
                return;
              }
              checkedItem[0] = -1;
              dialog.dismiss();
              binding.textInputEditTextMacAddressManualArma.requestFocus();
            })
            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
            .setSingleChoiceItems(choicesSsid, checkedItem[0], (dialog, which) -> {
              checkedItem[0] = which;
              binding.textInputEditTextMacAddressManualArma.setText(choicesMacAddress[which]);
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