package com.argonmobile.odinapp.util;

import com.argonmobile.odinapp.model.ScreenStructure;

public class ScaleFactorCaculator {
    public static int getScreenWindowTop(int deviceWindowTop, int deviceScreenWidth, int deviceScreenHeight) {
        return 0;
    }

    public static int getScreenWindowLeft(int deviceWindowLeft, int deviceScreenWidth, int deviceScreenHeight) {
        return 0;
    }

    public static int getScreenWindowWidth(int deviceWindowRight, int deviceScreenWidth, int deviceScreenHeight) {
        return 0;
    }

    public static int getDeviceWindowTop(int screenWindowTop, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = ScreenStructure.getInstance().screenGroups[0].verticalCount * 1080;
        int screenWidth = ScreenStructure.getInstance().screenGroups[0].horizontalCount * 1920;

        return screenWindowTop * deviceScreenHeight / screenHeight;
    }

    public static int getDeviceWindowLeft(int screenWindowLeft, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = ScreenStructure.getInstance().screenGroups[0].verticalCount * 1080;
        int screenWidth = ScreenStructure.getInstance().screenGroups[0].horizontalCount * 1920;

        return screenWindowLeft * deviceScreenWidth / screenWidth;
    }

    public static int getDeviceWindowWidth(int screenWindowWidth, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = ScreenStructure.getInstance().screenGroups[0].verticalCount * 1080;
        int screenWidth = ScreenStructure.getInstance().screenGroups[0].horizontalCount * 1920;

        return screenWindowWidth * deviceScreenWidth / screenWidth;
    }

    public static int getDeviceWindowHeight(int screenWindowHeight, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = ScreenStructure.getInstance().screenGroups[0].verticalCount * 1080;
        int screenWidth = ScreenStructure.getInstance().screenGroups[0].horizontalCount * 1920;

        return screenWindowHeight * deviceScreenHeight / screenHeight;
    }
}
