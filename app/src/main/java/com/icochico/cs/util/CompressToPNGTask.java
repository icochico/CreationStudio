package com.t2ksports.wwe2k16cs.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CompressToPNGTask extends AsyncTask<byte[], Void, File> {

    private final String TAG = "CompressToPNGTask";

    @Override
    protected File doInBackground(byte[]... data) {
        FileOutputStream outStream = null;

        // Write to SD Card
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/wwe2k16");
            dir.mkdirs();

            String fileName = new String(data[1]);
            File outFile = new File(dir, fileName);

            Bitmap bmp;
            bmp = BitmapHelper.byteArrayToBitmap(data[0]);
            if (bmp == null) Log.d(TAG, "Bitmap is null");

            //Bitmap result = BitmapHelper.rotateBitmap(bmp, 270);

            //convert to PNG
            outStream = new FileOutputStream(outFile);
            byte[] pngBuffer = BitmapHelper.bitmapToPNGByteArray(bmp);
            outStream.write(pngBuffer);
            //outStream.write(data[0]);
            outStream.flush();
            outStream.close();

            Log.d(TAG, "- - Wrote bytes: " + pngBuffer.length + " to " + outFile.getAbsolutePath());

            return outFile;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

}