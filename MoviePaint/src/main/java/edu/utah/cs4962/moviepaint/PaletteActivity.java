package edu.utah.cs4962.moviepaint;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by andresmonroy on 10/1/13.
 */
public class PaletteActivity extends Activity {
    private PaletteView paletteView;
//    private OnColorChangeListener colorChanged = new OnColorChangeListener() {
//        @Override
//        public void onColorChange(CmykColor color) {
//            paintAreaView.setPaintLineColor(color);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainViewGroup = new LinearLayout(this);

        paletteView = new PaletteView(this);
        // Prepare palette
        paletteView.addColorToPalette(1,0,0,0);
        paletteView.addColorToPalette(0,1,0,0);
        paletteView.addColorToPalette(0,0,1,0);
        paletteView.addColorToPalette(0,0,0,1);
        //paletteView.setOnColorChangeListener(colorChanged);

        mainViewGroup.addView(paletteView,  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        setContentView(mainViewGroup);
    }
}
