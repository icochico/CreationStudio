package com.t2ksports.wwe2k16cs.feather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;

import com.t2ksports.wwe2k16cs.util.State;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class <code>FeatherSurfaceView</code> extends a <code>SurfaceView</code> that handles the
 * feathering effect.
 */
public class FeatherSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int BYTES_PER_PIXEL = 4;
    private static final int BYTES_PER_COMPONENT = 8;

    private static final String TAG = "FeatherSurfaceView";

    private static final Object drawLock = new Object();

    Bitmap baseImage, featheredImage;
    private ProgressBar mProgress;
    private int baseImageWidthX, baseImageHeightY;
    private Matrix mMatrix;

    private final AtomicBoolean mIsProcessing = new AtomicBoolean(false);
    private final State state = State.getInstance();
    private boolean drawFeatheredImage = false;
    private FeatherImageTask mCurrentTask;

    public FeatherSurfaceView(Context context) {
        super(context);
    }

    public FeatherSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FeatherSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setZOrderOnTop(true);
        SurfaceHolder holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
    }

    /**
     * Initializes the feathering objects.
     *
     * @param bitmap the current bitmap
     * @param progress the current progress
     * @param baseWidth the base width of the image
     * @param baseHeight the base height of the image
     */
    public void init(Bitmap bitmap, ProgressBar progress, int baseWidth, int baseHeight) {
        getHolder().addCallback(this);
        setZOrderOnTop(true);
        SurfaceHolder holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);

        Log.i("width = ", Integer.toString(bitmap.getWidth()));
        Log.i("height = ", Integer.toString(bitmap.getHeight()));

        //baseImage = Bitmap.createScaledBitmap(bitmap, 512, 512, false);
        //featheredImage = Bitmap.createScaledBitmap(bitmap, 512, 512, false);
        baseImage = bitmap;
        featheredImage = bitmap;
        mProgress = progress;
        baseImageWidthX = bitmap.getWidth();
        baseImageHeightY = bitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * To stretch the image to the screen width while maintaining aspect ratio.
     * <p/>
     * Taken from: http://stackoverflow.com/questions/2991110/android-how-to-stretch-an-image-to-the-screen-width-while-maintaining-aspect-ra
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * state.bitmap.getHeight() / state.bitmap.getWidth();
        Log.d(TAG, "Stretched image. W: " + width + " H: " + height);
        setMeasuredDimension(width, height);
    }

    /**
     * Draws on canvas.
     *
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //check if canvas is null because of previously started tasks
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT);

        if (baseImage != null && featheredImage != null) {

            //this is for the scale (stretch to fit horizontally
            if (mMatrix == null) {
                float scaleWidth = (float) canvas.getWidth() / baseImage.getWidth();
                float scaleHeight = (float) canvas.getHeight() / baseImage.getHeight();
                mMatrix = new Matrix();
                mMatrix.postScale(scaleWidth, scaleHeight);
            }

            if (drawFeatheredImage) {
                canvas.drawBitmap(featheredImage, mMatrix, null);
            } else {
                canvas.drawBitmap(baseImage, mMatrix, null);
            }
        }
    }


    /**
     * Gets called once the surface is created.
     *
     * @param surfaceHolder the current <code>SurfaceHolder</code>
     */
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawImage();
    }

    public void drawImage() {
        Canvas c = getHolder().lockCanvas();
        if (c != null) {
            draw(c);
            getHolder().unlockCanvasAndPost(c);
        }
    }

    /**
     * Enable this image to be feathered thought the <code>AsyncTask</code>
     *
     * @param drawFeatheredImage yes if enabled, false if disabled
     */
    public void setDrawFeatheredImage(boolean drawFeatheredImage) {
        this.drawFeatheredImage = drawFeatheredImage;
    }

    /**
     * Gets called once the surface changes.
     *
     * @param surfaceHolder the current <code>SurfaceHolder</code>
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    /**
     * Gets called once the surface is destroyed.
     *
     * @param surfaceHolder the current <code>SurfaceHolder</code>
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    /**
     * Create a new <code>AsyncTask</code> to feather the image.
     *
     * @param featherDepth the depth value of the feathering effect
     */
    public void featherImage(int featherDepth) {

        mCurrentTask = new FeatherImageTask();
        mCurrentTask.execute(featherDepth);
    }

    /**
     * Cancel the current <code>AsyncTask</code>.
     */
    public void cancelTask() {

        if (mCurrentTask != null) mCurrentTask.cancel(true);
        mIsProcessing.set(false);
    }

    /**
     * Returns true if <code>AsyncTask</code> is processing.
     *
     * @return true if <code>AsyncTask</code> is still processing
     */
    public boolean isProcessing() {
        return mIsProcessing.get();
    }

    /**
     * The feathering task.
     */
    private class FeatherImageTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            Log.i(TAG, "DO IN BACKGROUND START");
            mIsProcessing.set(true);
            int featherDepth = params[0];

            for (int x = 0; x < baseImageWidthX; x++) {
                for (int y = 0; y < baseImageHeightY; y++) {

                    int xyPixel = baseImage.getPixel(x, y);
                    int xyAlpha = Color.alpha(xyPixel);
                    if (xyAlpha > 100) {
                        for (int row = 1; row < featherDepth; row++) {
                            if (testBorderLayer(x, y, row)) {
                                int newAlpha = 255 * (row + 1) / (featherDepth + 1);
                                double alphaDiv = (double) newAlpha / (double) xyAlpha;

                                int newR = (int) ((double) Color.red(xyPixel) * alphaDiv);
                                int newG = (int) ((double) Color.green(xyPixel) * alphaDiv);
                                int newB = (int) ((double) Color.blue(xyPixel) * alphaDiv);

                                featheredImage.setPixel(x, y, Color.argb(newAlpha, newR, newG, newB));

                                break;
                            }
                        }
                    }
                }
            }
            Log.i(TAG, "DO IN BACKGROUND END");
            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            drawFeatheredImage = true;
            drawImage();
            if (mProgress != null) {
                mProgress.setVisibility(View.GONE);
            } else {
                Log.e(TAG, "mProgress is NULL");
            }
            mIsProcessing.set(false);
        }

        /**
         * Test method to verify border layer.
         *
         * @param xIndex
         * @param yIndex
         * @param border
         * @return
         */
        private boolean testBorderLayer(int xIndex, int yIndex, int border) {

            int width = border * 2 + 1;
            int height = width - 2;

            for (int i = 1; i < width - 1; i++) {
                int topIndexX = xIndex - border + i;
                int topIndexY = yIndex - border;

                int bottomIndexX = xIndex - border + i;
                int bottomIndexY = yIndex + border;

                if (topIndexX > 1 && topIndexX < baseImageWidthX) {
                    if (testPoint(topIndexX, topIndexY) ||
                            testPoint(bottomIndexX, bottomIndexY))
                        return true;
                }
            }

            if (xIndex < baseImageWidthX - border - 1) {
                for (int k = 0; k < height; k++) {
                    int rightIndexX = xIndex + border;
                    int rightIndexY = yIndex - border + k;

                    if (testPoint(rightIndexX, rightIndexY))
                        return true;
                }
            }

            if (xIndex > border) {
                for (int k = 0; k < height; k++) {
                    int rightIndexX = xIndex - border;
                    int rightIntexY = yIndex - border + k;

                    if (testPoint(rightIndexX, rightIntexY))
                        return true;
                }
            }

            return false;
        }

        /**
         * Test method to verify a point.
         *
         * @param xIndex
         * @param yIndex
         * @return
         */
        private boolean testPoint(int xIndex, int yIndex) {
            return xIndex >= 0 && xIndex < baseImageWidthX &&
                    yIndex >= 0 && yIndex < baseImageHeightY &&
                    Color.alpha(baseImage.getPixel(xIndex, yIndex)) < 30;
        }

    }
}
