package edu.utah.cs4962.moviepaint;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * Created by Andres on 9/18/13.
 */
public class PaintAreaView extends View implements View.OnTouchListener{

    private Canvas mCanvas;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private List<PaintLine> paintLines;
    private PaintLine currentLine;
    private int currentRgbColor;
    private ArrayList<PaintPath> paintPaths;
    private float width;
    private float height;
    private boolean isInPlayMode;
    private Timer timer;
    private OnPlayTimeChangeListener onPlayTimeChangeListener;

    public PaintAreaView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);
        paintLines = new ArrayList<PaintLine>();
        currentLine = new PaintLine(Color.WHITE);
        paintPaths = new ArrayList<PaintPath>();
        paintLines.add(currentLine);
        mCanvas = new Canvas();
        isInPlayMode = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        drawPaths();
        for (PaintLine pl : paintLines){
            for (Path p : pl.getPaths()){
                canvas.drawPath(p, pl.getPaint());
            }
        }
    }

    private void touch_start(float x, float y) {
        x *= width;
        y *= height;

        currentLine.getPath().reset();
        currentLine.getPath().moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        x *= width;
        y *= height;

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            currentLine.getPath().quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        currentLine.getPath().lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(currentLine.getPath(), currentLine.getPaint());
        // kill this so we don't double draw
        currentLine.setPath(new Path());
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        // only add more points if the user is in paint mode
        if (!isInPlayMode){
            float x = event.getX();
            float y = event.getY();

            x /= width;
            y /=  height;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    PaintPath path = new PaintPath(x, y, MotionEvent.ACTION_DOWN);
                    path.color = currentRgbColor;
                    paintPaths.add(path);
                    break;
                case MotionEvent.ACTION_MOVE:
                    paintPaths.add(new PaintPath(x,y,MotionEvent.ACTION_MOVE));
                    break;
                case MotionEvent.ACTION_UP:
                    paintPaths.add(new PaintPath(x,y,MotionEvent.ACTION_UP));
                    break;
            }
            drawPaths();
        }
        return true;
    }

    public void setPaintLineColor(CmykColor color) {
        this.currentRgbColor = color.getRgbColor();
    }

    public ArrayList<PaintPath> getPaintPaths() {
        return paintPaths;
    }

    public void setPaintPaths(ArrayList paths) {
        ArrayList<PaintPath> paintPaths  = paths;
        this.paintPaths = paintPaths;
    }

    private void drawPaths(){
        if (!isInPlayMode){
            drawPaths(paintPaths);
        }
    }

    private void drawPaths(ArrayList<PaintPath> pathsToDraw){
        paintLines = new ArrayList<PaintLine>();
        for (PaintPath path : pathsToDraw){
            switch (path.motionEvent){
                case MotionEvent.ACTION_DOWN:
                    currentLine = new PaintLine(path.color);
                    paintLines.add(currentLine);
                    touch_start(path.x, path.y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(path.x, path.y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        }
    }

    public void drawPaths(int progress){
        // decompose only the specified number of paths
        ArrayList<PaintPath> pathsToDraw = new ArrayList<PaintPath>();
        float percent = ((float)progress) / 100f;
        int numPaths = Math.round((float)paintPaths.size() * percent);
        for (int i = 0; i < numPaths; i++){
            pathsToDraw.add(paintPaths.get(i));
        }
        pathsToDraw.add(new PaintPath(0,0,MotionEvent.ACTION_UP));
        drawPaths(pathsToDraw);
    }

    public boolean isInPlayMode() {
        return isInPlayMode;
    }

    public void setPlayMode(boolean isInPlayMode) {
        this.isInPlayMode = isInPlayMode;
    }

    public void beginPlay(int progress){
        drawPaths(progress);
        timer = new Timer();
        timer.scheduleAtFixedRate(new PaintTimer(), 1000, 1000);
    }

    public void pausePlay(){
        timer.cancel();
    }

    public OnPlayTimeChangeListener getOnPlayTimeChangeListener() {
        return onPlayTimeChangeListener;
    }

    public void setOnPlayTimeChangeListener(OnPlayTimeChangeListener onPlayTimeChangeListener) {
        this.onPlayTimeChangeListener = onPlayTimeChangeListener;
    }

    class PaintTimer extends TimerTask{

        @Override
        public void run() {
            onPlayTimeChangeListener.onPlayTimeChange();
        }
    }
}
