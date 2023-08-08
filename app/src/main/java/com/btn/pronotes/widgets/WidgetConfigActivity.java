package com.btn.pronotes.widgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import com.btn.pronotes.R;

public class WidgetConfigActivity extends Activity {

    private SeekBar transparencySeekBar;
    private Button colorPickerButton;
    private Button applyButton;
    private Button textColorPickerButton; // Add this line

    private int currentColor = Color.RED;  // Default color
    private int currentTransparency = 255;  // Default transparency (opaque)
    private int currentTextColor = Color.BLACK; // Default text color (black)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        transparencySeekBar = findViewById(R.id.transparencySeekBar);
        colorPickerButton = findViewById(R.id.colorPickerButton);
        textColorPickerButton = findViewById(R.id.textColorPickerButton); // Add this line
        applyButton = findViewById(R.id.applyButton);

        SharedPreferences sharedPref = getSharedPreferences("widget_settings", Context.MODE_PRIVATE);
        currentColor = sharedPref.getInt("color", Color.RED);
        currentTransparency = sharedPref.getInt("transparency", 255);
        currentTextColor = sharedPref.getInt("textColor", Color.BLACK); // Get the saved text color

        // Set the initial values for color and transparency
        setColorPickerButtonColor(currentColor);
        transparencySeekBar.setProgress(currentTransparency);

        // Transparency SeekBar listener
        transparencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTransparency = progress;
                // Update the sample view here if you have one
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Text Color Picker Button listener
        textColorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] colors = {"Black", "Red", "Blue", "Green", "Yellow"};
                AlertDialog.Builder builder = new AlertDialog.Builder(WidgetConfigActivity.this);
                builder.setTitle("Pick a text color")
                        .setItems(colors, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        currentTextColor = Color.BLACK;
                                        break;
                                    case 1:
                                        currentTextColor = Color.RED;
                                        break;
                                    case 2:
                                        currentTextColor = Color.BLUE;
                                        break;
                                    case 3:
                                        currentTextColor = Color.GREEN;
                                        break;
                                    case 4:
                                        currentTextColor = Color.YELLOW;
                                        break;
                                }
                                // Update the sample view here if you have one
                                setColorPickerButtonColor(currentColor);
                            }
                        }).show();
            }
        });

        // Color Picker Button listener
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] colors = {"Red", "Blue", "Green", "Yellow"};
                AlertDialog.Builder builder = new AlertDialog.Builder(WidgetConfigActivity.this);
                builder.setTitle("Pick a color")
                        .setItems(colors, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        currentColor = Color.RED;
                                        break;
                                    case 1:
                                        currentColor = Color.BLUE;
                                        break;
                                    case 2:
                                        currentColor = Color.GREEN;
                                        break;
                                    case 3:
                                        currentColor = Color.YELLOW;
                                        break;
                                }
                            }
                        }).show();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("widget_settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("color", currentColor);
                editor.putInt("transparency", currentTransparency);
                editor.putInt("textColor", currentTextColor); // Add this line to save the text color
                editor.apply();

                // Send a broadcast to update the widget immediately
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, WidgetConfigActivity.this, NoteWidget.class);
                intent.setComponent(new ComponentName(WidgetConfigActivity.this, NoteWidget.class));
                sendBroadcast(intent);

                finishAffinity(); // Close the entire app
            }
        });
    }

    // Show the color picker dialog
    private void showColorPickerDialog() {
        // Show the color picker dialog and update the currentColor
        // You can use any color picker library or custom implementation
        // For simplicity, let's assume we use a basic color picker dialog
        // Update the currentColor variable based on the selected color
        // setColorPickerButtonColor(currentColor);
    }

    // Helper method to update the color of the colorPickerButton
    private void setColorPickerButtonColor(int color) {
        currentColor = color;
        // Update the color of the colorPickerButton
        // colorPickerButton.setBackgroundColor(color);
    }

    // Helper method to apply the text color to the NoteWidget
    private void setColorForNoteWidget(int color) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetConfigActivity.this);
        ComponentName thisWidget = new ComponentName(WidgetConfigActivity.this, NoteWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.note_widget_layout);
        views.setTextColor(R.id.textView_note_title, color);
        views.setTextColor(R.id.textView_note_body, color);
        views.setTextColor(R.id.textView_note_date, color);

        for (int appWidgetId : allWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
