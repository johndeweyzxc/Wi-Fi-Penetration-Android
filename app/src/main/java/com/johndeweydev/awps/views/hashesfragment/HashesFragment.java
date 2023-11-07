package com.johndeweydev.awps.views.hashesfragment;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.databinding.FragmentHashesBinding;
import com.johndeweydev.awps.models.data.HashInfoEntity;
import com.johndeweydev.awps.viewmodels.hashinfoviewmodel.HashInfoViewModel;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HashesFragment extends Fragment {

  private FragmentHashesBinding binding;
  private HashInfoViewModel hashInfoViewModel;
  private HashInfoEntity deletedHashInfo;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = FragmentHashesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    hashInfoViewModel = new ViewModelProvider(this).get(HashInfoViewModel.class);

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


    ItemTouchHelper.SimpleCallback recyclerViewSwipeCallback = recyclerViewSwipe(hashesRvAdapter);
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(recyclerViewSwipeCallback);
    itemTouchHelper.attachToRecyclerView(binding.recyclerViewHashInformationHashes);

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

  private ItemTouchHelper.SimpleCallback recyclerViewSwipe(HashesRvAdapter hashesRvAdapter) {
    return new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT) {
      @Override
      public boolean onMove(
              @NonNull RecyclerView recyclerView,
              @NonNull RecyclerView.ViewHolder viewHolder,
              @NonNull RecyclerView.ViewHolder target
      ) {
        return false;
      }

      @Override
      public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // The current position of the swiped recycler view item
        int position = viewHolder.getBindingAdapterPosition();

        if (direction != ItemTouchHelper.LEFT) {
          // User swiped right on the recycler view item
          return;
        }

        // User swiped left on the recycler view item, this will partially delete the item in the
        // view but the information is still in the database.
        deletedHashInfo = hashesRvAdapter.removeHashInformation(position);
        Snackbar snackbar = Snackbar.make(binding.recyclerViewHashInformationHashes,
                "Deleted " + deletedHashInfo.ssid, Snackbar.LENGTH_LONG
        ).setAction("Undo", v -> {
          hashesRvAdapter.appendHashInformation(deletedHashInfo, position);
          deletedHashInfo = null;
        });

        Snackbar.Callback onShownAndDismissedCallback = new Snackbar.Callback() {
          @Override
          public void onShown(Snackbar sb) {
            super.onShown(sb);
          }
          @Override
          public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            if (deletedHashInfo == null) {
              return;
            }
            // Deletes the hash info in the database
            hashInfoViewModel.deleteHashInfo(deletedHashInfo);
            deletedHashInfo = null;
          }
        };
        snackbar.addCallback(onShownAndDismissedCallback);
        snackbar.show();
      }

      @Override
      public void onChildDraw(
              @NonNull Canvas c,
              @NonNull RecyclerView recyclerView,
              @NonNull RecyclerView.ViewHolder viewHolder,
              float dX, float dY, int actionState, boolean isCurrentlyActive
      ) {

        new RecyclerViewSwipeDecorator.Builder(
                c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(
                        requireActivity(), R.color.md_theme_light_primary
                ))
                .addSwipeLeftActionIcon(R.drawable.ic_btn_delete_24)
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      }
    };
  }


  private void showExitConfirmationDialog() {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Exit Database");
    builder.setMessage("You pressed the back button, do you want to leave the database?");
    builder.setPositiveButton("YES", (dialog, which) -> Navigation.findNavController(
                    binding.getRoot()).popBackStack());
    builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).show();
  }

  private void deleteConfirmationDialog(
          HashInfoViewModel hashInfoViewModel, HashesRvAdapter hashesRvAdapter) {

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
    builder.setTitle("Delete All Hashes");
    builder.setMessage("Do you want to delete all hash information? " +
                    "This action is irreversible");
    builder.setPositiveButton("DELETE", (dialog, which) -> {
              hashInfoViewModel.deleteAllHashInfo();
              hashesRvAdapter.removeAllHashInformation();
            });
    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss()).show();
  }

  @Override
  public void onDestroy() {
    binding = null;
    super.onDestroy();
  }
}