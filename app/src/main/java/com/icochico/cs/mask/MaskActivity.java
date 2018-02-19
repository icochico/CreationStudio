package com.t2ksports.wwe2k16cs.mask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.adjust.AdjustmentActivity;
import com.t2ksports.wwe2k16cs.color.ColorActivity;
import com.t2ksports.wwe2k16cs.feather.FeatherActivity;
import com.t2ksports.wwe2k16cs.erase.PaintActivity;
import com.t2ksports.wwe2k16cs.shape.ShapeActivity;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;


/**
 * MaskActivity.java
 *
 * Class <code>MaskActivity</code> is a container for four editing areas: eraser, color, edges and
 * and shape.
 *
 */
public class MaskActivity extends Activity {

    private final String TAG = "MaskActivity";

    private Button mBtnBack;
    private Button mBtnEraser;
    private Button mBtnColor;
    private Button mBtnEdges;
    private Button mBtnShape;

    private ImageView mMaskResultView;
    private ImageView mLogo;

    private final State state = State.getInstance();

    /**
     * Instantiates the UI.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mask_main);
        Intent pending = getIntent();

        if (pending != null && state.bitmap != null) {
            //load result image in view
            mMaskResultView = (ImageView) findViewById(R.id.maskResultView);
            mMaskResultView.setImageDrawable(new BitmapDrawable(getResources(), state.bitmap.copy(Bitmap.Config.ARGB_8888, true)));
            Log.d(TAG, "Bitmap size is, W: " + state.bitmap.getWidth() + " H: " + state.bitmap.getHeight());
        }

        mBtnShape = (Button) findViewById(R.id.btnShape);
        mBtnShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToShape = new Intent(getApplicationContext(), ShapeActivity.class);
                startActivity(goToShape);
            }
        });

        //hide shape if KFace
        if (state != null && state.appMode != null && state.appMode.equals(State.AppMode.KFACE)) {
            mBtnShape.setVisibility(View.GONE);

        }

        mBtnBack = (Button) findViewById(R.id.btnBack);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToAdjust = new Intent(getApplicationContext(), AdjustmentActivity.class);
                startActivity(backToAdjust);
            }
        });

        mBtnEraser = (Button) findViewById(R.id.btnEraser);
        mBtnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToEraser = new Intent(getApplicationContext(), PaintActivity.class);
                startActivity(goToEraser);
            }
        });

        mBtnColor = (Button) findViewById(R.id.btnColor);
        mBtnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToColor = new Intent(getApplicationContext(), ColorActivity.class);
                startActivity(goToColor);
            }
        });

        mBtnEdges = (Button) findViewById(R.id.btnEdges);
        mBtnEdges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFeather = new Intent(getApplicationContext(), FeatherActivity.class);
                startActivity(goToFeather);
            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
    }
}
