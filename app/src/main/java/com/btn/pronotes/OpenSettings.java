package com.btn.pronotes;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.utils.SharedPreferenceHelper;
import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.google.android.material.imageview.ShapeableImageView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;


public class OpenSettings extends AppCompatActivity {

    ImageView imageView_back1;
    private SwitchCompat colorTilesSwitch;
    private ShapeableImageView ivSelectColor;
    private String selectedColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        selectedColor = new SharedPreferenceHelper(this).getSelectedColor();

        findViewById(R.id.imageView_back1).setOnClickListener(view -> finish());
        colorTilesSwitch = findViewById(R.id.color_tile_switch);
        ivSelectColor = findViewById(R.id.iv_select_color);
        if (!selectedColor.isEmpty()) {
            ivSelectColor.setBackgroundColor(Color.parseColor(selectedColor));
        }
        colorTilesSwitch.setChecked(new SharedPreferenceHelper(this).isColorChangingTiles());
        colorTilesSwitch.setOnCheckedChangeListener((compoundButton, b) ->
                new SharedPreferenceHelper(OpenSettings.this).setColorChangingTile(b));

        ivSelectColor.setOnClickListener(v -> {
            new ColorPickerDialog
                    .Builder(this)
                    .setTitle("Pick Theme")
                    .setColorShape(ColorShape.SQAURE)
                    .setDefaultColor(selectedColor)
                    .setColorListener((color, colorHex) -> {
                        new SharedPreferenceHelper(this).setSelectedColor(colorHex);
                        ivSelectColor.setBackgroundColor(Color.parseColor(colorHex));
                    })
                    .show();
        });

    }


}





