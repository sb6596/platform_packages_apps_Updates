package com.aospextended.ota;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aospextended.ota.controller.UpdaterController;
import com.aospextended.ota.misc.Utils;
import com.aospextended.ota.model.Update;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shubham Singh on 26/06/21.
 */
public class LocalInstallationActivity extends UpdatesListActivity implements FileListener {

    private static final String TAG = "LocalInstallationActivity";

    private TextView tvNoFile;
    private RecyclerView rvFileList;
    private Update localUpdate;

    private UpdaterController mUpdaterController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_installation);

        mUpdaterController = UpdaterController.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoFile = findViewById(R.id.tv_no_local_installation_files);
        rvFileList = findViewById(R.id.rv_file_list);

        initView();
    }

    private void initView() {
        List<File> filteredFileList = getFileList();

        if(filteredFileList.isEmpty()) {
            showNoFile();
        } else {
            showFileList(filteredFileList);
        }
    }

    private void showFileList(List<File> fileList) {
        LocalInstallationListAdapter adapter = new LocalInstallationListAdapter(fileList, this);
        rvFileList.setLayoutManager(new LinearLayoutManager(this));
        rvFileList.setHasFixedSize(true);
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
                if(fileOutput.startsWith("AospExtended") && fileOutput.endsWith(".zip")) {
                    filteredFiles.add(file);
                }
            }
        }

        return filteredFiles;
    }

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
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
