package com.t2ksports.wwe2k16cs.Tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.widget.TextView;

import com.t2ksports.wwe2k16cs.MainActivity;
import com.t2ksports.wwe2k16cs.R;

import java.util.concurrent.TimeUnit;

public class LegalActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        Intent intent = getIntent();
        String text = intent.getStringExtra("text");
        if(text != null ){
            TextView tv = (TextView)findViewById(R.id.tutorialTextView);
            tv.setText(text);
        }



        (new Thread(new Runnable()
        {
            @Override
            public void run ()
            {
                int waitingTime = 8000;
                try {
                    int waited = 0;
                    while (waited < waitingTime) {
                        TimeUnit.MILLISECONDS.sleep(100);
                        waited += 100;
                    }
                }
                catch (InterruptedException e) {
                    // do nothing
                }
                finally {

                    Intent i = new Intent(LegalActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        })).start();

    }



}
