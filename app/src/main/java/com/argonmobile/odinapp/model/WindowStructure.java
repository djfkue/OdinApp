package com.argonmobile.odinapp.model;

import com.argonmobile.odinapp.protocol.deviceinfo.ScreenGroup;

public class WindowStructure {

    private static WindowStructure mInstance;
    public ScreenGroup[] screenGroups;

    private WindowStructure() {

    }

    static public WindowStructure getInstance() {
        if (mInstance == null) {
            mInstance = new WindowStructure();
        }
        return mInstance;
    }

}
