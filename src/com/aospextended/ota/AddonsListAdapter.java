package com.aospextended.ota;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aospextended.ota.model.Addon;

import java.util.ArrayList;

public class AddonsListAdapter extends RecyclerView.Adapter<AddonsListAdapter.ViewHolder> {
    private ArrayList<Addon> addons;
    private Context mContext;

    public AddonsListAdapter(ArrayList<Addon> addons, Context mContext) {
        this.addons = addons;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public AddonsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_addon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddonsListAdapter.ViewHolder holder, int position) {
        Addon addon = addons.get(position);
        holder.tvTitle.setText(addon.title);
        holder.tvSummary.setText(addon.summary);
        holder.cardAddon.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(addon.url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return addons.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSummary;
        CardView cardAddon;

       ViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tv_addon_title);
            this.tvSummary = itemView.findViewById(R.id.tv_addon_summary);
            this.cardAddon = itemView.findViewById(R.id.card_addon);
        }
    }
}
