package com.johndeweydev.awps.views.terminalfragment.terminalscreens.raw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.models.LauncherOutputModel;

import java.util.ArrayList;

public class TerminalRawRVAdapter
        extends RecyclerView.Adapter<TerminalRawRVAdapter.TerminalAdapterViewHolder> {

  private final ArrayList<LauncherOutputModel> launcherOutputModelList = new ArrayList<>();

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
  public void onBindViewHolder(
          @NonNull TerminalRawRVAdapter.TerminalAdapterViewHolder holder, int position
  ) {
    LauncherOutputModel currentItem = launcherOutputModelList.get(position);
    holder.textViewTimeSerialOutputListItem.setText(currentItem.getTime());
    holder.textViewMessageSerialOutputListItem.setText(currentItem.getOutput());
  }

  @Override
  public int getItemCount() {
    return launcherOutputModelList.size();
  }

  public void appendData(LauncherOutputModel launcherOutputModel) {
    launcherOutputModelList.add(launcherOutputModel);
    int pos = launcherOutputModelList.size() - 1;
    this.notifyItemInserted(pos);
  }
}
