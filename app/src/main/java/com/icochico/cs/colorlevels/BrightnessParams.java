package com.t2ksports.wwe2k16cs.colorlevels;

import android.graphics.Bitmap;

/**
 * Class is used for getting and setting values of the brightness seekbar
 * author Janusz Chudzynski.
 */
public class BrightnessParams extends SeekbarParams implements SeekbarInterface {
    /**Minimum Value of a slider*/
    public double minValue = -140;
    /**Maximum Value of a slider*/
    public double maxValue = 140;
    /**Default Max Value of a seekbar  */
    public int defaultSeekBarMax = 100;
    /**Default starting value of a seekbar  */
    public int defaultValue = 50;
    /**Caption under seekbar  */
    private String caption = "Brightness";


    @Override
    public Bitmap progressChanged(int progress, Bitmap bitmap){
        float contrast =  1;
        float brightness = getCurrentValue(progress);
        //it should be between -255 - 255

        return ImageOperations.changeBitmapContrastBrightness(bitmap, contrast, brightness);
    }

    @Override
    public String getCaption(){
        return  caption;
    }


    @Override
    public float getCurrentValue(int progress)
    {
        return (float) (getMinValue() + (getMaxValue() -  getMinValue())* (double)progress/(double)this.defaultSeekBarMax);
    }
    @Override
    public int getDefaultValue(){
        return  defaultValue;
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
