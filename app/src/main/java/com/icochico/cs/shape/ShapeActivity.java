package com.t2ksports.wwe2k16cs.shape;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.OctogonImageView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.siyamed.shapeimageview.StarImageView;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.mask.MaskActivity;
import com.t2ksports.wwe2k16cs.util.BitmapHelper;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;

/**Activity designed to cut out different shapes masks */
public class ShapeActivity extends Activity {

    private static final String TAG = "ColorActivity";

    private ImageView mBackgroundView;
    private RoundedImageView mRoundedImageView;
    private CircularImageView mCircularImageView;
    private OctogonImageView mOctogonImageView;
    private StarImageView mStarImageView;

    private Bitmap mBitmap;
    private Button mBtnDone;
    private Button mBtnCancel;
    private ImageView mLogo;


    private Button mBtnOval;
    private Button mBtnRound;
    private Button mBtnPolygon;
    private Button mBtnStar;

    private final State state = State.getInstance();

    private enum Shape {
        Rounded,
        Circular,
        Star,
        Octagon
    }

    private Shape mShape = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shape_main);

        Intent pending = getIntent();

        if (pending != null && state.bitmap != null) {
            //load result image in view
            mBitmap = state.bitmap;

            mBackgroundView = (ImageView) findViewById(R.id.backgroundView);
            mBackgroundView.setImageBitmap(mBitmap);
        }

//        mShapeView = (ImageView) findViewById(R.id.shapeView);
//        mShapeView.setVisibility(View.INVISIBLE);

        //pre load bitmap
        mRoundedImageView = (RoundedImageView) findViewById(R.id.roundedView);
        mCircularImageView = (CircularImageView) findViewById(R.id.circularView);
        mStarImageView = (StarImageView) findViewById(R.id.starView);
        mOctogonImageView = (OctogonImageView) findViewById(R.id.octogonView);

        hideAll();


        mBtnStar = (Button) findViewById(R.id.btnStar);
        mBtnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStarImageView.setImageBitmap(state.bitmap);
                mBackgroundView.setVisibility(View.INVISIBLE);
                adjustParams(mStarImageView, findViewById(R.id.imageLayout).getHeight());
                hideAll();
                mStarImageView.setVisibility(View.VISIBLE);
                mShape = Shape.Star;
            }
        });

        mBtnOval = (Button) findViewById(R.id.btnOval);
        mBtnOval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCircularImageView.setImageBitmap(state.bitmap);
                mBackgroundView.setVisibility(View.INVISIBLE);
                adjustParams(mCircularImageView, findViewById(R.id.imageLayout).getHeight());
                hideAll();

                mCircularImageView.setVisibility(View.VISIBLE);
                mShape = Shape.Circular;
            }
        });

        mBtnRound = (Button) findViewById(R.id.btnRound);
        mBtnRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoundedImageView.setImageBitmap(state.bitmap);
                mBackgroundView.setVisibility(View.INVISIBLE);
                adjustParams(mRoundedImageView, findViewById(R.id.imageLayout).getHeight());
                hideAll();
                mRoundedImageView.setVisibility(View.VISIBLE);
                mShape = Shape.Rounded;
            }
        });

        mBtnPolygon = (Button) findViewById(R.id.btnPolygon);
        mBtnPolygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOctogonImageView.setImageBitmap(state.bitmap);
                mBackgroundView.setVisibility(View.INVISIBLE);
                adjustParams(mOctogonImageView, findViewById(R.id.imageLayout).getHeight());
                hideAll();
                mOctogonImageView.setVisibility(View.VISIBLE);

                mShape = Shape.Octagon;
            }
        });

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

                if (mShape == null) {
                    back();
                    // we need to return otherwise program will crash on the switch statement.
                    return;
                }

                switch (mShape) {
                    case Star:
                        state.bitmap = BitmapHelper.extractBitmap(mStarImageView);
                        break;
                    case Octagon:
                        state.bitmap = BitmapHelper.extractBitmap(mOctogonImageView);
                        break;
                    case Rounded:
                        state.bitmap = BitmapHelper.extractBitmap(mRoundedImageView);
                        break;
                    case Circular:
                        state.bitmap = BitmapHelper.extractBitmap(mCircularImageView);
                        break;
                }

                back();
            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));
    }

    private void adjustParams(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        //get main layout
        params.height = height;
        view.requestLayout();

    }

    public void hideAll() {
        mRoundedImageView.setVisibility(View.GONE);
        mCircularImageView.setVisibility(View.GONE);
        mStarImageView.setVisibility(View.GONE);
        mOctogonImageView.setVisibility(View.GONE);
    }

    public void back() {
        Intent backToMask = new Intent(getApplicationContext(), MaskActivity.class);
        backToMask.setAction(com.t2ksports.wwe2k16cs.util.Util.AFTER_EDIT);
        startActivity(backToMask);

    }

}
