package edu.utah.cs4962.moviepaint;

import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PaintActivity extends Activity {

    private PaintAreaView paintAreaView;
    private PaletteView paletteView;
    private OnColorChangeListener colorChanged = new OnColorChangeListener() {
        @Override
        public void onColorChange(CmykColor color) {
            paintAreaView.setPaintColor(color);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainViewGroup = new LinearLayout(this);
        paintAreaView = new PaintAreaView(this);
        paletteView = new PaletteView(this);

        // Prepare palette
        paletteView.addColorToPalette(1,0,0,0);
        paletteView.addColorToPalette(0,1,0,0);
        paletteView.addColorToPalette(0,0,1,0);
        paletteView.addColorToPalette(0,0,0,1);
        paletteView.setOnColorChangeListener(colorChanged);

        mainViewGroup.setOrientation(LinearLayout.VERTICAL);
        mainViewGroup.addView(paintAreaView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mainViewGroup.addView(paletteView,  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        setContentView(mainViewGroup);
    }


}
