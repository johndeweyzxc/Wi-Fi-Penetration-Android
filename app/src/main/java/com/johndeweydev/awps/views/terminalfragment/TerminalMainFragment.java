package com.johndeweydev.awps.views.terminalfragment;

import android.annotation.SuppressLint;
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
import com.johndeweydev.awps.viewmodels.SessionViewModel;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted.TerminalFragment;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.raw.TerminalRawFragment;

import java.util.ArrayList;

public class TerminalMainFragment extends Fragment {

  private FragmentTerminalMainBinding binding;
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
      throw new NullPointerException("terminalArgs is null");
    }

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

    binding.materialToolBarTerminalMain.setNavigationOnClickListener(v ->
            binding.drawerLayoutTeminalViewPager.open()
    );
    binding.navigationViewTerminalMain.setNavigationItemSelectedListener(
            this::navItemSelected);
    setupErrorWriteListener();
    setupErrorOnNewDataListener();
  }

  private void setupErrorWriteListener() {
    final Observer<String> writeErrorListener = s -> {
      Toast.makeText(requireActivity(), "Error writing " + s, Toast.LENGTH_SHORT).show();
    };
    usbSerialViewModel.currentErrorInput.observe(getViewLifecycleOwner(), writeErrorListener);
  }

  private void setupErrorOnNewDataListener() {
    final Observer<String> onNewDataErrorListener = s -> {
      Toast.makeText(requireActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
    };
    usbSerialViewModel.currentErrorOnNewData.observe(
            getViewLifecycleOwner(), onNewDataErrorListener);
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "TerminalMainFragment.onResume: Fragment resumed");
    connectToDevice();
  }

  private void connectToDevice() {
    if (terminalArgs == null) {
      throw new NullPointerException("terminalArgs is null");
    }

    int deviceId = terminalArgs.getDeviceId();
    int portNum = terminalArgs.getPortNum();
    usbSerialViewModel.connectToDevice(
            19200, 8, 1, UsbSerialPort.PARITY_NONE, deviceId, portNum);
    usbSerialViewModel.startEventDrivenReadFromDevice();
  }

  @Override
  public void onPause() {
    disconnectFromDevice();
    super.onPause();
    Log.d("dev-log", "TerminalMainFragment.onPause: Fragment paused");
  }

  private void disconnectFromDevice() {
    usbSerialViewModel.stopEventDrivenReadFromDevice();
    usbSerialViewModel.disconnectFromDevice();
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
              Navigation.findNavController(binding.getRoot()).navigate(
                      R.id.action_terminalMainFragment_to_autoArmaMainFragment
              );

            }))
            .setNegativeButton("Cancel", ((dialog, which) -> {}))
            .setSingleChoiceItems(choices, checkedItem[0], ((dialog, which) -> {

              checkedItem[0] = which;
              sessionViewModel.setSelectedArmament(choices[which]);
            }));

    AlertDialog dialog = builder.create();
    dialog.show();
  }
}