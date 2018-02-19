package com.t2ksports.wwe2k16cs.logo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.t2ksports.wwe2k16cs.MainActivity;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.camera.CameraActivity;
import com.t2ksports.wwe2k16cs.util.MainMenuListener;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.TwoKImage;
import com.t2ksports.wwe2k16cs.util.Util;

public class LogoFormatsActivity extends Activity {

    private ListView listLogoFormats;
    private Button mBtnCancel;
    private final State state = State.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_formats_main);
        listLogoFormats = (ListView) findViewById(R.id.list);
        //We are interested only in logos, not face
        TwoKImage[] formats = Util.getLogoFormats(State.AppMode.KLOGO,null);
        LogoFormatAdapter adapter = new LogoFormatAdapter(this, R.layout.pick_logo_list_row, formats);

        if (state.appMode != null){
            Log.i("STATE", state.appMode.toString());
        }

        if (state.imageType != null){
            Log.i("STATE", state.imageType.toString());
        }

        ImageView mLogo = (ImageView) findViewById(R.id.imgLogo);
        mLogo.setOnClickListener(new MainMenuListener(getApplicationContext()));

        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backToMainIntent);
            }
        });

        listLogoFormats.setAdapter(adapter);
        listLogoFormats.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                TwoKImage item = (TwoKImage) listLogoFormats.getItemAtPosition(position);
                state.imageType = item.type;

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Format: " + item.name, Toast.LENGTH_SHORT)
                        .show();

                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(cameraIntent);
            }

        });
    }
}
