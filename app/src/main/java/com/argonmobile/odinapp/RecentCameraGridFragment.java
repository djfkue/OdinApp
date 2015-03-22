package com.argonmobile.odinapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.ImageAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentCameraGridFragment extends Fragment {


    private ImageAdapter mImageAdapter;
    private GridView gridview;
    private TextView mTextView;

    public RecentCameraGridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recent_camera_grid, container, false);
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


}
