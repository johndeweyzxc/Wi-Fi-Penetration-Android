package com.johndeweydev.himawhs.fragments.devicesFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.johndeweydev.himawhs.databinding.FragmentDevicesBinding;
import com.johndeweydev.himawhs.models.DeviceModel;
import com.johndeweydev.himawhs.viewmodels.DefaultMainViewModel;

import java.util.ArrayList;

public class DevicesFragment extends Fragment {

  private FragmentDevicesBinding binding;
  private DefaultMainViewModel defaultMainViewModel;
  private DevicesAdapter devicesAdapter;

  public DevicesFragment() {
    // Required empty public constructor
  }

  @Nullable
  @Override
  public View onCreateView(
          @NonNull LayoutInflater inflater,
          @Nullable ViewGroup container,
          @Nullable Bundle savedInstanceState
  ) {
    binding = FragmentDevicesBinding.inflate(inflater, container, false);
    defaultMainViewModel = new ViewModelProvider(requireActivity()).get(DefaultMainViewModel.class);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    devicesAdapter = new DevicesAdapter();
    binding.recyclerViewDevices.setAdapter(devicesAdapter);
    binding.recyclerViewDevices.setLayoutManager(new LinearLayoutManager(requireContext()));

    final Observer<ArrayList<DeviceModel>> deviceListObserver = this::handleUpdateFromLivedata;
    defaultMainViewModel.liveDeviceList.observe(getViewLifecycleOwner(), deviceListObserver);

    binding.scanDevices.setOnClickListener(view1 -> {
      int size = defaultMainViewModel.getAvailableDevicesFromSerial(requireActivity());
      if (size == 0) {
        Toast.makeText(requireActivity(), "No devices connected", Toast.LENGTH_SHORT).show();
      }
    });

    binding.backButtonDevices.setNavigationOnClickListener(v ->
            Navigation.findNavController(binding.getRoot()).popBackStack()
    );
  }

  private void handleUpdateFromLivedata(ArrayList<DeviceModel> deviceModelList) {
    for (int i = 0; i < deviceModelList.size(); i++) {
      devicesAdapter.appendData(deviceModelList.get(i));
      devicesAdapter.notifyItemInserted(i);
    }
  }
}