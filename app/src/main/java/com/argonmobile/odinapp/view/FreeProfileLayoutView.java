package com.argonmobile.odinapp.view;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;


public class FreeProfileLayoutView extends RelativeLayout {
    private static final String TAG = "FreeProfileLayoutView";
    private CheckedFrameLayout mCheckedView;
    private float mStartX;
    private float mStartY;

    private int mVScreenCount = 1;
    private int mHScreenCount = 1;

    private Paint mRectPaint;
    private Paint mInnerCellPaint;
    private boolean mEnableGesture = false;

    public FreeProfileLayoutView(Context context) {
        super(context);
    }

    public FreeProfileLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        //mScaleGestureDetector.setQuickScaleEnabled(true);
        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setColor(Color.LTGRAY);
        mRectPaint.setStrokeWidth(5.0f);

        mInnerCellPaint = new Paint();
        mInnerCellPaint.setStyle(Paint.Style.STROKE);
        mInnerCellPaint.setPathEffect(new DashPathEffect(new float[] {5,20}, 10));
        mInnerCellPaint.setColor(Color.BLUE);
        mInnerCellPaint.setStrokeWidth(2.0f);
    }

    public FreeProfileLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        //mScaleGestureDetector.setQuickScaleEnabled(true);
    }

    public void setVScreenCount(int vScreens) {
        mVScreenCount = vScreens;
    }

    public void setHScreenCount(int hScreenCount) {
        mHScreenCount = hScreenCount;
    }

    public void enableGesture(boolean enable) {
        mEnableGesture = enable;
    }

    @Override
    protected void dispatchDraw (Canvas canvas) {
        drawCells(canvas);
        super.dispatchDraw(canvas);
    }

    private void drawCells(Canvas canvas) {
        Rect rect = new Rect();
        getDrawingRect(rect);
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, mRectPaint);

        for (int i = 0; i < mHScreenCount * 2; i++) {
            int startX = i * getWidth() / mHScreenCount / 2;
            canvas.drawLine(startX, rect.top, startX, rect.bottom, mInnerCellPaint);
        }

        for (int i = 0; i < mVScreenCount * 2; i++) {
            int startY = i * getHeight() / mVScreenCount / 2;
            canvas.drawLine(rect.left, startY, rect.right, startY, mInnerCellPaint);
        }

        for (int i = 0; i < mHScreenCount; i++) {
            int startX = i * getWidth() / mHScreenCount;
            canvas.drawLine(startX, rect.top, startX, rect.bottom, mRectPaint);
        }

        for(int i = 0; i < mVScreenCount; i++) {
            int startY = i * getHeight() / mVScreenCount;
            canvas.drawLine(rect.left, startY, rect.right, startY, mRectPaint);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!mEnableGesture) {
            return super.onInterceptTouchEvent(ev);
        }

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

                        if (((CheckedFrameLayout) view).isChecked() ) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                            mCheckedView = (CheckedFrameLayout) view;
                            mStartX = ev.getX();
                            mStartY = ev.getY();
                            return true;
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(false);
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

        if (!mEnableGesture) {
            return super.onTouchEvent(ev);
        }

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

        private float mScaleX;
        private float mScaleY;


        public boolean onScale(ScaleGestureDetector detector) {
            if (mIsTrigging) {
                float currentSpanX = detector.getCurrentSpanX();
                float currentSpanY = detector.getCurrentSpanY();

                float xScaleFactor = detector.getPreviousSpanX() > 0 ? currentSpanX / detector.getPreviousSpanX() : 1;
                mScaleX *= xScaleFactor;

                float yScaleFactor = detector.getPreviousSpanY() > 0 ? currentSpanY / detector.getPreviousSpanY() : 1;
                mScaleY *= yScaleFactor;

                Log.e(TAG, "onScaleX: " + mScaleX);
                Log.e(TAG, "onScaleY: " + mScaleY);
                if (mCheckedView != null) {
                    mCheckedView.setScaleX(mScaleX);
                    mCheckedView.setScaleY(mScaleY);
                }
                return true;
            } else {
                return false;
            }
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (mIsTrigging) {
                Log.e(TAG, "onScaleBegin");
                float currentSpanX = detector.getCurrentSpanX();
                float currentSpanY = detector.getCurrentSpanY();
                float xScaleFactor = detector.getPreviousSpanX() > 0 ? currentSpanX / detector.getPreviousSpanX() : 1;
                mScaleX = xScaleFactor;

                float yScaleFactor = detector.getPreviousSpanY() > 0 ? currentSpanY / detector.getPreviousSpanY() : 1;
                mScaleY = yScaleFactor;
                return true;
            } else {
                return true;
            }
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            // Intentionally empty
            if (mIsTrigging) {

                Log.e(TAG, "onScaleEnd: " + detector.getScaleFactor());
                mScaleX = Float.NaN;
                mStartY = Float.NaN;
            }
            Log.e(TAG, "onScaleEnd......................");
            updateScaleViewProperty();
        }
    };

    private void updateScaleViewProperty() {
        if (mCheckedView != null) {
            Rect rect = new Rect();
            mCheckedView.getHitRect(rect);

            RelativeLayout.LayoutParams layoutParams = (LayoutParams) mCheckedView.getLayoutParams();
            layoutParams.width = rect.width();
            layoutParams.height = rect.height();
            mCheckedView.setX(rect.left);
            mCheckedView.setY(rect.top);
            mCheckedView.setScaleX(1);
            mCheckedView.setScaleY(1);

            mCheckedView.setLayoutParams(layoutParams);

            int mDestLeft = 0;
            int cellWidth = getWidth() / mHScreenCount / 2;
            int cellHeight = getHeight() / mVScreenCount / 2;
            if (mCheckedView.getX() < 0) {
                mDestLeft = 0;
            } else {
                mDestLeft = (int) ((mCheckedView.getX() / cellWidth)) * cellWidth;
            }

            int mDestRight = 0;
            if ((rect.left + rect.width()) > getWidth()) {
                mDestRight = getWidth();
            } else {
                mDestRight = ((int) ((rect.left + rect.width()) / cellWidth) + 1) * cellWidth;
            }

            int destTop = 0;
            if (rect.top < 0) {
                destTop = 0;
            } else {
                destTop = (rect.top / cellHeight) * cellHeight;
            }

            int destBottom = 0;
            if (rect.bottom > getHeight()) {
                destBottom = getHeight();
            } else {
                destBottom = ((rect.bottom / cellHeight) + 1) * cellHeight;
            }

            layoutParams = (LayoutParams) mCheckedView.getLayoutParams();
            layoutParams.width = mDestRight - mDestLeft;
            layoutParams.height = destBottom - destTop;

            mCheckedView.setLayoutParams(layoutParams);

            Log.d(TAG, "checked view x: " + mDestLeft);
            mCheckedView.setX(mDestLeft);
            mCheckedView.setY(destTop);

            if (mChildScaleChangeListener != null) {
                mChildScaleChangeListener.onChildScaleEnd(mCheckedView, layoutParams.width, layoutParams.height);
            }
        }
    }

    private ChildScaleChangeListener mChildScaleChangeListener;

    public void setOnChildScaleChangeListener(ChildScaleChangeListener childScaleChangeListener) {
        mChildScaleChangeListener = childScaleChangeListener;
    }

    public interface ChildScaleChangeListener {
        public void onChildScaleEnd(View view, int width, int height);
    }
}
