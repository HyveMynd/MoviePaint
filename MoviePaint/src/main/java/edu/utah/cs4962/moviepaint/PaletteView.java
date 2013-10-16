package edu.utah.cs4962.moviepaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.SpannableString;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andres on 9/18/13.
 */
public class PaletteView extends ViewGroup{

    private boolean isMixing;
    Button mixButton;
    private OnColorChangeListener onColorChangeListener;

    // Click listener for paint blotches. Selects the new paint or mixes the new paint.
    private OnClickListener selectedPaint = new OnClickListener() {
        @Override
        public void onClick(View view) {
            PaintBlotchView paintView = (PaintBlotchView)view;
            CmykColor color = null;

            // Iterate children to deactivate colors and get active color
            for (int childIndex = 0; childIndex < getChildCount(); childIndex++){
                View v = getChildAt(childIndex);
                if(v instanceof PaintBlotchView){
                    PaintBlotchView childView = (PaintBlotchView)v;
                    if (childView.isActive()){
                        color = childView.getColor();
                        childView.setIsActive(false);
                    }
                }
            }
            if (isMixing){
                PaintBlotchView paint = mixPaints(paintView.getColor(), color);
                paint.setIsActive(true);
                onColorChangeListener.onColorChange(paint.getColor());
            } else {
                paintView.setIsActive(true);
                onColorChangeListener.onColorChange(paintView.getColor());
            }
        }
    };

    public PaletteView(Context context) {
        super(context);
        this.isMixing = false;
        setWillNotDraw(false);

        // Setup mix button
        mixButton = new Button(this.getContext());
        SpannableString mix = new SpannableString("Mix");
        mixButton.setText("Mix");
        mixButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mixButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isMixing = true;
                mixButton.setText("Mixing");
            }
        });
        this.addView(mixButton);
        // Setup bitmap
//        paletteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.painterspalette);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
    }

    /**
     * Add a color to the painters palette view with the given CMYK values.
     * @param cyan
     * @param magenta
     * @param yellow
     * @param black
     */
    public PaintBlotchView addColorToPalette(float cyan, float magenta, float yellow, float black){
        CmykColor color = new CmykColor(cyan, magenta, yellow, black);
        return addColorToPalette(color);
    }

    public PaintBlotchView addColorToPalette(int rgbColor){
        CmykColor color = new CmykColor(rgbColor);
        return addColorToPalette(color);
    }

    private PaintBlotchView addColorToPalette(CmykColor color){
        PaintBlotchView paint = new PaintBlotchView(this.getContext(), color);
        paint.setOnClickListener(selectedPaint);
        this.addView(paint);
        isMixing = false;
        mixButton.setText("Mix");
        invalidate();
        return paint;
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

        // Don't take mix button into account
        int numChildren = getChildCount() - 1;
        float paletteWidth = (float)getWidth();
        float paletteHeight = (float)getHeight();

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++){
            View child = getChildAt(childIndex);
            float childWidth = 50.0f;
            float childHeight = 50.0f;
            float centerX = (float)getWidth() / 2f;
            float centerY = (float)getHeight() / 2f;
            if (child instanceof PaintBlotchView){
                // Calculate where to put view around ellipse
                float angle = (float)(2.0f * Math.PI) * ((float)childIndex / (float) numChildren);
                centerX = (paletteWidth * 0.5f) + ((paletteWidth * 0.4f) * FloatMath.cos(angle));
                centerY = (paletteHeight * 0.5f) + ((paletteHeight * 0.4f) * FloatMath.sin(angle));
            } else {
                // Change size for button
                childWidth = 150f;
                childHeight = 80f;
            }
            RectF r = new RectF();
            r.left = -childWidth * 0.5f + centerX;
            r.right = childWidth * 0.5f + centerX;
            r.top = -childHeight * 0.5f + centerY;
            r.bottom = childHeight * 0.5f + centerY;
            child.layout((int)r.left, (int)r.top, (int)r.right, (int)r.bottom);
        }
    }

    private PaintBlotchView mixPaints(CmykColor selectedColor, CmykColor previousColor) {
        float cyan = selectedColor.getCyan() -  ((selectedColor.getCyan() - previousColor.getCyan()) / 2f);
        float magenta = selectedColor.getMagenta() - ((selectedColor.getMagenta() - previousColor.getMagenta()) / 2f);
        float yellow = selectedColor.getYellow() - ((selectedColor.getYellow() - previousColor.getYellow()) / 2f);
        float black = selectedColor.getBlack() - ((selectedColor.getBlack() - previousColor.getBlack()) / 2f);
        return addColorToPalette(cyan, magenta, yellow, black);
    }

    private void removeColorFromPalette(int viewIndex){
        View view = this.getChildAt(viewIndex);
        if (view instanceof PaintBlotchView){
            PaintBlotchView paintBlotchView = (PaintBlotchView)view;
            paintBlotchView.setIsActive(false);
            this.removeViewAt(viewIndex);
        }
    }

    public ArrayList<Integer> getPaletteColors(){
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int i = 0; i < this.getChildCount(); i++){
            View child = this.getChildAt(i);
            if (child instanceof PaintBlotchView){
                PaintBlotchView blotchView = (PaintBlotchView)child;
                colors.add(blotchView.getColor().getRgbColor());
            }
        }
        return colors;
    }

    public void setPaletteColors(List<Integer> rgbColors){
        for (Integer color : rgbColors){
            addColorToPalette(color);
        }
    }

    public OnColorChangeListener getOnColorChangeListener() {
        return onColorChangeListener;
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.onColorChangeListener = onColorChangeListener;
    }
}
