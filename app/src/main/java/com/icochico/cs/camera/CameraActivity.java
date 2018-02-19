package com.t2ksports.wwe2k16cs.camera;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;

import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import com.t2ksports.wwe2k16cs.MainActivity;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.util.BitmapHelper;

import com.t2ksports.wwe2k16cs.util.State;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CameraActivity.java
 *
 * Camera activity is responsible for initializing the <code><Camera/code> and <code>CameraPreview</code>
 * objects that display the camera preview on screen.
 * The class also initialized a shutter <code>Button</code>,one to switch between front and back
 * Cameras, one to load pictures from SDCard and divides the screen through a vertical and horizontal line.
 */
public class CameraActivity extends Activity {

    private final String TAG = "CameraActivity";

    private ImageButton btnShutter;
    private ImageView mImgVerticalLine;
    private ImageView mImgHorizontalLine;
    private Button btnLibrary;
    private Button btnCancel;
    private SurfaceView mCameraView;
    private int mCurrentCameraId;
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    Context context = this;
    private static final int SELECT_PHOTO = 100;

    private final State state = State.getInstance();

    /**
     * Shows the the tutorial on the screen
     * */
    private void showTutorial() {
        String text = "";
        if (state.appMode.equals(State.AppMode.KLOGO)) {
            text = getString(R.string.logo_tip_header) + "\n";
            text += "\n\u2022" + " " + getString(R.string.logo_tip_01);
            text += "\n\u2022" + " " + getString(R.string.logo_tip_02);
        } else {
            text = getString(R.string.face_tip_header) + "\n";
            text += "\n\u2022" + " " + getString(R.string.face_tip_01);
            text += "\n\u2022" + " " + getString(R.string.face_tip_02);
            text += "\n\u2022" + " " + getString(R.string.face_tip_03);
            text += "\n\u2022" + " " + getString(R.string.face_tip_04);
            text += "\n\u2022" + " " + getString(R.string.face_tip_05);
        }
        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text", text);
        startActivity(i);

    }

    /**
     * Initialized the main UI, loads lines, shutters, switch camera and library buttons.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            setContentView(R.layout.camera_main);

            Button infoButton = (Button) findViewById(R.id.infoButton);
            infoButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTutorial();
                }
            });

            mImgHorizontalLine = (ImageView) findViewById(R.id.horizontalLine);
            mImgVerticalLine = (ImageView) findViewById(R.id.verticalLine);

            if (state.appMode.equals(State.AppMode.KLOGO)) {
                mImgHorizontalLine.setVisibility(View.GONE);
                mImgVerticalLine.setVisibility(View.GONE);
            }

            btnShutter = (ImageButton) findViewById(R.id.btnShutter);

            btnShutter.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        btnShutter.setImageDrawable(getResources().getDrawable((R.drawable.shutter_selected_40)));
                        try {
                            mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
                        } catch (Exception e) {
                             Crashlytics.getInstance().logException(e);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        btnShutter.setImageDrawable(getResources().getDrawable(R.drawable.shutter_40));
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        //do something
                    }

                    return false;
                }
            });

            btnCancel = (Button) findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(backToMainIntent);
                }
            });

            btnLibrary = (Button) findViewById(R.id.btnLibrary);
            btnLibrary.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            });

            ImageButton imgSwitchCamera = (ImageButton) findViewById(R.id.btnSwitchCamera);


            //if phone has only one camera, hide "switch camera" button
            if (Camera.getNumberOfCameras() == 1) {
                imgSwitchCamera.setVisibility(View.INVISIBLE);
            } else {
                imgSwitchCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            mCamera.stopPreview();
                            //NB: if you don't release the current camera before switching, you app will crash
                            mCameraPreview.setCamera(null);
                            mCamera.release();
                            mCamera = null;
                            //swap the id of the camera to be used
                            if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                            } else {
                                mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                            }
                            mCamera = Camera.open(mCurrentCameraId);

                            setCameraDisplayOrientation(CameraActivity.this, mCurrentCameraId, mCamera);
                            //this step is critical or preview on new camera will no know where to render to

                            mCamera.startPreview();
                            mCameraPreview.setCamera(mCamera);
                            mCameraPreview.setPreviewDisplay();
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to start preview: " + e.getLocalizedMessage());
                            Crashlytics.logException(e);
                        }
                    }
                });
            }

            mCameraView = (SurfaceView) findViewById(R.id.cameraView);
           //Setting up the surface view
            mCameraPreview = new CameraPreview(this, mCameraView);

            mCameraPreview.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            ((RelativeLayout) findViewById(R.id.mainLayout)).addView(mCameraPreview);
            mCameraPreview.setKeepScreenOn(true);
        }
    }


    /**
     * Opens the front facing camera.
     *
     * @return a <code>Camera</code> object.
     */
    private Camera openFrontFacingCamera() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        int camId = 0;
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                    camId = camIdx;
                    mCurrentCameraId = camIdx;
                } catch (RuntimeException e) {
                   // Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                    Crashlytics.getInstance().logException(e);
                }
            }
        }


        setCameraDisplayOrientation(this, camId, cam);

        return cam;
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                mCamera = openFrontFacingCamera();
                mCamera.startPreview();
                mCameraPreview.setCamera(mCamera);
                mCameraPreview.setPreviewDisplay();

            } catch (RuntimeException ex) {
                Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show();
             //   Log.e(TAG, "Failed to start preview: " + ex.getLocalizedMessage());
                        Crashlytics.getInstance().logException(ex);
            }
        }
    }

    @Override
    protected void onPause() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCameraPreview.setCamera(null);
                mCamera.release();
                mCamera = null;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to stop preview during onPause(): " + e.getLocalizedMessage());
            Crashlytics.logException(e);
        }

        super.onPause();
    }

    private void resetCam() {
        mCamera.startPreview();
        mCameraPreview.setCamera(mCamera);
    }


/**Shutter callback*/
Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };
    /**Picture callback*/
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null) {
                Log.d(TAG, "onPictureTaken - raw - data is null");
            } else {
                Log.d(TAG, "onPictureTaken - raw data content");
            }

        }
    };

/**JPEG Callback*/
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            try {
                Log.d(TAG, "onPictureTaken - jpeg. Size: " + data.length);

                //convertToBitmap(data, camera);
                Bitmap bmp = cleanUpCameraData(data,mCurrentCameraId);

                if(bmp == null){
                    Crashlytics.getInstance().log("Bitmap is null!");
                }
                else{
                    state.bitmap = bmp;
                }
                startConfirmRetake();
                //saveToFormats(data);
                resetCam();
            } catch (Exception e) {
                Log.e(TAG, "Camera exception during jpegCallback onPictureTaken", e);
                Crashlytics.getInstance().logException(e);
            }
        }
    };


    /**
     * Converts the byte array to a <code>Bitmap</code>, saving it on the <code>State</code>
     * singleton.
     * @param data a byte array containing the bitmap
     * @param cameraId id of camera, either fron or back
     */
    private Bitmap cleanUpCameraData(byte[] data, int cameraId){
        if (data != null) {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

             Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0,options);




            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Notice that width and height are reversed
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenHeight, screenWidth, true);
                int w = scaled.getWidth();
                int h = scaled.getHeight();

                // Setting post rotate to 90
                Matrix mtx = new Matrix();
                int rotAngle;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    rotAngle = 90;
                } else {
                    rotAngle = 270;

                    //Apply mirror effect
                   scaled = flip(scaled);
                }

                mtx.postRotate(rotAngle);
                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
            }else{// LANDSCAPE MODE
                //No need to reverse width and height
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, screenWidth,screenHeight , true);
                bitmap=scaled;
            }

            return bitmap;
        }
    return null;
    }


    /**Flips image vertically eliminating problem on some of the devices where mirroring occurs*/
    public Bitmap flip(Bitmap src)
    {
        Matrix m = new Matrix();
        m.preScale(1, -1);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);

        return dst;
    }


    /**
     * Converts the <code>Uri</code> to a <code>Bitmap</code>, saving it on the <code>State</code>
     * singleton.
     * @param uri <code>Uri</code> with the path of the bitmap
     */
    private Bitmap cleanUpLibraryBitmap(Uri uri) {
        Bitmap bmp = null;

        try {
            int orientation = getOrientation(CameraActivity.this, uri);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            bitmap = BitmapHelper.shrinkBitmap(bitmap, 1024, orientation);
            bmp = bitmap;

        } catch (IOException e) {
           // Log.e(TAG, "Image not found!!!!", e);
            Crashlytics.getInstance().logException(e);
        }
        return bmp;
    }

/**Gets orientation from the Media Store rather than using exif data*/
    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor=null;

        try {
            cursor = context.getContentResolver().query(photoUri,
                    new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
                    null, null, null);

            /**On some egzotic Android devices that can be null*/
            if(cursor == null){
                //get real path
                String path = getRealPathFromURI(context, photoUri);
                if(path == null){
                    return 0;
                }
                //In this case we will exif orientation as alternative to querying media
                int exifOrientation = getExifOrientation(path);
                return exifOrientation;
            }

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            } else {
                return 0;
            }
        }
        catch (Exception e){
            Crashlytics.getInstance().logException(e);

        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        //if the cursor is null we will retun 0
        return 0;
    }


    public static int getExifOrientation(String filepath) {// YOUR MEDIA PATH AS STRING
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }



    /**
     * Starts the activity to confirm or retake the picture.
     */
    private void startConfirmRetake() {

        Intent confirmIntent = new Intent(this, ConfirmRetakeActivity.class);
        startActivity(confirmIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {

            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    Bitmap bmp = cleanUpLibraryBitmap(selectedImage);
                    state.bitmap = bmp;
                    startConfirmRetake();
                }
                break;
        }
    }

    /**Returns the rotation of the window */
    public static int getWindowOrientation(int cameraId, Activity activity){
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();


        return rotation;
    }


    /**
     * Sets the camera display orientation
     * @param activity the current activity
     * @param cameraId the camera id
     * @param camera the current camera object
     */
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = getWindowOrientation(cameraId, activity);

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

   /**Some of the URIS are not correctly returned therefore this is a fix */
    private static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}