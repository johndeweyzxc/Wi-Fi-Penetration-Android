package com.johndeweydev.awps.views.autoarmafragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.johndeweydev.awps.databinding.FragmentAutoArmaMainBinding;
import com.johndeweydev.awps.viewmodels.SessionViewModel;
import com.johndeweydev.awps.viewmodels.UsbSerialViewModel;
import com.johndeweydev.awps.views.autoarmafragment.autoarmascreens.AutoArmaDashboardFragment;
import com.johndeweydev.awps.views.autoarmafragment.autoarmascreens.AutoArmaLogsFragment;

import java.util.ArrayList;

public class AutoArmaMainFragment extends Fragment {

  private FragmentAutoArmaMainBinding binding;
  private SessionViewModel sessionViewModel;
  private UsbSerialViewModel usbSerialViewModel;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);
    binding = FragmentAutoArmaMainBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ArrayList<Fragment> fragmentList = new ArrayList<>();
    AutoArmaDashboardFragment autoArmaDashboardFragment = new AutoArmaDashboardFragment();
    fragmentList.add(autoArmaDashboardFragment);
    AutoArmaLogsFragment autoArmaLogsFragment = new AutoArmaLogsFragment();
    fragmentList.add(autoArmaLogsFragment);

    FragmentStateAdapter adapter = new AutoArmaMainVPAdapter(
            fragmentList, getChildFragmentManager(), getLifecycle());

    binding.viewPagerAutoArmaViewPager.setAdapter(adapter);

    binding.appBarAutoArmaMain.setOnClickListener(v -> {
      Navigation.findNavController(binding.getRoot()).popBackStack();
    });

  }


}