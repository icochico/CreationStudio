package com.t2ksports.wwe2k16cs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.t2ksports.wwe2k16cs.Tutorial.TutorialActivity;
import com.t2ksports.wwe2k16cs.adjust.AdjustmentActivity;
import com.t2ksports.wwe2k16cs.qrcode.QRCodeScanner;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;

public class UploadActivity extends Activity {

    private Button mUploadButton;
    private Button mCancelButton;
    private Context mContext = this;
    private EditText mTokenText;
    private ImageView mImageForeground;

    final State state = State.getInstance();

    private void showTutorial(){
        String text = "";
        text += "\n\u2022"+" "+ getString(R.string.upload_qrcode_advisory);
        text += "\n\u2022"+" "+ getString(R.string.upload_token_advisory);

        Intent i = new Intent(this, TutorialActivity.class);
        i.putExtra("text",text);
        startActivity(i);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        final ImageView qr = (ImageView) findViewById(R.id.qrcode);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, QRCodeScanner.class);
                startActivity(i);

            }
        });

        Button infoButton = (Button) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();

            }
        });
        if(State.showTutorialsForActivity(this)){
            showTutorial();
        }

        ImageView mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));

        mUploadButton = (Button) findViewById(R.id.btnUpload);
        mTokenText = (EditText) findViewById(R.id.editTxtToken);


        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTokenText.getText().toString().length() > 0) {

                    String token = mTokenText.getText().toString();

                    state.token = token;

                    Intent i = new Intent(mContext, UploadingActivity.class);
                    startActivity(i);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "PLEASE ENTER THE TOKEN", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        mCancelButton = (Button) findViewById(R.id.btnCancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadOrSave = new Intent(getApplicationContext(), AdjustmentActivity.class);
                startActivity(uploadOrSave);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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
    protected void onResume() {
        super.onResume();
        if (state.token != null) {
            mTokenText.setText(state.token);
        }

    }
}
