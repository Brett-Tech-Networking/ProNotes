package com.btn.pronotes;

import static com.btn.pronotes.utils.CONSTANTS.FILE_PATH;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.btn.pronotes.databinding.ActivityImageBinding;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageActivity extends AppCompatActivity {

    private ActivityImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setImage();
    }

    private void setImage() {

        if (getIntent().hasExtra(FILE_PATH)) {
            binding.photoView.setImageURI(Uri.parse(getIntent().getStringExtra(FILE_PATH)));
        }
    }
}