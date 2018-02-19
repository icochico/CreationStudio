package com.t2ksports.wwe2k16cs.colorlevels;

import android.graphics.Bitmap;
import android.util.Log;


/**
 * Created by janusz on 8/8/15.
 */

public class ContrastParams extends SeekbarParams implements SeekbarInterface {
    public double minValue = 0.5;
    public double maxValue = 1.5;

    //from 0-100
    public double defaultValue = 50;
    public int defaultSeekBarMax  = 100;

    public String caption = "Contrast";

    @Override
    public Bitmap progressChanged(int progress, Bitmap bitmap){
        float brightness =  0;
        float contrast = getCurrentValue(progress);
        return ImageOperations.changeBitmapContrastBrightness(bitmap, contrast, brightness);
    }

    @Override
    public String getCaption(){

        return  caption;
    }

    @Override
    public int getMaxSeekBarValue(){
        return defaultSeekBarMax;
    }


    @Override
    public float getCurrentValue(int progress)
    {
        Log.i("CONTRAST", progress+ " progress");
        float currentVal =  (float) (getMinValue() + (getMaxValue() -  getMinValue())* (double)progress/(double)this.defaultSeekBarMax);
        Log.i("CONTRAST", progress+ " progress" + currentVal);
        return currentVal;
    }
    @Override
    public int getDefaultValue(){

        return (int) defaultValue;
        //return  (int) ( (defaultValue/maxValue) *(double) (defaultSeekBarMax));
    }

    @Override
    public double getMinValue()
    {
        return  minValue;
    }

    @Override
    public double getMaxValue()
    {
        return  maxValue;
    }


}
