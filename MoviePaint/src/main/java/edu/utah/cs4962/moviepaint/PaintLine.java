package edu.utah.cs4962.moviepaint;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 9/21/13.
 */
public class PaintLine {
    private Path mPath;
    private Paint mPaint;
    private List<Path> paths = new ArrayList<Path>();

    public PaintLine(int color) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        mPath = new Path();
        paths.add(mPath);
    }

    public Path getPath() {
        return mPath;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPath(Path mPath) {
        this.mPath = mPath;
    }
}
