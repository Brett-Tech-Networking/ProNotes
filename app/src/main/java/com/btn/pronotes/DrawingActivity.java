package com.btn.pronotes;

import static com.btn.pronotes.NotesTakerActivity.REQUEST_CODE_DRAW;
import static com.btn.pronotes.utils.CONSTANTS.FILE_PATH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.btn.pronotes.databinding.ActivityDrawingBinding;
import com.btn.pronotes.utils.SharedPreferenceHelper;
import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.redhoodhan.draw.draw_option.data.LineType;

import java.io.File;
import java.io.FileOutputStream;

public class DrawingActivity extends AppCompatActivity {

    private ActivityDrawingBinding binding;

    private String destination= "note taker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrawingBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.drawView.setBrushColor(ContextCompat.getColor(this, R.color.white));
        btnListeners();
        bottomSheetSetup();
        getArguments();
    }

    private void getArguments() {
        if (getIntent().hasExtra("FROM"))
        {
            destination = getIntent().getStringExtra("FROM");
        }
    }

    private void bottomSheetSetup() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.
                from(binding.layoutMiscellaneous.layoutMiscellaneous2);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.fabDone.setVisibility(View.VISIBLE);
                } else {
                    binding.fabDone.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void btnListeners() {

        binding.fabDone.setOnClickListener(view -> {
            if (!binding.drawView.getDrawStateRef().isPathEmpty()) {
                saveDrawing();
            } else {
                Toast.makeText(this, "Drawing is empty", Toast.LENGTH_SHORT).show();
            }
        });
        binding.layoutMiscellaneous.ivRedo.setOnClickListener(v -> binding.drawView.redo());
        binding.layoutMiscellaneous.ivUndo.setOnClickListener(v -> binding.drawView.undo());
        binding.layoutMiscellaneous.ivEraser.setOnClickListener(v -> {
            if (binding.drawView.getLineType() == LineType.ERASER) {
                binding.drawView.setLineType(LineType.SOLID);
            } else {
                binding.drawView.setLineType(LineType.ERASER);
            }

        });

        binding.layoutMiscellaneous.ivColorPicker.setOnClickListener(view -> {
            new ColorPickerDialog
                    .Builder(this)
                    .setTitle("Pick Color")
                    .setColorShape(ColorShape.SQAURE)
                    .setDefaultColor(R.color.color1)
                    .setColorListener((color, colorHex) -> {
                        binding.drawView.setBrushColor(color);
                    })
                    .show();
        });

        binding.layoutMiscellaneous.viewBlack.setOnClickListener(view -> {
            binding.drawView.setBrushColor(ContextCompat.getColor(this, R.color.black));
            binding.layoutMiscellaneous.blackCheck.setVisibility(View.VISIBLE);
            binding.layoutMiscellaneous.blueCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.whiteCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.redCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.yellowCheck.setVisibility(View.GONE);
        });
        binding.layoutMiscellaneous.viewBlue.setOnClickListener(view -> {
            binding.drawView.setBrushColor(ContextCompat.getColor(this, R.color.colorNoteColor4));
            binding.layoutMiscellaneous.blueCheck.setVisibility(View.VISIBLE);
            binding.layoutMiscellaneous.blackCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.whiteCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.redCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.yellowCheck.setVisibility(View.GONE);
        });
        binding.layoutMiscellaneous.viewRed.setOnClickListener(view -> {
            binding.drawView.setBrushColor(ContextCompat.getColor(this, R.color.colorNoteColor3));
            binding.layoutMiscellaneous.redCheck.setVisibility(View.VISIBLE);
            binding.layoutMiscellaneous.blackCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.whiteCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.blueCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.yellowCheck.setVisibility(View.GONE);
        });
        binding.layoutMiscellaneous.viewWhite.setOnClickListener(view -> {
            binding.drawView.setBrushColor(ContextCompat.getColor(this, R.color.white));
            binding.layoutMiscellaneous.whiteCheck.setVisibility(View.VISIBLE);
            binding.layoutMiscellaneous.blackCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.redCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.blueCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.yellowCheck.setVisibility(View.GONE);
        });
        binding.layoutMiscellaneous.viewYellow.setOnClickListener(view -> {
            binding.drawView.setBrushColor(ContextCompat.getColor(this, R.color.colorNoteColor2));
            binding.layoutMiscellaneous.yellowCheck.setVisibility(View.VISIBLE);
            binding.layoutMiscellaneous.blackCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.redCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.blueCheck.setVisibility(View.GONE);
            binding.layoutMiscellaneous.whiteCheck.setVisibility(View.GONE);
        });
    }

    private void saveDrawing() {

        Bitmap bitmap = binding.drawView.saveAsBitmap();
        File file = new File(getExternalFilesDir(null), "IMG"+System.currentTimeMillis()+".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            if (destination=="note taker") {
                Intent intent = new Intent();
                intent.putExtra(FILE_PATH, file.getAbsolutePath());
                setResult(REQUEST_CODE_DRAW, intent);
                finish();
            } else {
                Intent intent = new Intent(this,NotesTakerActivity.class);
                intent.putExtra(FILE_PATH, file.getAbsolutePath());
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_LONG).show();
        }
    }
}