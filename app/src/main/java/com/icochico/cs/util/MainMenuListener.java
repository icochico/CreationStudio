package com.t2ksports.wwe2k16cs.util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.t2ksports.wwe2k16cs.MainActivity;
import com.t2ksports.wwe2k16cs.R;

/**
 * MainMenuListener.java
 */
public class MainMenuListener implements View.OnClickListener {

    private final Context mContext;

    public MainMenuListener(Context context)
    {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle item selection
                switch (item.getItemId()) {
                    case R.id.return_home:
                        Intent i = new Intent(mContext, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(i);
                        return true;
                    case R.id.cancel:
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
    }
}
