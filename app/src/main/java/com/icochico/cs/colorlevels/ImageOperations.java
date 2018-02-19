package com.t2ksports.wwe2k16cs.colorlevels;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
/**
 * Set of image processing methods
 */
public class ImageOperations {

    public static Bitmap rotateImage(Bitmap bmp, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp,bmp.getWidth(),bmp.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
        return rotatedBitmap;
    }


    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    /**High level Adjusts saturation method
     * @param  bmp bitmap to process
     * @param  value of the saturation
     * */
    public static Bitmap adjustSaturation(Bitmap bmp, float value )
    {
        ColorMatrix cm = new ColorMatrix();
        adjustSaturation(cm, value);

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }


   /* Creates a HUE ajustment ColorFilter
   * @see http://groups.google.com/group/android-developers/browse_thread/thread/9e215c83c3819953
   * @see http://gskinner.com/blog/archives/2007/12/colormatrix_cla.html
   */
    public static Bitmap adjustHue(Bitmap bmp, float value )
    {
        ColorMatrix cm = new ColorMatrix();
        adjustHue(cm, value);

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);


        return ret;
    }

    /* Creates a HUE ajustment ColorFilter
    * @see http://groups.google.com/group/android-developers/browse_thread/thread/9e215c83c3819953
    * @see http://gskinner.com/blog/archives/2007/12/colormatrix_cla.html
    */
    public static void adjustHue(ColorMatrix cm, float value)
    {

        value = cleanValue(value, 180f) / 180f * (float) Math.PI;
        if (value == 0)
        {
            return;
        }
        float cosVal = (float) Math.cos(value);
        float sinVal = (float) Math.sin(value);
        float lumR = 0.213f;
        float lumG = 0.715f;
        float lumB = 0.072f;
        float[] mat = new float[]
                {
                        lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0,
                        0f, 0f, 0f, 1f, 0f,
                        0f, 0f, 0f, 0f, 1f};
        cm.postConcat(new ColorMatrix(mat));
    }

    protected static float cleanValue(float p_val, float p_limit)
    {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }


    /**
     * Modifies the color matrix to change a saturation
     * @param  cm color matrix
     *@param value value of saturation
     * */
    public static void adjustSaturation(ColorMatrix cm, float value) {


        value = cleanValue(value,100);
        if (value == 0) {
            return;
        }

        float x = 1+((value > 0) ? 3 * value / 100 : value / 100);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;

        float[] mat = new float[]
                {
                        lumR*(1-x)+x,lumG*(1-x),lumB*(1-x),0,0,
                        lumR*(1-x),lumG*(1-x)+x,lumB*(1-x),0,0,
                        lumR*(1-x),lumG*(1-x),lumB*(1-x)+x,0,0,
                        0,0,0,1,0,
                        0,0,0,0,1
                };
        cm.postConcat(new ColorMatrix(mat));
    }
}
