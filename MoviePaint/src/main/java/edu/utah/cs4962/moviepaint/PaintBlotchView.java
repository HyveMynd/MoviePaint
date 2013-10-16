package edu.utah.cs4962.moviepaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Andres on 9/18/13.
 */
public class PaintBlotchView extends View {

    private CmykColor color;
    private boolean isActive;

    public PaintBlotchView(Context context, CmykColor color){
        super(context);
        this.color = color;
    }

    public void setColor(CmykColor color){
        this.color = color;
    }

    public CmykColor getColor(){
        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isActive){
            canvas.drawColor(Color.YELLOW);
        }
        Paint paint = new Paint();
        int rgb = color.getRgbColor();
        paint.setColor(rgb);
        canvas.drawCircle((this.getWidth()/2f), (this.getHeight()/2f), (this.getWidth()/2f), paint);
    }



    public void setIsActive(boolean active) {
        this.isActive = active;
        this.invalidate();
    }

    public boolean isActive() {
        return isActive;
    }
}
