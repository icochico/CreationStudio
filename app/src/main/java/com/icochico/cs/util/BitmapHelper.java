package com.t2ksports.wwe2k16cs.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.View;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;


/**
 * A helper class to conveniently alter Bitmap data.
 */
public class BitmapHelper {

    public static Bitmap extractBitmap(ImageView v) {

        v.setDrawingCacheEnabled(true);
        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        //Log.d(TAG, "Bitmap size is W: " + b.getWidth() + " H: " + b.getHeight());
        v.setDrawingCacheEnabled(false); // clear drawing cache

        return b;
    }


	/**
     * Converts a Bitmap to a byteArray using PNG compression.
     *
     * @return byteArray
     */
    public static byte[] bitmapToPNGByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Converts a Bitmap to a byteArray using JPG compression.
     *
     * @return byteArray
     */
    public static byte[] bitmapToJPGByteArray(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Converts a byteArray to a Bitmap object
     * @param byteArray bitmap image data
     * @return Bitmap
     */
    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        } else {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
    }



    /**
     * Shrinks and rotates (if necessary) a passed Bitmap.
     * 
     * @param bm bitmap
     * @param maxLengthOfEdge maximum length of the edge
     * @param rotateXDegree degree
     * @return Bitmap
     */
    public static Bitmap shrinkBitmap(Bitmap bm, int maxLengthOfEdge, int rotateXDegree) {
        if (maxLengthOfEdge > bm.getWidth() && maxLengthOfEdge > bm.getHeight()) {
            return bm;
        } else {
            // shrink image
            float scale = (float) 1.0;
            if (bm.getHeight() > bm.getWidth()) {
                scale = ((float) maxLengthOfEdge) / bm.getHeight();
            } else {
                scale = ((float) maxLengthOfEdge) / bm.getWidth();
            }
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scale, scale);
            matrix.postRotate(rotateXDegree);

            // RECREATE THE NEW BITMAP
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                    matrix, false);

            matrix = null;
            System.gc();

            return bm;
        }
    }
}