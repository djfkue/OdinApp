package com.argonmobile.odinapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * TODO: document your custom view class.
 */
public class FreeProfileLayoutView extends RelativeLayout {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public FreeProfileLayoutView(Context context) {
        super(context);
    }

    public FreeProfileLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreeProfileLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        final int action = MotionEventCompat.getActionMasked(ev);

        Log.e("SD_TRACE", "onInterceptTouchEvent.....................:" + ev.getPointerCount());

        Log.e("SD_TRACE", "onInterceptTouchEvent.....................action:" + ev.getAction());

        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if (ev.getPointerCount() == 2) {
                    return true;
                } else {
                    return false;
                }
            }
            case MotionEvent.ACTION_DOWN: {
                View view = getHitView(ev);
                if (view != null) {
                    if (view instanceof CheckedFrameLayout) {
                        Log.e("SD_TRACE", "touch down on me........");
                        if (((CheckedFrameLayout) view).isChecked()) {
                            Log.e("SD_TRACE", "check me check me........");
                        }
                    }
                }
                break;
            }
        }

        // In general, we don't want to intercept touch events. They should be
        // handled by the child view.
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e("SD_TRACE", "onTouchEvent.....................:" + ev.getPointerCount());
        Log.e("SD_TRACE", "onTouchEvent.....................:" + ev.getAction());
        return true;
    }

    private View getHitView(MotionEvent ev) {
        int x = Math.round(ev.getX());
        int y = Math.round(ev.getY());
        for (int i=0; i<getChildCount(); i++){
            View child = getChildAt(i);
            if(x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()){
                //touch is within this child
                return child;
            }
        }
        return null;
    }
}
