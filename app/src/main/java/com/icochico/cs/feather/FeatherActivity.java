package com.t2ksports.wwe2k16cs.feather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.mask.MaskActivity;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;

/**
 * Class <code>FeatherActivity</code> handles the feathering effect. It instantiates a
 * <code>SurfaceView</code> that create the feathering on a background thread with
 * an <code>AsyncTask</code>.
 */
public class FeatherActivity extends Activity {

    private FeatherSurfaceView mFeatherSurfaceView;
    private ImageView backgroundView;

    private Bitmap mBitmap;
    private Bitmap mOriginalBitmap;
    private Button mBtnDone;
    private Button mBtnCancel;
    private ImageView mLogo;
    private SeekBar mSeekStrength;
    private ProgressBar mProcessing;


    private final State state = State.getInstance();

    /**
     * Shows the tutorial for this module.
     */
    private void showTutorial() {

        String text = "";
        text += "\n\u2022" + " " + getString(R.string.mask_edges_tip_01);
        text += "\n\u2022" + " " + getString(R.string.mask_edges_tip_02);

        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text", text);
        startActivity(i);
    }

    /**
     * Retrieves the current <code>Bitmap</code> from the singleton <code>State</code>.
     * Instantiates the <code>SurfaceView</code> and sets the default strength and progress
     * on the <code>SeekBar/code>.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feather_main);
        Intent pending = getIntent();

        backgroundView = (ImageView) findViewById(R.id.backgroundView);
        mProcessing = (ProgressBar) findViewById(R.id.processing);
        mProcessing.setVisibility(View.GONE);

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
            mBitmap = state.bitmap;
            mOriginalBitmap = state.bitmap.copy(Bitmap.Config.ARGB_8888, true);

            mBitmap = mBitmap.isMutable()
                    ? mBitmap
                    : mBitmap.copy(Bitmap.Config.ARGB_8888, true);


            mFeatherSurfaceView = (FeatherSurfaceView) findViewById(R.id.featherResultView);
            mFeatherSurfaceView.measure(backgroundView.getWidth(), backgroundView.getHeight());

            android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mFeatherSurfaceView.setLayoutParams(params);
            mFeatherSurfaceView.setDrawFeatheredImage(false);

            int width = backgroundView.getWidth();
            int height = width * state.bitmap.getHeight() / state.bitmap.getWidth();
            mFeatherSurfaceView.init(mBitmap, mProcessing, width, height);
            //mFeatheredImageSurfaceView.drawImage();

            mSeekStrength = (SeekBar) findViewById(R.id.seekStrength);
            mSeekStrength.setProgress(0);
            mSeekStrength.setMax(10);
        }

        mSeekStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                if (progress == 0) {
                    mFeatherSurfaceView.setDrawFeatheredImage(false);
                    mFeatherSurfaceView.drawImage();
                } else {
                    mProcessing.setVisibility(View.VISIBLE);
                    mFeatherSurfaceView.featherImage(progress);
                }
            }
        });

        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if it was processing
                if (mFeatherSurfaceView.isProcessing()) {
                    mFeatherSurfaceView.cancelTask();
                }

                Intent goToMask = new Intent(getApplicationContext(), MaskActivity.class);
                startActivity(goToMask);
            }
        });

        mBtnDone = (Button) findViewById(R.id.btnDone);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFeatherSurfaceView.isProcessing()) {
                    //processing not finished, restore original bitmap
                    mFeatherSurfaceView.cancelTask();
                    state.bitmap = mOriginalBitmap;
                } else {
                    //processing is finished, assign new bitmap
                    state.bitmap = mBitmap;
                }

                back();
            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
    }

    /**
     * Returns to <code>MaskActivity</code>
     */
    public void back() {
        Intent backToMask = new Intent(getApplicationContext(), MaskActivity.class);
        backToMask.setAction(com.t2ksports.wwe2k16cs.util.Util.AFTER_EDIT);
        startActivity(backToMask);
    }
}
