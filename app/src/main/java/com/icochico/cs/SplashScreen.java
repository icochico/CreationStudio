package com.t2ksports.wwe2k16cs;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.t2ksports.wwe2k16cs.Tutorial.LegalActivity;

import io.fabric.sdk.android.Fabric;
import java.util.concurrent.TimeUnit;

/**
 * SplashScreen.java
 * <p/>
 * Class <code>SplashScreen</code> gets called when the apps get launched.
 */
public class SplashScreen extends Activity
{
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash);

        // Now get a handle to any View contained
        // within the main layout you are using
        //
        loadBackground();

        (new Thread(new Runnable()
        {
            @Override
            public void run ()
            {
                int waitingTime = 800;
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

                    Intent i = new Intent(SplashScreen.this, LegalActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        })).start();
    }

    public void loadBackground ()
    {
        ImageView background = (ImageView) findViewById(R.id.imgBackground);
        background.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
