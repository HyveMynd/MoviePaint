package edu.utah.cs4962.moviepaint;

import android.graphics.Color;

/**
 * Created by Andres on 9/19/13.
 */
public class CmykColor {
    private final float cyan;
    private final float magenta;
    private final float yellow;
    private final float black;

    public CmykColor(float cyan, float magenta, float yellow, float black) {
        this.cyan = cyan;
        this.magenta = magenta;
        this.yellow = yellow;
        this.black = black;
    }

    public CmykColor(int red, int green, int blue){
        float r = (float)red / 255f;
        float g = (float)green / 255f;
        float b = (float)blue / 255f;
        black = 1f - getMax(r,g,b);
        cyan = (1f - r - black) / (1f - black);
        magenta = (1f - g - black) / (1f - black);
        yellow = (1f - b - black) / (1f - black);
    }

    public CmykColor(int color){
        this(Color.red(color), Color.green(color), Color.blue(color));
    }

    private float getMax(float r, float g, float b) {
        float m1 = Math.max(r, g);
        float m2 = Math.max(g, b);
        return Math.max(m1, m2);
    }

    public int getRgbColor(){
        int red = Math.round(255f * (1f-cyan) * (1f-black));
        int green = Math.round(255f * (1f-magenta) * (1f-black));
        int blue = Math.round(255f * (1f-yellow) * (1f- black));
        return Color.rgb(red, green, blue);
    }

    public float getCyan() {
        return cyan;
    }

    public float getMagenta() {
        return magenta;
    }

    public float getYellow() {
        return yellow;
    }

    public float getBlack() {
        return black;
    }
}
