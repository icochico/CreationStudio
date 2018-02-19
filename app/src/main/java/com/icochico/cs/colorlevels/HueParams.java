package com.t2ksports.wwe2k16cs.colorlevels;

import android.graphics.Bitmap;

/**
 * Class is used for getting and setting values of the Hue seekbar
 * author Janusz Chudzynski.
 */
public class HueParams extends SeekbarParams implements SeekbarInterface {
    public double minValue = -90;
    public double maxValue = 90;
    public double defaultValue = 50;
    public int defaultSeekBarMax = 100;

    private String caption = "Hue";


    @Override
    public Bitmap progressChanged(int progress, Bitmap bitmap){

        float hue = getCurrentValue(progress);

        return ImageOperations.adjustHue(bitmap, hue);
    }

    @Override
    public String getCaption(){
        return  caption;
    }



    @Override
    public float getCurrentValue(int progress)
    {
        return (int) (getMinValue() + (getMaxValue() -  getMinValue())* (double)progress/(double)this.defaultSeekBarMax);
    }
    @Override
    public int getDefaultValue(){
        return  (int) defaultValue;
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
