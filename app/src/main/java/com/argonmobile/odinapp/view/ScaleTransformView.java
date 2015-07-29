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
import android.view.ScaleGestureDetector;
import android.widget.RelativeLayout;

import com.argonmobile.odinapp.R;


public class ScaleTransformView extends RelativeLayout {
    private static final String TAG = "ScaleTransformView";

    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private boolean mIsTrigging = false;

    private ScaleGestureDetector mScaleGestureDetector;

    private OnScaleListener mOnScaleListener;

    public ScaleTransformView(Context context) {
        super(context);
        init(null, 0);
    }

    public ScaleTransformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);

        // Sets up interactions
        Log.e(TAG, "init scale gesture detector");
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        //mScaleGestureDetector.setQuickScaleEnabled(true);
    }

    public ScaleTransformView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setOnScaleListener(OnScaleListener scaleListener) {
        mOnScaleListener = scaleListener;
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ScaleTransformView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.ScaleTransformView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.ScaleTransformView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.ScaleTransformView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.ScaleTransformView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.ScaleTransformView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

    }

    int mTriggerPointerCount = 5;

    public void setTriggerPointerCount(int triggerPointerCount) {
        mTriggerPointerCount = triggerPointerCount;
    }

    private boolean checkGestureTrigger(MotionEvent ev) {
        if (MotionEventCompat.getPointerCount(ev) >= mTriggerPointerCount && MotionEventCompat.getPointerCount(ev) <= 6) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {

        if (checkGestureTrigger(ev)) {
            mIsTrigging = true;
        } else {
            mIsTrigging = false;
        }

        boolean retValue = mScaleGestureDetector.onTouchEvent(ev);
        if (!mIsTrigging) {
            return super.dispatchTouchEvent(ev) || retValue;
        } else {
            return true;
        }
    }

    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private float mScale;

        public boolean onScale(ScaleGestureDetector detector) {
            if (mIsTrigging) {
                mScale *= detector.getScaleFactor();
                Log.e(TAG, "onScale: " + mScale);
                if (mOnScaleListener != null) {
                    mOnScaleListener.onScale(detector.getScaleFactor());
                }
                return true;
            } else {
                return false;
            }
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (mIsTrigging) {
                Log.e(TAG, "onScaleBegin");
                mScale = detector.getScaleFactor();

                if (mOnScaleListener != null) {
                    mOnScaleListener.onScaleBegin();
                }
                return true;
            } else {
                return true;
            }
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            // Intentionally empty
            if (mIsTrigging) {

                Log.e(TAG, "onScaleEnd: " + detector.getScaleFactor());
                mScale = Float.NaN;
                if (mOnScaleListener != null) {
                    mOnScaleListener.onScaleEnd();
                }
            }
        }
    };

    public interface OnScaleListener {
        public void onScaleBegin();
        public void onScale(float scaleFactor);
        public void onScaleEnd();
    }
}
