/*
 * Copyright (C) 2012 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 * Copyright (C) 2018 Pixel Experience (jhenrique09)
 * Copyright (C) 2019 AospExtended ROM
 *
 * * Licensed under the GNU GPLv2 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl-2.0.txt
 */
package com.aospextended.ota;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.aospextended.ota.model.Addon;

import java.util.ArrayList;

public class AddonsActivity extends AppCompatActivity {
    private ArrayList<Addon> addons;
    private AddonsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addons);
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView addonsRv = findViewById(R.id.recycler_view_addons);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try {
            addons = new Gson().fromJson(getIntent().getStringExtra("addons"), new TypeToken<ArrayList<Addon>>() {}.getType());
        } catch (Exception ignored) {
        }
        try {
            if (addons.size() > 0) {
                addonsRv.setHasFixedSize(true);
                addonsRv.setLayoutManager(new LinearLayoutManager(this));
                adapter = new AddonsListAdapter(addons, this);
                addonsRv.setAdapter(adapter);
            } else {
                Toast.makeText(AddonsActivity.this, getString(R.string.addons_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception ex) {
            Toast.makeText(AddonsActivity.this, getString(R.string.addons_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
