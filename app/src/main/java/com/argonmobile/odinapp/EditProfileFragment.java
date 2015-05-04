package com.argonmobile.odinapp;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.EditProfileCameraAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.model.ScreenStructure;
import com.argonmobile.odinapp.protocol.deviceinfo.ScreenGroup;
import com.argonmobile.odinapp.view.CheckedFrameLayout;
import com.argonmobile.odinapp.view.FreeProfileLayoutView;
import com.argonmobile.odinapp.view.ShadowLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private static final int ANIM_DURATION = 500;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    private EditProfileCameraAdapter mEditProfileCameraAdapter;
    private GridView mGridView;
    private ColorDrawable mBackground;
    private TextView mTextView;
    private FreeProfileLayoutView mEditProfileLayoutView;
    private CheckedFrameLayout mCurrentChecked;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mEditProfileLayoutView = (FreeProfileLayoutView) rootView.findViewById(R.id.edit_container);

        mEditProfileLayoutView.setOnDragListener(mFrameDragListener);

        mGridView = (GridView) rootView.findViewById(R.id.grid_view);
        mEditProfileCameraAdapter = new EditProfileCameraAdapter(rootView.getContext());
        mGridView.setAdapter(mEditProfileCameraAdapter);

        if (savedInstanceState == null) {
            mGridView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    mGridView.getViewTreeObserver().removeOnPreDrawListener(this);

                    runEnterAnimation();

                    ScreenGroup screenGroup = ScreenStructure.getInstance().screenGroups[0];

                    float screenWidth = screenGroup.horizontalCount * 1920.0f;
                    float screenHeight = screenGroup.verticalCount * 1080.0f;

                    boolean isLandScape = screenWidth > screenHeight ? true : false;

                    mEditProfileLayoutView.setVScreenCount(screenGroup.verticalCount);
                    mEditProfileLayoutView.setHScreenCount(screenGroup.horizontalCount);

                    //if (isLandScape) {
                    int height = mEditProfileLayoutView.getMeasuredHeight();
                    Log.d(TAG, "editProfileLayoutHeight: " + height);
                    int width = (int) (height * ( screenWidth / screenHeight ));
                    ViewGroup.LayoutParams layoutParams = mEditProfileLayoutView.getLayoutParams();
                    layoutParams.width = width;
                    mEditProfileLayoutView.setLayoutParams(layoutParams);
                    //mEditProfileLayoutView.setBackgroundColor(Color.BLUE);
                    //}

                    return true;
                }
            });
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditProfileCameraAdapter.toggleItemChecked(position);
//                view.setTag(position);
//                view.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        ClipData.Item item = new ClipData.Item("TEST");
//                        ClipData dragData = new ClipData("TEST", new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},item);
//                        // Instantiates the drag shadow builder.
//                        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
//                        v.startDrag(dragData, myShadow, null, 0);
//                        return true;
//                    }
//                });
            }
        });

        mBackground = new ColorDrawable(Color.BLACK);
        rootView.setBackground(mBackground);

        mTextView = (TextView) rootView.findViewById(R.id.title_view);
        return rootView;
    }

    private void runEnterAnimation() {
        Log.e(TAG, "gridview child count: " + mGridView.getChildCount());

        final long duration = (long) (ANIM_DURATION);

        for (int i = 0; i < mGridView.getChildCount(); i++) {
            View cameraView = mGridView.getChildAt(i);

            ShadowLayout shadowLayout = (ShadowLayout) cameraView.findViewById(R.id.shadowLayout);

            int[] screenLocation = new int[2];
            cameraView.getLocationOnScreen(screenLocation);
            CameraInfo cameraInfo = EditProfileModel.getInstance().getCameraInfoArrayList().get(i);
            int leftDelta = cameraInfo.getLeft() - screenLocation[0];
            int topDelta = cameraInfo.getTop() - screenLocation[1];
            cameraView.setTranslationX(leftDelta);
            cameraView.setTranslationY(topDelta);

            cameraView.animate().setDuration(duration).
                    translationX(0).translationY(0).
                    setInterpolator(sDecelerator).withLayer();

            // Fade in the black background
            ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
            bgAnim.setDuration(duration);
            bgAnim.start();

            // Animate a drop-shadow of the image
//            ObjectAnimator shadowAnim = ObjectAnimator.ofFloat(shadowLayout, "shadowDepth", 0, 1);
//            shadowAnim.setDuration(duration);
//            shadowAnim.start();
        }

        mTextView.setAlpha(0);

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTextView.setTranslationY(mTextView.getHeight());
                mTextView.animate().setDuration(duration).
                        translationY(0).alpha(1).
                        setInterpolator(sDecelerator).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        performUpdateProfileModel();
                        createEditProfileLayout();
                    }
                });

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        bgAnim.start();

    }

    public void performUpdateProfileModel() {
        Log.e(TAG, "performUpdateProfileModel");
        ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();
        for (int i = 0; i < mGridView.getChildCount(); i++) {
            View view = mGridView.getChildAt(i);
            CameraInfo cameraInfo = cameraInfos.get(i);
            cameraInfo.mLeft = view.getLeft();
            cameraInfo.mTop = view.getTop();
        }
    }

    private void createEditProfileLayout() {
        ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < cameraInfos.size(); i++) {
            //View cameraView = mGridView.getChildAt(i);
            CameraInfo cameraInfo = cameraInfos.get(i);
            View child = inflater.inflate(R.layout.camera_grid_item, mEditProfileLayoutView, false);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cameraInfo.getWidth(), cameraInfo.getHeight());
            layoutParams.leftMargin = cameraInfo.getLeft();
            layoutParams.topMargin = cameraInfo.getTop();
            ImageView imageView = (ImageView)child.findViewById(R.id.camera_view);
            imageView.setImageResource(cameraInfo.getBitmap());
            mEditProfileLayoutView.addView(child, layoutParams);
            final CheckedFrameLayout checkedFrameLayout = (CheckedFrameLayout) child.findViewById(R.id.checked_frame);

            checkedFrameLayout.setOnDragListener(mCameraDragListener);

            checkedFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedFrameLayout.toggle();
                    v.bringToFront();
                }
            });

            checkedFrameLayout.setOnCheckedListener(new CheckedFrameLayout.OnCheckedListener() {
                @Override
                public void onChecked(CheckedFrameLayout checkedView, boolean checked) {
                    if (checked == false) {
                        checkedView.setOnTouchListener(null);
                        checkedView.setOnDragListener(mCameraDragListener);
                        if (mCurrentChecked == checkedView) {
                            mCurrentChecked = null;
                        }
                    }
                    if (mCurrentChecked != checkedView && checked) {
                        if (mCurrentChecked != null) {
                            mCurrentChecked.setChecked(false);
                            //mCurrentChecked.setOnDragListener(mCameraDragListener);
                        }
                        mCurrentChecked = checkedView;
                        Log.e(TAG, "current checked: " +mCurrentChecked.toString());

                        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getActivity(), new ScaleListener(mCurrentChecked));

//                        mCurrentChecked.setOnTouchListener(new View.OnTouchListener() {
//                            @Override
//                            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                                Log.e(TAG, "onTouch............" + motionEvent.getPointerCount() + " : " + motionEvent.getAction());
//
//                                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                                    ClipData data = ClipData.newPlainText("", "");
//                                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//                                    view.startDrag(data, shadowBuilder, view, 0);
//                                    view.setVisibility(View.INVISIBLE);
//                                    return true;
//                                } else {
//                                    return false;
//                                }
//                            }
//                        });

                        mCurrentChecked.setOnDragListener(null);
                    }
                }
            });
            mGridView.setVisibility(View.GONE);
        }
    }

    private View.OnDragListener mFrameDragListener = new View.OnDragListener() {

        float mStartX = 0;
        float mStartY = 0;

        @Override
        public boolean onDrag(View v, DragEvent event) {

            int action = event.getAction();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    // do nothing
                    mStartX = event.getX();
                    mStartY = event.getY();

                    Log.e(TAG, "ACTION_DRAG_STARTED................. x: " + mStartX);
                    Log.e(TAG, "ACTION_DRAG_STARTED................. y: " + mStartY);


                }
                case DragEvent.ACTION_DRAG_ENTERED:
//                    mStartX = event.getX();
//                    mStartY = event.getY();

                    Log.e(TAG, "ACTION_DRAG_ENTERED................. x: " + mStartX);
                    Log.e(TAG, "ACTION_DRAG_ENTERED................. y: " + mStartY);

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.e(TAG, "ACTION_DRAG_EXITED ");

                    break;
                case DragEvent.ACTION_DROP:
                    if (v.getId() == R.id.edit_container) {
                        View view = (View) event.getLocalState();
                        Log.e(TAG, "ACTION_DROP................. x: " + event.getX());
                        Log.e(TAG, "ACTION_DROP................. y: " + event.getY());
                        view.setX(event.getX() - view.getWidth() / 2);
                        view.setY(event.getY() - view.getHeight() / 2);
                        view.bringToFront();
                        view.setVisibility(View.VISIBLE);
                        //mEditProfileLayoutView.getParent().requestDisallowInterceptTouchEvent(false);
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.e(TAG, "ACTION_DRAG_ENDED ");
                    mStartX = 0;
                    mStartY = 0;
                    View view = (View) event.getLocalState();
                    view.bringToFront();
                    view.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private View.OnDragListener mCameraDragListener = new View.OnDragListener() {

        float mStartX = 0;
        float mStartY = 0;

        float mDeltaX = 0;
        float mDeltaY = 0;

        @Override
        public boolean onDrag(View v, DragEvent event) {

            int action = event.getAction();
            Log.e(TAG, "Camera onDrag.................: " + event.toString());
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    // do nothing
                    mStartX = event.getX();
                    mStartY = event.getY();

                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. x: " + mStartX);
                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. y: " + mStartY);

                    int[] screenLocation = new int[2];
                    v.getLocationOnScreen(screenLocation);
                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. v x: " + screenLocation[0]);
                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. v y: " + screenLocation[1]);

                    break;
                }

                case DragEvent.ACTION_DRAG_LOCATION: {
                    if (mDeltaX != 0) {
                        mDeltaX += event.getX() - mStartX;
                    } else {
                        mDeltaX = event.getX();
                    }
                    if (mDeltaY != 0) {
                        mDeltaY += event.getY() - mStartY;
                    } else {
                        mDeltaY = event.getY();
                    }
                    mStartX = event.getX();
                    mStartY = event.getY();
                    break;
                }

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.e(TAG, "Camera ACTION_DRAG_ENTERED................. x: " + event.getX());
                    Log.e(TAG, "Camera ACTION_DRAG_ENTERED................. y: " + event.getY());
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.e(TAG, "Camera ACTION_DRAG_EXITED ");

                    break;
                case DragEvent.ACTION_DROP:
                    if (v.getId() == R.id.checked_frame) {
                        View view = (View) event.getLocalState();
                        if (view.getWidth() * view.getScaleX() == v.getWidth() * v.getScaleX()) {
                            int[] dropViewScreenLocation = new int[2];
                            v.getLocationOnScreen(dropViewScreenLocation);

                            int[] dragViewScreenLocation = new int[2];
                            view.getLocationOnScreen(dragViewScreenLocation);

                            Log.e(TAG, "Camera ACTION_DROP................. x: " + dropViewScreenLocation[0]);
                            Log.e(TAG, "Camera ACTION_DROP................. y: " + dropViewScreenLocation[1]);

                            float deltaX = dragViewScreenLocation[0] - dropViewScreenLocation[0];
                            float deltaY = dragViewScreenLocation[1] - dropViewScreenLocation[1];

                            v.animate().translationXBy(deltaX).translationYBy(deltaY).withLayer();

                            view.setX(v.getX());
                            view.setY(v.getY());

                            view.bringToFront();
                            view.setVisibility(View.VISIBLE);
                        } else {
                            return false;
                        }

                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.e(TAG, "Camera ACTION_DRAG_ENDED ");
                    mStartX = 0;
                    mStartY = 0;
                    return false;
                    //break;
                default:
                    break;
            }
            return true;
        }
    };

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mScaleFactor;
        private View mView;

        public ScaleListener(View view) {
            mView = view;
            mScaleFactor = 1.0f;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.e(TAG, "..............................");
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));

            mView.setScaleX(mScaleFactor);
            mView.setScaleY(mScaleFactor);

            return true;
        }
    }
}
