package com.t2ksports.wwe2k16cs;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.camera.CameraActivity;

import com.t2ksports.wwe2k16cs.logo.LogoFormatsActivity;
import com.t2ksports.wwe2k16cs.util.State;
/** Home screen of the application */
public class MainActivity extends Activity
{

    private Button mBtnFacePhotoCapture;
    private Button mBtnLogoCapture;

    private ImageView mImageBackground;
    private ImageView mImageForeground;
    private String TAG = "HOME ACTIVITY";
    private final State state = State.getInstance();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mImageBackground = (ImageView) findViewById(R.id.imgBackground);
        mImageForeground = (ImageView) findViewById(R.id.imgForeground);

        //this will stretch images to fit screen
        mImageBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageForeground.setScaleType(ImageView.ScaleType.FIT_XY);

        //setup buttons
        mBtnFacePhotoCapture = (Button) findViewById(R.id.btnFaceCapture);
        mBtnFacePhotoCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //state activity
                //Quite important. A lot of logic in later part of the application depends on setting up the right values here
                State state = State.getInstance();
                state.appMode = State.AppMode.KFACE;
                state.imageType = State.ImageType.FACE;


                startCameraActivity();

            }
        });

        Button infoButton = (Button) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();

            }
        });


        mBtnLogoCapture = (Button) findViewById(R.id.btnLogoCapture);
        mBtnLogoCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Quite important. A lot of logic in later part of the application depends on setting up the right values here
                State state = State.getInstance();
                state.appMode = State.AppMode.KLOGO;

                startLogoActivity();
            }
        });
    }

    /**Shows the tutorial */
    private void showTutorial(){
        String text = "";
        text += getString(R.string.legal_credits_notices);
        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text",text);
        i.putExtra("header","NOTICES");
        startActivity(i);

    }

    /**Shows the camera screen*/
    public void startCameraActivity()
    {
        //TODO add extra to intent to distinguish between two modes of operation (face and logo)
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        startActivity(cameraIntent);
        //Setting the mode to either face or logo

    }
    /**Shows the list of logos*/
    public void startLogoActivity()
    {
        //Setting the mode to either face or logo
        //TODO add extra to intent to distinguish between two modes of operation (face and logo)
        Intent logoIntent = new Intent(this, LogoFormatsActivity.class);
        startActivity(logoIntent);

    }


}
