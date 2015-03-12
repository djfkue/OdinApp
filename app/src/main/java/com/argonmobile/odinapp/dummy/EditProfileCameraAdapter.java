package com.argonmobile.odinapp.dummy;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.argonmobile.odinapp.R;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.view.CheckedFrameLayout;

public class EditProfileCameraAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private Context mContext;

    private SparseBooleanArray mCheckStates;
    private int mCheckedItemCount;

    public EditProfileCameraAdapter(Context c) {
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
        return EditProfileModel.getInstance().getCameraInfoArrayList().size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        CameraInfo cameraInfo = EditProfileModel.getInstance().getCameraInfoArrayList().get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.camera_grid_item, parent, false);
        }
        //final float scale = mContext.getResources().getDisplayMetrics().density;
        //convertView.setLayoutParams(new GridView.LayoutParams((int)(220 * scale), (int)(135 * scale)));

        CheckedFrameLayout checkedLayout = (CheckedFrameLayout) convertView.findViewById(R.id.checked_frame);
        checkedLayout.setChecked(isItemChecked(position));

        ImageView imageView = (ImageView) convertView.findViewById(R.id.camera_view);

        imageView.setImageResource(cameraInfo.getBitmap());
        return convertView;
    }
}
