package com.johndeweydev.awps.views.hashinfomodalbottomsheetdialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.johndeweydev.awps.databinding.DialogSheetBottomModalHashInfoBinding;

public class HashInfoModalBottomSheetDialog extends BottomSheetDialogFragment {

  private DialogSheetBottomModalHashInfoBinding binding;
  private HashInfoModalBottomArgs hashInfoModalBottomArgs = null;

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    binding = DialogSheetBottomModalHashInfoBinding.inflate(inflater, container, false);
    if (getArguments() == null) {
      Log.d("dev-log", "HashInfoModalBottomSheetDialog.onCreateView: " +
              "Get arguments is null");
    } else {
      Log.d("dev-log", "HashInfoModalBottomSheetDialog.onCreateView: " +
              "Initializing fragment args");
      HashInfoModalBottomSheetDialogArgs hashInfoModalBottomSheetDialogArgs;
      hashInfoModalBottomSheetDialogArgs = HashInfoModalBottomSheetDialogArgs.fromBundle(
              getArguments());
      hashInfoModalBottomArgs = hashInfoModalBottomSheetDialogArgs.getHashInfoModalBottomArgs();
    }
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.textViewAccessPointNameSheetBottomModalHash.setText(hashInfoModalBottomArgs.getSsid());
    binding.textViewBssidSheetBottomModalHash.setText(hashInfoModalBottomArgs.getBssid());

    binding.textViewClientMacAddressValueSheetBottomModalHash.setText(
            hashInfoModalBottomArgs.getClientMacAddress());

    binding.textViewKeyTypeSheetBottomModalHash.setText(hashInfoModalBottomArgs.getKeyType());
    binding.textViewHashDataSheetBottomModalHash.setText(hashInfoModalBottomArgs.getHashData());
    binding.textViewKeyDataSheetBottomModalHash.setText(hashInfoModalBottomArgs.getKeyData());
    binding.textViewDateCapturedSheetBottomModalHash.setText(
            hashInfoModalBottomArgs.getDateCaptured());
  }
}
