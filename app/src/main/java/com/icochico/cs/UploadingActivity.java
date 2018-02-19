package com.t2ksports.wwe2k16cs;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.t2ksports.wwe2k16cs.API.JMCVolleyRequest;
import com.t2ksports.wwe2k16cs.API.WebAPIInterface;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.Util;


public class UploadingActivity extends Activity implements WebAPIInterface
{   private Button mHomeButton;
    private Button mCancelButton;
    private Context mContext = this;
    private TextView mMessageText;
    private JMCVolleyRequest request = new JMCVolleyRequest();
    private ImageView mImageForeground;
    public String TAG = "Uploading Activity";
    private ImageView mLogo;
    private final State state = State.getInstance();
    public void errorResponse(String message){

        mHomeButton.setEnabled(true);
        mMessageText.setText(R.string.upload_error_message);

    }
    public void successfulResponse(String message){
        Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT)
                .show();
        mMessageText.setText("");

        //Check if the operation was successful
        if(JMCVolleyRequest.APIKeys.kAPISuccessfuleResponse.toString().equals(message)){
            mMessageText.setText(R.string.upload_successful_message);
        }
        else{


        }

        mHomeButton.setEnabled(true);

    }
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading);

        //this will stretch images to fit screen
        mImageForeground = (ImageView) findViewById(R.id.imgForeground);
        mImageForeground.setScaleType(ImageView.ScaleType.FIT_XY);

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));

        mMessageText = (TextView) findViewById(R.id.messageTextView);
        mHomeButton = (Button)findViewById( R.id.btnHome);
        mCancelButton = (Button)findViewById( R.id.btnCancel);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                request.cancel();
                finish();
            }
        });

        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.cancel();
                Intent i = new Intent(mContext, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);

            }
        });



        mHomeButton.setEnabled(false);
        mMessageText.setText(R.string.upload_during_message);

        Bitmap bitmap = state.bitmap;
        Bitmap resized = Util.cropToLogo(bitmap, state.appMode, state.imageType);
        if (resized != null){
            request.sendFile(state.bitmap, state.token, state.imageType.getValue()+"", mContext, (UploadingActivity)mContext);
        }
        else {
            Log.i(TAG, "ERRROR ");
            mMessageText.setText(R.string.upload_error_message);

        }

    }

}
