package com.btn.pronotes.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.RemoteViews;

import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.NotesTakerActivity;
import com.btn.pronotes.R;
import com.btn.pronotes.utils.SharedPreferenceHelper;

import java.util.List;

public class NoteWidget extends AppWidgetProvider {

    private int[] widgetIDs;
    public static final String WIDGET_UPDATE_ACTION = "com.btn.pronotes.WIDGET_UPDATE_ACTION";
    private int currentTextColor = Color.BLACK; // Default text color (black)

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        widgetIDs = appWidgetIds;

        SharedPreferences sharedPref = context.getSharedPreferences("widget_settings", Context.MODE_PRIVATE);
        int selectedColor = sharedPref.getInt("color", Color.RED);  // Default color is RED
        int selectedTransparency = sharedPref.getInt("transparency", 255);  // Default transparency is opaque
        currentTextColor = sharedPref.getInt("textColor", Color.BLACK); // Get the saved text color

        RoomDB roomDB = RoomDB.getInstance(context);
        new SharedPreferenceHelper(context).setWidgetId(appWidgetIds[0]);

        List<Notes> notesList = roomDB.mainDAO().getPinNotes();

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_widget_layout);

            // Setting the color and transparency to the widget layout
            views.setInt(R.id.notes_container, "setBackgroundColor", Color.argb(selectedTransparency, Color.red(selectedColor), Color.green(selectedColor), Color.blue(selectedColor)));

            // Update the text color in the widget layout
            views.setTextColor(R.id.textView_note_title, currentTextColor);
            views.setTextColor(R.id.textView_note_body, currentTextColor);
            views.setTextColor(R.id.textView_note_date, currentTextColor);

            if (notesList.isEmpty()) {
                views.setViewVisibility(R.id.textView_note_title, View.GONE);
                views.setViewVisibility(R.id.textView_note_body, View.GONE);
                views.setViewVisibility(R.id.textView_note_date, View.GONE);
                views.setViewVisibility(R.id.imageView_edit, View.GONE);
                views.setViewVisibility(R.id.textView_empty, View.VISIBLE);
            } else {
                Spanned spannedString = Html.fromHtml(notesList.get(0).getNotes());

                views.setViewVisibility(R.id.textView_note_title, View.VISIBLE);
                views.setViewVisibility(R.id.textView_note_body, View.VISIBLE);
                views.setViewVisibility(R.id.textView_note_date, View.VISIBLE);
                views.setViewVisibility(R.id.imageView_edit, View.VISIBLE);
                views.setViewVisibility(R.id.textView_empty, View.GONE);
                views.setTextViewText(R.id.textView_note_title, notesList.get(0).getTitle());
                views.setTextViewText(R.id.textView_note_body, spannedString);
                views.setTextViewText(R.id.textView_note_date, notesList.get(0).getDate());

                // Set up a pending intent to launch the note editor when the edit button is clicked
                Intent editIntent = new Intent(context, NotesTakerActivity.class);
                editIntent.putExtra("old_note", notesList.get(0));
                PendingIntent editPendingIntent = PendingIntent.getActivity(context, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setOnClickPendingIntent(R.id.imageView_edit, editPendingIntent);
            }

            // Set up a pending intent to launch the WidgetConfigActivity when the settings icon is clicked
            Intent configIntent = new Intent(context, WidgetConfigActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 1, configIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.settingsIcon, configPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, NoteWidget.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            onUpdate(context, appWidgetManager, allWidgetIds);
        }
    }

    // Override the onEnabled method to set up the widget update interval.
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        // Set up a repeating alarm to update the widget every minute.
        // ...
    }

    // Override the onDisabled method to cancel the widget update interval.
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        // Cancel the repeating alarm that updates the widget.
        // ...
    }
}
