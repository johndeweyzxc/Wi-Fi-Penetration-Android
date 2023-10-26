package com.johndeweydev.awps.views.terminalfragment;

import android.app.AlertDialog;
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
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentTerminalMainBinding;
import com.johndeweydev.awps.usbserial.UsbSerialStatus;
import com.johndeweydev.awps.viewmodels.sessionviewmodel.SessionViewModel;
import com.johndeweydev.awps.viewmodels.usbserialviewmodel.UsbSerialViewModel;
import com.johndeweydev.awps.views.autoarmafragment.AutoArmaArgs;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted.TerminalFragment;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.raw.TerminalRawFragment;

import java.util.ArrayList;

public class TerminalMainFragment extends Fragment {

  private FragmentTerminalMainBinding binding;
  private String selectedArmament;
  private UsbSerialViewModel usbSerialViewModel;
  private SessionViewModel sessionViewModel;
  private TerminalArgs terminalArgs = null;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);
    binding = FragmentTerminalMainBinding.inflate(inflater, container, false);

    if (getArguments() == null) {
      throw new NullPointerException("getArguments is null");
    } else {
      Log.d("dev-log", "TerminalMainFragment.onCreateView: Initializing fragment args");
      initializeTerminalMainFragmentArgs();
    }
    return binding.getRoot();
  }

  private void initializeTerminalMainFragmentArgs() {
    TerminalMainFragmentArgs terminalMainFragmentArgs;
    terminalMainFragmentArgs = TerminalMainFragmentArgs.fromBundle(getArguments());
    terminalArgs = terminalMainFragmentArgs.getTerminalArgs();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (terminalArgs == null) {
      // TODO: Replace NPE, instead show an error message and pop this fragment
      throw new NullPointerException("terminalArgs is null");
    }
    initializeViewPager();
    synchronizeTabsWithViewPager();
    binding.materialToolBarTerminalMain.setNavigationOnClickListener(v ->
            binding.drawerLayoutTeminalViewPager.open()
    );
    binding.navigationViewTerminalMain.setNavigationItemSelectedListener(
            this::navItemSelected);
    setupErrorWriteListener();
    setupErrorOnNewDataListener();
  }

  private void synchronizeTabsWithViewPager() {
    TabLayout tabLayout = binding.tabLayoutTerminalMain;
    ViewPager2 viewPager2 = binding.viewPagerTerminalMain;
    TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy;
    tabConfigurationStrategy = (tab, position) -> {
      if (position == 0) {
        tab.setText("Formatted");
      } else if (position == 1) {
        tab.setText("Raw");
      }
    };
    new TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy).attach();
  }

  private void initializeViewPager() {
    Bundle bundle = new Bundle();
    bundle.putInt("deviceId", terminalArgs.getDeviceId());
    bundle.putInt("portNum", terminalArgs.getPortNum());
    bundle.putInt("baudRate", terminalArgs.getBaudRate());

    ArrayList<Fragment> fragmentList = new ArrayList<>();

    TerminalFragment terminalFragment = new TerminalFragment();
    terminalFragment.setArguments(bundle);
    fragmentList.add(terminalFragment);

    TerminalRawFragment terminalRawFragment = new TerminalRawFragment();
    terminalRawFragment.setArguments(bundle);
    fragmentList.add(terminalRawFragment);

    FragmentStateAdapter adapter = new TerminalMainVPAdapter(terminalArgs, fragmentList,
            getChildFragmentManager(), getLifecycle()
    );
    binding.viewPagerTerminalMain.setAdapter(adapter);
  }

  private void setupErrorWriteListener() {
    final Observer<String> writeErrorListener = s -> {
      if (s == null) {
        return;
      }
      usbSerialViewModel.currentSerialInputError.setValue(null);
      Log.d("dev-log", "TerminalMainFragment.setupErrorWriteListener: " +
              "Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "TerminalMainFragment.setupErrorWriteListener: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Error writing " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "TerminalMainFragment.setupErrorWriteListener: " +
              "Popping this fragment off the back stack");
      Navigation.findNavController(binding.getRoot()).popBackStack();
    };
    usbSerialViewModel.currentSerialInputError.observe(getViewLifecycleOwner(), writeErrorListener);
  }

  private void setupErrorOnNewDataListener() {
    final Observer<String> onNewDataErrorListener = s -> {
      if (s == null) {
        return;
      }
      usbSerialViewModel.currentSerialOutputError.setValue(null);
      Log.d("dev-log", "TerminalMainFragment.setupErrorOnNewDataListener: " +
              "Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "TerminalMainFragment.setupErrorOnNewDataListener: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "TerminalMainFragment.setupErrorOnNewDataListener: " +
              "Popping this fragment off the back stack");
      Navigation.findNavController(binding.getRoot()).popBackStack();
    };
    usbSerialViewModel.currentSerialOutputError.observe(
            getViewLifecycleOwner(), onNewDataErrorListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "TerminalMainFragment.onResume: Fragment resumed");
    Log.d("dev-log", "TerminalMainFragment.onResume: Connecting to device");
    connectToDevice();
  }

  private void connectToDevice() {
    if (terminalArgs == null) {
      throw new NullPointerException("terminalArgs is null");
    }

    int deviceId = terminalArgs.getDeviceId();
    int portNum = terminalArgs.getPortNum();
    UsbSerialStatus status = usbSerialViewModel.connectToDevice(
            19200, 8, 1, UsbSerialPort.PARITY_NONE, deviceId, portNum);

    if (status.equals(UsbSerialStatus.SUCCESSFULLY_CONNECTED)
            || status.equals(UsbSerialStatus.ALREADY_CONNECTED)
    ) {
      Log.d("dev-log",
              "TerminalMainFragment.connectToDevice: Starting event read");
      usbSerialViewModel.startEventDrivenReadFromDevice();
    } else if (status.equals(UsbSerialStatus.FAILED_TO_CONNECT)) {
      Log.d("dev-log", "TerminalMainFragment.connectToDevice: Stopping event read");
      usbSerialViewModel.stopEventDrivenReadFromDevice();
      Log.d("dev-log", "TerminalMainFragment.connectToDevice: " +
              "Disconnecting from the device");
      usbSerialViewModel.disconnectFromDevice();
      Toast.makeText(requireActivity(), "Failed to connect to the device", Toast.LENGTH_SHORT)
              .show();
      Log.d("dev-log", "TerminalMainFragment.connectToDevice: " +
              "Popping this fragment off the back stack");
      Navigation.findNavController(binding.getRoot()).popBackStack();
    }
  }

  @Override
  public void onPause() {
    Log.d("dev-log", "TerminalMainFragment.onPause: Stopping event read");
    usbSerialViewModel.stopEventDrivenReadFromDevice();
    Log.d("dev-log", "TerminalMainFragment.onPause: Disconnecting from the device");
    usbSerialViewModel.disconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "TerminalMainFragment.onPause: Fragment paused");
  }

  private boolean navItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.automaticAttackMain) {
      binding.drawerLayoutTeminalViewPager.close();
      showAttackTypeDialogSelector();
      return true;
    } else if (item.getItemId() == R.id.manualAttackMain) {
      binding.drawerLayoutTeminalViewPager.close();
      Navigation.findNavController(binding.getRoot()).navigate(
              R.id.action_terminalMainFragment_to_armamentSelectionFragment);
      return true;
    } else if (item.getItemId() == R.id.settingsMenuNavItemTerminalMain) {
      binding.drawerLayoutTeminalViewPager.close();

      // TODO: Navigate to settings

      return true;
    } else if (item.getItemId() == R.id.infoMenuNavItemsTerminalMain) {
      binding.drawerLayoutTeminalViewPager.close();

      // TODO: Navigate to info

      return true;
    }
    return false;
  }

  private void showAttackTypeDialogSelector() {
    final String[] choices = new String[]{"PMKID Based Attack", "MIC Based Attack", "Deauther"};
    sessionViewModel = new ViewModelProvider(requireActivity())
            .get(SessionViewModel.class);

    final int[] checkedItem = {-1};

    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
    builder
            .setTitle("Select attack mode")
            .setPositiveButton("Proceed", ((dialog, which) -> {
              if (checkedItem[0] == -1) {
                return;
              }
              checkedItem[0] = -1;
              navigateToAutoArmaFragment();
            }))
            .setNegativeButton("Cancel", ((dialog, which) -> {}))
            .setSingleChoiceItems(choices, checkedItem[0], ((dialog, which) -> {

              checkedItem[0] = which;
              selectedArmament = choices[which];
            }));

    AlertDialog dialog = builder.create();
    dialog.show();
  }

  private void navigateToAutoArmaFragment() {
    Log.d("dev-log", "TerminalMainFragment.showAttackTypeDialogSelector: " +
            "Navigating to auto arma main fragment");

    AutoArmaArgs autoArmaArgs = new AutoArmaArgs(
            terminalArgs.getDeviceId(),
            terminalArgs.getPortNum(),
            terminalArgs.getBaudRate(),
            selectedArmament);

    TerminalMainFragmentDirections
            .ActionTerminalMainFragmentToAutoArmaMainFragment action;
    action = TerminalMainFragmentDirections
            .actionTerminalMainFragmentToAutoArmaMainFragment(autoArmaArgs);
    Navigation.findNavController(binding.getRoot()).navigate(action);
  }
}