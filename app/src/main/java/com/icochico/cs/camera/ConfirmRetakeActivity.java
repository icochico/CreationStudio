package com.t2ksports.wwe2k16cs.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.colorlevels.ImageOperations;

import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.Util;
import com.t2ksports.wwe2k16cs.zoomcrop.ParentActivity;
import com.t2ksports.wwe2k16cs.zoomcrop.ZoomCropActivity;

/**
 *  ConfirmRetakeActivity.java
 *
 *  Class <code>ConfirmRetakeActivity</code> serves as a transition between the picture taken
 *  by the user and the editing area of the app. The user can choose whether he wants to rotate,
 *  confirm the current picture and move the editing area or retake the picture and go back in
 *  camera mode.
 *
 */
public class ConfirmRetakeActivity extends Activity {

    private final String TAG = "ConfirmRetakeActivity";

    private ImageView mResultView;
    private Button mBtnRetake;
    private Button mBtnConfirm;
    private Bitmap mBmpImage;
    private Button mBtnRotate;

    private ImageView mLogo;

    private final State state = State.getInstance();

    /**
     * Creates the main buttons to retake, confirm and rotate.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.confirm_main);
        Intent pending = getIntent();

        if (pending != null && state.bitmap != null) {
            //load result image in view
            mBmpImage = state.bitmap;
            mResultView = (ImageView) findViewById(R.id.resultView);
            mResultView.setImageDrawable(new BitmapDrawable(getResources(), mBmpImage));
            mResultView.setScaleType(ImageView.ScaleType.FIT_XY);
        }


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


        mBtnRotate = (Button) findViewById(R.id.btnRotate);
        mBtnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rotate image 90 degrees
                if (state.bitmap != null) {
                    state.bitmap = ImageOperations.rotateImage(state.bitmap,90);
                    mResultView.setImageDrawable(new BitmapDrawable(getResources(), state.bitmap));
                }
            }
        });

        mBtnRetake = (Button) findViewById(R.id.btnRetake);
        mBtnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToCamera = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(backToCamera);
            }
        });

        mBtnConfirm = (Button) findViewById(R.id.btnCancel);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startZoomCrop();
            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
    }

    private void startZoomCrop() {
        try{
            Intent i = new Intent(this, ZoomCropActivity.class);
            i.putExtra(Util.PARENT_ACTIVITY, ParentActivity.ConfirmRetakeActivity.toString());
            startActivity(i);

        }catch(Exception e){
            Crashlytics.getInstance().logException(e);

        }

    }


    private void showTutorial() {

        String text = "";

        text += "\n\u2022" + " " + getString(R.string.retake_use_tip) + "\n";


        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text", text);
        startActivity(i);
    }


}
