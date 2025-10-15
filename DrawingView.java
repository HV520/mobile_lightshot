package com.lightshot.mobile;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import java.util.*;

public class DrawingView extends View {
    private Bitmap backgroundBitmap;
    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private String currentTool = "brush";
    private int currentColor = Color.RED;
    private float brushSize = 10f;
    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<String> tools = new ArrayList<>();
    
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }
    
    private void setupDrawing() {
        drawPath = new Path();
        
        drawPaint = new Paint();
        drawPaint.setColor(currentColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(drawBitmap);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, canvasPaint);
        }
        canvas.drawBitmap(drawBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                paths.add(new Path(drawPath));
                paints.add(new Paint(drawPaint));
                tools.add(currentTool);
                drawPath.reset();
                break;
            default:
                return false;
        }
        
        invalidate();
        return true;
    }
    
    public void setCurrentTool(String tool) {
        this.currentTool = tool;
        switch (tool) {
            case "brush":
                drawPaint.setStyle(Paint.Style.STROKE);
                drawPaint.setColor(currentColor);
                break;
            case "eraser":
                drawPaint.setColor(Color.WHITE);
                break;
            case "arrow":
                // Arrow drawing logic
                break;
        }
    }
    
    public void setBackgroundBitmap(Bitmap bitmap) {
        this.backgroundBitmap = bitmap;
        invalidate();
    }
    
    public Bitmap getFinalBitmap() {
        Bitmap result = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }
        canvas.drawBitmap(drawBitmap, 0, 0, null);
        
        return result;
    }
    
    public void undo() {
        if (!paths.isEmpty()) {
            paths.remove(paths.size() - 1);
            paints.remove(paints.size() - 1);
            tools.remove(tools.size() - 1);
            redrawCanvas();
        }
    }
    
    private void redrawCanvas() {
        drawBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(drawBitmap);
        
        for (int i = 0; i < paths.size(); i++) {
            drawCanvas.drawPath(paths.get(i), paints.get(i));
        }
        invalidate();
    }
}
