package com.argonmobile.odinapp;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.ChosenProfileImageAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ViewProfileFragment";
    public static final String ENABLE_ANIMATION = "enable_animation";
    public static final String PROFILE_NAME = "profile_name";
    public static final String PROFILE_ID = "profile_id";
    public static final String PACKAGE_NAME = "com.argonmobile.odinapp";

    private static final int ANIM_DURATION = 380;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    private OnFragmentInteractionListener mListener;

    private ColorDrawable mBackground;
    private boolean mEnableAnimation = false;
    private String mProfileName;

    private ChosenProfileImageAdapter mImageAdapter;
    private GridView gridview;
    private TextView mTextView;
    private View mRootView;

    private int mProfileId;

    private RelativeLayout mEditProfileLayoutView;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mEnableAnimation = getArguments().getBoolean(ENABLE_ANIMATION, false);
            mProfileId = getArguments().getInt(PROFILE_ID, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = null;
        if (!mEnableAnimation) {
            mRootView = inflater.inflate(R.layout.fragment_view_profile_default, container, false);

            gridview = (GridView) mRootView.findViewById(R.id.grid_view);

            mImageAdapter = new ChosenProfileImageAdapter(mRootView.getContext());
            gridview.setAdapter(mImageAdapter);
        } else {
            mRootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
            mEditProfileLayoutView = (RelativeLayout) mRootView.findViewById(R.id.camera_container);
            if (mProfileId == 0) {
                createProfileLayout();
            }
        }

        mTextView = (TextView) mRootView.findViewById(R.id.title_view);

        mBackground = new ColorDrawable(Color.BLACK);
        mRootView.setBackground(mBackground);

        if (mProfileId != 0 && mEnableAnimation) {
            mTextView.setVisibility(View.GONE);
            mRootView.setBackgroundResource(mProfileId);
        }

        if (savedInstanceState == null) {
            if (mEnableAnimation) {
                mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                        runEnterAnimation();
                        return true;
                    }
                });
            }
        }

        return mRootView;
    }

    private void createProfileLayout() {
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
        }
    }

    private void runEnterAnimation() {
        if (getArguments() != null) {
            Log.e(TAG, "runEnterAnimation...........");
            final long duration = (long) (ANIM_DURATION);

            final int top = getArguments().getInt(PACKAGE_NAME + ".top");
            final int left = getArguments().getInt(PACKAGE_NAME + ".left");
            final int width = getArguments().getInt(PACKAGE_NAME + ".width");
            final int height = getArguments().getInt(PACKAGE_NAME + ".height");
            Log.e(TAG, "runEnterAnimation........... top: " + top);
            mProfileName = getArguments().getString(PROFILE_NAME);
            mTextView.setText(mProfileName);
            // Figure out where the thumbnail and full size versions are, relative
            // to the screen and each other
            int[] screenLocation = new int[2];
            getView().getLocationOnScreen(screenLocation);
            int leftDelta = left - screenLocation[0];
            int topDelta = top - screenLocation[1];

            // Scale factors to make the large version the same size as the thumbnail
            float widthScale = (float) width / getView().getWidth();
            float heightScale = (float) height / getView().getHeight();

            getView().setPivotX(0);
            getView().setPivotY(0);
            getView().setScaleX(widthScale);
            getView().setScaleY(heightScale);
            getView().setTranslationX(leftDelta);
            getView().setTranslationY(topDelta);

            // Animate scale and translation to go from thumbnail to full size
            getView().animate().setDuration(duration).
                    scaleX(1).scaleY(1).
                    translationX(0).translationY(0).
                    setInterpolator(sDecelerator).withLayer();

        }
    }

    public void performUpdateProfileModel() {
        if (!mEnableAnimation) {
            for (int i = 0; i < gridview.getChildCount(); i++) {
                View view = gridview.getChildAt(i);
                int[] screenLocation = new int[2];
                view.getLocationOnScreen(screenLocation);
                CameraInfo cameraInfo = new CameraInfo(view.getTop(), view.getLeft(), view.getWidth(), view.getHeight(), i, mImageAdapter.mThumbIds[i]);
                EditProfileModel.getInstance().addCameraInfo(cameraInfo);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
