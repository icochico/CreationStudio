package com.t2ksports.wwe2k16cs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.Util;
import com.t2ksports.wwe2k16cs.zoomcrop.ParentActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
/**Class designated to either save the processed photo or continue to upload to screen*/
public class UploadOrSaveActivity extends Activity {

    private Uri mImageUri;
    private ImageView mAdjustResultView;
    private Button mSaveButton;
    private Button mUploadButton;
    private Bitmap resultImage;
    private ImageView mLogo;
    State state = State.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_or_save);
        Intent pending = getIntent();

        if (pending != null && state.bitmap != null) {
            //load result image in view

            mAdjustResultView = (ImageView) findViewById(R.id.resultView);
            mAdjustResultView.setImageBitmap(state.bitmap);

        }

        mUploadButton = (Button) findViewById(R.id.btnUpload);

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadOrSave = new Intent(getApplicationContext(), UploadActivity.class);
                uploadOrSave.putExtra(Util.PARENT_ACTIVITY, ParentActivity.AdjustmentActivity);
                uploadOrSave.setData(mImageUri);
                startActivity(uploadOrSave);
            }
        });

        mSaveButton = (Button) findViewById(R.id.btnSave);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();

            }
        });

        mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    public File saveImage() {

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/wwe2k16");
        dir.mkdirs();

        File f = null;

        f = new File(dir, "2kAndroid" + UUID.randomUUID().toString() + ".png");

        try {
            State state = State.getInstance();
            FileOutputStream strm = new FileOutputStream(f);
            state.bitmap.compress(Bitmap.CompressFormat.PNG, 80, strm);
            strm.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), state.bitmap,"WWE Creation Studio", "");


            Toast.makeText(getApplicationContext(), "IMAGE SUCCESSFULLY SAVED.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_or_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "UploadOrSave Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.t2ksports.wwe2k16cs/http/host/path")
        );

    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "UploadOrSave Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.t2ksports.wwe2k16cs/http/host/path")
        );

    }
}
