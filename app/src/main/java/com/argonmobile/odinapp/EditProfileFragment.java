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
import android.view.LayoutInflater;
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

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mEditProfileLayoutView = (FreeProfileLayoutView) rootView.findViewById(R.id.edit_container);

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
            mGridView.setVisibility(View.GONE);
        }
    }

}
