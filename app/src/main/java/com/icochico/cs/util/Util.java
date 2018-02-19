package com.t2ksports.wwe2k16cs.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Util.java
 */
public class Util {

    public final static String AFTER_EDIT = "com.izotx.wwe2k16cs.util.AFTER_EDIT";
    public final static String PARENT_ACTIVITY = "com.izotx.wwe2k16cs.util.PARENT_ACTIVITY";
    public final static String ACTION_FRONT_CAMERA = "com.izotx.wwe2k16cs.util.ACTION_FRONT_CAMERA";
    public final static String ACTION_BACK_CAMERA = "com.izotx.wwe2k16cs.util.ACTION_BACK_CAMERA";

    public static String TAG = "UTIL";

//prepare possible formats
/*
    @objc static    var logoFormats:[NSDictionary]{
        let logoAdict = ["apiKey":"0", "resolution": ["height":512, "width":512], "short":"512x512", "title": ["CREATE A SUPERSTAR"], "celltext":["ALL PARTS"],
        "description":"Can be used in the following modes:\n Create a Superstar (all parts)\nCreate an Arena: (all parts)\nCreate a Show (all parts)\nCreate a Championship (all parts)"]

        let logoBdict = ["apiKey":"1", "resolution": ["height":256, "width":256],"short":"256x256", "description":"Can be used in the following modes:\n Create a Superstar (all parts)","title": ["CREATE A SUPERSTAR"], "celltext":["ALL PARTS"] ]

        let logoCdict = ["apiKey":"2", "resolution": ["height":128, "width":128],"short":"128x128", "description":"Can be used in the following modes:\n Create a Superstar (all parts)","title": ["CREATE A SUPERSTAR/CREATE AN AREA, CREATE A SHOW/ CREATE A CHAMPIONSHIP"], "celltext":["ALL PARTS"]]

        let logoDdict = ["apiKey":"3", "resolution": ["height":1024, "width":1024],"short":"1024x1024", "description":"Can be used in the following modes:\n Create an Arena((stage, stage object, ramp, mat, steel steps)", "title": ["CREATE AN ARENA"], "celltext":["STAGE, STAGE OBJECT, RAMP, MAT, STEEL STEPS "]]

        let logoEdict = ["apiKey":"4", "resolution": ["height":512, "width":1024],"short":"1024x512", "description":"Can be used in the following modes:\n Create an Arena (mini-tron, header, wall, stage panel, ring post, barricade, ringside mat)\n Create a Show (brand logo, locator logo, replay logo) \nCreate a Championship (strap)", "title": ["CREATE AN ARENA", "CREATE A SHOW", "CREATE A CHAMPIONSHIP"], "celltext":["MINI-TRON, HEADER, WALL, STAGE, PANEL, RING POST, BARRICADE, RINGSIDE MAT", "BRAND LOGO, LOCATOR LOGO, REPLAY LOGO", "STRAP"]]

        let logoFdict = ["apiKey":"5", "resolution": ["height":256, "width":1024],"short":"1024x256", "description":"Can be used in the following modes:\n Create an Arena (apron, announce table)","title": ["CREATE AN ARENA"], "celltext":["APRON/ANNOUNCE TABLE"] ]

        let logoGdict = ["apiKey":"6", "resolution": ["height":128, "width":1024],"short":"1024x128", "description":"Can be used in the following modes:\n Create an Arena Create an Arena (electronic billboard)","title": ["CREATE AN ARENA"], "celltext":["ELECTRONIC BILLBOARD"]]


        return [logoAdict, logoBdict, logoCdict, logoDdict, logoEdict, logoFdict, logoGdict]
    }
  */


    public static Bitmap cropToLogo(Bitmap bmp, State.AppMode kind, State.ImageType type ){
        TwoKImage[] formats = Util.getLogoFormats(kind ,type);
        Bitmap resized=null;
        if (formats.length == 1){
            TwoKImage format = formats[0];
            resized =    Bitmap.createScaledBitmap(bmp, format.width, format.height,false);
        }
        else{
            Log.i(TAG, "serious error, something is wrong with app's logic");

        }
        return resized;
    }


    public static TwoKImage[] getLogoFormats(State.AppMode kind, State.ImageType type){

        TwoKImage logo0 = new TwoKImage();
        logo0.width = 512;
        logo0.height = 512;
        logo0.name = "512x512";
        logo0.title = "CREATE A SUPERSTAR";
        logo0.subtitle = "ALL PARTS";
        logo0.type = State.ImageType.LOGOA;
        logo0.kind = State.AppMode.KLOGO;

        TwoKImage logo1 = new TwoKImage();
        logo1.width = 256;
        logo1.height = 256;
        logo1.name = "256x256";
        logo1.title = "CREATE A SUPERSTAR";
        logo1.subtitle = "ALL PARTS";
        logo1.type = State.ImageType.LOGOB;
        logo1.kind = State.AppMode.KLOGO;

        TwoKImage logo2 = new TwoKImage();
        logo2.width = 128;
        logo2.height = 128;
        logo2.name = "128x128";
        logo2.title = "CREATE A SUPERSTAR/CREATE AN ARENA, CREATE A SHOW/CREATE A CHAMPIONSHIP";
        logo2.subtitle = "ALL PARTS";
        logo2.type = State.ImageType.LOGOC;
        logo2.kind = State.AppMode.KLOGO;

        TwoKImage logo3 = new TwoKImage();
        logo3.width = 1024;
        logo3.height = 1024;
        logo3.name = "1024x1024";
        logo3.title = "CREATE AN ARENA";
        logo3.subtitle = "STAGE, STAGE OBJECT, RAMP, MAT, STEEL STEPS";
        logo3.type = State.ImageType.LOGOD;
        logo3.kind = State.AppMode.KLOGO;

        TwoKImage logo4 = new TwoKImage();
        logo4.width = 1024;
        logo4.height = 512;
        logo4.name = "1024x512";
        logo4.title = "CREATE AN ARENA";

        logo4.subtitle = "MINI-TRON, HEADER, WALL, STAGE, PANEL, RING POST, BARRICADE, RINGSIDE MAT "+"\n CREATE A SHOW\n BRAND LOGO, LOCATOR LOGO, REPLAY LOGO\n CREATE A CHAMPIONSHIP \nSTRAP\n ";
       //Can be used in the following modes:\n Create an Arena (mini-tron, header, wall, stage panel, ring post, barricade, ringside mat)\n Create a Show (brand logo, locator logo, replay logo) \nCreate a Championship (strap)", "title": ["CREATE AN ARENA", , ""], "celltext":["MINI-TRON, HEADER, WALL, STAGE, PANEL, RING POST, BARRICADE, RINGSIDE MAT", ", ""]
        logo4.type = State.ImageType.LOGOE;
        logo4.kind = State.AppMode.KLOGO;

        TwoKImage logo5 = new TwoKImage();
        logo5.width = 1024;
        logo5.height = 256;
        logo5.name = "1024x256";
        logo5.title = "CREATE AN ARENA";
        logo5.subtitle = "APRON/ANNOUNCE TABLE";
        logo5.type = State.ImageType.LOGOF;
        logo5.kind = State.AppMode.KLOGO;

        TwoKImage logo6 = new TwoKImage();
        logo6.width = 1024;
        logo6.height = 128;
        logo6.name = "1024x128";
        logo6.title = "CREATE AN ARENA";
        logo6.subtitle = "ELECTRONIC BILLBOARD";
        logo6.type = State.ImageType.LOGOG;
        logo6.kind = State.AppMode.KLOGO;

        TwoKImage face = new TwoKImage();
        face.width = 512;
        face.height = 512;
        face.name = "512x512";
        face.type = State.ImageType.FACE;
        face.kind = State.AppMode.KFACE;

        //TODO temporary commented out to figure out why width not represented correctly
        TwoKImage[] tempArray = {logo0,logo1,logo2,logo3,logo4,logo5,logo6,face};
        //TwoKImage[] array = {logo3,logo4,logo5,logo6};
     //   TwoKImage[] resultArray = new TwoKImage[tempArray.length];
       ArrayList<TwoKImage> mutableList = new ArrayList<>();

        for (TwoKImage format : tempArray) {

            if (type != null){
                if (kind.equals(format.kind)&&type.equals(format.type))
                {
                    mutableList.add(format);
                    Log.i("UTIL", "Added Format with type and : "+format.toString());
                }

            }else{
                if (kind.equals(format.kind)){
                    Log.i("UTIL", "Added Format with kind only: "+format.toString());
                    mutableList.add(format);

                }
            }

        }

        return mutableList.toArray(new TwoKImage[mutableList.size()]);
    }

    public static File saveImage(Bitmap bmp, String file) {

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/wwe2k16");
        dir.mkdirs();

        File f = null;
        Date date = new Date();


        f = new File(dir, file+ date.toString() + ".png");
        Log.i("UTIL", "SAVING"+f.toString());
        try {
            FileOutputStream strm = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 80, strm);
            strm.close();

           // Toast.makeText(getApplicationContext(), "IMAGE SUCCESSFULLY SAVED.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }


    public static Bitmap getImage(Context context, Uri image) {
        InputStream imageStream = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {

            //OLD METHOD
            //imageStream = context.getContentResolver().openInputStream(image);
            //bitmap = BitmapFactory.decodeStream(imageStream);


            bitmap = BitmapFactory.decodeFile(image.getPath(), options);
            if (bitmap == null) throw new FileNotFoundException("Image not found");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}
