/*******************************************************************************
 * Copyright 2013-present Winson Tan
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.t2ksports.wwe2k16cs.erase;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.util.State;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.view.WindowManager;

/**
 * This view start with full gray color bitmap and onTouch to make it
 * transparent
 *
 * @author winsontan520
 */
public class WScratchView extends SurfaceView implements IWScratchView, SurfaceHolder.Callback {
    private static final String TAG = "WScratchView";
    State state = State.getInstance();
    // default value constants
    private final int DEFAULT_COLOR = 0xff444444; // default color is dark gray
    private final int DEFAULT_REVEAL_SIZE = 30;

    public static final int DEFAULT_SCRATCH_TEST_SPEED = 4;
    private WScratchViewThread mThread;

    List<Pair> mPathList = new ArrayList<>();
    private int mOverlayColor;
    private Paint mOverlayPaint;
    private int mRevealSize;
    private boolean mIsScratchable = true;
    private boolean mIsAntiAlias = false;

    private Path path; // contains current eraser path

    private boolean mScratchStart = false;
    public Bitmap mScratchBitmap;
    private Drawable mScratchDrawable = null;
    private Paint mBitmapPaint;
    private Matrix mMatrix;
    public Bitmap mScratchedTestBitmap;
    private Canvas mScratchedTestCanvas;
    public Canvas myFreakingCanvas;
    public Bitmap myFreakingBitmap;

    private OnScratchCallback mOnScratchCallback;

    //Enable scratch all area if mClearCanvas is true
    private boolean mClearCanvas = false;
    //Enable click on WScratchView if mIsClickable is true
    private boolean mIsClickable = false;
    //Enable zoom on WScratchView if mIsZoomMode is true
    private boolean mIsZoomMode = false;


    //ZOOM stuff
    private float displayWidth;
    private float displayHeight;


    private static final int INVALID_POINTER_ID = -1;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private float MIN_ZOOM = (float) 1.0;
    private float MAX_ZOOM = (float) 2.0;

    //zoom modes
    private static int NONE = 0;
    private static int DRAG = 1;
    private static int ZOOM = 2;
    private int mode;

    //These two variables keep track of the X and Y coordinate of the finger when it first
    //touches the screen
    private float startX = 0f;
    private float startY = 0f;

    //These two variables keep track of the amount we need to translate the canvas along the X
    //and the Y coordinate
    private float translateX = 0f;
    private float translateY = 0f;

    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;

    //End ZOOM stuff




    public WScratchView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx, attrs);
    }

    public WScratchView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {

        //initialize the scale gesture detector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        // default value
        mOverlayColor = DEFAULT_COLOR;
        mRevealSize = DEFAULT_REVEAL_SIZE;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        displayWidth = display.getWidth();
        displayHeight = display.getHeight();//we are setting it up to the canvas WIDTH


        this.setDrawingCacheEnabled(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WScratchView, 0, 0);

        final int indexCount = ta.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = ta.getIndex(i);
            switch (attr) {
                case R.styleable.WScratchView_overlayColor:
                    mOverlayColor = ta.getColor(attr, DEFAULT_COLOR);
                    break;
                case R.styleable.WScratchView_revealSize:
                    mRevealSize = ta.getDimensionPixelSize(attr, DEFAULT_REVEAL_SIZE);
                    break;
                case R.styleable.WScratchView_antiAlias:
                    mIsAntiAlias = ta.getBoolean(attr, false);
                    break;
                case R.styleable.WScratchView_scratchable:
                    mIsScratchable = ta.getBoolean(attr, true);
                    break;
                case R.styleable.WScratchView_scratchDrawable:
                    mScratchDrawable = ta.getDrawable(R.styleable.WScratchView_scratchDrawable);
                    break;
            }
        }

        setZOrderOnTop(true);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);

        mOverlayPaint = new Paint();
        mOverlayPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        mOverlayPaint.setStyle(Paint.Style.STROKE);
        mOverlayPaint.setStrokeCap(Paint.Cap.ROUND);
        mOverlayPaint.setStrokeJoin(Paint.Join.ROUND);

        // convert drawable to bitmap if drawable already set in xml
        if (mScratchDrawable != null) {
            mScratchBitmap = ((BitmapDrawable) mScratchDrawable).getBitmap();
        }

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //Clear all area if mClearCanvas is true
        if (mClearCanvas) {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            return;
        }

        synchronized (mPathList) {

            if (mScratchBitmap != null) {
                float scaledWidth = (float) canvas.getWidth() / mScratchBitmap.getWidth();
                float scaledHeight = (float) canvas.getHeight() / mScratchBitmap.getHeight();

                if (mMatrix == null) {

                    mMatrix = new Matrix();
                    mMatrix.postScale(scaledWidth, scaledHeight);
                }



            //TODO: Adjusting  Display Height to get rid of flickering

                //  displayWidth = canvas.getWidth();
                //  displayHeight = canvas.getHeight();
               // displayHeight = scaledHeight * canvas.getHeight();



                //ADDED
                canvas.save();
                canvas.scale(mScaleFactor, mScaleFactor);
               // canvas.scale(this.mScaleFactor, this.mScaleFactor, this.mScaleDetector.getFocusX(), this.mScaleDetector.getFocusY());

                //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
                if((translateX * -1) < 0) {
                    translateX = 0;
                }

                //This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
                //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
                //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
                //as doing -1 * (scaleFactor - 1) * displayWidth
                else if((translateX * -1) > (mScaleFactor - 1) * displayWidth) {
                    translateX = (1 - mScaleFactor) * displayWidth;
                }

                if(translateY * -1 < 0) {
                    translateY = 0;
                }

                //We do the exact same thing for the bottom bound, except in this case we use the height of the display


                else if((translateY * -1) > (mScaleFactor - 1) * canvas.getHeight()) {
                    translateY = (1 - mScaleFactor) *canvas.getHeight();
                }

                canvas.translate(translateX/mScaleFactor, translateY/mScaleFactor);

                //ADDED
                canvas.drawBitmap(mScratchBitmap, mMatrix, mBitmapPaint);
            } else {
                canvas.drawColor(mOverlayColor);
            }

            for (Pair pair : mPathList) {
                mOverlayPaint.setAntiAlias(mIsAntiAlias);
                mOverlayPaint.setStrokeWidth((Float) pair.second);

                canvas.drawPath((Path) pair.first, mOverlayPaint);
            }

            //ADDED
            canvas.restore();
            //ADDED
        }
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            // float x = detector.getFocusX();
            // float y = detector.getFocusY();



            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));


            return true;
        }

    }

    /**
     * Enables zoom mode.
     *
     * @param isEnabled
     */
    public void setZoomMode(boolean isEnabled) {
        mIsZoomMode = isEnabled;
    }

    /**
     * Removes last paint.
     */
    public void eraseLastDraw() {
        synchronized (mPathList) {
            if (mPathList.size() > 0) {
                mPathList.remove(mPathList.size() - 1);
            }
        }
    }



    private void updateScratchedPercentage() {
        if (mOnScratchCallback == null) return;
        mOnScratchCallback.onScratch(getScratchedRatio());
    }


    /**Processing the handle and pan (zoom mode) */
    public boolean handlePanAndZoom(MotionEvent event){


        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            //This event happens when the first finger is pressed onto the screen
            case MotionEvent.ACTION_DOWN: {

                //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                //amount for each coordinates This works even when we are translating the first time because the initial
                //values for these two variables is zero.
                startX = event.getX() - previousTranslateX;
                startY = event.getY() - previousTranslateY;


                mode = DRAG;
                break;
            }
            //This event fires when the finger moves across the screen, although in practice I've noticed that
            //this fires even when you're simply holding the finger on the screen.
            case MotionEvent.ACTION_MOVE: {
                //We calculate the values of translateX and translateY by finding the difference between the X/Y coordinate
               //and the starting X/Y coordinate. Since this event is fired every time the finger moves, we're constantly
                //updating the values of these two coordinates
                translateX = event.getX() - startX;
                translateY = event.getY() - startY;

               //mode = DRAG;
               // invalidate();
                break;
            }


            case MotionEvent.ACTION_UP: {
                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslateY
                previousTranslateX = translateX;
                previousTranslateY = translateY;
                mode = NONE;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mode = NONE;
                break;
            }
//This event fires when a second finger is pressed onto the screen
            case MotionEvent.ACTION_POINTER_DOWN: {
                mode = ZOOM;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                mode = DRAG;
                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                //previousTranslateX = translateX;
                //previousTranslateY = translateY;

                break;
            }
        }
        mScaleDetector.onTouchEvent(event);
        if ((mode == DRAG && mScaleFactor != 1f) || mode == ZOOM) {


            invalidate();
        }

        return true;
    }


    public boolean handleErasing(MotionEvent me){
        if (!mIsScratchable) {
            return true;
        }

        //Resetting the translation offset
        if (mScaleFactor == 1){
            previousTranslateY = 0;
            previousTranslateX = 0;

        }

        //private variables created to simplify the zoom/transaltion operations
        float x = (me.getX() - translateX)/mScaleFactor;
        float y = (me.getY() - translateY)/mScaleFactor;

        switch (me.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //we are starting a new path

                path = new Path();
                //Moving to the first point
                path.moveTo(x, y);

                //setting up the coordinates of the first point. We will use to draw a line in th Action.MOVE
                startX = x;
                startY = y;

                mPathList.add(new Pair(path, new Float(mRevealSize)));
                break;
            case MotionEvent.ACTION_MOVE:

                if (mScratchStart) {
                    //here
                    path.lineTo(x, y);
                } else {
                    if (isScratch(startX, x, startY, y)) {
                        mScratchStart = true;
                        path.lineTo(x, y);
                    }
                }
                updateScratchedPercentage();
                break;
            case MotionEvent.ACTION_UP:
                //Set call back if user's finger detach
                if (mOnScratchCallback != null) {
                    mOnScratchCallback.onDetach(true);
                }
                //perform Click action if the motion is not move
                //and the WScratchView is clickable
                if (!mScratchStart && mIsClickable) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            performClick();
                        }
                    });
                }
                mScratchStart = false;
                break;
        }
        return true;


    }


    @Override
    public boolean onTouchEvent(MotionEvent me) {

        if (mIsZoomMode) { //Zoom mode is on which means that all the touch events will processed as pinch and zoom events
            // Let the ScaleGestureDetector inspect all events.
                return  handlePanAndZoom(me);

        } else {
            synchronized (mThread.getSurfaceHolder()) {
                return handleErasing(me);
            }
        }
    }



    /**
     * Gets the resulting image.  It should resolve threading issues
     */
    public Bitmap getResultingImage() {

        synchronized (mPathList) {
            if (mScratchBitmap != null) {
                if (mMatrix == null) {
                    float scaleWidth = (float) myFreakingCanvas.getWidth() / mScratchBitmap.getWidth();
                    float scaleHeight = (float) myFreakingCanvas.getHeight() / mScratchBitmap.getHeight();
                    mMatrix = new Matrix();
                    mMatrix.postScale(scaleWidth, scaleHeight);
                }

                myFreakingCanvas.drawBitmap(mScratchBitmap, mMatrix, mBitmapPaint);

                for (Pair pair : mPathList) {
                    mOverlayPaint.setAntiAlias(mIsAntiAlias);
                    mOverlayPaint.setStrokeWidth((Float) pair.second);
                    myFreakingCanvas.drawPath((Path) pair.first, mOverlayPaint);
                }
            }

            if (myFreakingBitmap == null) {
                Log.d(TAG, "My freaking bitmap is NULL");
            } else {
                Log.d(TAG, "My freaking bitmap is W: " + myFreakingBitmap.getWidth() + " H: "
                        + myFreakingBitmap.getHeight());
            }

        }


        return myFreakingBitmap;
    }


    private boolean isScratch(float oldX, float x, float oldY, float y) {
        float distance = (float) Math.sqrt(Math.pow(oldX - x, 2) + Math.pow(oldY - y, 2));
        return distance > mRevealSize * 2;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // do nothing
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        mThread = new WScratchViewThread(getHolder(), this);
        mThread.setRunning(true);
        mThread.start();

        mScratchedTestBitmap = Bitmap.createBitmap(arg0.getSurfaceFrame().width(), arg0.getSurfaceFrame().height(), Bitmap.Config.ARGB_8888);
        myFreakingBitmap = Bitmap.createBitmap(arg0.getSurfaceFrame().width(), arg0.getSurfaceFrame().height(), Bitmap.Config.ARGB_8888);

        myFreakingCanvas = new Canvas(myFreakingBitmap);
        mScratchedTestCanvas = new Canvas(mScratchedTestBitmap);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        mThread.setRunning(false);
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // do nothing but keep retry
            }
        }

    }

    class WScratchViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private WScratchView mView;
        private boolean mRun = false;

        public WScratchViewThread(SurfaceHolder surfaceHolder, WScratchView view) {
            mSurfaceHolder = surfaceHolder;
            mView = view;
        }

        public void setRunning(boolean run) {
            mRun = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return mSurfaceHolder;
        }

        @Override
        public void run() {
            Canvas c;
            while (mRun) {
                c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (c != null) {
                            mView.draw(c);
                        }
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    @Override
    public void resetView() {
        synchronized (mThread.getSurfaceHolder()) {
            mPathList.clear();
        }
    }

    /*Unused methods*/

//    public void setScaleFactor(float factor) {
//        mScaleFactor = factor;
//    }
//
//    public float getScaleFactor() {
//        return mScaleFactor;
//    }
//
//    public void setFocusX(float focusX) {
//        this.focusX = focusX;
//    }
//
//    public void setFocusY(float focusY) {
//        this.focusY = focusY;
//    }
//
//    public void setPosX(float posX) {
//        this.mPosX = posX;
//    }
//
//    public void setPosY(float posY) {
//        this.mPosY = posY;
//    }
//
//    public void setMatrix(Matrix matrix) {
//        this.mMatrix = matrix;
//    }



/*Mostly Getters and setters, nothing exciting really happens below this line */
    @Override
    public boolean isScratchable() {
        return mIsScratchable;
    }

    @Override
    public void setScratchable(boolean flag) {
        mIsScratchable = flag;
    }

    @Override
    public void setOverlayColor(int ResId) {
        mOverlayColor = ResId;
    }

    @Override
    public void setRevealSize(int size) {
        mRevealSize = size;
    }

    @Override
    public void setAntiAlias(boolean flag) {
        mIsAntiAlias = flag;
    }

    @Override
    public void setScratchDrawable(Drawable d) {
        mScratchDrawable = d;
        if (mScratchDrawable != null) {
            mScratchBitmap = ((BitmapDrawable) mScratchDrawable).getBitmap();
        }
    }

    @Override
    public void setScratchBitmap(Bitmap b) {
        mScratchBitmap = b;
    }

    @Override
    public float getScratchedRatio() {
        return getScratchedRatio(DEFAULT_SCRATCH_TEST_SPEED);
    }

    /**
     * thanks to https://github.com/daveyfong for providing this method
     */
    @Override
    public float getScratchedRatio(int speed) {
        if (null == mScratchedTestBitmap) {
            return 0;
        }
        draw(mScratchedTestCanvas);

        final int width = mScratchedTestBitmap.getWidth();
        final int height = mScratchedTestBitmap.getHeight();

        int count = 0;
        for (int i = 0; i < width; i += speed) {
            for (int j = 0; j < height; j += speed) {
                if (0 == Color.alpha(mScratchedTestBitmap.getPixel(i, j))) {
                    count++;
                }
            }
        }
        float completed = (float) count / ((width / speed) * (height / speed)) * 100;

        return completed;
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
        setMeasuredDimension(width, height);
    }

    @Override
    public void setOnScratchCallback(OnScratchCallback callback) {
        mOnScratchCallback = callback;
    }

    public static abstract class OnScratchCallback {
        public abstract void onScratch(float percentage);

        //Call back funtion to monitor the status of finger
        public abstract void onDetach(boolean fingerDetach);
    }

    //Set the mClearCanvas
    @Override
    public void setScratchAll(boolean scratchAll) {
        mClearCanvas = scratchAll;
    }

    //Set the WScartchView clickable
    @Override
    public void setBackgroundClickable(boolean clickable) {
        mIsClickable = clickable;
    }

    public Bitmap get() {
        return this.getDrawingCache();
    }
}