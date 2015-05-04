package com.argonmobile.odinapp.model;

import com.argonmobile.odinapp.protocol.deviceinfo.WindowInfo;

import java.util.List;

/**
 * Created by argon on 5/4/15.
 */
public class WindowInfoModel {

    private static WindowInfoModel mInstance;

    public List<WindowInfo> windowInfos;

    private WindowInfoModel() {

    }

    public static WindowInfoModel getInstance() {
        if (mInstance == null) {
            mInstance = new WindowInfoModel();
        }
        return mInstance;
    }

}
