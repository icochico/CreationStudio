package com.t2ksports.wwe2k16cs.Tutorial;

import android.app.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.t2ksports.wwe2k16cs.R;
/**Class used for displaying tutorials for some of the screens and also legal notices*/
public class TutorialActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Button doneButton = (Button)findViewById(R.id.btnDone);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Dimissing the tutorial view


            }
        });

        Intent intent = getIntent();
        //Checking for header
        String header = intent.getStringExtra("header");
        if(header != null ){
            TextView tv = (TextView)findViewById(R.id.headerLabel);

            tv.setText(header);

        }

        //Checking for content
        String text = intent.getStringExtra("text");
        if(text != null ){
            TextView tv = (TextView)findViewById(R.id.tutorialTextView);
            SpannableString ss =  processText(text);
            tv.setText(ss);
        }

    }


//TODO Refactor and automate process of replacing the strings
    private SpannableString processText(String text)
    {

        int arrowIndex = -1;
        int handIndex = -1;
        int logoIndex = -1;
        String lowercase = text.toLowerCase();
       // String result = lowercase;
        if (lowercase.indexOf("u_arrow") >= 0){
            arrowIndex = lowercase.indexOf("u_arrow");
            lowercase = lowercase.replace("u_arrow"," ");
        }

        if (lowercase.indexOf("u_hand_icon") >= 0){
            handIndex =  lowercase.indexOf("u_hand_icon");
            lowercase = lowercase.replace("u_hand_icon"," ");
        }

        if (lowercase.indexOf("u_wwe2klogo") >= 0){
            logoIndex =  lowercase.indexOf("u_wwe2klogo");
            lowercase = lowercase.replace("u_wwe2klogo"," ");
        }

        //If we didn't find any logos lets just return
        if(logoIndex==-1&&handIndex==-1&&logoIndex==-1)
        {
            return new SpannableString(text);
        }
        SpannableString ss = new SpannableString(lowercase.toUpperCase());
        if(logoIndex != -1){
            Drawable d = getResources().getDrawable(R.drawable.w2klogo);
            d.setBounds(0, 0, 200, 81);
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, logoIndex, logoIndex+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        }


        if(arrowIndex != -1){
            Drawable d = getResources().getDrawable(R.drawable.undo);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, arrowIndex, arrowIndex+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        }

        if(handIndex != -1){
            Drawable d = getResources().getDrawable(R.drawable.pan2x);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, handIndex, handIndex + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }


        return ss;
    }



}
