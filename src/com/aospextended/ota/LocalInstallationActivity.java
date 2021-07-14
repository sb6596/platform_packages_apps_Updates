package com.aospextended.ota;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aospextended.ota.controller.UpdaterController;
import com.aospextended.ota.misc.Utils;
import com.aospextended.ota.model.Update;
import com.aospextended.ota.view.SlideItemAnimator;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Shubham Singh on 26/06/21.
 */
public class LocalInstallationActivity extends UpdatesListActivity implements FileListener, SlideItemAnimator.OnRecyclerViewListener, View.OnTouchListener {

    private static final String TAG = "LocalInstallationActivity";

    private TextView tvNoFile;
    private RecyclerView rvFileList;
    private Update localUpdate;
    private GestureDetector mGestureDetec;
    private LocalInstallationListAdapter adapter;
    private List<File> filteredFileList;

    private UpdaterController mUpdaterController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_installation);

        mUpdaterController = UpdaterController.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tvNoFile = findViewById(R.id.tv_no_local_installation_files);
        rvFileList = findViewById(R.id.rv_file_list);

        initView();
    }

    private void initView() {
        filteredFileList = getFileList();

        if(filteredFileList.isEmpty()) {
            showNoFile();
        } else {
            showFileList(filteredFileList);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFileList(List<File> fileList) {
        adapter = new LocalInstallationListAdapter(fileList, this);
        rvFileList.setLayoutManager(new LinearLayoutManager(this));
        rvFileList.setHasFixedSize(true);

        SlideItemAnimator animator = new SlideItemAnimator();
        animator.setRecyclerViewListener(this);
        rvFileList.setItemAnimator(animator);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvFileList);

        mGestureDetec = new GestureDetector(this, gestureDetector);
        rvFileList.setOnTouchListener(this);

        rvFileList.setAdapter(adapter);

        tvNoFile.setVisibility(View.GONE);
        rvFileList.setVisibility(View.VISIBLE);
    }

    private void showNoFile() {
        tvNoFile.setVisibility(View.VISIBLE);
        rvFileList.setVisibility(View.GONE);
    }

    private List<File> getFileList() {
        // gets the files in the root of internal storage
        File fileDirectory = new File(Environment.getExternalStorageDirectory()+"/");
       // lists all the files into an array
        File[] dirFiles = fileDirectory.listFiles();

        List<File> filteredFiles = new ArrayList<File>();

        if (dirFiles != null && dirFiles.length != 0) {
            // loops through the array of files, and filter out valid zip files
            for (File file : dirFiles) {
                String fileOutput = file.getName();
                // Filter out AEX builds
                if(fileOutput.startsWith("AospExtended") && fileOutput.endsWith(".zip")) {
                    filteredFiles.add(file);
                }
            }
        }

        return filteredFiles;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        Drawable background;
        Drawable deleteIcon;
        int deleteMargin = 0;
        boolean initiated = false;

        private void init() {
            background = ContextCompat.getDrawable(getApplicationContext(), R.drawable.slide_bg_shape);
            deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete);
            deleteMargin = (int) getResources().getDimension(R.dimen.swipe_delete_icon_right_margin);
            initiated = true;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder, @NonNull  RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            if(position != -1 && adapter != null) {
                showDeleteConfirmationDialog(position);
            }

            if(filteredFileList.isEmpty()) {
                showNoFile();
            }
        }

        @Override
        public void onChildDraw(@NonNull  Canvas c, @NonNull  RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            if(viewHolder.getAdapterPosition() == -1) {
                return;
            }

            if(!initiated) {
                init();
            }
            //to draw the background
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            // draw delete icon
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = deleteIcon.getIntrinsicWidth();

            int left = itemView.getRight() - deleteMargin - intrinsicWidth;
            int right = itemView.getRight() - deleteMargin;
            int top = itemView.getTop() + (itemHeight - intrinsicWidth) / 2;
            int bottom = top + intrinsicWidth;
            deleteIcon.setBounds(left, top, right, bottom);
            deleteIcon.draw(c);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    GestureDetector.SimpleOnGestureListener gestureDetector = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    };

    @Override
    public void showSnackbar(int stringId, int duration) {
        Snackbar snack = Snackbar.make(findViewById(R.id.view_snackbar), stringId, duration);
        TextView tv = snack.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(getColor(R.color.text_primary));
        snack.show();
    }

    @Override
    public void showSnackbar(String text, int duration) {
        Snackbar snack = Snackbar.make(findViewById(R.id.view_snackbar), text, duration);
        TextView tv = snack.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(getColor(R.color.text_primary));
        snack.show();
    }

    @Override
    public void onFileSelected(File selectedfile) {
        proceedLocalInstallation(selectedfile.getPath());
    }

    private void proceedLocalInstallation(String path) {
        localUpdate = new Update();
        File file = new File(path);
        localUpdate.setFile(file);
        localUpdate.setName(file.getName());
        localUpdate.setFileSize(file.length());
        localUpdate.setTimestamp(new Date().getTime()/1000l);
        localUpdate.setDownloadId(String.valueOf(new Date().getTime()/1000L));
        localUpdate.setVersion("");

        Log.d(TAG, "Adding local installation");
        mUpdaterController.addUpdate(localUpdate);

        getInstallDialog().show();
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppTheme_AlertDialogStyle)
                .setTitle(R.string.delete_file_dialog_title)
                .setMessage(R.string.delete_file_dialog_desc)
                .setPositiveButton(R.string.lbl_yes, (dialog, which) -> {
                    File file = new File(filteredFileList.get(position).getPath());
                    boolean success = file.delete();
                    if(success) {
                        filteredFileList.remove(position);
                        adapter.removeItem(position);
                        showSnackbar(getString(R.string.file_deletion_success), Snackbar.LENGTH_SHORT);
                    } else {
                        adapter.cancelSwipe(position);
                        showSnackbar(getString(R.string.file_deletion_failed), Snackbar.LENGTH_SHORT);
                    }

                    if(filteredFileList.isEmpty()) {
                        showNoFile();
                    }
                })
                .setNegativeButton(R.string.lbl_no, (dialog, which) -> {
                    adapter.cancelSwipe(position);
                });

        alertDialog.show();
    }

    private AlertDialog.Builder getInstallDialog() {
        if (!Utils.isBatteryLevelOk(this)) {
            Resources resources = this.getResources();
            String message = resources.getString(R.string.dialog_battery_low_message_pct,
                    resources.getInteger(R.integer.battery_ok_percentage_discharging),
                    resources.getInteger(R.integer.battery_ok_percentage_charging));
            return new AlertDialog.Builder(this, R.style.AppTheme_AlertDialogStyle)
                    .setTitle(R.string.dialog_battery_low_title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null);
        }
        int resId;
        String extraMessage = "";
        if (Utils.isABDevice()) {
            resId = R.string.apply_update_dialog_message_ab;
        } else {
            resId = R.string.apply_update_dialog_message;
            extraMessage = " (" + localUpdate.getFile().getPath() + ")";
        }

        return new AlertDialog.Builder(this, R.style.AppTheme_AlertDialogStyle)
                .setTitle(R.string.apply_update_dialog_title)
                .setMessage(getString(resId, localUpdate.getName(),
                        getString(android.R.string.ok)) + extraMessage)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> Utils.triggerUpdate(this, true))
                .setNegativeButton(android.R.string.cancel, null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemAdded() {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.rv_file_list) {
            return mGestureDetec.onTouchEvent(event);
        }
        return true;
    }
}
