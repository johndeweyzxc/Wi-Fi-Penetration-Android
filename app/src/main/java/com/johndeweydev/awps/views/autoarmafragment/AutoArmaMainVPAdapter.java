package com.johndeweydev.awps.views.autoarmafragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AutoArmaMainVPAdapter extends FragmentStateAdapter {

  private final ArrayList<Fragment> fragmentList;

  public AutoArmaMainVPAdapter(
          ArrayList<Fragment> list,
          @NonNull FragmentManager fragmentManager,
          @NonNull Lifecycle lifecycle
  ) {
    super(fragmentManager, lifecycle);
    fragmentList = list;
  }

  @NonNull
  @Override
  public Fragment createFragment(int position) {
    return fragmentList.get(position);
  }

  @Override
  public int getItemCount() {
    return fragmentList.size();
  }
}
