package com.btn.pronotes;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.btn.pronotes.utils.SharedPreferenceHelper;
import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.google.android.material.imageview.ShapeableImageView;

public class OpenSettings extends AppCompatActivity {

    private ImageView imageView_back1;
    private SwitchCompat colorTilesSwitch;
    private SwitchCompat autosaveSwitch;
    private ShapeableImageView ivSelectColor;
    private String selectedColor;
    private TextView textView_title;
    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Initialize SharedPreferenceHelper
        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        // Retrieve selected color from preferences
        selectedColor = sharedPreferenceHelper.getSelectedColor();

        // Initialize views
        imageView_back1 = findViewById(R.id.imageView_back1);
        colorTilesSwitch = findViewById(R.id.color_tile_switch);
        autosaveSwitch = findViewById(R.id.autosave_switch);
        ivSelectColor = findViewById(R.id.iv_select_color);
        textView_title = findViewById(R.id.textView_title);

        // Set up back button
        imageView_back1.setOnClickListener(view -> finish());

        // Set initial color for the color picker
        if (!selectedColor.isEmpty()) {
            ivSelectColor.setBackgroundColor(Color.parseColor(selectedColor));
        }

        // Set the switches to their saved states or default state
        colorTilesSwitch.setChecked(sharedPreferenceHelper.isColorChangingTiles());
        autosaveSwitch.setChecked(sharedPreferenceHelper.isAutosaveEnabled()); // Defaults to true

        // Listener for Color Tiles Switch
        colorTilesSwitch.setOnCheckedChangeListener((compoundButton, isChecked) ->
                sharedPreferenceHelper.setColorChangingTile(isChecked));

        // Listener for Autosave Switch
        autosaveSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferenceHelper.setAutosaveEnabled(isChecked));

        // Listener for Color Picker
        ivSelectColor.setOnClickListener(v -> {
            new ColorPickerDialog
                    .Builder(this)
                    .setTitle("Pick Theme")
                    .setColorShape(ColorShape.SQAURE)
                    .setDefaultColor(selectedColor.isEmpty() ? "#FFFFFF" : selectedColor)
                    .setColorListener((color, colorHex) -> {
                        sharedPreferenceHelper.setSelectedColor(colorHex);
                        ivSelectColor.setBackgroundColor(Color.parseColor(colorHex));
                    })
                    .show();
        });
    }
}
