package edu.utah.cs4962.moviepaint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PaintActivity extends Activity {

    private PaintAreaView paintAreaView;
    private LinearLayout sideMenu;
    private LinearLayout buttonLayout;
    private Intent paletteIntent;
    private static final String PAINT_LINES = "paintLines";
    private Button paintModeButton, colorChooserButton, playModeButton, playButton, pauseButton;
    private SeekBar scrubber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainViewGroup = new LinearLayout(this);
        paintAreaView = new PaintAreaView(this);
        paletteIntent = new Intent(this, PaletteActivity.class);
        buttonLayout = new LinearLayout(this);
        sideMenu = new LinearLayout(this);

        // Prepare controls
        playModeButton = new Button(this);
        paintModeButton = new Button(this);
        colorChooserButton = new Button(this);
        playButton = new Button(this);
        pauseButton = new Button(this);
        scrubber = new SeekBar(this);

        // Set button text
        colorChooserButton.setText("Color");
        paintModeButton.setText("Paint Mode");
        playModeButton.setText("Play Mode");
        playButton.setText("Play");
        pauseButton.setText("Pause");

        // Set click listeners
        colorChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivityForResult(paletteIntent, 0);     }
        });
        playModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlayMode();
            }
        });
        paintModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaintMode();
            }
        });
        scrubber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintAreaView.drawPaths(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                paintAreaView.drawPaths(seekBar.getProgress());
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintAreaView.beginPlay(scrubber.getProgress());
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintAreaView.pausePlay();
            }
        });
        paintAreaView.setOnPlayTimeChangeListener(new OnPlayTimeChangeListener() {
            @Override
            public void onPlayTimeChange() {
                int progress = scrubber.getProgress() + 1;
                if (progress <= 100){
                    scrubber.setProgress(progress);
                } else {
                    paintAreaView.pausePlay();
                }
            }
        });

        // Prepare button layout
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.addView(paintModeButton);
        buttonLayout.addView(playModeButton);
        buttonLayout.addView(colorChooserButton);
        buttonLayout.addView(playButton);
        buttonLayout.addView(pauseButton);

        // Prepare side menu
        sideMenu.setOrientation(LinearLayout.VERTICAL);
        sideMenu.addView(buttonLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        sideMenu.addView(scrubber, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        mainViewGroup.setOrientation(LinearLayout.VERTICAL);
        mainViewGroup.addView(paintAreaView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mainViewGroup.addView(sideMenu, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        setContentView(mainViewGroup);
        setPaintMode();
    }

    private void setPlayMode() {
        // Set invisible controls
        colorChooserButton.setVisibility(View.GONE);
        playModeButton.setVisibility(View.GONE);

        // Set visible controls
        paintModeButton.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        scrubber.setVisibility(View.VISIBLE);

        paintAreaView.setPlayMode(true);
    }

    private void setPaintMode(){
        // Set invisible controls
        paintModeButton.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        scrubber.setVisibility(View.GONE);

        // Set visible controls
        colorChooserButton.setVisibility(View.VISIBLE);
        playModeButton.setVisibility(View.VISIBLE);

        paintAreaView.setPlayMode(false);
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

    @Override
    protected void onStart() {
        super.onStart();
        try{
            Gson gson  = new Gson();
            String paintLines = gson.toJson(paintAreaView.getPaintPaths());
            FileOutputStream fos = openFileOutput(PAINT_LINES, Context.MODE_PRIVATE);
            IOUtils.write(paintLines, fos);
            fos.close();
        } catch (IOException e){
            Log.e("Error saving", e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            Gson gson  = new Gson();
            FileInputStream fis = new FileInputStream(PAINT_LINES);
            String contents = IOUtils.toString(fis);
            ArrayList<PaintPath> paths = gson.fromJson(contents, new TypeToken<ArrayList<PaintPath>>(){}.getType());
            fis.close();
            paintAreaView.setPaintPaths(paths);
        } catch (IOException e){
            Log.e("Error restoring", e.getMessage());
        }
    }
}
