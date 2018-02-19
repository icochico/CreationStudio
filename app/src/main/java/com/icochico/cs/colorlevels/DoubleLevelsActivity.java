package com.t2ksports.wwe2k16cs.colorlevels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.SeekBar;
import android.widget.TextView;


import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.adjust.AdjustmentActivity;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;

import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.Util;
/**
 * Class is a starting point to process images using two seekbars.
 * */

public class DoubleLevelsActivity extends Activity {

    private final String TAG = "DoubleLevelsActivity";

    /**Left seekbar's paramateres  */
    public SeekbarParams leftSliderParams;
    /**Right seekbar's paramateres */
    public SeekbarParams rightSliderParams;
    /**Left seekbar\*/
    private SeekBar mLeftBar;
    /**Right seekbar */
    private SeekBar mRightBar;
    /**Left caption*/
    private TextView mLeftCaption;
    /**Right caption*/
    private TextView mRightCaption;

    /**Cancel button*/
    private Button mBtnCancel;
    /**Done  button*/
    private Button mBtnDone;

    /**Image View that displays te processed picture*/
    private ImageView mImageView;
    /**Bitmap storing the current processed picture*/
    private Bitmap mBmpImage;
    /**Home Button*/
    private ImageView mLogo;

    /**Contains information about  state of the application*/
    private final State state = State.getInstance();

    public final static String ACTION_COLOR = "com.izotx.wwe2k16cs.colorlevels.ACTION_COLOR";
    public final static String ACTION_LEVELS = "com.izotx.wwe2k16cs.colorlevels.ACTION_LEVELS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_levels_main);

        Intent pending = getIntent();
        String action = null;

        if (pending != null && state.bitmap != null) {


            //load result image in view
            mBmpImage = state.bitmap;
            mImageView = (ImageView) findViewById(R.id.backgroundView);
            mImageView.setImageBitmap(mBmpImage);

            //determine action
            action = pending.getAction();

            mLeftBar = (SeekBar) findViewById(R.id.seekLeft);
            mRightBar = (SeekBar) findViewById(R.id.seekRight);
            mLeftCaption = (TextView) findViewById(R.id.txtLeft);
            mRightCaption = (TextView) findViewById(R.id.txtRight);

            if (action.equals(ACTION_COLOR)) {
                mLeftCaption.setText(getString(R.string.seek_hue));
                mRightCaption.setText(getString(R.string.seek_saturation));
                leftSliderParams = new HueParams();
                rightSliderParams = new SaturationParams();
            }
            else if (action.equals(ACTION_LEVELS)) {
                mLeftCaption.setText(getString(R.string.seek_contrast));
                mRightCaption.setText(getString(R.string.seek_brightness));
                leftSliderParams = new ContrastParams();
                rightSliderParams = new BrightnessParams();
            }

            this.setSeekBar(mLeftBar, leftSliderParams);
            this.setSeekBar(mRightBar, rightSliderParams);
        }

        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        mBtnDone = (Button) findViewById(R.id.btnDone);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state.bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                back();
            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
    }
    /**Navigates app to the previous activity */
    public void back()
    {
        Intent backToAdjust = new Intent(getApplicationContext(), AdjustmentActivity.class);
        backToAdjust.setAction(Util.AFTER_EDIT);
        startActivity(backToAdjust);
    }

    /** Method creates association between seekbar and its paramaters. That's how we setup the kind of processing operation like brightness, contrast and etc.*/
    public void setSeekBar(SeekBar seekBar, SeekbarParams params){
        final SeekbarParams finalParams = params;

        seekBar.setMax(params.getMaxSeekBarValue());
        seekBar.setProgress(finalParams.getDefaultValue());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

         int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //progressChanged = finalParams.getCurrentValue(progress);
                Context context = getApplicationContext();
                progressChanged = progress;


                int leftProgress =  mLeftBar.getProgress();
                int rightProgress =  mRightBar.getProgress();

                //Apply changes using left slider values
               Bitmap temp =  leftSliderParams.progressChanged(leftProgress, mBmpImage);
               Bitmap newBitmap = rightSliderParams.progressChanged(rightProgress, temp);

                mImageView.setImageBitmap(newBitmap);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("TAG","l: start ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(progressChanged);
                Log.i("TAG","l: stop ");
            }
        });
    }


}
