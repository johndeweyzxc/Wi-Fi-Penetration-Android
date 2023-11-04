package com.johndeweydev.awps.views.devicesfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.models.data.UsbDeviceData;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

import java.util.ArrayList;

public class DevicesRVAdapter extends RecyclerView.Adapter<DevicesRVAdapter.DevicesAdapterViewHolder> {

  private final ArrayList<UsbDeviceData> deviceModelList = new ArrayList<>();
  private final DevicesRvAdapterEvent devicesRvAdapterEvent;

  public static class DevicesAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewDeviceNameValueDeviceListItem;
    public TextView textViewDeviceVendorIdDeviceListItem;
    public TextView textViewDeviceProductIdValueDeviceListItem;
    public MaterialCardView materialCardViewDeviceListItem;
    public DevicesAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewDeviceNameValueDeviceListItem = itemView.findViewById(
              R.id.textViewDeviceNameValueDeviceListItem);
      textViewDeviceVendorIdDeviceListItem = itemView.findViewById(
              R.id.textViewDeviceVendorIdDeviceListItem);
      textViewDeviceProductIdValueDeviceListItem = itemView.findViewById(
              R.id.textViewDeviceProductIdValueDeviceListItem);
      materialCardViewDeviceListItem = itemView.findViewById(R.id.materialCardViewDeviceListItem);
    }
  }

   public DevicesRVAdapter(DevicesRvAdapterEvent devicesRvAdapterEvent) {
    this.devicesRvAdapterEvent = devicesRvAdapterEvent;
   }

  @NonNull
  @Override
  public DevicesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.rv_device_item, parent, false
    );
    return new DevicesAdapterViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull DevicesAdapterViewHolder holder, int position) {
    UsbDeviceData currentItem = deviceModelList.get(position);
    holder.textViewDeviceNameValueDeviceListItem.setText(currentItem.getDeviceName());
    holder.textViewDeviceProductIdValueDeviceListItem.setText(currentItem.getDeviceProductId());
    holder.textViewDeviceVendorIdDeviceListItem.setText(currentItem.getDeviceVendorId());

    holder.materialCardViewDeviceListItem.setOnClickListener(v -> {

      TerminalArgs terminalArgs = new TerminalArgs(
              currentItem.usbDevice.getDeviceId(), currentItem.devicePort, 19200);
      devicesRvAdapterEvent.onDeviceClick(terminalArgs);
    });
  }

  @Override
  public int getItemCount() {
    return deviceModelList.size();
  }

  public void appendData(UsbDeviceData usbDeviceData) {
    deviceModelList.add(usbDeviceData);
  }
}
