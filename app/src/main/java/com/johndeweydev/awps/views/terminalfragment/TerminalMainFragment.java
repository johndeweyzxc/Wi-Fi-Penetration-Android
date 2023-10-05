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
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentTerminalMainBinding;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted.TerminalFragment;
import com.johndeweydev.awps.views.terminalfragment.terminalscreens.raw.TerminalRawFragment;

import java.util.ArrayList;

public class TerminalMainFragment extends Fragment {

  private FragmentTerminalMainBinding binding;
  private TerminalArgs terminalArgs = null;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (getArguments() == null) {
      Log.d("dev-log", "TerminalViewPagerFragment.onCreateView: No arguments found");
    } else {
      initializeTerminalViewPagerFragmentArgs();
    }

    binding = FragmentTerminalMainBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  private void initializeTerminalViewPagerFragmentArgs() {
    TerminalMainFragmentArgs terminalViewPagerFragmentArgs = null;

    try {
      terminalViewPagerFragmentArgs = TerminalMainFragmentArgs.fromBundle(getArguments());
    } catch (IllegalArgumentException e) {
      Log.d("dev-log", "TerminalViewPagerFragment" +
              ".initializeTerminalViewPagerFragmentArgs: " + e.getMessage());
    }
    if (terminalViewPagerFragmentArgs != null) {
      terminalArgs = terminalViewPagerFragmentArgs.getTerminalArgs();
    }
  }

  ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      super.onPageScrolled(position, positionOffset, positionOffsetPixels);
      super.onPageSelected(position);
      TabLayout tabLayout = binding.tabLayoutTerminalViewPager;
      tabLayout.selectTab(tabLayout.getTabAt(position));
    }
  };

  TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
      binding.viewPagerTerminalViewPager.setCurrentItem(tab.getPosition());
      TabLayout tabLayout = binding.tabLayoutTerminalViewPager;
      tabLayout.selectTab(tabLayout.getTabAt(tab.getPosition()));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

  };

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (terminalArgs != null) {
      ArrayList<Fragment> fragmentList = new ArrayList<>();
      fragmentList.add(new TerminalFragment());
      fragmentList.add(new TerminalRawFragment());

      FragmentStateAdapter adapter = new TerminalMainVPAdapter(
              terminalArgs,
              fragmentList,
              requireActivity().getSupportFragmentManager(),
              getLifecycle()
      );

      binding.viewPagerTerminalViewPager.setAdapter(adapter);
      binding.tabLayoutTerminalViewPager.addOnTabSelectedListener(tabSelectedListener);
      binding.viewPagerTerminalViewPager.registerOnPageChangeCallback(pageChangeCallback);

    } else {
      Log.d("dev-log", "TerminalViewPagerFragment.onViewCreate: No arguments found");
    }

    binding.appBarTerminalViewPager.setNavigationOnClickListener(v ->
            binding.drawerLayoutTeminalViewPager.open()
    );
    binding.navMenuViewTerminalViewPager.setNavigationItemSelectedListener(this::navItemSelected);
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  @Override
  public void onResume() {
    super.onResume();
    Log.d("dev-log", "TerminalViewPagerFragment.onResume: Fragment resumed");

    if (terminalArgs != null) {
      int deviceId = terminalArgs.getDeviceId();
      int portNum = terminalArgs.getPortNum();
      UsbSerialViewModel.setTheDriverOfDevice(deviceId, portNum);

      if (UsbSerialViewModel.hasUsbDevicePermission()) {
        int baudRate = terminalArgs.getBaudRate();
        UsbSerialViewModel.connectToDevice(portNum, baudRate);
        UsbSerialViewModel.startEventDrivenReadFromDevice();
      } else {
        Log.w("dev-log", "TerminalViewPagerFragment.onResume: " +
                "Permission for usb device, not found");
      }
    }
  }

  @Override
  public void onPause() {

    if (terminalArgs != null) {
      if (UsbSerialViewModel.hasUsbDevicePermission()) {
        UsbSerialViewModel.disconnectFromDevice();
        UsbSerialViewModel.stopEventDrivenReadFromDevice();
      } else {
        Log.w("dev-log", "TerminalViewPagerFragment.onPause: " +
                "Permission for usb device, not found");
      }
    }
    super.onPause();
    Log.d("dev-log", "TerminalViewPagerFragment.onPause: Fragment paused");
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