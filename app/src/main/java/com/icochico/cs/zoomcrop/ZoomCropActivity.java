package com.t2ksports.wwe2k16cs.zoomcrop;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.adjust.AdjustmentActivity;
import com.t2ksports.wwe2k16cs.camera.ConfirmRetakeActivity;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.Util;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.cropoverlay.CropOverlayView;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.cropoverlay.edge.Edge;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.cropoverlay.utils.ImageViewUtil;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.photoview.PhotoView;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.photoview.PhotoViewAttacher;

/*ZoomCrop Activity is used to crop the iamges to desired logo/face size**/
public class ZoomCropActivity extends Activity {

    public static final String TAG = "ZoomCropActivity";
    PhotoView mImageView;
    CropOverlayView mCropOverlayView;
    Button mBtnDone;
    Button mBtnCancel;
    String mParentActivity;
    private ImageView mLogo;


    private float minScale = 1f;


    private final State state = State.getInstance();
    /**Shows Tutorial on the Screen*/
    private void showTutorial(){
        String text = "";
        text += "\n\u2022"+" "+ getString(R.string.adjust_tip_01);
        text += "\n\u2022"+" "+getString(R.string.adjust_tip_02);

        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text",text);
        startActivity(i);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.zoomcrop_main);


            Button infoButton = (Button) findViewById(R.id.infoButton);
            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTutorial();

                }
            });
            if (State.showTutorialsForActivity(this)) {
                showTutorial();
            }


            mImageView = (PhotoView) findViewById(R.id.iv_photo);
            mCropOverlayView = (CropOverlayView) findViewById(R.id.crop_overlay);

            mBtnCancel = (Button) findViewById(R.id.btnCancel);
            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    back(false);
                }
            });

            mBtnDone = (Button) findViewById(R.id.btnDone);
            mBtnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //crop image
                    Bitmap bitmap = getCroppedImage();
                    Bitmap resized = Util.cropToLogo(bitmap, state.appMode, state.imageType);

                    //Setting up resized bitmap
                    state.bitmap = resized;

                    Log.i(TAG, "Resized bitmap " + resized.getHeight() + "");
                    Log.i(TAG, "Resized bitmap " + resized.getWidth() + "");


                    back(true);
                }
            });


            mImageView.addListener(new PhotoViewAttacher.IGetImageBounds() {
                @Override
                public Rect getImageBounds() {
                    return new Rect((int) Edge.LEFT.getCoordinate(),
                            (int) Edge.TOP.getCoordinate(),
                            (int) Edge.RIGHT.getCoordinate(),
                            (int) Edge.BOTTOM.getCoordinate());
                }
            });

            mLogo = (ImageView) findViewById(R.id.imgLogo);
            mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
        }
        catch (Exception e){
            Crashlytics.getInstance().logException(e);
        }

        Intent pending = getIntent();
        if (pending != null && state.bitmap != null) {
            //load result image in view
            mParentActivity = pending.getStringExtra(Util.PARENT_ACTIVITY);

            try{
                init();

            }catch(Exception e){
                Crashlytics.getInstance().logException(e);

            }


        }
    }


    /**Go back to the prvious activity */
    public void back(boolean isDone)
    {
        if (mParentActivity == null) {
            Log.e(TAG, "ParentActivity is null, error");
            return;
        }

        Class clazz;
        if (mParentActivity.equals(ParentActivity.ConfirmRetakeActivity.toString()) && !isDone) {
            clazz = ConfirmRetakeActivity.class;
        }
        else {
            clazz = AdjustmentActivity.class;
        }
        Intent backToAdjust = new Intent(getApplicationContext(), clazz);
        backToAdjust.setAction(com.t2ksports.wwe2k16cs.util.Util.AFTER_EDIT);
        startActivity(backToAdjust);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
/**Starting point of the Zoom Crop activity*/
    private void init() {

        Log.i(TAG,"Init in the zoom crop activity");

        Drawable bitmap = new BitmapDrawable(getResources(), state.bitmap);
        int h = bitmap.getIntrinsicHeight();
        int w = bitmap.getIntrinsicWidth();
        final float cropWindowWidth = Edge.getWidth();
        final float cropWindowHeight = Edge.getHeight();

        //minimum scale should be
        float m =  (cropWindowWidth + 1f)/w;

        if (h <= w) {
            //Set the image view height to
            //HACK : Have to add 1f.
            minScale = (cropWindowHeight + 1f) / h;
        } else if (w < h) {
            //HACK : Have to add 1f.
            minScale = (cropWindowWidth + 1f) / w;
        }

        //so get the screen of the image
        float maxWidth = cropWindowWidth * 1.5f;
        float maxScale = maxWidth/w;
        //Fix for the scale that has wrong paramaters
        if (minScale > maxScale) {
           float tempScale = maxScale;
           maxScale = minScale;
           minScale = tempScale;
        }

//Setting UP Scale
        mImageView.setMaximumScale(maxScale);
        mImageView.setMediumScale(minScale * 2);//That should be scale to fit
        mImageView.setMinimumScale(minScale);
        mImageView.setImageDrawable(bitmap);
        mImageView.setScale(minScale);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("restoreState", true);
    }


    /*Gets currently displayed image
    *
    * @return returns Bitmap
    * */
    private Bitmap getCurrentDisplayedImage() {
        Bitmap result = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(result);
        mImageView.draw(c);
        return result;
    }

    /**
     * Returns currently displayed iamges cropped to the size of the cropping view
     * */
    public Bitmap getCroppedImage() {

        Bitmap mCurrentDisplayedBitmap = getCurrentDisplayedImage();
        Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(mCurrentDisplayedBitmap, mImageView);

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for width.
        float actualImageWidth = mCurrentDisplayedBitmap.getWidth();
        float displayedImageWidth = displayedImageRect.width();
        float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for height.
        float actualImageHeight = mCurrentDisplayedBitmap.getHeight();
        float displayedImageHeight = displayedImageRect.height();
        float scaleFactorHeight = actualImageHeight / displayedImageHeight;

        // Get crop window position relative to the displayed image.
        float cropWindowX = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        float cropWindowY = Edge.TOP.getCoordinate() - displayedImageRect.top;
        float cropWindowWidth = Edge.getWidth();
        float cropWindowHeight = Edge.getHeight();

        // Scale the crop window position to the actual size of the Bitmap.
        float actualCropX = cropWindowX * scaleFactorWidth;
        float actualCropY = cropWindowY * scaleFactorHeight;
        float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        float actualCropHeight = cropWindowHeight * scaleFactorHeight;

        // Crop the subset from the original Bitmap.
//        Bitmap croppedBitmap = Bitmap.createBitmap(mCurrentDisplayedBitmap, (int) actualCropX, (int) actualCropY, (int) actualCropWidth, (int) actualCropHeight);
    //Applied fix for the crashing application if the resulution or dimensions are causing problems
        final Bitmap croppedBitmap = Bitmap.createBitmap(mCurrentDisplayedBitmap,
                (int) Math.max(0, actualCropX),
                (int) Math.max(0, actualCropY),
                (int) Math.min(mCurrentDisplayedBitmap.getWidth(), actualCropWidth) ,
                (int) Math.min(mCurrentDisplayedBitmap.getHeight() ,actualCropHeight));



        return croppedBitmap;
    }

}
