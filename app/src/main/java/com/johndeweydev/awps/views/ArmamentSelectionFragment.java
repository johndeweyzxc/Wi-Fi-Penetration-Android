package com.johndeweydev.awps.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentArmamentSelectionBinding;

public class ArmamentSelectionFragment extends Fragment {

  private FragmentArmamentSelectionBinding binding;
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentArmamentSelectionBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.materialToolBarArmamentSelection.setOnClickListener(v -> {
      Navigation.findNavController(binding.getRoot()).popBackStack();
    });

    binding.constraintLayoutPmkidBasedAttackArmamentSelection.setOnClickListener(v -> {
      // TODO: Pass bundle arguments to fragment
      navigateToManualArma();
    });
    binding.constraintLayoutDeautherArmamentSelection.setOnClickListener(v -> {
      // TODO: Pass bundle arguments to fragment
      navigateToManualArma();
    });
    binding.constraintLayoutMicBasedAttackArmamentSelection.setOnClickListener(v -> {
      // TODO: Pass bundle arguments to fragment
      navigateToManualArma();
    });
    binding.constraintLayoutReconnaissanceArmamentSelection.setOnClickListener(v -> {
      // TODO: Pass bundle arguments to fragment
      navigateToManualArma();
    });
  }

  private void navigateToManualArma() {
    Navigation.findNavController(binding.getRoot()).navigate(
            R.id.action_armamentSelectionFragment_to_manualArmaFragment);
  }
}