package com.argonmobile.odinapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.argonmobile.odinapp.R;

/**
 * Created by sean on 3/19/15.
 */
public class WheelWidget extends ListView {
    private final static String TAG = "WheelWidget";
    private Drawable mCenterDrawable;
    public WheelWidget(Context context) {
        super(context);
        init();
    }

    public WheelWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WheelWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCenterDrawable = getContext().getResources().getDrawable(R.drawable.center);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //Log.i(TAG, "getScrollY():" + getScrollY() +
                //", firstVisiblePos:" + getFirstVisiblePosition() + ", lastVisiblePos:" + getLastVisiblePosition());
        View firstChild = this.getChildAt(getFirstVisiblePosition());
        if (firstChild == null) {
            super.dispatchDraw(canvas);
            return;
        }
        //Log.i(TAG, "firstChild.getTop():" + firstChild.getTop());
        //float mScrollY = firstChild.getTop();
        int childHeight = firstChild.getHeight();
        View visibleChild;
        TextView content;
        int firstPositon = getFirstVisiblePosition();
        float centerY, delta, rate;
        for(int index = firstPositon; index <= getLastVisiblePosition(); ++index) {
            //Log.i(TAG, "index:" + index);
            visibleChild = getChildAt(index - firstPositon);
            centerY = visibleChild.getTop() + visibleChild.getHeight() / 2;
            delta = Math.abs(centerY - getHeight() / 2);
            rate = 1.0f - (float)Math.pow((delta / (getHeight() / 2)), 2.0f);
            if(rate < 0) rate = 0;
            if(rate > 1) rate = 1;
            //Log.i(TAG, "index:" + index + ",centerY:" + centerY + ", getHeight():" + getHeight() +
                   // ", visibleChild.getTop():" + visibleChild.getTop() + ", delta:" + delta + ", rate:" + rate);
            content = (TextView) visibleChild.findViewById(R.id.content);
            if (content != null) {
                content.setScaleY(rate);
                int alpha = (int)(255 * rate);
                content.setTextColor(Color.argb(alpha, 0, 0, 0));
                //content.getPaint().setColor(Color.argb(alpha, 0, 0, 0));
                //Log.i(TAG, "alpha:" + alpha);
            }

        }
        super.dispatchDraw(canvas);
        canvas.save();
        mCenterDrawable.setBounds(0, (getHeight() - childHeight) / 2, getWidth(), (getHeight() + childHeight) / 2);
        mCenterDrawable.draw(canvas);
        canvas.restore();
    }
    public void scrollToCenterOfItem() {
        int firstChildPos = getFirstVisiblePosition();
        View firstChild = this.getChildAt(firstChildPos);
        if(firstChild != null) {
            int remainVisible = (firstChild.getTop() + firstChild.getHeight()) % firstChild.getHeight();
            Log.i(TAG, "remainVisible:" + remainVisible + ",firstChildPos:" + firstChildPos + ",firstChild.getTop():" + firstChild.getTop());
            if((remainVisible > firstChild.getHeight() / 2) || (remainVisible == 0)) {
                Log.i(TAG, "gaga");
                setSelection(firstChildPos);
            } else {
                // scroll to next
                setSelection(firstChildPos + 1);
                Log.i(TAG, "jijii");
            }
            Log.i(TAG, "f:" + (firstChild.getTop() + firstChild.getHeight()) % firstChild.getHeight() + ", firstChild h=" + firstChild.getHeight());
        }
    }

    public int getEnabledItemIndex() {
        return getFirstVisiblePosition() + 4;
    }
}
