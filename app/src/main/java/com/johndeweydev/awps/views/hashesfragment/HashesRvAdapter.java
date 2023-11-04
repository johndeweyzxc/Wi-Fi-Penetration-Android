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

  private final ArrayList<HashInfoEntity> hashInfoEntityList = new ArrayList<>();
  private final HashesRvAdapterEvent hashesRvAdapterEvent;

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

  public HashesRvAdapter(HashesRvAdapterEvent hashesRvAdapterEvent) {
    this.hashesRvAdapterEvent = hashesRvAdapterEvent;
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
    StringBuilder bssidFormatted = new StringBuilder();

    // This formats the mac address (BSSID), e.g. "1234567890AB" will become "12:34:56:78:90:AB"
    for (int i = 0; i < currentHashInfo.bssid.length(); i += 2) {
      // Append two characters at a time
      bssidFormatted.append(currentHashInfo.bssid.charAt(i));
      bssidFormatted.append(currentHashInfo.bssid.charAt(i + 1));

      // Append a colon if not at the end
      if (i + 2 < currentHashInfo.bssid.length()) {
        bssidFormatted.append(":");
      }
    }

    holder.textViewApNameHashesItem.setText(currentHashInfo.ssid);
    holder.textViewMacAddressHashesItem.setText(bssidFormatted.toString());

    StringBuilder hash = new StringBuilder();
    if (currentHashInfo.keyType.equals("PMKID")) {
      hash.append("PMKID: ");
    } else if (currentHashInfo.keyType.equals("MIC")) {
      hash.append("MIC: ");
    }

    hash.append(currentHashInfo.hashData);
    holder.textViewHashDataHashesItem.setText(hash.toString());

    holder.materialCardViewHashesListItem.setOnClickListener(v -> {
      HashInfoModalBottomArgs hashInfoModalBottomArgs = new HashInfoModalBottomArgs(
              currentHashInfo.ssid, currentHashInfo.bssid, currentHashInfo.clientMacAddress,
              currentHashInfo.keyType, currentHashInfo.keyData, currentHashInfo.hashData,
              currentHashInfo.dateCaptured);
      hashesRvAdapterEvent.onHashInfoClick(hashInfoModalBottomArgs);
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

  public void removeAllHashInformation() {
    int size = hashInfoEntityList.size();
    hashInfoEntityList.clear();
    this.notifyItemRangeRemoved(0, size);
  }
}
