package com.argonmobile.odinapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by argon on 3/12/15.
 */
public class CameraInfo implements Parcelable {

    private int mTop;
    private int mLeft;
    private int mWidth;
    private int mHeight;

    private int mId;
    private int mBitmap;

    public CameraInfo(int top, int left, int width, int height, int id, int bitmap) {
        mTop = top;
        mLeft = left;
        mWidth = width;
        mHeight = height;
        mId = id;
        mBitmap = bitmap;
    }

    public CameraInfo(Parcel in){
        int[] data = new int[5];

        in.readIntArray(data);
        this.mTop = data[0];
        this.mLeft = data[1];
        this.mWidth = data[2];
        this.mHeight = data[3];
        this.mId = data[4];
        this.mBitmap = data[5];
    }

    public int getId() {
        return mId;
    }
    public int getWidth() {
        return mWidth;
    }
    public int getHeight() {
        return mHeight;
    }

    public int getLeft() {
        return mLeft;
    }

    public int getTop() {
        return mTop;
    }

    public int getBitmap() {
        return mBitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[] {this.mTop, this.mLeft, this.mWidth, this.mHeight, this.mId, this.mBitmap});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CameraInfo createFromParcel(Parcel in) {
            return new CameraInfo(in);
        }

        public CameraInfo[] newArray(int size) {
            return new CameraInfo[size];
        }
    };
}
