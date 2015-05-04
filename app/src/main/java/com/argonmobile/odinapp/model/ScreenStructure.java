package com.argonmobile.odinapp.model;

import com.argonmobile.odinapp.protocol.deviceinfo.ScreenGroup;

public class ScreenStructure {

    private static ScreenStructure mInstance;
    public ScreenGroup[] screenGroups;

    private ScreenStructure() {

    }

    static public ScreenStructure getInstance() {
        if (mInstance == null) {
            mInstance = new ScreenStructure();
        }
        return mInstance;
    }

}
