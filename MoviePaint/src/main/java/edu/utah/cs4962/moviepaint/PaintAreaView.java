package edu.utah.cs4962.moviepaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 9/18/13.
 */
public class PaintAreaView extends View implements View.OnTouchListener{

    private Canvas mCanvas;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private List<PaintLine> paintLines;
    private PaintLine currentLine;

    public PaintAreaView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);
        paintLines = new ArrayList<PaintLine>();
        currentLine = new PaintLine(Color.WHITE);
        paintLines.add(currentLine);
        mCanvas = new Canvas();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (PaintLine pl : paintLines){
            for (Path p : pl.getPaths()){
                canvas.drawPath(p, pl.getPaint());
            }
        }
    }

    private void touch_start(float x, float y) {
        currentLine.getPath().reset();
        currentLine.getPath().moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
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
        currentLine.getPaths().add(currentLine.getPath());
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void setPaintColor(CmykColor color) {
        int rgb = color.getRgbColor();
        currentLine = new PaintLine(rgb);
        paintLines.add(currentLine);
    }
}
