package com.t2ksports.wwe2k16cs.erase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.mask.MaskActivity;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.Util;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * PaintActivity.java
 *
 * Class <code>PaintActivity</code> handles the interaction with the erase module. Enables zoom
 * and erasing with transparent color on a <code>WScratchView</code>. Instantiates buttons to enable
 * the zoom and to clear the changes on the image.
 */
public class PaintActivity extends Activity {

    private final String TAG = "PaintActivity";
    private Button btnClear;
    private WScratchView scratchView;
    private boolean isZoomMode = false;

    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1888;

    private final State state = State.getInstance();


    /**
     * Shows the tutorial for this module.
     */
    private void showTutorial() {

        String text = "";
        if (state.appMode.equals(State.AppMode.KLOGO)) {
            text += "\n\u2022" + " " + getString(R.string.mask_eraser_tool_tip_01) + "\n";
            text += "\n\u2022" + " " + getString(R.string.mask_eraser_tool_tip_02) + "\n";
            text += "\n\u2022" + " " + getString(R.string.mask_eraser_tool_tip_03);
            text += "\n\u2022" + " " + getString(R.string.mask_eraser_tool_tip_04);
        } else {
            text += "\n\u2022" + " " + getString(R.string.face_eraser_tool_tip_01);
        }


        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text", text);
        startActivity(i);
    }

    /**
     * Retrieves the current <code>Bitmap</code> from the singleton <code>State</code>.
     * Instantiates a <code>WScratchView</code> and keeps track of <code>isZoomMode</code>. If zoom
     * is enabled, changes <code>onDraw</code> method to represent the zoomed image.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            setContentView(R.layout.paint_main);

            if(state.bitmap == null){
                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.image3);

                state.bitmap = icon;

            }


            Intent pending = getIntent();
            scratchView = (WScratchView) findViewById(R.id.scratchView);
            ImageView backgroundView = (ImageView) findViewById(R.id.backgroundView);
            scratchView.setDrawingCacheEnabled(true);
            scratchView.measure(backgroundView.getWidth(), backgroundView.getHeight());

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


            if (pending != null && state.bitmap != null) {
                //load result image in view
                scratchView.setScratchBitmap(state.bitmap);
                android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                scratchView.setLayoutParams(params);
                //add touch view

                Log.d(TAG, "Bitmap size is, W: " + state.bitmap.getWidth() + " H: " + state.bitmap.getHeight());
                //scratchView.getHolder().setFixedSize(state.bitmap.getWidth(), state.bitmap.getHeight());
            }

            final Button mBtnZoom = (Button) findViewById(R.id.btnZoom);
            mBtnZoom.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!isZoomMode) {
                        isZoomMode = true;
                        mBtnZoom.setBackgroundResource(R.drawable.pan2x);

                    } else {
                        isZoomMode = false;
                        mBtnZoom.setBackgroundResource(R.drawable.pandisabled2x);
                    }

                    scratchView.setZoomMode(isZoomMode);
                }
            });

            Button mBtnBack = (Button) findViewById(R.id.btnBack);
            mBtnBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    scratchView.eraseLastDraw();
                }
            });

            Button mBtnCancel = (Button) findViewById(R.id.btnCancel);
            mBtnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToMask = new Intent(getApplicationContext(), MaskActivity.class);
                    startActivity(goToMask);
                }
            });

            Button mBtnDone = (Button) findViewById(R.id.btnDone);
            mBtnDone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap bmp = scratchView.getResultingImage();
                    //Bitmap bmp = scratchView.get();

                    //Util.saveImage(bmp,"RESULT");
                    state.bitmap = bmp;

                    back();
                }
            });

            btnClear = (Button) findViewById(R.id.btnClear);
            btnClear.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        btnClear.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        scratchView.resetView();
                        scratchView.setScratchAll(false);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        btnClear.setTextColor(getResources().getColor(android.R.color.white));
                    }

                    return true;
                }
            });


            SeekBar brushSizeBar = (SeekBar) findViewById(R.id.seekStrokeSIze);
            brushSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //change stroke size
                    scratchView.setRevealSize(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }

        ImageView mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
    }


    /**
     * Returns to <code>MaskActivity</code>
     */
    public void back() {
        Intent backToMask = new Intent(getApplicationContext(), MaskActivity.class);
        backToMask.setAction(Util.AFTER_EDIT);
        startActivity(backToMask);
    }

}
