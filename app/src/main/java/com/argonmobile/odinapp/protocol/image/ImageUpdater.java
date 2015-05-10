package com.argonmobile.odinapp.protocol.image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created by sean on 4/22/15.
 */
public class ImageUpdater {
    private final static String TAG = "ImageUpdater";

    public final Handler handler;
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == ImageProcessor.MSG_ON_REC_IMAGE) {
                // on received image from
                updateImage(msg.arg1, (Bitmap)msg.obj);
            }
            return true;
        }
    };
    private SparseArray<ArrayList<SoftReference<ImageView>>> imageViewMap;

    public ImageUpdater() {
        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("ImageUpdater must be instantiated from main thread!");
        }
        handler = new Handler(callback);
        imageViewMap = new SparseArray<ArrayList<SoftReference<ImageView>>>();
    }

    public void subscribe(int planOrSignalIndex, ImageView imageView) {
        synchronized (imageViewMap) {
            ArrayList<SoftReference<ImageView>> imageViews = imageViewMap.get(planOrSignalIndex);
            if(imageViews == null) imageViews = new ArrayList<SoftReference<ImageView>>();
            imageViews.add(new SoftReference<ImageView>(imageView));
            imageViewMap.put(planOrSignalIndex, imageViews);
        }
    }

    public void unsubscribe(int planOrSignalIndex, ImageView imageView) {
        synchronized (imageViewMap) {
            ArrayList<SoftReference<ImageView>> imageViews = imageViewMap.get(planOrSignalIndex);
            if(imageViews == null) return;
            for(SoftReference<ImageView> srImageView : imageViews) {
                if(srImageView.get() != null && srImageView.get() == imageView) {
                    imageViews.remove(srImageView);
                    return;
                }
            }
            if(imageViews.size() == 0) {
                imageViewMap.remove(planOrSignalIndex);
            }
        }
    }

    private void updateImage(int planOrSignalIndex, Bitmap bitmap) {
        ArrayList<SoftReference<ImageView>> imageViews = null;
        synchronized (imageViewMap) {
            imageViews = imageViewMap.get(planOrSignalIndex);
            if(imageViews == null || imageViews.size() == 0) {
                imageViewMap.remove(planOrSignalIndex);
                //imageViewMap.removeAt(planOrSignalIndex);
                bitmap.recycle();
            } else {
                boolean usingImage = false;
                for(SoftReference<ImageView> srImageView : imageViews) {
                    if(srImageView.get() != null) {
                        usingImage = true;
                        srImageView.get().setImageBitmap(bitmap);
                    }
                }
                if(!usingImage) {
                    imageViewMap.remove(planOrSignalIndex);
                    bitmap.recycle();
                }
            }
        }

    }
}
