package com.johndeweydev.awps.views.devicesfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.usbserial.UsbDeviceItem;
import com.johndeweydev.awps.views.terminalfragment.TerminalArgs;

import java.util.ArrayList;

public class DevicesRVAdapter extends RecyclerView.Adapter<DevicesRVAdapter.DevicesAdapterViewHolder> {

  private final ArrayList<UsbDeviceItem> deviceModelList = new ArrayList<>();
  private final RVAdapterCallback rvAdapterCallback;

  public interface RVAdapterCallback {
    void onDeviceClick(TerminalArgs terminalArgs);
  }

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

   public DevicesRVAdapter(RVAdapterCallback rvAdapterCallback) {
    this.rvAdapterCallback = rvAdapterCallback;
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
      rvAdapterCallback.onDeviceClick(terminalArgs);
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
