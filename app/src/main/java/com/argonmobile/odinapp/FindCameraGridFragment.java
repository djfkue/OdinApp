package com.argonmobile.odinapp;


import android.animation.TimeInterpolator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.ImageAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindCameraGridFragment extends Fragment {

    private static final String TAG = "FindCameraGridFragment";
//    private static final int ANIM_DURATION = 500;
//
//    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
//    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

//    private OnExitAnimationListener mOnExitAnimationListner;

    private ImageAdapter mImageAdapter;
    private GridView gridview;
    private TextView mTextView;

    public FindCameraGridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_camera_grid, container, false);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);

        mImageAdapter = new ImageAdapter(rootView.getContext());

        gridview.setAdapter(mImageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mImageAdapter.toggleItemChecked(position);
                if (mImageAdapter.isItemChecked(position)) {
                    CameraInfo cameraInfo = new CameraInfo(view.getTop(), view.getLeft(), view.getWidth(), view.getHeight(), position, mImageAdapter.mThumbIds[position]);
                    EditProfileModel.getInstance().addCameraInfo(cameraInfo);
                } else {
                    EditProfileModel.getInstance().removeCameraInfo(position);
                }
            }
        });

        mTextView = (TextView) rootView.findViewById(R.id.title_view);

        return rootView;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mOnExitAnimationListner = (OnExitAnimationListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnExitAnimationListener");
//        }
//    }
//
//    public void runExitAnimation() {
//
//        final long duration = (long) (ANIM_DURATION);
//
//        ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();
//        for (CameraInfo cameraInfo : cameraInfos) {
//            View childView = gridview.getChildAt(cameraInfo.getId());
//            int deltaLeft = cameraInfo.getLeft() - childView.getLeft();
//
//            int deltaTop = cameraInfo.getTop() - childView.getTop();
//            Log.e(TAG, "animate deltaTop: " + deltaTop);
//            Log.e(TAG, "animate deltaLeft: " + deltaLeft);
//            childView.animate().setDuration(duration)
//                    .translationY(deltaTop)
//                    .translationX(deltaLeft)
//                    .setInterpolator(sAccelerator).withLayer();
//        }
//        mTextView.animate().setDuration(duration).
//                translationY(mTextView.getHeight()).alpha(0).
//                setInterpolator(sDecelerator).withEndAction(new Runnable() {
//            @Override
//            public void run() {
//                if (mOnExitAnimationListner != null) {
//                    mOnExitAnimationListner.onExitAnimationFinish();
//                }
//            }
//        });
//    }
//
//    public interface OnExitAnimationListener {
//        public void onExitAnimationFinish();
//    }

}
