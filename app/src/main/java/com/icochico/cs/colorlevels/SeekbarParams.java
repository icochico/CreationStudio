package com.t2ksports.wwe2k16cs.colorlevels;

import android.graphics.Bitmap;

/**
 * The class is designed for setting up various settings related to image processing
 */
public class SeekbarParams implements SeekbarInterface {

    public double minValue = -255.0;
    public double maxValue = 255.0;
    public double defaultValue = 0;
    public int defaultSeekBarMax = 100;

    private String caption = "Default Value";

    public String getCaption(){
        return  caption;
    }

    public Bitmap progressChanged(int progress, Bitmap bitmap){
        float contrast =  1;
        float brightness = getCurrentValue(progress);
        //it should be between -255 - 255

        return ImageOperations.changeBitmapContrastBrightness(bitmap, contrast, brightness);
    }

    @Override
    public float getCurrentValue(int progress)
    {
        return (float) (getMinValue() + (getMaxValue() -  getMinValue())* (double)progress/(double)this.defaultSeekBarMax);
    }

    @Override
    public int getDefaultValue(){
//TODO Hardcoded value
        return  50;
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

    public int getMaxSeekBarValue(){
        return defaultSeekBarMax;
    }

}