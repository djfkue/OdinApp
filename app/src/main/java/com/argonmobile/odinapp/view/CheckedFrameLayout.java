
package com.argonmobile.odinapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.argonmobile.odinapp.R;

public class CheckedFrameLayout extends FrameLayout implements Checkable {
    private boolean mChecked;

    public CheckedFrameLayout(Context context) {
        super(context);
    }

    public CheckedFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundResource(checked ? R.drawable.image_border_bg_focus_blue
                : R.drawable.grid_background);
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
