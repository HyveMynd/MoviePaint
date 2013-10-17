package edu.utah.cs4962.moviepaint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by andresmonroy on 10/1/13.
 */
public class PaletteActivity extends Activity {
    private PaletteView paletteView;
    private static final String PALETTE_COLORS = "paletteColors";
    private static final String ACTIVE_COLOR = "activeColor";
    private boolean isStateSaved;
    private boolean isStateRestored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paletteView = new PaletteView(this);
        isStateSaved = false;
        isStateRestored = false;

        // Prepare palette
        paletteView.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void onColorChange(CmykColor color) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("color", color.getRgbColor());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
        setContentView(paletteView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(PALETTE_COLORS, paletteView.getPaletteColors());
        outState.putInt(ACTIVE_COLOR, paletteView.getActiveColor());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        paletteView.setPaletteColors(savedInstanceState.getIntegerArrayList(PALETTE_COLORS));
        int color = savedInstanceState.getInt(ACTIVE_COLOR);
        paletteView.setActiveColor(color);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isStateSaved){
            return;
        }
        try {
            Gson gson = new Gson();
            String activeString = gson.toJson(paletteView.getActiveColor());
            String colorsString = gson.toJson(paletteView.getPaletteColors());

            // Write active color
            FileOutputStream fos = openFileOutput(ACTIVE_COLOR, Context.MODE_PRIVATE);
            IOUtils.write(activeString, fos);
            fos.close();

            // Write palette colors
            fos = openFileOutput(PALETTE_COLORS, Context.MODE_PRIVATE);
            IOUtils.write(colorsString, fos);
            fos.close();

        } catch (IOException e) {
            Log.e("Error saving", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStateRestored){
            return;
        }
        try {
            Gson gson = new Gson();

            // Read active color
            FileInputStream fis = openFileInput(ACTIVE_COLOR);
            String contents = IOUtils.toString(fis);
            int activeColor = gson.fromJson(contents, int.class);
            paletteView.setActiveColor(activeColor);

            // Read palette colors
            fis = openFileInput(PALETTE_COLORS);
            contents = IOUtils.toString(fis);
            Collection<Integer> colors = gson.fromJson(contents, new TypeToken<Collection<Integer>>(){}.getType());
            paletteView.setPaletteColors(colors);
        } catch (IOException e){
            Log.e("Error reading", e.getMessage());
        }
    }
}
