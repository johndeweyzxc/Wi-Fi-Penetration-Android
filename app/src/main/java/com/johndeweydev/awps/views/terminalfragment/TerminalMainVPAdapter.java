package com.johndeweydev.awps.views.terminalfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class TerminalMainVPAdapter extends FragmentStateAdapter {

  private final ArrayList<Fragment> fragmentList;
  private final TerminalArgs terminalArgs;

  public TerminalMainVPAdapter(
          TerminalArgs terminalArgs,
          ArrayList<Fragment> list,
          @NonNull FragmentManager fragmentManager,
          @NonNull Lifecycle lifecycle) {
    super(fragmentManager, lifecycle);
    fragmentList = list;
    this.terminalArgs = terminalArgs;
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Fragment currentFragment = fragmentList.get(position);
    Bundle args = new Bundle();
    args.putInt("deviceId", terminalArgs.getDeviceId());
    args.putInt("portNum", terminalArgs.getPortNum());
    args.putInt("baudRate", terminalArgs.getBaudRate());
    currentFragment.setArguments(args);
    return currentFragment;
  }

  @Override
  public int getItemCount() {
    return fragmentList.size();
  }


}
