package com.argonmobile.odinapp.view;

import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * TODO: document your custom view class.
 */
public class FreeProfileLayoutView extends RelativeLayout {
    private static final String TAG = "FreeProfileLayoutView";
    private CheckedFrameLayout mCheckedView;
    private float mStartX;
    private float mStartY;

    public FreeProfileLayoutView(Context context) {
        super(context);
    }

    public FreeProfileLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mScaleGestureDetector.setQuickScaleEnabled(true);
    }

    public FreeProfileLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mScaleGestureDetector.setQuickScaleEnabled(true);
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
            getParent().requestDisallowInterceptTouchEvent(false);
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
                        Log.e("SD_TRACE", "touch down on checkedView: " + view.toString());
                        getParent().requestDisallowInterceptTouchEvent(true);
                        if (((CheckedFrameLayout) view).isChecked() ) {
                            mCheckedView = (CheckedFrameLayout) view;
                            mStartX = ev.getX();
                            mStartY = ev.getY();
                            return true;
                        } else {
                            mCheckedView = (CheckedFrameLayout) view;
                            mStartX = ev.getX();
                            mStartY = ev.getY();
                        }
                    } else {
                        mCheckedView = null;
                        return false;
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
        Log.e("SD_TRACE", "onTouchEvent.....................action:" + ev.getAction());
        if (checkGestureTrigger(ev)) {
            mIsTrigging = true;
            getParent().requestDisallowInterceptTouchEvent(true);

            mScaleGestureDetector.onTouchEvent(ev);
        } else {
            mIsTrigging = false;

            if (ev.getAction() == MotionEvent.ACTION_MOVE && getHitView(ev) != null && mCheckedView != null) {
                if (Math.abs(ev.getX() - mStartX) > 10 && Math.abs(ev.getY() - mStartY) > 10) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(mCheckedView);
                    mCheckedView.startDrag(data, shadowBuilder, mCheckedView, 0);
                    mCheckedView.setVisibility(View.INVISIBLE);
                }
            }

        }


        return true;
    }

    private boolean checkGestureTrigger(MotionEvent ev) {
        if (MotionEventCompat.getPointerCount(ev) == 2 ) {
            return true;
        } else {
            return false;
        }
    }


    private View getHitView(MotionEvent ev) {
        int x = Math.round(ev.getX());
        int y = Math.round(ev.getY());
        Rect rect = new Rect();
        for (int i=0; i<getChildCount(); i++){
            View child = getChildAt(i);
            child.getHitRect(rect);
            if(x > rect.left && x < rect.right && y > rect.top && y < rect.bottom) {
                //touch is within this child
                return child;
            }
        }
        return null;
    }

    private boolean mIsTrigging = false;

    private ScaleGestureDetector mScaleGestureDetector;

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
                if (mCheckedView != null) {
//                    RelativeLayout.LayoutParams params = (LayoutParams) mCheckedView.getLayoutParams();
//                    params.width = (int) (mCheckedView.getWidth() * mScale);
//                    params.height = (int) (mCheckedView.getHeight() * mScale);
//                    mCheckedView.setLayoutParams(params);
                    mCheckedView.setScaleX(mScale);
                    mCheckedView.setScaleY(mScale);
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
            }
        }
    };
}
