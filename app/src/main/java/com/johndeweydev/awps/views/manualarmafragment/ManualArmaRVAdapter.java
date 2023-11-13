package com.johndeweydev.awps.views.manualarmafragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.johndeweydev.awps.R;

import java.util.ArrayList;

public class ManualArmaRVAdapter extends
        RecyclerView.Adapter<ManualArmaRVAdapter.ManualArmaAdapterViewHolder> {

  private final ArrayList<String> attackLogList = new ArrayList<>();

  public static class ManualArmaAdapterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewManualArmaAttackLogListItem;

    public ManualArmaAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewManualArmaAttackLogListItem = itemView.findViewById(
              R.id.textViewManualArmaAttackLogListItem);
    }
  }

  @NonNull
  @Override
  public ManualArmaAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.rv_manual_arma_attack_log_item, parent, false);
    return new ManualArmaAdapterViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ManualArmaAdapterViewHolder holder, int position) {
    String currentAttackLog = attackLogList.get(position);
    holder.textViewManualArmaAttackLogListItem.setText(currentAttackLog);
  }

  @Override
  public int getItemCount() {
    return attackLogList.size();
  }

  public void appendData(String attackLog) {
    attackLogList.add(attackLog);
    notifyItemInserted(attackLogList.size() - 1);
  }

  public void clearLogs() {
    int size = attackLogList.size();
    attackLogList.clear();
    notifyItemRangeRemoved(0, size);
  }
}
