package com.t2ksports.wwe2k16cs.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

/**Stores information about state of the application*/
public class State {
    /** Different app modes */
    public enum AppMode{
        KFACE,
        KLOGO
    }
    /** Different image logo formats */
    public enum ImageType{
        LOGOA(0),
        LOGOB(1),
        LOGOC(2),
        LOGOD(3),
        LOGOE(4),
        LOGOF(5),
        LOGOG(6),
        FACE(7);

        private final int value;

        ImageType(final int newValue)
        {
            this.value = newValue;
        }

        public int getValue() { return value; }
    }

    private State(){}
    /**Current app mode*/
    public AppMode appMode;
    /**Current image type */
    public ImageType imageType;
    /**Current processed bitmap*/
    public Bitmap bitmap;
    /**Value of the token entered by user*/
    public String token;
    /**Height of toolbar*/
    public int toolbarHeight;

    private static class SingletonHolder {
        private static final State INSTANCE = new State();
    }
    /**Returns instance of the State as singleton*/
    public static State getInstance() {
        return SingletonHolder.INSTANCE;
    }


   /**
    **keeps track of tutorials
    * @param context context in which tutorial was shown
    * */
   public static boolean showTutorialsForActivity(Context context){

        String name = context.getClass().getSimpleName();
        SharedPreferences preferences = context.getSharedPreferences("WWE2KMyPreferences", Context.MODE_PRIVATE);

        if(preferences.getInt(name, 0)== 0){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(name, 1);
            editor.commit();

            //we want to display the tutorial here
            return  true;
        }

        return false;
    }


}
