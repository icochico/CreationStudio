package com.t2ksports.wwe2k16cs.color;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.mask.MaskActivity;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import java.lang.ref.WeakReference;
import static java.lang.Math.abs;

/**
 * ColorActivity.java
 *
 * Class <code>ColorActivity</code> allows the user to pick a color by touching on the surface
 * of the screen where the image is and select one specific color. Using the <code>SeekBar</code>,
 * the user can then decide to replace that color with a Color.TRANSPARENT. Based on the strength
 * that the user decides to apply on the seek bar, the range of colors that will also be removed
 * will increase or decrease. This activity uses an <code>AsyncTask</code> to perform its
 * operations.
 */
public class ColorActivity extends Activity {

    private static final String TAG = "ColorActivity";

    private ImageView mResultView;
    private ImageView mColorView;
    private Bitmap mBitmap;
    private SeekBar mSeekStrength;
    private Button mBtnDone;
    private Button mBtnCancel;
    private ImageView mLogo;
    private ProgressBar mProcessing;
    private int mSelectedColor = Color.TRANSPARENT;
    private static final int MAX_VALUE = 10;

    private Bitmap[] mBitmapsAtLevel = new Bitmap[MAX_VALUE+1];
    private int mHighestLevel = 0;
    private float[] mHueMatrix;

    private final State state = State.getInstance();


    /**
     * Shows the tutorial.
     */
    private void showTutorial(){
        String text = "";
        text += "\n\u2022"+" "+ getString(R.string.mask_color_tip_01);
        text += "\n\u2022"+" "+ getString(R.string.mask_color_tip_02);

        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text",text);
        startActivity(i);

    }

    /**
     * Instantiates the main UI elements.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_main);

        Button infoButton = (Button) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();

            }
        });
        if(State.showTutorialsForActivity(this)){
            showTutorial();
        }

        Intent pending = getIntent();

        if (pending != null && state.bitmap != null) {
            //load result image in view

            mBitmap = state.bitmap;

        Bitmap.Config c =    mBitmap.getConfig();
            Log.i(TAG, c.name());


            initHueMatrix(mBitmap);
            resetBitmaps();



            mBitmapsAtLevel[0] = mBitmap;
            mResultView = (ImageView) findViewById(R.id.resultView);
            mResultView.setImageBitmap(mBitmap);
        }

        mProcessing = (ProgressBar) findViewById(R.id.processing);
        mProcessing.setVisibility(View.GONE);

        mColorView = (ImageView) findViewById(R.id.colorView);
        mResultView.setOnTouchListener(new View.OnTouchListener() {

            /**
             * Retrieves the color based on the X and Y of the pixes selected by the user.
             *
             * @param v view
             * @param event event
             * @return boolean
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                int y = (int) event.getY();

                int color = getProjectedColor(mResultView, mBitmap, x, y);

                if (color == Color.TRANSPARENT) {
                    Log.d(TAG, "Transparent selected, doing nothing");
                    return false;
                }

                int a = Color.alpha(color);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                float[] hsv = new float[3];

                Color.RGBToHSV(r, g, b, hsv);


                Log.d(TAG, "HSV value. Hue: " + hsv[0] + " Saturation: " + hsv[1] + " Value: " + hsv[2]);
                mSelectedColor = color;

                setColor(color);


                //computation
                resetBitmaps();
                BitmapWorkerTask worker = new BitmapWorkerTask(mResultView);
                worker.execute(mSelectedColor, mSeekStrength.getProgress());
                mProcessing.setVisibility(View.VISIBLE);

                return false;
            }
        });

        mSeekStrength = (SeekBar) findViewById(R.id.seekStrength);
        mSeekStrength.setMax(MAX_VALUE);
        mSeekStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Changes the tolerance of the current selection, passing the value to
             * the <code>BitmapWorkerTask</code>.
             * @param seekBar seekbar
             * @param progress current progress
             * @param fromUser selected by user?
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "Tolerance is: " + progress);

                if (mSelectedColor == Color.TRANSPARENT) {
                    Log.d(TAG, "Transparent is selected, doing nothing");
                    return;
                }

                BitmapWorkerTask worker = new BitmapWorkerTask(mResultView);
                worker.execute(mSelectedColor, progress);
                mProcessing.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMask = new Intent(getApplicationContext(), MaskActivity.class);
                startActivity(goToMask);
            }
        });


        mBtnDone = (Button) findViewById(R.id.btnDone);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.bitmap = mBitmap;
                back();
            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));

    }


    /**
     * Projects the position on ImageView to the position on Bitmap
     *
     * @param iv the original imageview
     * @param bm the original bitmap
     * @param x the position x of the pixel
     * @param y the position y of the pixel
     * @return the color on the projected position
     */
    private int getProjectedColor(ImageView iv, Bitmap bm, int x, int y) {


        if ( x < 0 || y < 0 || x > iv.getWidth() || y > iv.getHeight())
        {
            return Color.TRANSPARENT;
        }
        else
        {
            int projectedX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
            int margin = (iv.getHeight() -  bm.getHeight()) / 2;
            //Log.d(TAG, "Margin m: " + margin);
            int projectedY = y - margin;

            if (projectedY < 0 || projectedY > bm.getHeight()) {
                //Log.w(TAG, "Y is out of bounds: " + projectedY + ". Using TRANSPARENT");
                return Color.TRANSPARENT;
            }

            return bm.getPixel(projectedX, projectedY);
        }
    }


    /**
     * Goes back to mask selection
     */
    public void back()
    {
        Intent backToMask = new Intent(getApplicationContext(), MaskActivity.class);
        backToMask.setAction(com.t2ksports.wwe2k16cs.util.Util.AFTER_EDIT);
        startActivity(backToMask);
    }

    /**
     * Initializes the hue matrix
     *
     *
     * @param bitmap the input bitmap
     */
    private void initHueMatrix(Bitmap bitmap)
    {
        int matrixLength = bitmap.getHeight() * bitmap.getWidth();
        mHueMatrix = new float[matrixLength];
        for (int i = 0; i < matrixLength; i++) {
            mHueMatrix[i] = (float) -1;
        }
    }

    /**
     * Resets the bitmaps in the array that stores bitmaps at different levels
     */
    private void resetBitmaps()
    {
        for (int i = 1; i <= MAX_VALUE; i++) {
            mBitmapsAtLevel[i] = null;
        }
    }

    /**
     * Removes a certain color from the bitmap, within the selected tolerance
     *
     * @param color the value of the color
     * @param tolerance the value of the tolerance
     * @return the bitmap
     */
    private Bitmap removeColorFromBitmap(int color, int tolerance)
    {
        if (mBitmapsAtLevel[tolerance] != null) {
            Log.d(TAG, "Bitmap at tolerance " + tolerance + " already processed, returning it");
            mBitmap = mBitmapsAtLevel[tolerance];
            return mBitmapsAtLevel[tolerance];
        }
        else if (tolerance > mHighestLevel){
            Log.d(TAG, "Providing bitmap at level " + mHighestLevel + " as a base for level " + tolerance);
            mBitmap = mBitmapsAtLevel[mHighestLevel];
        }
        else {
            Log.d(TAG, "Providing base bitmap at level 0");
            mBitmap = mBitmapsAtLevel[0];
        }

        int [] allPixels = new int[mBitmap.getHeight() * mBitmap.getWidth()];

        mBitmap.getPixels(allPixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());

        float selectedHueAngle = getHueAngle(color);
        float upperBound = (selectedHueAngle + (tolerance * 7)) % 360;
        Log.d(TAG, "Upper bound is: " + upperBound);
        float lowerBound = (selectedHueAngle - (tolerance * 7)) % 360;
        Log.d(TAG, "Lower bound is: " + lowerBound);

        float[] hsv = new float[3];
        for (int i = 0; i < allPixels.length; i++)
        {
            if (allPixels[i] == Color.TRANSPARENT) continue;

            //separate components
            //int a = Color.alpha(color);
            if (mHueMatrix[i] == -1) {
                int r = Color.red(allPixels[i]);
                int g = Color.green(allPixels[i]);
                int b = Color.blue(allPixels[i]);
                Color.RGBToHSV(r, g, b, hsv);
                mHueMatrix[i] = hsv[0];
            }

            if (mHueMatrix[i] < upperBound &&
                    mHueMatrix[i] > lowerBound) {
                allPixels[i] = Color.TRANSPARENT;
            }
        }

        Log.d(TAG, "Storing computed bitmap at pos: " + tolerance);
        mBitmapsAtLevel[tolerance] = mBitmap.copy(mBitmap.getConfig(), true);
        mBitmapsAtLevel[tolerance].setPixels(allPixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        if (tolerance > mHighestLevel) mHighestLevel = tolerance; //save this as the highest tolerance
        mBitmap = mBitmapsAtLevel[tolerance];
        return mBitmapsAtLevel[tolerance];
    }


    /**
     * Gets the hue angle of the specific color
     *
     * @param color the value of the color
     * @return hue of the given color
     */
    private float getHueAngle(int color) {
        return getHSVColor(color)[0];
    }

    /**
     * Gets the color in HSV format
     *
     * @param color the value of the color
     * @return the color in HSV format
     */
    private float[] getHSVColor(int color) {

        float[] hsv = new float[3];

        //separate components
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        Color.RGBToHSV(r, g, b, hsv);

        return hsv;
    }

    /**
     * Sets the color to a specific value
     *
     * @param color the value of the color
     */
    private void setColor(int color)
    {
        Bitmap image = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        image.eraseColor(color);
        Bitmap colorBmp = getRoundedShape(image);
        mColorView.setImageBitmap(colorBmp);
    }

    /**
     * Transforms the input bitmap into a rounded shape bitmap
     *
     * @param scaleBitmapImage the input bitmap
     * @return the returned bitmap
     */
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight ,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    /**
     * The <code>AsyncTaks</code> that performs the operation of removing a color from the bitmap,
     * all in background.
     */
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {

            Log.d(TAG, "Performing removal with tolerance: " + params[1]);
            return removeColorFromBitmap(params[0], params[1]);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "Finished processing bitmap");
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    Log.d(TAG, "********************* UPDATING BITMAP");
                    imageView.setImageBitmap(bitmap);
                    mProcessing.setVisibility(View.GONE);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_double_slider, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
