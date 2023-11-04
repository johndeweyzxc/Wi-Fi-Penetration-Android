package com.johndeweydev.awps.views.terminalfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.awps.R;
import com.johndeweydev.awps.models.data.LauncherOutputData;

import java.util.ArrayList;

public class TerminalRVAdapter extends
        RecyclerView.Adapter<TerminalRVAdapter.TerminalAdapterViewHolder> {

  private final ArrayList<LauncherOutputData> terminalLog = new ArrayList<>();

  public static class TerminalAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewRvTerminalLogTime;
    public TextView textViewRvTerminalLogOutput;

    public TerminalAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewRvTerminalLogTime = itemView.findViewById(R.id.textViewRvTerminalLogTime);
      textViewRvTerminalLogOutput = itemView.findViewById(R.id.textViewRvTerminalLogOutput);
    }
  }

  @NonNull
  @Override
  public TerminalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.rv_terminal_log_item, parent, false);
    return new TerminalAdapterViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull TerminalAdapterViewHolder holder, int position) {
    LauncherOutputData launcherOutputData = terminalLog.get(position);
    holder.textViewRvTerminalLogTime.setText(launcherOutputData.getTime());
    holder.textViewRvTerminalLogOutput.setText(launcherOutputData.getOutput());
  }

  @Override
  public int getItemCount() {
    return terminalLog.size();
  }

  public void appendNewTerminalLog(LauncherOutputData launcherOutputData) {
    terminalLog.add(launcherOutputData);
    this.notifyItemInserted(terminalLog.size() - 1);
  }
}
