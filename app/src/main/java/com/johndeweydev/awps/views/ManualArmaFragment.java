package com.johndeweydev.awps.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.johndeweydev.awps.databinding.FragmentManualArmaBinding;

public class ManualArmaFragment extends Fragment {

  private FragmentManualArmaBinding binding;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentManualArmaBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.materialToolBarManualArma.setOnClickListener(v -> {
      Navigation.findNavController(binding.getRoot()).popBackStack();
    });
  }
}