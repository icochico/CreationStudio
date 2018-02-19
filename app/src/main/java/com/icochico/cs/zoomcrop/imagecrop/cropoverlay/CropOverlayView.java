package com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.cropoverlay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.t2ksports.wwe2k16cs.R;
import com.t2ksports.wwe2k16cs.util.State;
import com.t2ksports.wwe2k16cs.util.TwoKImage;
import com.t2ksports.wwe2k16cs.util.Util;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.cropoverlay.edge.Edge;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.cropoverlay.utils.PaintUtil;
import com.t2ksports.wwe2k16cs.zoomcrop.imagecrop.photoview.PhotoViewAttacher;


/**
 * @author GT
 * Modified/stripped down Code from cropper library : https://github.com/edmodo/cropper
 *
 */
public class CropOverlayView extends View implements PhotoViewAttacher.IGetImageBounds{

    private final String TAG = "CropOverlayView";
    //Defaults
    private boolean DEFAULT_GUIDELINES = true;
    private int DEFAULT_MARGINTOP = 300;
    private int DEFAULT_MARGINSIDE = 200;
    private int DEFAULT_MIN_WIDTH = 256;
    private int DEFAULT_MAX_WIDTH = 1024;

    // we are croping square image so width and height will always be equal
    private int DEFAULT_CROPWIDTH = 600;
    private static final float CORNER_RADIUS = 6;


    // The Paint used to darken the surrounding areas outside the crop area.
    private Paint mBackgroundPaint;

    // The Paint used to draw the white rectangle around the crop area.
    private Paint mBorderPaint;

    // The Paint used to draw the guidelines within the crop area.
    private Paint mGuidelinePaint;

    // The bounding box around the Bitmap that we are cropping.
    private Rect mBitmapRect;

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    private int cropHeight = DEFAULT_CROPWIDTH;
    private int cropWidth = DEFAULT_CROPWIDTH;


    private boolean mGuidelines;
    private int mMarginTop;
    private int mMarginSide;
    private int mMinWidth;
    private int mMaxWidth;
    private Context mContext;

    private final State state = State.getInstance();

    public CropOverlayView(Context context) {
        super(context);
        init(context);
        this.mContext = context;
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropOverlayView, 0, 0);
        try {
            mGuidelines = ta.getBoolean(R.styleable.CropOverlayView_guideLines,DEFAULT_GUIDELINES);
            mMarginTop = ta.getDimensionPixelSize(R.styleable.CropOverlayView_marginTop, DEFAULT_MARGINTOP);
            mMarginSide = ta.getDimensionPixelSize(R.styleable.CropOverlayView_marginSide,DEFAULT_MARGINSIDE);
            mMinWidth = ta.getDimensionPixelSize(R.styleable.CropOverlayView_minWidth,DEFAULT_MIN_WIDTH);
            mMaxWidth = ta.getDimensionPixelSize(R.styleable.CropOverlayView_maxWidth,DEFAULT_MAX_WIDTH);
        } finally {
            ta.recycle();
        }

        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //BUG FIX : Turn of hardware acceleration. Clip path doesn't work with hardware acceleration
        //BUG FIX : Will have to do it here @ View level. Activity level not working on HTC ONE X
        //http://stackoverflow.com/questions/8895677/work-around-canvas-clippath-that-is-not-supported-in-android-any-more/8895894#8895894
        //Disable hardware acceleration
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        final float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, mContext.getResources().getDisplayMetrics());
        RectF rectF = new RectF(Edge.LEFT.getCoordinate(),
                Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(),
                Edge.BOTTOM.getCoordinate());

        Path clipPath = new Path();
        clipPath.addRoundRect(rectF,radius,radius, Path.Direction.CW);
        try {
            canvas.clipPath(clipPath, Region.Op.DIFFERENCE);
        }
        catch (Exception e){
            Crashlytics.getInstance().logException(e);
        }

        canvas.drawARGB(204, 41, 48, 63);
        canvas.restore();
        canvas.drawRoundRect(rectF,radius,radius,mBorderPaint);

        //GT :  Drop shadow not working right now. Commenting the code now
//        //Draw shadow
        drawRuleOfThirdsGuidelines(canvas);
    }

    public Rect getImageBounds(){
        return new Rect((int) Edge.LEFT.getCoordinate(), (int) Edge.TOP.getCoordinate(), (int) Edge.RIGHT.getCoordinate(), (int) Edge.BOTTOM.getCoordinate());
    }

    public void adjustEdgesToFitLogo(int w, int h){
            TwoKImage[] formats = Util.getLogoFormats(state.appMode, state.imageType);
            if (formats.length == 1 ) {
                TwoKImage format = formats[0];
                float aspectRatio = format.height * 1.f / format.width * 1.f;
                cropWidth = (w - 2 * mMarginSide); //always cover most of the screen
                cropHeight = (int) (cropWidth * aspectRatio); //it needs to be proportional
            }
       }

    // Private Methods /////////////////////////////////////////////////////////
    private void init(Context context){


        Log.i(TAG,"INSIDE THE INIT METHOD");

        int w = context.getResources().getDisplayMetrics().widthPixels;
        int h = context.getResources().getDisplayMetrics().heightPixels;
        //Getting list of available formats

        cropWidth = w - 2 * mMarginSide;
        cropHeight = cropWidth;

        if (state.appMode != null ) {
            adjustEdgesToFitLogo(w,h);
        }


        Log.d(TAG, "cropWidth: " + cropWidth + " cropHeigth: " + cropHeight + " w: " + w + " h: " + h + " marginSide: " + mMarginSide);

///TODO:Hardcoded for now. How can I pass a real height of the control to the instance of View?
        int mainBarHeight = state.toolbarHeight;

        //Here we are assigning the edge properties of the crop view
        int edgeT = (int)((h - mainBarHeight)/2.0 - cropHeight/2.0);
        int edgeB = edgeT + cropHeight;
        int edgeL = mMarginSide;
        int edgeR = w - mMarginSide;

        mBackgroundPaint = PaintUtil.newBackgroundPaint(context);
        mBorderPaint = PaintUtil.newBorderPaint(context);
        mGuidelinePaint = PaintUtil.newGuidelinePaint();

    //Setting the edge should resize the crop view
        Edge.TOP.setCoordinate(edgeT);
        Edge.BOTTOM.setCoordinate(edgeB);
        Edge.LEFT.setCoordinate(edgeL);
        Edge.RIGHT.setCoordinate(edgeR);
        new Rect(edgeL, edgeT, edgeR, edgeB);

        mBitmapRect = new Rect(0,0, w ,h);
    }


    private void drawRuleOfThirdsGuidelines(Canvas canvas) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        // Draw vertical guidelines.
        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = left + oneThirdCropWidth;
        canvas.drawLine(x1, top, x1, bottom, mGuidelinePaint);
        final float x2 = right - oneThirdCropWidth;
        canvas.drawLine(x2, top, x2, bottom, mGuidelinePaint);

        // Draw horizontal guidelines.
        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = top + oneThirdCropHeight;
        canvas.drawLine(left, y1, right, y1, mGuidelinePaint);
        final float y2 = bottom - oneThirdCropHeight;
        canvas.drawLine(left, y2, right, y2, mGuidelinePaint);
    }

}
