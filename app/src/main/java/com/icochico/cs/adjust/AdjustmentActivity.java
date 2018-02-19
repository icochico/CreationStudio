package com.t2ksports.wwe2k16cs.adjust;

import android.app.Activity;
import android.content.Intent;

import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.UploadOrSaveActivity;
import com.t2ksports.wwe2k16cs.camera.CameraActivity;
import com.t2ksports.wwe2k16cs.colorlevels.DoubleLevelsActivity;
import com.t2ksports.wwe2k16cs.mask.MaskActivity;

import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.Util;
import com.t2ksports.wwe2k16cs.zoomcrop.ParentActivity;
import com.t2ksports.wwe2k16cs.zoomcrop.ZoomCropActivity;

/**
 * AdjustmentActivity.java
 *
 * Class <code>AdjustmentActivity</code> is a container for four editing areas: adjust, color,
 * levels and mask.
 *
 */
public class AdjustmentActivity extends Activity {

    private final String TAG = "AdjustmentActivity";


    private final State state = State.getInstance();

    /**
     * Instantiates the UI.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView mAdjustResultView;
        Button mBtnCropAdjust;
        Button mBtnColor;
        Button mBtnLevels;
        Button mBtnMask;
        Button mBtnDone;
        Button mBtnCancel;

        ImageView mLogo;
        try{
            setContentView(R.layout.adjust_main);
        }
        catch (Exception e){
            Crashlytics.getInstance().logException(e);

        }

        Intent pending = getIntent();

        if (pending != null) {
            //load result image in view
            mAdjustResultView = (ImageView) findViewById(R.id.adjustResultView);
            mAdjustResultView.setImageDrawable(new BitmapDrawable(getResources(), state.bitmap));
        }

        mBtnDone = (Button) findViewById(R.id.btnDone);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadOrSave = new Intent(getApplicationContext(), UploadOrSaveActivity.class);
                uploadOrSave.putExtra(Util.PARENT_ACTIVITY, ParentActivity.AdjustmentActivity.toString());
                startActivity(uploadOrSave);
            }});


        mBtnCropAdjust = (Button) findViewById(R.id.btnAdjust);
        mBtnCropAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent goToCrop = new Intent(getApplicationContext(), CropImage.class);
                Intent goToCrop = new Intent(getApplicationContext(), ZoomCropActivity.class);
                goToCrop.putExtra(Util.PARENT_ACTIVITY, ParentActivity.AdjustmentActivity.toString());
                startActivity(goToCrop);
            }
        });

        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToCamera = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(backToCamera);
            }
        });

        mBtnMask = (Button) findViewById(R.id.btnMask);
        mBtnMask.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goToMask = new Intent(getApplicationContext(), MaskActivity.class);
                startActivity(goToMask);
            }
        });

        mBtnColor = (Button) findViewById(R.id.btnColor);
        mBtnColor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goToColor = new Intent(getApplicationContext(), DoubleLevelsActivity.class);
                goToColor.setAction(DoubleLevelsActivity.ACTION_COLOR);
                startActivity(goToColor);
            }
        });

        mBtnLevels = (Button) findViewById(R.id.btnLevels);
        mBtnLevels.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent goToLevels = new Intent(getApplicationContext(), DoubleLevelsActivity.class);
                goToLevels.setAction(DoubleLevelsActivity.ACTION_LEVELS);
                startActivity(goToLevels);
            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
    }
}
