package com.johndeweydev.himawhs.fragments.devicesFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.himawhs.R;
import com.johndeweydev.himawhs.models.DeviceModel;
import com.johndeweydev.himawhs.models.TerminalArgsModel;

import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesAdapterViewHolder> {

  private final ArrayList<DeviceModel> deviceModelList = new ArrayList<>();

  public static class DevicesAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewDeviceName;
    public TextView textViewDeviceVendorId;
    public TextView textViewDeviceProductId;
    public LinearLayout deviceListItemLayout;
    public DevicesAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewDeviceName = itemView.findViewById(R.id.textViewDeviceName);
      textViewDeviceVendorId = itemView.findViewById(R.id.textViewDeviceVendorId);
      textViewDeviceProductId = itemView.findViewById(R.id.textViewDeviceProductId);
      deviceListItemLayout = itemView.findViewById(R.id.deviceListItemLayout);
    }
  }

  @NonNull
  @Override
  public DevicesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.device_list_item, parent, false
    );
    return new DevicesAdapterViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull DevicesAdapterViewHolder holder, int position) {
    DeviceModel currentItem = deviceModelList.get(position);
    holder.textViewDeviceName.setText(currentItem.getDeviceName());
    holder.textViewDeviceProductId.setText(currentItem.getDeviceProductId());
    holder.textViewDeviceVendorId.setText(currentItem.getDeviceVendorId());

    holder.deviceListItemLayout.setOnClickListener(v -> {
      TerminalArgsModel terminalArgsModel = new TerminalArgsModel(
              currentItem.getDeviceId(),
              currentItem.getDevicePort(),
              115200
      );

      DevicesFragmentDirections.ActionDevicesFragmentToTerminalFragment action;
      action = DevicesFragmentDirections.actionDevicesFragmentToTerminalFragment(terminalArgsModel);
      Navigation.findNavController(holder.itemView).navigate(action);
    });
  }

  @Override
  public int getItemCount() {
    return deviceModelList.size();
  }

  public void appendData(DeviceModel deviceModel) {
    deviceModelList.add(deviceModel);
  }
}
