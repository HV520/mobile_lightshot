package com.lightshot.mobile;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.io.*;
import java.util.*;

public class EditImageActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private ImageView imageView;
    private Bitmap originalBitmap;
    private String currentTool = "brush";
    private int currentColor = Color.RED;
    private int brushSize = 10;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        
        initializeViews();
        setupToolbar();
        loadCapturedImage();
    }
    
    private void initializeViews() {
        drawingView = findViewById(R.id.drawingView);
        imageView = findViewById(R.id.capturedImage);
        
        // Tool buttons
        findViewById(R.id.brushBtn).setOnClickListener(v -> setTool("brush"));
        findViewById(R.id.textBtn).setOnClickListener(v -> setTool("text"));
        findViewById(R.id.arrowBtn).setOnClickListener(v -> setTool("arrow"));
        findViewById(R.id.rectangleBtn).setOnClickListener(v -> setTool("rectangle"));
        findViewById(R.id.eraserBtn).setOnClickListener(v -> setTool("eraser"));
        findViewById(R.id.saveBtn).setOnClickListener(v -> saveImage());
        findViewById(R.id.shareBtn).setOnClickListener(v -> shareImage());
        findViewById(R.id.undoBtn).setOnClickListener(v -> drawingView.undo());
    }
    
    private void setTool(String tool) {
        currentTool = tool;
        drawingView.setCurrentTool(tool);
        Toast.makeText(this, "Tool: " + tool, Toast.LENGTH_SHORT).show();
    }
    
    private void loadCapturedImage() {
        // Load captured screenshot
        originalBitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(originalBitmap);
        canvas.drawColor(Color.WHITE);
        imageView.setImageBitmap(originalBitmap);
        drawingView.setBackgroundBitmap(originalBitmap);
    }
    
    private void saveImage() {
        Bitmap editedBitmap = drawingView.getFinalBitmap();
        String fileName = "lightshot_" + System.currentTimeMillis() + ".png";
        
        try {
            File file = new File(getExternalFilesDir(null), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            editedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            
            Toast.makeText(this, "Saved: " + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void shareImage() {
        Bitmap editedBitmap = drawingView.getFinalBitmap();
        try {
            File file = new File(getExternalFilesDir(null), "share.png");
            FileOutputStream fos = new FileOutputStream(file);
            editedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(Intent.createChooser(share, "Share Image"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
