package com.johndeweydev.awps.view.settingsfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.johndeweydev.awps.UserDefinedSettings;
import com.johndeweydev.awps.databinding.FragmentSettingsBinding;

import java.util.Objects;

public class SettingsFragment extends Fragment {

  private FragmentSettingsBinding binding;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentSettingsBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.materialToolBarSettings.setOnClickListener(v ->
            Navigation.findNavController(binding.getRoot()).popBackStack());

    binding.numberPickerTargetSizeSettings.setValue(
            UserDefinedSettings.NUMBER_OF_PREVIOUSLY_ATTACKED_TARGETS);
    binding.numberPickerAllocatedAttackTimeSettings.setValue(
            UserDefinedSettings.ALLOCATED_TIME_FOR_EACH_ATTACK);
    binding.textInputEditTextServerUrlSettings.setText(UserDefinedSettings.REST_API_URL);

    binding.numberPickerTargetSizeSettings.setMaxValue(30);
    binding.numberPickerTargetSizeSettings.setMinValue(2);
    binding.numberPickerAllocatedAttackTimeSettings.setMaxValue(20);
    binding.numberPickerAllocatedAttackTimeSettings.setMinValue(2);

    binding.buttonApplySettings.setOnClickListener(v -> {
      UserDefinedSettings.ALLOCATED_TIME_FOR_EACH_ATTACK = binding
              .numberPickerAllocatedAttackTimeSettings.getValue();
      UserDefinedSettings.NUMBER_OF_PREVIOUSLY_ATTACKED_TARGETS = binding
              .numberPickerTargetSizeSettings.getValue();
      UserDefinedSettings.REST_API_URL = Objects.requireNonNull(binding.
              textInputEditTextServerUrlSettings.getText()).toString();
      Toast.makeText(requireActivity(), "Settings applied", Toast.LENGTH_LONG).show();
      Navigation.findNavController(binding.getRoot()).popBackStack();
    });
  }
}