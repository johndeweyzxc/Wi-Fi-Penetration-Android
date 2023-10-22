package com.johndeweydev.awps.views.autoarmafragment.autoarmascreens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.johndeweydev.awps.databinding.FragmentAutoArmaDashboardBinding;

public class AutoArmaDashboardFragment extends Fragment {

  private FragmentAutoArmaDashboardBinding binding;
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentAutoArmaDashboardBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }
}