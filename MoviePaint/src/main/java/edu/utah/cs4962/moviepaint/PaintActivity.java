package edu.utah.cs4962.moviepaint;

import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class PaintActivity extends Activity {

    private PaintAreaView paintAreaView;
    private PaletteView paletteView;
    private LinearLayout sideMenu;
    private static final String PAINT_LINES = "paintLines";
    private static final String PALETTE_COLORS = "paletteColors";
    private static final String ACTIVE_COLOR = "activeColor";
    private OnColorChangeListener colorChanged = new OnColorChangeListener() {
        @Override
        public void onColorChange(CmykColor color) {
            paintAreaView.setPaintLineColor(color);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainViewGroup = new LinearLayout(this);
        paintAreaView = new PaintAreaView(this);
        paletteView = new PaletteView(this);

        // Prepare side menu
        sideMenu = new LinearLayout(this);
        sideMenu.setOrientation(LinearLayout.HORIZONTAL);
        Button playModeButton = new Button(this);
        Button paintModeButton = new Button(this);
        paintModeButton.setText("Paint Mode");
        playModeButton.setText("Play Mode");
        sideMenu.addView(paintModeButton);
        sideMenu.addView(playModeButton);

        // Prepare palette
        paletteView.setOnColorChangeListener(colorChanged);

        mainViewGroup.setOrientation(LinearLayout.VERTICAL);
        mainViewGroup.addView(paintAreaView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mainViewGroup.addView(paletteView,  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mainViewGroup.addView(sideMenu, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        setContentView(mainViewGroup);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PAINT_LINES, paintAreaView.getPaintPaths());
        outState.putIntegerArrayList(PALETTE_COLORS, paletteView.getPaletteColors());
        outState.putInt(ACTIVE_COLOR, paletteView.getActiveColor());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        paintAreaView.setPaintPaths(savedInstanceState.getParcelableArrayList(PAINT_LINES));
        paletteView.setPaletteColors(savedInstanceState.getIntegerArrayList(PALETTE_COLORS));
        int color = savedInstanceState.getInt(ACTIVE_COLOR);
        paletteView.setActiveColor(color);
        paintAreaView.setPaintLineColor(new CmykColor(color));
    }

}
