package com.argonmobile.odinapp.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.argonmobile.odinapp.Constants;
import com.argonmobile.odinapp.R;
import com.argonmobile.odinapp.RecentsConfiguration;
import com.argonmobile.odinapp.model.Profile;

/**
 * Created by argon on 3/23/15.
 */
public class TaskView extends FrameLayout implements View.OnClickListener {


    /** The TaskView callbacks */
    interface TaskViewCallbacks {
        public void onTaskViewClicked(TaskView tv, Profile task);
    }


    Profile mTask;

    RecentsConfiguration mConfig;

    float mTaskProgress;
    ObjectAnimator mTaskProgressAnimator;

    float mMaxDimScale;
    int mDim;

    AccelerateInterpolator mDimInterpolator = new AccelerateInterpolator(1f);
    PorterDuffColorFilter mDimColorFilter = new PorterDuffColorFilter(0, PorterDuff.Mode.MULTIPLY);

    boolean mIsFullScreenView;
    boolean mClipViewInStack;

    //AnimateableViewBounds mViewBounds;

    Paint mLayerPaint = new Paint();


    View mContent;
    //TaskViewThumbnail mThumbnailView;
    TaskViewHeader mHeaderView;

    TaskViewCallbacks mCb;

    // Optimizations
    ValueAnimator.AnimatorUpdateListener mUpdateDimListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setTaskProgress((Float) animation.getAnimatedValue());
                }
            };

    public TaskView(Context context) {
        this(context, null);
    }

    public TaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mConfig = RecentsConfiguration.getInstance();
        mMaxDimScale = mConfig.taskStackMaxDim / 255f;
        mClipViewInStack = true;
        setTaskProgress(getTaskProgress());
        setDim(getDim());
        if (mConfig.fakeShadows) {
            setBackground(new FakeShadowDrawable(context.getResources(), mConfig));
        }
    }

    /** Set callback */
    void setCallbacks(TaskViewCallbacks cb) {
        mCb = cb;
    }

    @Override
    protected void onFinishInflate() {
        // Bind the views
        mContent = findViewById(R.id.task_view_content);
        mHeaderView = (TaskViewHeader) findViewById(R.id.task_view_bar);
        //mThumbnailView = (TaskViewThumbnail) findViewById(R.id.task_view_thumbnail);
        //mThumbnailView.enableTaskBarClip(mHeaderView);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        // Measure the content
        mContent.measure(MeasureSpec.makeMeasureSpec(widthWithoutPadding, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(widthWithoutPadding, MeasureSpec.EXACTLY));

        // Measure the bar view, thumbnail, and footer
        mHeaderView.measure(MeasureSpec.makeMeasureSpec(widthWithoutPadding, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mConfig.taskBarHeight, MeasureSpec.EXACTLY));

//        mThumbnailView.measure(
//                MeasureSpec.makeMeasureSpec(widthWithoutPadding, MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec(widthWithoutPadding, MeasureSpec.EXACTLY));

        setMeasuredDimension(width, height);
    }

    /** Gets the task */
    Profile getTask() {
        return mTask;
    }

    /** Sets the current task progress. */
    public void setTaskProgress(float p) {
        mTaskProgress = p;
        //mViewBounds.setAlpha(p);
        Log.e("SD_TRACE", "setTaskProgress: " + p);
        updateDimFromTaskProgress();
    }

    /** Update the dim as a function of the scale of this view. */
    void updateDimFromTaskProgress() {
        setDim(getDimFromTaskProgress());
    }

    /** Compute the dim as a function of the scale of this view. */
    int getDimFromTaskProgress() {
        float dim = mMaxDimScale * mDimInterpolator.getInterpolation(1f - mTaskProgress);

        return (int) (dim * 255);
    }

    public float getTaskProgress() {
        return mTaskProgress;
    }

    /** Returns the current dim. */
    public void setDim(int dim) {
        mDim = dim;
        if (true) {
            // Defer setting hardware layers if we have not yet measured, or there is no dim to draw
            if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {

                int inverse = 255 - mDim;

                Log.e("SD_TRACE", "setDim: " + inverse);

                mDimColorFilter = new PorterDuffColorFilter(Color.argb(dim, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
                mLayerPaint.setColorFilter(mDimColorFilter);
                mContent.setLayerType(LAYER_TYPE_HARDWARE, mLayerPaint);
                mContent.invalidate();
            }

        } else {
            float dimAlpha = mDim / 255.0f;
//            if (mThumbnailView != null) {
//                mThumbnailView.setDimAlpha(dimAlpha);
//            }
            if (mHeaderView != null) {
                mHeaderView.setDimAlpha(dim);
            }
        }
    }

    /** Returns the current dim. */
    public int getDim() {
        return mDim;
    }


    /**
     * Returns whether this view should be clipped, or any views below should clip against this
     * view.
     */
    boolean shouldClipViewInStack() {
        return mClipViewInStack && !mIsFullScreenView && (getVisibility() == View.VISIBLE);
    }

    /** Sets whether this view should be clipped, or clipped against. */
    void setClipViewInStack(boolean clip) {
        if (clip != mClipViewInStack) {
            mClipViewInStack = clip;
        }
    }

    /** Sets whether this task view is full screen or not. */
    void setIsFullScreen(boolean isFullscreen) {
        mIsFullScreenView = isFullscreen;
        mHeaderView.setIsFullscreen(isFullscreen);
    }

    /** Returns whether this task view should currently be drawn as a full screen view. */
    boolean isFullScreenView() {
        return mIsFullScreenView;
    }

    /** Synchronizes this view's properties with the task's transform */
    void updateViewPropertiesToTaskTransform(TaskViewTransform toTransform, int duration) {
        updateViewPropertiesToTaskTransform(toTransform, duration, null);
    }

    void updateViewPropertiesToTaskTransform(TaskViewTransform toTransform, int duration,
                                             ValueAnimator.AnimatorUpdateListener updateCallback) {
        // If we are a full screen view, then only update the Z to keep it in order
        // XXX: Also update/animate the dim as well
        if (mIsFullScreenView) {
            return;
        }

        // Apply the transform
        toTransform.applyToTaskView(this, duration, mConfig.fastOutSlowInInterpolator, false,
                !mConfig.fakeShadows, updateCallback);

        // Update the task progress
        if (mTaskProgressAnimator != null) {
            mTaskProgressAnimator.removeAllListeners();
            mTaskProgressAnimator.cancel();
        }
        if (duration <= 0) {
            setTaskProgress(toTransform.p);
        } else {
            mTaskProgressAnimator = ObjectAnimator.ofFloat(this, "taskProgress", toTransform.p);
            mTaskProgressAnimator.setDuration(duration);
            mTaskProgressAnimator.addUpdateListener(mUpdateDimListener);
            mTaskProgressAnimator.start();
        }
    }


    /** Animates this task view as it enters recents */
    void startEnterRecentsAnimation(final ViewAnimation.TaskViewEnterContext ctx) {
        final TaskViewTransform transform = ctx.currentTaskTransform;
        Log.e("SD_TRACE", "startEnterRecentsAnimation............");
        if (mConfig.launchedFromAppWithScreenshot) {
            Log.e("SD_TRACE", "launchedFromAppWithScreenshot");
            if (mTask.isLaunchTarget) {
                Log.e("SD_TRACE", "launchedFromAppWithScreenshot " + "isLaunchTarget");
                Rect taskRect = ctx.currentTaskRect;
                int duration = mConfig.taskViewEnterFromHomeDuration * 10;
                int windowInsetTop = mConfig.systemInsets.top; // XXX: Should be for the window
                float taskScale = ((float) taskRect.width() / getMeasuredWidth()) * transform.scale;
                float scaledYOffset = ((1f - taskScale) * getMeasuredHeight()) / 2;
                float scaledWindowInsetTop = (int) (taskScale * windowInsetTop);
                float scaledTranslationY = taskRect.top + transform.translationY -
                        (scaledWindowInsetTop + scaledYOffset);

                // Animate the task bar of the first task view
                animate()
                        .scaleX(taskScale)
                        .translationY(scaledTranslationY)
                        .setDuration(duration)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                setIsFullScreen(false);
                                requestLayout();

                                //mHeaderView.setTranslationY(0);

                                // Decrement the post animation trigger
                                ctx.postAnimationTrigger.decrement();
                            }
                        })
                        .start();
            } else {
                animateDimToProgress(mConfig.taskViewEnterFromHomeDelay,
                        mConfig.taskViewEnterFromHomeDuration,
                        ctx.postAnimationTrigger.decrementOnAnimationEnd());
            }
            ctx.postAnimationTrigger.increment();

        } else if (mConfig.launchedFromAppWithThumbnail) {
            Log.e("SD_TRACE", "launchedFromAppWithThumbnail");
            if (mTask.isLaunchTarget) {
                // Animate the dim/overlay
                if (Constants.DebugFlags.App.EnableThumbnailAlphaOnFrontmost) {
                    // Animate the thumbnail alpha before the dim animation (to prevent updating the
                    // hardware layer)
//                    mThumbnailView.startEnterRecentsAnimation(mConfig.taskBarEnterAnimDelay,
//                            new Runnable() {
//                                @Override
//                                public void run() {
//                                    animateDimToProgress(0, mConfig.taskBarEnterAnimDuration,
//                                            ctx.postAnimationTrigger.decrementOnAnimationEnd());
//                                }
//                            });
                } else {
                    // Immediately start the dim animation
                    animateDimToProgress(mConfig.taskBarEnterAnimDelay,
                            mConfig.taskBarEnterAnimDuration,
                            ctx.postAnimationTrigger.decrementOnAnimationEnd());
                }
                ctx.postAnimationTrigger.increment();

            } else {
                // Animate the task up if it was occluding the launch target
                if (ctx.currentTaskOccludesLaunchTarget) {
                    setTranslationY(transform.translationY + mConfig.taskViewAffiliateGroupEnterOffsetPx);
                    setAlpha(0f);
                    animate().alpha(1f)
                            .translationY(transform.translationY)
                            .setStartDelay(mConfig.taskBarEnterAnimDelay)
                            .setUpdateListener(null)
                            .setInterpolator(mConfig.fastOutSlowInInterpolator)
                            .setDuration(mConfig.taskViewEnterFromHomeDuration)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Decrement the post animation trigger
                                    ctx.postAnimationTrigger.decrement();
                                }
                            })
                            .start();
                    ctx.postAnimationTrigger.increment();
                }
            }

        } else if (mConfig.launchedFromHome) {
            Log.e("SD_TRACE", "launchedFromHome");
            // Animate the tasks up
            int frontIndex = (ctx.currentStackViewCount - ctx.currentStackViewIndex - 1);
            int delay = mConfig.taskViewEnterFromHomeDelay +
                    frontIndex * mConfig.taskViewEnterFromHomeStaggerDelay;

            setScaleX(transform.scale);
            animate()
                    .translationY(transform.translationY)
                    .setStartDelay(delay)
                    .setUpdateListener(ctx.updateListener)
                    .setInterpolator(mConfig.quintOutInterpolator)
                    .setDuration(mConfig.taskViewEnterFromHomeDuration +
                            frontIndex * mConfig.taskViewEnterFromHomeStaggerDelay)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // Decrement the post animation trigger
                            ctx.postAnimationTrigger.decrement();
                        }
                    })
                    .start();
            ctx.postAnimationTrigger.increment();

        }

    }

    /** Resets this view's properties */
    void resetViewProperties() {
        setDim(0);
        TaskViewTransform.reset(this);
    }

    /** Prepares this task view for the enter-recents animations.  This is called earlier in the
     * first layout because the actual animation into recents may take a long time. */
    void prepareEnterRecentsAnimation(boolean isTaskViewLaunchTargetTask,
                                      boolean occludesLaunchTarget, int offscreenY) {
        int initialDim = getDim();
        if (mConfig.launchedFromAppWithScreenshot) {
            if (isTaskViewLaunchTargetTask) {
            } else {
                // Don't do anything for the side views when animating in
            }

        } else if (mConfig.launchedFromAppWithThumbnail) {
            if (isTaskViewLaunchTargetTask) {
                // Set the dim to 0 so we can animate it in
                initialDim = 0;
            } else if (occludesLaunchTarget) {
                // Move the task view off screen (below) so we can animate it in
                setTranslationY(offscreenY);
            }

        } else if (mConfig.launchedFromHome) {
            // Move the task view off screen (below) so we can animate it in
            setTranslationY(offscreenY);
            setScaleX(1f);
            setScaleY(1f);
        }
        // Apply the current dim
        setDim(initialDim);
        // Prepare the thumbnail view alpha
//        mThumbnailView.prepareEnterRecentsAnimation(isTaskViewLaunchTargetTask);
    }

    /** Animates the dim to the task progress. */
    void animateDimToProgress(int delay, int duration, Animator.AnimatorListener postAnimRunnable) {
        // Animate the dim into view as well
        int toDim = getDimFromTaskProgress();
        if (toDim != getDim()) {
            ObjectAnimator anim = ObjectAnimator.ofInt(TaskView.this, "dim", toDim);
            anim.setStartDelay(delay);
            anim.setDuration(duration);
            if (postAnimRunnable != null) {
                anim.addListener(postAnimRunnable);
            }
            anim.start();
        }
    }

    /** Animates this task view as it leaves recents by pressing home. */
    void startExitToHomeAnimation(ViewAnimation.TaskViewExitContext ctx) {
        animate()
                .translationY(ctx.offscreenTranslationY)
                .setStartDelay(0)
                .setUpdateListener(null)
                .setInterpolator(mConfig.fastOutLinearInInterpolator)
                .setDuration(mConfig.taskViewExitToHomeDuration)
                .withEndAction(ctx.postAnimationTrigger.decrementAsRunnable())
                .start();
        ctx.postAnimationTrigger.increment();
    }

    /** Binds this task view to the task */
    public void onTaskBound(Profile t) {
        mTask = t;
        mHeaderView.mActivityDescription.setText(t.activityLabel);
        setOnClickListener(this);
//        mThumbnailView.rebindToTask(t);
        //mTask.setCallbacks(this);
    }

    /**** View.OnClickListener Implementation ****/

    @Override
    public void onClick(final View v) {
        Log.e("SD_TRACE", "click me click me click me.............");
        final TaskView tv = this;
        if (mCb != null) {
            mCb.onTaskViewClicked(tv, tv.getTask());
        }
    }
}
