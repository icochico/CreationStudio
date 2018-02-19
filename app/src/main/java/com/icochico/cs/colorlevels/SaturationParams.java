package com.t2ksports.wwe2k16cs.colorlevels;

import android.graphics.Bitmap;

/**
 * Class is used for getting and setting values of the Saturation seekbar
 * author Janusz Chudzynski.
 */
public class SaturationParams extends SeekbarParams implements SeekbarInterface {
    public double minValue = -50;
    public double maxValue = 50;
    public double defaultValue = 50;//that should be translated as 0
    public int defaultSeekBarMax = 100;

    private String caption = "Saturation";



    public Bitmap progressChanged(int progress, Bitmap bitmap){

        float saturation = getCurrentValue(progress);

        return ImageOperations.adjustSaturation(bitmap, saturation);
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

        return (int) defaultValue;
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
