package com.btn.pronotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.btn.pronotes.MainActivity;

public class OpenHelp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
    }

   /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.websitebutton) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.brett-techrepair.com")));
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void btnwebsite(View view) {
        // Handle button click here
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.brett-techrepair.com")));
    }
}
