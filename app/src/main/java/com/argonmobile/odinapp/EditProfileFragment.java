package com.argonmobile.odinapp;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.EditProfileCameraAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
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

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.grid_view);
        mEditProfileCameraAdapter = new EditProfileCameraAdapter(rootView.getContext());
        mGridView.setAdapter(mEditProfileCameraAdapter);

        if (savedInstanceState == null) {
            mGridView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    mGridView.getViewTreeObserver().removeOnPreDrawListener(this);

                    runEnterAnimation();

                    return true;
                }
            });
        }

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
                mTextView.animate().setDuration(duration/2).
                        translationY(0).alpha(1).
                        setInterpolator(sDecelerator);
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


}
