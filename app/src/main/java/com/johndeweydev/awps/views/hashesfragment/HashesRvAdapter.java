package com.johndeweydev.awps.views.hashesfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.johndeweydev.awps.R;
import com.johndeweydev.awps.models.data.HashInfoEntity;
import com.johndeweydev.awps.views.hashinfomodalbottomsheetdialog.HashInfoModalBottomArgs;

import java.util.ArrayList;

public class HashesRvAdapter extends RecyclerView.Adapter<HashesRvAdapter.HashesAdapterViewHolder> {

  public interface Event {
    void onHashInfoClick(HashInfoModalBottomArgs hashInfoModalBottomArgs);
  }

  private final ArrayList<HashInfoEntity> hashInfoEntityList = new ArrayList<>();
  private final Event event;

  public static class HashesAdapterViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewApNameHashesItem;
    public TextView textViewMacAddressHashesItem;
    public TextView textViewHashDataHashesItem;
    public MaterialCardView materialCardViewHashesListItem;

    public HashesAdapterViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewApNameHashesItem = itemView.findViewById(R.id.textViewApNameHashesItem);
      textViewMacAddressHashesItem = itemView.findViewById(R.id.textViewMacAddressHashesItem);
      textViewHashDataHashesItem = itemView.findViewById(R.id.textViewHashDataHashesItem);
      materialCardViewHashesListItem = itemView.findViewById(R.id.materialCardViewHashesListItem);
    }
  }

  public HashesRvAdapter(HashesRvAdapter.Event event) {
    this.event = event;
  }
  @NonNull
  @Override
  public HashesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_hashes_item,
            parent, false);
    return new HashesAdapterViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull HashesAdapterViewHolder holder, int position) {
    HashInfoEntity currentHashInfo = hashInfoEntityList.get(position);
    holder.textViewApNameHashesItem.setText(currentHashInfo.ssid);
    holder.textViewMacAddressHashesItem.setText(currentHashInfo.bssid);
    holder.textViewHashDataHashesItem.setText(currentHashInfo.hashData);

    holder.materialCardViewHashesListItem.setOnClickListener(v -> {
      HashInfoModalBottomArgs hashInfoModalBottomArgs = new HashInfoModalBottomArgs(
              currentHashInfo.ssid, currentHashInfo.bssid, currentHashInfo.clientMacAddress,
              currentHashInfo.keyType, currentHashInfo.keyData, currentHashInfo.aNonce,
              currentHashInfo.hashData, currentHashInfo.latitude, currentHashInfo.longitude,
              currentHashInfo.address, currentHashInfo.dateCaptured);
      event.onHashInfoClick(hashInfoModalBottomArgs);
    });
  }

  @Override
  public int getItemCount() {
    return hashInfoEntityList.size();
  }

  public void addAllHashInformation(ArrayList<HashInfoEntity> hashInfoEntities) {
    hashInfoEntityList.addAll(hashInfoEntities);
    this.notifyItemRangeInserted(0, hashInfoEntities.size());
  }

  public void appendHashInformation(HashInfoEntity hashInfoEntity, int position) {
    hashInfoEntityList.add(position, hashInfoEntity);
    this.notifyItemInserted(position);
  }

  public void removeAllHashInformation() {
    int size = hashInfoEntityList.size();
    hashInfoEntityList.clear();
    this.notifyItemRangeRemoved(0, size);
  }

  public HashInfoEntity removeHashInformation(int position) {
    HashInfoEntity hashInfoEntity = hashInfoEntityList.remove(position);
    this.notifyItemRemoved(position);
    return hashInfoEntity;
  }
}
