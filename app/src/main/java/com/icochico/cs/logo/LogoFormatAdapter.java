package com.t2ksports.wwe2k16cs.logo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.util.TwoKImage;

/**
 * LogoFormatAdapter.java
 *
 */
public class LogoFormatAdapter extends ArrayAdapter<TwoKImage> {

    Context context;
    int layoutResourceId;
    TwoKImage data[] = null;

    public LogoFormatAdapter(Context context, int layoutResourceId, TwoKImage[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LogoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new  LogoHolder();
         //   holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtName = (TextView)row.findViewById(R.id.txtName);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtSubtitle = (TextView)row.findViewById(R.id.txtSubtitle);

           row.setTag(holder);
        }
        else
        {
            holder = (LogoHolder)row.getTag();
        }

        TwoKImage logo = data[position];
        holder.txtName.setText(logo.name);
        holder.txtTitle.setText(logo.title);
        holder.txtSubtitle.setText(logo.subtitle);
       // holder.imgIcon.setImageResource(logo.icon);

        return row;
    }

    static class LogoHolder
    {
        TextView txtName;
        TextView txtTitle;
        TextView txtSubtitle;
    }
}