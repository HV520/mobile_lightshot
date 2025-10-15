package com.lightshot.mobile;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.hardware.display.*;
import android.media.*;
import android.media.projection.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private MediaProjectionManager projectionManager;
    private Bitmap capturedBitmap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        
        // Start Floating Service
        startService(new Intent(this, FloatingCaptureService.class));
        
        setupUI();
    }
    
    private void setupUI() {
        Button instantCapture = findViewById(R.id.instantCapture);
        Button areaCapture = findViewById(R.id.areaCapture);
        Button settingsBtn = findViewById(R.id.settingsBtn);
        
        instantCapture.setOnClickListener(v -> startInstantCapture());
        areaCapture.setOnClickListener(v -> startAreaSelection());
        settingsBtn.setOnClickListener(v -> openSettings());
    }
    
    private void startInstantCapture() {
        Intent intent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_CODE);
    }
    
    private void startAreaSelection() {
        // Area selection logic
        Toast.makeText(this, "Area Selection Mode", Toast.LENGTH_SHORT).show();
    }
    
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Open Editing Screen
            Intent editIntent = new Intent(this, EditImageActivity.class);
            editIntent.putExtra("resultCode", resultCode);
            editIntent.putExtra("data", data);
            startActivity(editIntent);
        }
    }
}
