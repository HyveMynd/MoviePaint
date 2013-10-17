package edu.utah.cs4962.moviepaint;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class PaintActivity extends Activity {

    private PaintAreaView paintAreaView;
    private LinearLayout sideMenu;
    private Intent paletteIntent;
    private static final String PAINT_LINES = "paintLines";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainViewGroup = new LinearLayout(this);
        paintAreaView = new PaintAreaView(this);
        paletteIntent = new Intent(this, PaletteActivity.class);

        // Prepare side menu
        sideMenu = new LinearLayout(this);
        sideMenu.setOrientation(LinearLayout.HORIZONTAL);
        Button playModeButton = new Button(this);
        Button paintModeButton = new Button(this);
        Button colorChooser = new Button(this);
        colorChooser.setText("Color");
        colorChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(paletteIntent, 0);
            }
        });
        paintModeButton.setText("Paint Mode");
        playModeButton.setText("Play Mode");
        sideMenu.addView(paintModeButton);
        sideMenu.addView(playModeButton);
        sideMenu.addView(colorChooser);

        mainViewGroup.setOrientation(LinearLayout.VERTICAL);
        mainViewGroup.addView(paintAreaView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mainViewGroup.addView(sideMenu, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        setContentView(mainViewGroup);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PAINT_LINES, paintAreaView.getPaintPaths());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        paintAreaView.setPaintPaths(savedInstanceState.getParcelableArrayList(PAINT_LINES));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            paintAreaView.setPaintLineColor(new CmykColor(data.getIntExtra("color", 0)));
        }
    }
}
