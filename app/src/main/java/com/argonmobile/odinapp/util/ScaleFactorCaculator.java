package com.argonmobile.odinapp.util;

import android.util.Log;

import com.argonmobile.odinapp.model.ScreenStructure;

public class ScaleFactorCaculator {

    public static final int DIVIDE_SCREEN_HEIGHT = 1080;
    public static final int DIVIDE_SCREEN_WIDTH = 1920;

    public static int getScreenHeight() {
        return ScreenStructure.getInstance().screenGroups[0].verticalCount * DIVIDE_SCREEN_HEIGHT;
    }

    public static int getScreenWidth() {
        return ScreenStructure.getInstance().screenGroups[0].horizontalCount * DIVIDE_SCREEN_WIDTH;
    }

    public static int getScreenWindowTop(int deviceWindowTop, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = getScreenHeight();

        return deviceWindowTop * screenHeight / deviceScreenHeight;
    }

    public static int getScreenWindowLeft(int deviceWindowLeft, int deviceScreenWidth, int deviceScreenHeight) {
        int screenWidth = getScreenWidth();
        return deviceWindowLeft * screenWidth / deviceScreenWidth;
    }

    public static int getScreenWindowWidth(int deviceWindowWidth, int deviceScreenWidth, int deviceScreenHeight) {
        int screenWidth = getScreenWidth();
        return deviceWindowWidth * screenWidth / deviceScreenWidth;
    }

    public static int getScreenWindowHeight(int deviceWindowHeight, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = getScreenHeight();
        return deviceWindowHeight * screenHeight / deviceScreenHeight;
    }

    public static int getDeviceWindowTop(int screenWindowTop, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = getScreenHeight();
        int screenWidth = getScreenWidth();

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

        float scaleFactor = deviceScreenWidth * 1.0f / screenWidth;

        Log.e("TD_TRACE", "scaleFactor: " + scaleFactor);
        return (int) (screenWindowWidth * scaleFactor);
    }

    public static int getDeviceWindowHeight(int screenWindowHeight, int deviceScreenWidth, int deviceScreenHeight) {
        int screenHeight = ScreenStructure.getInstance().screenGroups[0].verticalCount * 1080;
        int screenWidth = ScreenStructure.getInstance().screenGroups[0].horizontalCount * 1920;

        return screenWindowHeight * deviceScreenHeight / screenHeight;
    }
}
