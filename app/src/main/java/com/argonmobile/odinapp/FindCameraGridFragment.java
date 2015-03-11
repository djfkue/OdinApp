package com.argonmobile.odinapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.argonmobile.odinapp.dummy.ImageAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindCameraGridFragment extends Fragment {


    public FindCameraGridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_camera_grid, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.grid_view);
        gridview.setAdapter(new ImageAdapter(rootView.getContext()));
        return rootView;
    }

}
