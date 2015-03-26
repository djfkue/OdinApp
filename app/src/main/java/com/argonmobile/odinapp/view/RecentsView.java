package com.argonmobile.odinapp.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;


import com.argonmobile.odinapp.RecentsConfiguration;
import com.argonmobile.odinapp.model.Profile;
import com.argonmobile.odinapp.model.ProfileStack;

import java.util.ArrayList;

/**
 * This view is the the top level layout that contains TaskStacks (which are laid out according
 * to their SpaceNode bounds.
 */
public class RecentsView extends FrameLayout implements TaskStackView.TaskStackViewCallbacks {

    /** The RecentsView callbacks */
    public interface RecentsViewCallbacks {
        public void onTaskViewClicked(TaskView view, Profile t);
    }

    LayoutInflater mInflater;
    RecentsConfiguration mConfig;

    ArrayList<ProfileStack> mStacks;

    RecentsViewCallbacks mCb;

    public RecentsView(Context context) {
        super(context);
    }

    public RecentsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mConfig = RecentsConfiguration.getInstance();
        mInflater = LayoutInflater.from(context);
    }

    /** Sets the callbacks */
    public void setCallbacks(RecentsViewCallbacks cb) {
        mCb = cb;
    }

    /** Set/get the bsp root node */
    public void setTaskStacks(ArrayList<ProfileStack> stacks) {
        // Remove all TaskStackViews (but leave the search bar)
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View v = getChildAt(i);
            removeViewAt(i);
        }

        // Create and add all the stacks for this partition of space.
        mStacks = stacks;
        int numStacks = mStacks.size();
        for (int i = 0; i < numStacks; i++) {
            ProfileStack stack = mStacks.get(i);
            TaskStackView stackView = new TaskStackView(getContext(), stack);
            stackView.setCallbacks(this);

            addView(stackView);
        }

    }

    /** Removes all the task stack views from this recents view. */
    public void removeAllTaskStacks() {
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = getChildAt(i);
            removeViewAt(i);
        }
    }

    /** Requests all task stacks to start their enter-recents animation */
    public void startEnterRecentsAnimation(ViewAnimation.TaskViewEnterContext ctx) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            TaskStackView stackView = (TaskStackView) child;
            stackView.startEnterRecentsAnimation(ctx);
        }
    }

    /** Requests all task stacks to start their exit-recents animation */
    public void startExitToHomeAnimation(ViewAnimation.TaskViewExitContext ctx) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            TaskStackView stackView = (TaskStackView) child;
            stackView.startExitToHomeAnimation(ctx);
        }
    }

    /**
     * This is called with the full size of the window since we are handling our own insets.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Rect taskStackBounds = new Rect();
        mConfig.getTaskStackBounds(width, height, mConfig.systemInsets.top,
                mConfig.systemInsets.right, taskStackBounds);

        // Measure each TaskStackView with the full width and height of the window since the
        // transition view is a child of that stack view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                TaskStackView tsv = (TaskStackView) child;
                // Set the insets to be the top/left inset + search bounds
                tsv.setStackInsetRect(taskStackBounds);
                tsv.measure(widthMeasureSpec, heightMeasureSpec);
            }
        }

        setMeasuredDimension(width, height);
    }

    /**
     * This is called with the full size of the window since we are handling our own insets.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        // Layout each TaskStackView with the full width and height of the window since the
        // transition view is a child of that stack view
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(left, top, left + child.getMeasuredWidth(),
                        top + child.getMeasuredHeight());
            }
        }
    }

    @Override
    public void onTaskViewClicked(TaskStackView stackView, TaskView tv, Profile t) {
        if (mCb != null) {
            mCb.onTaskViewClicked(tv, t);
        }
    }

}