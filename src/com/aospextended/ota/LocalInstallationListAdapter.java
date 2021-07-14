package com.aospextended.ota;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aospextended.ota.misc.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by Shubham Singh on 26/06/21.
 */
public class LocalInstallationListAdapter extends RecyclerView.Adapter<LocalInstallationListAdapter.ViewHolder> {

    private static final String TAG = "LocalInstallationListAdapter";
    private final List<File> fileList;
    private final FileListener fileListener;

    LocalInstallationListAdapter(List<File> fileList, FileListener fileListener) {
        this.fileList = fileList;
        this.fileListener = fileListener;
    }

    @NonNull
    @Override
    public LocalInstallationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_local_files, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalInstallationListAdapter.ViewHolder holder, int position) {
        File currentFile = fileList.get(position);

        holder.fileCard.setTitle(currentFile.getName());
        holder.fileCard.enableTitleMarquee();
        holder.fileCard.setSummary(Utils.readableFileSize(currentFile.length()));
        holder.fileCard.setOnClickListener(v -> {
            fileListener.onFileSelected(currentFile);
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ExtraCardView fileCard;

        ViewHolder(final View view) {
           super(view);
           fileCard = view.findViewById(R.id.file_card);
        }
    }
}

interface FileListener{
    void onFileSelected(File selectedfile);
}
