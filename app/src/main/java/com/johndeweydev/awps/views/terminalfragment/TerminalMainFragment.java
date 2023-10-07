package com.johndeweydev.awps.views.terminalfragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentTerminalMainBinding;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted.TerminalFragment;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.raw.TerminalRawFragment;

import java.util.ArrayList;

public class TerminalMainFragment extends Fragment {

  private FragmentTerminalMainBinding binding;
  private UsbSerialViewModel usbSerialViewModel;
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
            requireActivity().getSupportFragmentManager(), getLifecycle()
    );

    binding.viewPagerTerminalViewPager.setAdapter(adapter);

    TabLayout tabLayout = binding.tabLayoutTerminalViewPager;
    ViewPager2 viewPager2 = binding.viewPagerTerminalViewPager;
    TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy;
    tabConfigurationStrategy = (tab, position) -> {
      if (position == 0) {
        tab.setText("Formatted");
      } else if (position == 1) {
        tab.setText("Raw");
      }
    };
    new TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy).attach();

    binding.appBarTerminalViewPager.setNavigationOnClickListener(v ->
            binding.drawerLayoutTeminalViewPager.open()
    );
    binding.navMenuViewTerminalViewPager.setNavigationItemSelectedListener(this::navItemSelected);
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
    if (item.getItemId() == R.id.settingsMenuNavItemTerminalMain) {
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
}