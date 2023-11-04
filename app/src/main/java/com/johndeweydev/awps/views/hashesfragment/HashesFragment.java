package com.johndeweydev.awps.views.hashesfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.data.HashInfoEntity;
import com.johndeweydev.awps.databinding.FragmentHashesBinding;
import com.johndeweydev.awps.viewmodels.hashinfoviewmodel.HashInfoViewModel;

import java.util.ArrayList;

public class HashesFragment extends Fragment {

  private FragmentHashesBinding binding;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentHashesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    HashInfoViewModel hashInfoViewModel = new ViewModelProvider(this)
            .get(HashInfoViewModel.class);

    HashesRvAdapterEvent hashesRvAdapterEvent = hashInfoModalBottomArgs -> {
      HashesFragmentDirections.ActionHashesFragmentToHashInfoModalBottomSheetDialog action;
      action = HashesFragmentDirections.actionHashesFragmentToHashInfoModalBottomSheetDialog(
              hashInfoModalBottomArgs);
      Navigation.findNavController(binding.getRoot()).navigate(action);
    };

    HashesRvAdapter hashesRvAdapter = new HashesRvAdapter(hashesRvAdapterEvent);
    binding.recyclerViewHashInformationHashes.setAdapter(hashesRvAdapter);
    binding.recyclerViewHashInformationHashes.setLayoutManager(
            new LinearLayoutManager(requireContext()));

    binding.materialToolBarHashes.setOnMenuItemClickListener(item -> {
      if (item.getItemId() == R.id.deleteMenuNavItemHashes) {
        deleteConfirmationDialog(hashInfoViewModel, hashesRvAdapter);
        return true;
      }
      return false;
    });

    binding.materialToolBarHashes.setNavigationOnClickListener(v -> showExitConfirmationDialog());

    final Observer<ArrayList<HashInfoEntity>> hashInfoEntitiesObserver =
            hashesRvAdapter::addAllHashInformation;
    hashInfoViewModel.getHashInfoEntities().observe(getViewLifecycleOwner(),
            hashInfoEntitiesObserver);

    hashInfoViewModel.getAllHashInfo();
  }

  private void showExitConfirmationDialog() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Exit Database")
            .setMessage("You pressed the back button, do you want to leave the database?")
            .setPositiveButton("YES", (dialog, which) -> {
              Navigation.findNavController(binding.getRoot()).popBackStack();
            })
            .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();
  }

  private void deleteConfirmationDialog(
          HashInfoViewModel hashInfoViewModel, HashesRvAdapter hashesRvAdapter) {

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Delete All Hashes")
            .setMessage("Do you want to delete all hash information? " +
                    "This action is irreversible")
            .setPositiveButton("DELETE", (dialog, which) -> {
              hashInfoViewModel.deleteAllHashInfo();
              hashesRvAdapter.removeAllHashInformation();
            })
            .setNegativeButton("CANCEL", (dialog, which) -> {
              dialog.dismiss();
            }).show();
  }

  @Override
  public void onDestroy() {
    binding = null;
    super.onDestroy();
  }
}