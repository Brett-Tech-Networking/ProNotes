package com.btn.pronotes;

import android.content.Intent;
import android.content.pm.PackageInfo; // Import PackageInfo
import android.content.pm.PackageManager; // Import PackageManager
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView; // Import TextView

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// No need to import MainActivity here if not directly used in this class
// import com.btn.pronotes.MainActivity;

public class OpenHelp extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help); // Ensure this matches your layout XML file name (e.g., activity_open_help.xml or help.xml)

        // Find the TextView by its ID
        TextView versionTextView = findViewById(R.id.textViewVersion);

        // Try to get the application's version name
        try {
            // Get the PackageInfo object for your application
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            // Extract the versionName from the PackageInfo
            String versionName = pInfo.versionName;

            // Set the text of the TextView to display the version
            versionTextView.setText("Version " + versionName);

        } catch (PackageManager.NameNotFoundException e) {
            // If the package name is not found (highly unlikely for your own app)
            // Print the stack trace for debugging purposes
            e.printStackTrace();
            // Set a fallback text in case of an error
            versionTextView.setText("Version N/A");
        }
    }

   /* @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.websitebutton) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.brett-techrepair.com")));
        }
        return super.onOptionsItemSelected(item);
    }*/

    // This method is called when the "Visit Our Website" button is clicked
    public void btnwebsite(View view) {
        // Create an Intent to open a web browser with the specified URL
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.brett-techrepair.com")));
    }
}