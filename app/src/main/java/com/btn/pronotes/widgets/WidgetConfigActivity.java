package com.btn.pronotes.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import com.btn.pronotes.R;

public class WidgetConfigActivity extends Activity {

    private SeekBar transparencySeekBar;
    private Button colorPickerButton;
    private Button applyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

    transparencySeekBar = findViewById(R.id.transparencySeekBar);
        colorPickerButton = findViewById(R.id.colorPickerButton);
        applyButton = findViewById(R.id.applyButton);

        // TODO: Add logic for handling transparency and color change

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save settings to SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences("widget_settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                // TODO: Save the selected transparency and color values
                editor.apply();

                // Update the widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetConfigActivity.this);
                ComponentName thisWidget = new ComponentName(WidgetConfigActivity.this, NoteWidget.class);
                int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                for (int widgetId : allWidgetIds) {
                    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.note_widget_layout);
                    appWidgetManager.updateAppWidget(widgetId, remoteViews);
                }

                finish();  // Close the activity
            }
        });

        // Add this to show the popup menu when the settings button is clicked
      }
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(WidgetConfigActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.widget_config_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_change_color:
                        // Handle color change
                        return true;
                    case R.id.action_change_transparency:
                        // Handle transparency change
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
}
