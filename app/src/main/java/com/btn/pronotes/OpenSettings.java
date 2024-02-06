package com.btn.pronotes;


import static com.btn.pronotes.R.*;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.btn.pronotes.Adapters.NotesListAdapter;
import com.btn.pronotes.R;
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
    TextView textView_title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.settings);
        selectedColor = new SharedPreferenceHelper(this).getSelectedColor();

        Spinner spinner = findViewById(id.color_spinner);
        TextView textViewTitle = findViewById(id.textView_title);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = spinner.getSelectedItem().toString();
                changeTextViewBackground(selectedOption);
                textView_title.setText(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle if nothing is selected, if needed
            }
        });

        findViewById(id.imageView_back1).setOnClickListener(view -> finish());
        colorTilesSwitch = findViewById(id.color_tile_switch);
        ivSelectColor = findViewById(id.iv_select_color);
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
    private void changeTextViewBackground(String selectedOption) {
        switch (selectedOption) {
            case "Default":
                // Use default background color (e.g., from your app's theme)
                textView_title.setBackgroundResource(android.R.color.transparent); // Example
                break;
            case "Faded Black":
                textView_title.setBackgroundResource(drawable.multiblack);
                break;
            case "Faded Grey":
                textView_title.setBackgroundResource(drawable.multigrey);
                break;
            case "Faded Red":
                textView_title.setBackgroundResource(drawable.multired);
                break;
            case "Faded Blue":
                textView_title.setBackgroundResource(drawable.multiblue);
                break;
            default:
                // Handle if an unexpected option is selected
        }
    }
}





