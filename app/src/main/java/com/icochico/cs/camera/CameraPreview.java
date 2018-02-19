package com.t2ksports.wwe2k16cs.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.util.List;

/**
 * Camera Preview is used to display camera view layer on the screen
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

    //Tag
    private final static String TAG = "CameraPreview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Camera.Size mPreviewSize;

    Camera mCamera;
    /**
     * Default constructor
     * */
    public CameraPreview(Context context, SurfaceView sv) {
        super(context);

        mSurfaceView = sv;
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


/**
 * Sets the camera
 * @param camera to set
 * */
    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
             requestLayout();
            // get Camera parameters
            Camera.Parameters params = mCamera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes != null && params != null){
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    // set the focus mode
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    // set Camera parameters
                    mCamera.setParameters(params);
                }
            }
        }
    }

    /**
     * Sets up the preview display
     * */
    public void setPreviewDisplay() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", e);
                Crashlytics.getInstance().core.logException(e);
            }
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        float ratio = 0;


        try {
         List<Camera.Size> mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
         if (mSupportedPreviewSizes != null) {
             mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);

             if (mPreviewSize.height >= mPreviewSize.width) {
                 ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
             } else {
                 ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;
             }
         }
     }catch (Exception ex){
        Crashlytics.getInstance().core.logException(ex);
     }




        // One of these methods should be used, second method squishes preview slightly
        setMeasuredDimension(width, (int) (width * ratio));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            Crashlytics.getInstance().core.logException(exception);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, "Camera exception during stopPreview()", e);
                Crashlytics.getInstance().core.logException(e);
            }
        }
    }

/**
 * Loops through the available preview sizes
 * @param h height
 * @param w width
 * @param sizes available sizes
 * @return optimal size of the preview
 * */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera != null) {
            try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            requestLayout();


                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.e(TAG, "Camera exception during startPreview()", e);
                //Crashlytics.logException(e);

                Crashlytics.getInstance().core.logException(e);
            }
        }
    }
}