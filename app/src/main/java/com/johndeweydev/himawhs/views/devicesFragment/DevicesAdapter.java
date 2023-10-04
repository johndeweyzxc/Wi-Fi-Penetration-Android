package com.johndeweydev.himawhs.views.devicesFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.himawhs.R;
import com.johndeweydev.himawhs.usbserial.UsbDeviceItem;
import com.johndeweydev.himawhs.views.terminalFragment.TerminalArgs;

import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesAdapterViewHolder> {

  private final ArrayList<UsbDeviceItem> deviceModelList = new ArrayList<>();

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
    UsbDeviceItem currentItem = deviceModelList.get(position);
    holder.textViewDeviceName.setText(currentItem.getDeviceName());
    holder.textViewDeviceProductId.setText(currentItem.getDeviceProductId());
    holder.textViewDeviceVendorId.setText(currentItem.getDeviceVendorId());

    holder.deviceListItemLayout.setOnClickListener(v -> {
      TerminalArgs terminalArgs = new TerminalArgs(
              currentItem.device.getDeviceId(),
              currentItem.devicePort,
              19200
      );

      DevicesFragment.setTerminalArgs(terminalArgs);
      DevicesFragment.requestUsbPermission();
    });
  }

  @Override
  public int getItemCount() {
    return deviceModelList.size();
  }

  public void appendData(UsbDeviceItem usbDeviceItem) {
    deviceModelList.add(usbDeviceItem);
  }
}
