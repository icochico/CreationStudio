package com.t2ksports.wwe2k16cs.colorlevels;

import android.graphics.Bitmap;

/**
 * Created by janusz on 8/8/15.
 */
public interface SeekbarInterface {
   /**
    * Called when seekbar changes value
    * @param progress progress
    * @param bitmap bitmap
    * @return processed bitmap
    */
   Bitmap progressChanged(int progress, Bitmap bitmap);

   /**
    * Returns maximum value of seekbar
    * @return maximum value of seekbar
    */
   int getMaxSeekBarValue();

   /**
    * Calculates current value of the seekbar based on the passed paramaters
    * @param progress current progress
    * @return calculated progress
    */
   float getCurrentValue(int progress);

   /**
    * Returns default value of the seekbar
    * @return default value
    */
   int getDefaultValue();

   /**
    * Gets minimum value of the seekbar
    * @return minimum value
    */
   double getMinValue();

   /**
    * Get maximum value of the seekbar
    * @return maximum value
    */
   double getMaxValue();

   /**
    * Returns a label related to a seekbar
    * @return returns caption
    */
   String getCaption();
}
