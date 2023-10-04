package com.johndeweydev.himawhs.views.devicesFragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.johndeweydev.himawhs.usbserial.UsbDeviceItem;
import com.johndeweydev.himawhs.viewmodels.UsbSerialViewModel;

import java.util.ArrayList;

public class DevicesFragment extends Fragment {

  private FragmentDevicesBinding binding;
  private UsbSerialViewModel usbSerialViewModel;
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
    usbSerialViewModel = new ViewModelProvider(requireActivity()).get(UsbSerialViewModel.class);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    devicesAdapter = new DevicesAdapter();
    binding.recyclerViewDevices.setAdapter(devicesAdapter);
    binding.recyclerViewDevices.setLayoutManager(new LinearLayoutManager(requireContext()));

    final Observer<ArrayList<UsbDeviceItem>> deviceListObserver = this::handleUpdateFromLivedata;
    usbSerialViewModel.devicesList.observe(getViewLifecycleOwner(), deviceListObserver);

    int size = usbSerialViewModel.checkAvailableUsbDevices(requireActivity(), Context.USB_SERVICE);
    if (size == 0) {
      Toast.makeText(requireActivity(), "No devices connected", Toast.LENGTH_SHORT).show();
      Log.d("dev-log", "DevicesFragment.onViewCreate: No devices connected");
    }

    binding.backButtonDevices.setNavigationOnClickListener(v ->
            Navigation.findNavController(binding.getRoot()).popBackStack()
    );
  }

  private void handleUpdateFromLivedata(ArrayList<UsbDeviceItem> usbDeviceItemList) {
    for (int i = 0; i < usbDeviceItemList.size(); i++) {
      devicesAdapter.appendData(usbDeviceItemList.get(i));
      devicesAdapter.notifyItemInserted(i);
    }
  }
}