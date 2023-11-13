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
import java.util.Locale;

public class DevicesRVAdapter extends RecyclerView.Adapter<DevicesRVAdapter.DevicesAdapterViewHolder> {

  public interface Event {
    void onDeviceClick(TerminalArgs terminalArgs);
  }

  private final ArrayList<UsbDeviceData> deviceModelList = new ArrayList<>();
  private final Event event;

  public static class DevicesAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewDeviceName;
    public TextView textViewManufacturerName;
    public TextView textViewVendorId;
    public TextView textViewProductId;
    public MaterialCardView materialCardViewDeviceListItem;
    public DevicesAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewDeviceName = itemView.findViewById(R.id.textViewDeviceNameValueDeviceListItem);
      textViewManufacturerName = itemView.findViewById(R.id.textViewManufacturerDeviceListItem);
      textViewVendorId = itemView.findViewById(R.id.textViewDeviceVendorIdDeviceListItem);
      textViewProductId = itemView.findViewById(R.id.textViewDeviceProductIdValueDeviceListItem);
      materialCardViewDeviceListItem = itemView.findViewById(R.id.materialCardViewDeviceListItem);
    }
  }

   public DevicesRVAdapter(Event event) {
    this.event = event;
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
    String deviceName = currentItem.usbSerialDriver().getClass().getSimpleName().replace(
            "SerialDriver", "").toUpperCase();
    String manufacturerName = currentItem.usbDevice().getManufacturerName();
    String productId = String.format(Locale.US, "%04X",
            currentItem.usbDevice().getProductId());
    String vendorId = String.format(Locale.US, "%04X",
            currentItem.usbDevice().getVendorId());

    holder.textViewDeviceName.setText(deviceName);
    holder.textViewManufacturerName.setText(manufacturerName);
    holder.textViewProductId.setText(productId);
    holder.textViewVendorId.setText(vendorId);

    holder.materialCardViewDeviceListItem.setOnClickListener(v -> {

      TerminalArgs terminalArgs = new TerminalArgs(
              currentItem.usbDevice().getDeviceId(),
              currentItem.devicePort(),
              19200);
      event.onDeviceClick(terminalArgs);
    });
  }

  @Override
  public int getItemCount() {
    return deviceModelList.size();
  }

  public void appendData(ArrayList<UsbDeviceData> usbDeviceDataList) {
    int itemCount = deviceModelList.size();
    deviceModelList.clear();
    notifyItemRangeRemoved(0, itemCount);

    for (int i = 0; i < usbDeviceDataList.size(); i++) {
      deviceModelList.add(usbDeviceDataList.get(i));
      notifyItemInserted(i);
    }
  }
}
