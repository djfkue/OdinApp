package com.argonmobile.odinapp.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by argon on 3/12/15.
 */
public class EditProfileModel {

    private static EditProfileModel ourInstance = new EditProfileModel();

    private ArrayList<CameraInfo> mCameraInfoArrayList = new ArrayList<CameraInfo>();

    public static EditProfileModel getInstance() {
        return ourInstance;
    }

    private EditProfileModel() {
    }

    public ArrayList<CameraInfo> getCameraInfoArrayList() {
        return mCameraInfoArrayList;
    }

    public void clearCameraInfoArrayList() {
        mCameraInfoArrayList.clear();
    }

    public void addCameraInfo(CameraInfo cameraInfo) {
        Log.e("TALOS", "addCameraInfo............");
        mCameraInfoArrayList.add(cameraInfo);
    }

    public void removeCameraInfo(int id) {
        Iterator<CameraInfo> iterator = mCameraInfoArrayList.iterator();
        while(iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
            }
        }
    }
}
