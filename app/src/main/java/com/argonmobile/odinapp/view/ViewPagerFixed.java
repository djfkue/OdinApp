package com.argonmobile.odinapp.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class ViewPagerFixed extends ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {

        if (v instanceof HorizontalScrollView) {
            HorizontalScrollView scroll = (HorizontalScrollView) v;
            //return scroll.canScrollHorizontally(-dx);
            //return false;
            //Log.e("SD_TRACE", "return true.................");
            //return false;

            int vScrollX = scroll.getScrollX();

            LinearLayout table = (LinearLayout) scroll.getChildAt(scroll
                    .getChildCount() - 1);
            int diff = (table.getRight() - (scroll.getWidth()
                    + scroll.getScrollX() + table.getLeft()));

            if (vScrollX == 0 && diff <= 0) {// table without scroll
                if (dx > 2 && this.getCurrentItem() > 0) {
                    this.setCurrentItem(this.getCurrentItem() - 1, true);
                } else if (dx < -2
                        && this.getCurrentItem() + 1 < this.getChildCount()) {
                    this.setCurrentItem(this.getCurrentItem() + 1, true);
                }
                return false; // change page
            }
            if (vScrollX == 0 && dx > 2) {// left edge, swiping right
                if (this.getCurrentItem() > 0) {
                    this.setCurrentItem(this.getCurrentItem() - 1, true);
                }
                return false; // change page
            }
            if (vScrollX == 0 && dx < -2) {// left edge, swiping left
                return true;// scroll
            }
            if (diff <= 0 && dx > 2) {// right edge, swiping right
                return true;// scroll
            }
            if (diff <= 0 && dx < -2) {// right edge, swiping left
                if (this.getCurrentItem() + 1 < this.getChildCount()) {
                    this.setCurrentItem(this.getCurrentItem() + 1, true);
                }
                return false;// change page
            }

        }

        return super.canScroll(v, checkV, dx, x, y);
    }
}