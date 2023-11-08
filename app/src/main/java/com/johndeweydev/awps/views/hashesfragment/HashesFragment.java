package com.johndeweydev.awps.views.hashesfragment;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
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
import com.johndeweydev.awps.viewmodels.bridgeviewmodel.BridgeViewModel;
import com.johndeweydev.awps.viewmodels.hashinfoviewmodel.HashInfoViewModel;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HashesFragment extends Fragment {

  private FragmentHashesBinding binding;
  private HashInfoViewModel hashInfoViewModel;
  private BridgeViewModel bridgeViewModel;
  private HashInfoEntity swipedHashInfo;

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
    bridgeViewModel = new ViewModelProvider(this).get(BridgeViewModel.class);

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
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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

        if (direction == ItemTouchHelper.RIGHT) {
          swipedRight(position);
        } else if (direction == ItemTouchHelper.LEFT) {
          swipedLeft(position);
        }
      }

      private void swipedRight(int position) {
        // User swiped right on the recycler view item, this will upload the item in the rest api
        // server
        swipedHashInfo = hashesRvAdapter.removeHashInformation(position);

        Snackbar snackbarUpload = Snackbar.make(binding.recyclerViewHashInformationHashes,
                "Uploaded " + swipedHashInfo.ssid, Snackbar.LENGTH_LONG);
        snackbarUpload.setAction("Undo", v -> {
          hashesRvAdapter.appendHashInformation(swipedHashInfo, position);
          swipedHashInfo = null;
        });

        Snackbar.Callback callbackUpload = createCallBackForUploading();
        snackbarUpload.addCallback(callbackUpload);
        snackbarUpload.show();
      }

      private Snackbar.Callback createCallBackForUploading() {
        return new Snackbar.Callback() {
          @Override
          public void onShown(Snackbar sb) {
            super.onShown(sb);
          }
          @Override
          public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            if (swipedHashInfo == null) {
              return;
            }

            Log.d("dev-log", "HashesFragment.createCallBackForUploading: " +
                    "Uploading hash info to the rest api server");
            bridgeViewModel.uploadHash(swipedHashInfo);

            // Deletes the hash info in the database
            Log.d("dev-log", "HashesFragment.createCallBackForUploading: " +
                    "Deleting hash info in the database");
            hashInfoViewModel.deleteHashInfo(swipedHashInfo);
            swipedHashInfo = null;
          }
        };
      }

      private void swipedLeft(int position) {
        // User swiped left on the recycler view item, this will partially delete the item in the
        // view but the information is still in the database.
        swipedHashInfo = hashesRvAdapter.removeHashInformation(position);

        Snackbar snackbarDelete = Snackbar.make(binding.recyclerViewHashInformationHashes,
                "Deleted " + swipedHashInfo.ssid, Snackbar.LENGTH_LONG);
        snackbarDelete.setAction("Undo", v -> {
          hashesRvAdapter.appendHashInformation(swipedHashInfo, position);
          swipedHashInfo = null;
        });

        Snackbar.Callback callbackDelete = createSnackbarCallbackForDeleting();
        snackbarDelete.addCallback(callbackDelete);
        snackbarDelete.show();
      }

      private Snackbar.Callback createSnackbarCallbackForDeleting() {
        return new Snackbar.Callback() {
          @Override
          public void onShown(Snackbar sb) {
            super.onShown(sb);
          }
          @Override
          public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            if (swipedHashInfo == null) {
              return;
            }

            // Deletes the hash info in the database
            Log.d("dev-log", "HashesFragment.createSnackbarCallbackForDeleting: " +
                    "Deleting hash info in the database");
            hashInfoViewModel.deleteHashInfo(swipedHashInfo);
            swipedHashInfo = null;
          }
        };
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
                .addSwipeRightBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.md_theme_light_primary))
                .addSwipeRightActionIcon(R.drawable.ic_icn_upload_24)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(
                        requireActivity(), R.color.md_theme_light_secondary
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