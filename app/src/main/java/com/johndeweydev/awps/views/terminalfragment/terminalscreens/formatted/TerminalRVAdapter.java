package com.johndeweydev.awps.views.terminalfragment.terminalscreens.formatted;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.data.LauncherOutputData;

import java.util.ArrayList;

public class TerminalRVAdapter extends RecyclerView
        .Adapter<TerminalRVAdapter.TerminalAdapterViewHolder> {

  private final ArrayList<LauncherOutputData> launcherOutputDataList = new ArrayList<>();

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
    LauncherOutputData currentItem = launcherOutputDataList.get(position);
    holder.textViewTimeSerialOutputListItem.setText(currentItem.getTime());
    holder.textViewMessageSerialOutputListItem.setText(currentItem.getOutput());
  }

  @Override
  public int getItemCount() {
    return launcherOutputDataList.size();
  }

  public void appendData(LauncherOutputData launcherOutputData) {
    launcherOutputDataList.add(launcherOutputData);
    int pos = launcherOutputDataList.size() - 1;
    this.notifyItemInserted(pos);
  }
}
