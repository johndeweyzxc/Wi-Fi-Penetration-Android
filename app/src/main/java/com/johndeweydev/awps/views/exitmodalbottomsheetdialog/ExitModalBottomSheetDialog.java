package com.johndeweydev.awps.views.exitmodalbottomsheetdialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.DialogSheetBottomModalExitBinding;

public class ExitModalBottomSheetDialog extends BottomSheetDialogFragment {

  private DialogSheetBottomModalExitBinding binding;

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    binding = DialogSheetBottomModalExitBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.buttonGoTerminalSheetBottomModalExit.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigate(
                    R.id.action_exitModalBottomSheetDialog_to_terminalFragment
            )
    );

    binding.buttonGoDevicesSheetBottomModalExit.setOnClickListener(v ->
            NavHostFragment.findNavController(this).navigate(
                    R.id.action_exitModalBottomSheetDialog_to_devicesFragment
            )
    );

    binding.buttonCancelSheetBottomModalExit.setOnClickListener(v -> this.dismiss());
  }
}
