package edu.utah.cs4962.moviepaint;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andres on 10/15/13.
 */
public class PaintPath implements Parcelable {
    float x;
    float y;
    int motionEvent;
    int color;

    public PaintPath(float x, float y, int motionEvent) {
        this.x = x;
        this.y = y;
        this.motionEvent = motionEvent;
    }

    public PaintPath(){
        x=0;
        y=0;
        motionEvent=0;
        color = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeInt(motionEvent);
        dest.writeInt(color);
    }
}
