package com.johndeweydev.awps.views.autoarmafragment;

import static com.johndeweydev.awps.AppConstants.BAUD_RATE;
import static com.johndeweydev.awps.AppConstants.DATA_BITS;
import static com.johndeweydev.awps.AppConstants.PARITY_NONE;
import static com.johndeweydev.awps.AppConstants.STOP_BITS;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentAutoArmaBinding;
import com.johndeweydev.awps.models.data.AccessPointData;
import com.johndeweydev.awps.models.data.DeviceConnectionParamData;
import com.johndeweydev.awps.models.data.HashInfoEntity;
import com.johndeweydev.awps.models.repo.serial.sessionreposerial.SessionRepoSerial;
import com.johndeweydev.awps.viewmodels.hashinfoviewmodel.HashInfoViewModel;
import com.johndeweydev.awps.viewmodels.serial.sessionviewmodel.SessionAutoViewModel;
import com.johndeweydev.awps.viewmodels.serial.sessionviewmodel.SessionAutoViewModelFactory;

import java.util.Objects;

public class AutoArmaFragment extends Fragment {

  private FragmentAutoArmaBinding binding;
  private AutoArmaArgs autoArmaArgs = null;
  private SessionAutoViewModel sessionAutoViewModel;
  private HashInfoViewModel hashInfoViewModel;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    SessionRepoSerial sessionRepoSerial = new SessionRepoSerial();
    SessionAutoViewModelFactory sessionAutoViewModelFactory = new SessionAutoViewModelFactory(
            sessionRepoSerial);

    sessionAutoViewModel = new ViewModelProvider(this, sessionAutoViewModelFactory)
            .get(SessionAutoViewModel.class);
    hashInfoViewModel = new ViewModelProvider(this).get(HashInfoViewModel.class);
    binding = FragmentAutoArmaBinding.inflate(inflater, container, false);

    if (getArguments() == null) {
      Log.d("dev-log", "AutoArmaFragment.onCreateView: Get arguments is null");
    } else {
      Log.d("dev-log", "AutoArmaFragment.onCreateView: Initializing fragment args");
      AutoArmaFragmentArgs autoArmaFragmentArgs;
      autoArmaFragmentArgs = AutoArmaFragmentArgs.fromBundle(getArguments());
      autoArmaArgs = autoArmaFragmentArgs.getAutoArmaArgs();
    }
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (autoArmaArgs == null) {
      Log.d("dev-log", "AutoArmaFragment.onViewCreated: Auto arma args is null");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaFragment_to_devicesFragment);
      return;
    }

    sessionAutoViewModel.automaticAttack = false;
    sessionAutoViewModel.selectedArmament = autoArmaArgs.getSelectedArmament();
    String attackConfig = "Config: " + sessionAutoViewModel.selectedArmament;
    binding.textViewAttackConfigValueAutoArma.setText(attackConfig);

    binding.materialToolBarAutoArma.setOnClickListener(v ->
            showDialogAskUserToExitOfThisFragment());
    binding.materialToolBarAutoArma.setOnMenuItemClickListener(this::showDialogMenuOptionsToUser);
    binding.buttonStopStartAutoArma.setOnClickListener(v -> buttonPressed());

    AutoArmaRvAdapter autoArmaRvAdapter = setupRecyclerView();
    setupObservers(autoArmaRvAdapter);
  }

  private void showDialogAskUserToExitOfThisFragment() {
    if (Objects.equals(sessionAutoViewModel.userCommandState.getValue(), "RUN")) {
      sessionAutoViewModel.stopAttack();
    }

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Exit Manual Attack");

    if (sessionAutoViewModel.userCommandState.getValue().equals("RUN")) {
      builder.setMessage("You have an ongoing attack, Do you really want to exit auto attack?");
    } else {
      builder.setMessage("Do you really want to exit manual attack? You will be navigated back" +
              " to terminal");
    }

    builder.setPositiveButton("EXIT", (dialog, which) ->
            Navigation.findNavController(binding.getRoot()).popBackStack());
    builder.setNegativeButton("CANCEL", (dialog, which) -> {
      sessionAutoViewModel.startAttack();
      dialog.dismiss();
    });
    builder.show();
  }

  private void buttonPressed() {
    if (Objects.equals(sessionAutoViewModel.userCommandState.getValue(), "STOPPED")) {
      sessionAutoViewModel.startAttack();
      Log.d("dev-log", "AutoArmaFragment.buttonPressed: Attack started by user");
    } else if (Objects.equals(sessionAutoViewModel.userCommandState.getValue(), "RUN")) {
      sessionAutoViewModel.stopAttack();
      Log.d("dev-log", "AutoArmaFragment.buttonPressed: Attack stopped by user");
    }
  }

  private boolean showDialogMenuOptionsToUser(MenuItem menuItem) {
    String[] choices = getResources().getStringArray(R.array.dialog_options_auto_arma);

    String change_attack_type = getResources().getString(R.string.change_attack_type);
    String clearAttackLogs = getResources().getString(R.string.clear_attack_logs);
    String database = getResources().getString(R.string.database);
    String restart = getResources().getString(R.string.restart_launcher);
    String settings = getResources().getString(R.string.settings);

    if (menuItem.getItemId() != R.id.moreOptionsAutoArmaTopRightDialogMenu) {
      return false;
    }

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Menu Options");
    builder.setItems(choices, (dialog, which) -> {

      if (choices[which].equals(change_attack_type)) {
        showDialogAskUserToSelectAttackType();
      } else if (choices[which].equals(clearAttackLogs)) {
        AutoArmaRvAdapter adapter = (AutoArmaRvAdapter)
                binding.recyclerViewAttackLogsAutoArma.getAdapter();
        if (adapter != null) {
          adapter.clearLogs();
        } else {
          Toast.makeText(requireActivity(), "Attack logs adapter is not set",
                  Toast.LENGTH_LONG).show();
        }
      } else if (choices[which].equals(database)) {
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_autoArmaFragment_to_hashesFragment);
      } else if (choices[which].equals(restart)) {
        sessionAutoViewModel.writeControlCodeRestartLauncher();
      } else if (choices[which].equals(settings)) {
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_autoArmaFragment_to_settingsFragment);
      }
    }).show();

    return true;
  }

  private void showDialogAskUserToSelectAttackType() {
    final String[] choices = getResources().getStringArray(
            R.array.dialog_options_select_attack_auto_arma);

    final int[] checkedItem = {-1};

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Select attack type");
    builder.setPositiveButton("SELECT", (dialog, which) -> {
      if (checkedItem[0] == -1) return;

      checkedItem[0] = -1;
      String attackConfig = "Config: " + sessionAutoViewModel.selectedArmament;
      binding.textViewAttackConfigValueAutoArma.setText(attackConfig);
    });
    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
    builder.setSingleChoiceItems(choices, checkedItem[0], (dialog, which) -> {
      checkedItem[0] = which;
      sessionAutoViewModel.selectedArmament = choices[which];
    }).show();
  }

  private AutoArmaRvAdapter setupRecyclerView() {
    AutoArmaRvAdapter autoArmaRvAdapter = new AutoArmaRvAdapter();
    LinearLayoutManager layout = new LinearLayoutManager(requireActivity());
    layout.setStackFromEnd(true);
    binding.recyclerViewAttackLogsAutoArma.setAdapter(autoArmaRvAdapter);
    binding.recyclerViewAttackLogsAutoArma.setLayoutManager(layout);
    return autoArmaRvAdapter;
  }

  private void setupObservers(AutoArmaRvAdapter autoArmaRvAdapter) {
    final Observer<String> attackLogsObserver = log -> {
      if (log == null) {
        return;
      }
      autoArmaRvAdapter.appendData(log);
      binding.recyclerViewAttackLogsAutoArma.scrollToPosition(
              autoArmaRvAdapter.getItemCount() - 1
      );
    };
    sessionAutoViewModel.currentAttackLog.observe(getViewLifecycleOwner(), attackLogsObserver);
    setupSerialInputErrorListener();
    setupSerialOutputErrorListener();

    final Observer<AccessPointData> currentTargetObserver = target -> {
      if (target == null) {
        return;
      }
      String macAddress = target.macAddress();
      String ssid = target.ssid();
      binding.textViewMacAddressValueAutoArma.setText(
              sessionAutoViewModel.formatMacAddress(macAddress));
      binding.textViewSsidValueAutoArma.setText(ssid);
    };
    sessionAutoViewModel.currentTarget.observe(getViewLifecycleOwner(),
            currentTargetObserver);

    final Observer<Integer> nearbyApsObserver = nearby -> {
      String text = "Nearby Access Points: " + nearby;
      binding.textViewNearbyApAutoArma.setText(text);
    };
    sessionAutoViewModel.nearbyAps.observe(getViewLifecycleOwner(), nearbyApsObserver);

    final Observer<Integer> failedAttacksObserver = failed -> {
      String text = "Failed Attacks: " + failed;
      binding.textViewFailedAttacksAutoArma.setText(text);
    };
    sessionAutoViewModel.failedAttacks.observe(getViewLifecycleOwner(), failedAttacksObserver);

    final Observer<Integer> keysFoundObserver = keys -> {
      String text = "Pwned: " + keys;
      binding.textViewPwnedAutoArma.setText(text);
    };
    sessionAutoViewModel.pwned.observe(getViewLifecycleOwner(), keysFoundObserver);

    final Observer<String> userCommandStateObserver = state -> {
      switch (state) {
        case "PENDING STOP" -> buttonUiPendingStopState();
        case "RUN" -> buttonUiStopState();
        case "STOPPED" -> buttonUiStartState();
      }
    };
    sessionAutoViewModel.userCommandState.observe(getViewLifecycleOwner(),
            userCommandStateObserver);

    final Observer<String> launcherExecutionResultObserver = result -> {
      if (result == null) {
        return;
      }
      sessionAutoViewModel.launcherExecutionResult.setValue(null);
      if (result.equals("Success")) {
        // Save result in the database
        HashInfoEntity copyOfLauncherExecutionResultData;
        copyOfLauncherExecutionResultData = sessionAutoViewModel.launcherExecutionResultData;
        hashInfoViewModel.addNewHashInfo(copyOfLauncherExecutionResultData);
      }
    };
    sessionAutoViewModel.launcherExecutionResult.observe(getViewLifecycleOwner(),
            launcherExecutionResultObserver);
  }

  private void buttonUiStopState() {
    Drawable stop = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_btn_stop_24);
    binding.buttonStopStartAutoArma.setText(R.string.stop_manual_arma);
    binding.buttonStopStartAutoArma.setIcon(stop);
  }

  private void buttonUiStartState() {
    Drawable start = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_btn_start_24);
    binding.buttonStopStartAutoArma.setText(R.string.start_manual_arma);
    binding.buttonStopStartAutoArma.setIcon(start);
    binding.buttonStopStartAutoArma.setEnabled(true);
  }

  private void buttonUiPendingStopState() {
    binding.buttonStopStartAutoArma.setEnabled(false);
    String text = "Pending Stop";
    binding.buttonStopStartAutoArma.setText(text);
  }

  private void setupSerialInputErrorListener() {
    final Observer<String> serialInputErrorObserver = inputError -> {
      if (inputError == null) {
        return;
      }
      sessionAutoViewModel.currentSerialInputError.setValue(null);
      Log.d("dev-log", "AutoArmaFragment.setupSerialInputErrorListener: " +
              "Error on user input");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error writing " + inputError, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "AutoArmaFragment.setupSerialInputErrorListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaFragment_to_devicesFragment);
    };
    sessionAutoViewModel.currentSerialInputError.observe(getViewLifecycleOwner(),
            serialInputErrorObserver);
  }

  private void setupSerialOutputErrorListener() {
    final Observer<String> serialOutputErrorObserver = outputError -> {
      if (outputError == null) {
        return;
      }
      sessionAutoViewModel.currentSerialOutputError.setValue(null);
      Log.d("dev-log", "AutoArmaFragment.setupSerialOutputErrorListener: " +
              "Error on serial output");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Error: " + outputError, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "AutoArmaFragment.setupSerialOutputErrorListener: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaFragment_to_devicesFragment);
    };
    sessionAutoViewModel.currentSerialOutputError.observe(
            getViewLifecycleOwner(), serialOutputErrorObserver);

  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "AutoArmaFragment.onResume: Fragment resumed");
    Log.d("dev-log", "AutoArmaFragment.onResume: Connecting to device");
    connectToDevice();
    sessionAutoViewModel.setLauncherEventHandler();
  }

  private void connectToDevice() {
    int deviceId = autoArmaArgs.getDeviceId();
    int portNum = autoArmaArgs.getPortNum();
    DeviceConnectionParamData deviceConnectionParamData = new DeviceConnectionParamData(
            BAUD_RATE, DATA_BITS, STOP_BITS, PARITY_NONE,
            deviceId, portNum);
    String result = sessionAutoViewModel.connectToDevice(deviceConnectionParamData);

    if (result.equals("Successfully connected") || result.equals("Already connected")) {

      Log.d("dev-log",
              "AutoArmaFragment.connectToDevice: Starting event read");
      sessionAutoViewModel.startEventDrivenReadFromDevice();
    } else if (result.equals("Failed to connect")) {

      Log.d("dev-log", "AutoArmaFragment.connectToDevice: " +
              "Failed to connect to the device");
      stopEventReadAndDisconnectFromDevice();
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      Log.d("dev-log", "AutoArmaFragment.connectToDevice: " +
              "Popping fragments up to but not including devices fragment");
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_autoArmaFragment_to_devicesFragment);
    }
  }

  @Override
  public void onPause() {
    sessionAutoViewModel.currentAttackLog.setValue(null);
    Log.d("dev-log", "AutoArmaFragment.onPause: Fragment pausing");
    stopEventReadAndDisconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "AutoArmaFragment.onPause: Fragment paused");
  }

  @Override
  public void onDestroyView() {
    sessionAutoViewModel.attackLogNumber = 0;
    binding = null;
    super.onDestroyView();
  }

  private void stopEventReadAndDisconnectFromDevice() {
    Log.d("dev-log", "AutoArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Stopping event read");
    sessionAutoViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "AutoArmaFragment.stopEventReadAndDisconnectFromDevice: " +
            "Disconnecting from the device");
    sessionAutoViewModel.disconnectFromDevice();
  }
}