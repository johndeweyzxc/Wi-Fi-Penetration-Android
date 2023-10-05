package com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.usbserial.UsbSerialOutputItem;

import java.util.ArrayList;

public class TerminalRVAdapter extends RecyclerView
        .Adapter<TerminalRVAdapter.TerminalAdapterViewHolder> {

  private final ArrayList<UsbSerialOutputItem> usbSerialOutputItemList = new ArrayList<>();

  public static class TerminalAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView timeSerialOutput;
    public TextView textSerialOutput;
    public TerminalAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      timeSerialOutput = itemView.findViewById(R.id.timeSerialOutput);
      textSerialOutput = itemView.findViewById(R.id.textSerialOutput);
    }
  }

  @NonNull
  @Override
  public TerminalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.serial_ouput_list_item, parent, false
    );
    return new TerminalAdapterViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull TerminalAdapterViewHolder holder, int position) {
    UsbSerialOutputItem currentItem = usbSerialOutputItemList.get(position);
    holder.timeSerialOutput.setText(currentItem.getTimeInString());
    holder.textSerialOutput.setText(currentItem.getSerialOutputInString());
  }

  @Override
  public int getItemCount() {
    return usbSerialOutputItemList.size();
  }

  public void appendData(UsbSerialOutputItem usbSerialOutputItem) {
    usbSerialOutputItemList.add(usbSerialOutputItem);
  }
}
