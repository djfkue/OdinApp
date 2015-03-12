package com.argonmobile.odinapp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.DummyContent;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ViewProfileFragment";
    private static final String ENABLE_ANIMATION = "enable_animation";
    private static final String PROFILE_NAME = "profile_name";
    private static final String PACKAGE_NAME = "com.argonmobile.odinapp";

    private static final int ANIM_DURATION = 380;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    private OnFragmentInteractionListener mListener;

    private ColorDrawable mBackground;
    private boolean mEnableAnimation = false;
    private TextView mTextView;
    private String mProfileName;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mEnableAnimation = getArguments().getBoolean(ENABLE_ANIMATION, false);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_view_profile, container, false);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e(TAG, "onTouch.......");
                return false;
            }
        });
        mBackground = new ColorDrawable(Color.BLACK);
        rootView.setBackground(mBackground);

        mTextView = (TextView) rootView.findViewById(R.id.profile_name);

        if (savedInstanceState == null) {
            if (mEnableAnimation) {
                rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                        runEnterAnimation();
                        return true;
                    }
                });
            }
        }

        return rootView;
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
            mTextView.getLocationOnScreen(screenLocation);
            int leftDelta = left - screenLocation[0];
            int topDelta = top - screenLocation[1];

            // Scale factors to make the large version the same size as the thumbnail
            float widthScale = (float) width / mTextView.getWidth();
            float heightScale = (float) height / mTextView.getHeight();

            mTextView.setPivotX(0);
            mTextView.setPivotY(0);
            mTextView.setScaleX(widthScale);
            mTextView.setScaleY(heightScale);
            mTextView.setTranslationX(leftDelta);
            mTextView.setTranslationY(topDelta);

            // Animate scale and translation to go from thumbnail to full size
            mTextView.animate().setDuration(duration).
                    scaleX(1).scaleY(1).
                    translationX(0).translationY(0).
                    setInterpolator(sDecelerator);

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
