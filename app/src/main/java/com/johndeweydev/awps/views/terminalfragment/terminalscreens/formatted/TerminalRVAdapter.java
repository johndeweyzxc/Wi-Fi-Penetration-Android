package com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.repository.UsbSerialOutputModel;

import java.util.ArrayList;

public class TerminalRVAdapter extends RecyclerView
        .Adapter<TerminalRVAdapter.TerminalAdapterViewHolder> {

  private final ArrayList<UsbSerialOutputModel> usbSerialOutputModelList = new ArrayList<>();

  public static class TerminalAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewTimeSerialOutputListItem;
    public TextView textViewMessageSerialOutputListItem;
    public TerminalAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewTimeSerialOutputListItem = itemView.findViewById(
              R.id.textViewTimeSerialOutputListItem);
      textViewMessageSerialOutputListItem = itemView.findViewById(
              R.id.textViewMessageSerialOutputListItem);
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
    UsbSerialOutputModel currentItem = usbSerialOutputModelList.get(position);
    holder.textViewTimeSerialOutputListItem.setText(currentItem.getTimeInString());
    holder.textViewMessageSerialOutputListItem.setText(currentItem.getSerialOutputInString());
  }

  @Override
  public int getItemCount() {
    return usbSerialOutputModelList.size();
  }

  public void appendData(UsbSerialOutputModel usbSerialOutputModel) {
    usbSerialOutputModelList.add(usbSerialOutputModel);
    int pos = usbSerialOutputModelList.size() - 1;
    this.notifyItemInserted(pos);
  }
}
