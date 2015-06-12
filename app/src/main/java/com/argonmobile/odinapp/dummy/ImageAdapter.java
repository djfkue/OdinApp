package com.argonmobile.odinapp.dummy;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.argonmobile.odinapp.R;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.view.CheckedFrameLayout;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private Context mContext;

    private SparseBooleanArray mCheckStates;
    private int mCheckedItemCount;

    public ImageAdapter(Context c) {
        mContext = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCheckedItemCount = 0;
        mCheckStates = new SparseBooleanArray();
    }

    public boolean isItemChecked(int position) {
        return mCheckStates.get(position);
    }

    public void toggleItemChecked(int position) {
        boolean oldValue = mCheckStates.get(position);
        mCheckStates.put(position, !oldValue);
        if (oldValue) {
            mCheckedItemCount--;
        } else {
            mCheckedItemCount++;
        }
        notifyDataSetChanged();
    }

    public SparseBooleanArray getCheckedItemPositions() {
        return mCheckStates;
    }

    public int getCheckedItemCount() {
        return mCheckedItemCount;
    }

    public boolean isAllItemChecked() {
        return mCheckedItemCount == getCount();
    }

    public boolean hasItemChecked() {
        return mCheckedItemCount > 0;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.camera_grid_item, parent, false);
        }
        //final float scale = mContext.getResources().getDisplayMetrics().density;
        //convertView.setLayoutParams(new GridView.LayoutParams((int)(220 * scale), (int)(135 * scale)));

        boolean shouldBeChecked = false;
        ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();

        for (CameraInfo cameraInfo : cameraInfos) {
            if (cameraInfo.getId() == position) {
                shouldBeChecked = true;
                break;
            }
        }

        CheckedFrameLayout checkedLayout = (CheckedFrameLayout) convertView.findViewById(R.id.checked_frame);
        checkedLayout.setChecked(isItemChecked(position) || shouldBeChecked);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.camera_view);

        imageView.setImageResource(mThumbIds[position]);
        return convertView;
    }

    // references to our images
    public Integer[] mThumbIds = {
            R.drawable.sample_1, R.drawable.sample_2,
            R.drawable.sample_4, R.drawable.sample_3,
            R.drawable.sample_1, R.drawable.sample_2,
            R.drawable.sample_4, R.drawable.sample_3,
            R.drawable.sample_1, R.drawable.sample_2,
            R.drawable.sample_4, R.drawable.sample_3,
            R.drawable.sample_1, R.drawable.sample_2,
            R.drawable.sample_4, R.drawable.sample_3,
            R.drawable.sample_1, R.drawable.sample_2,
            R.drawable.sample_4, R.drawable.sample_3,
//            R.drawable.sample_6, R.drawable.sample_7
    };
}
