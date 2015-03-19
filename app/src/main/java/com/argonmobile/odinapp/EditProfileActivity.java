package com.argonmobile.odinapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.argonmobile.odinapp.view.ScaleTransformView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditProfileActivity extends ActionBarActivity {

    private static final String TAG = "EditProfileActivity";

    private ScaleTransformView mScaleTransformView;
    private float mScaleFactor = Float.NaN;

    private ViewPager mViewPager;
    private EditProfilePageAdapter mEditProfilePageAdapter;
    private File pictureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_profile);

        mScaleTransformView = (ScaleTransformView)findViewById(R.id.gesture_view);
        mScaleTransformView.setOnScaleListener(new ScaleTransformView.OnScaleListener() {


            @Override
            public void onScaleBegin() {
                mScaleFactor = 1.0f;
            }

            @Override
            public void onScale(float scaleFactor) {
                mScaleFactor *= scaleFactor;
            }

            @Override
            public void onScaleEnd() {
                Log.e(TAG, "onScaleEnd....: " + mScaleFactor);
                if (mViewPager.getCurrentItem() != 1) {
                    return;
                }
                if (mScaleFactor < 1.0f && mScaleFactor > 0.7f) {
                    Intent intent = new Intent(EditProfileActivity.this, TempChosenProfileActivity.class);
                    View view = mEditProfilePageAdapter.getRegisteredFragment(mViewPager.getCurrentItem()).getView();
                    view.setDrawingCacheBackgroundColor(Color.BLACK);
                    view.setDrawingCacheEnabled(true);
                    view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
                    view.buildDrawingCache();
                    Bitmap cache = view.getDrawingCache(true);
                    storeImage(cache);
                    intent.putExtra(TempChosenProfileActivity.MODE_ENABLE_SELECT, true);
                    intent.putExtra("TEST_BITMAP", pictureFile.getAbsolutePath());
                    startActivity(intent);
                } else if (mScaleFactor > 1.1f){
                    Intent intent = new Intent(EditProfileActivity.this, FindCameraActivity.class);
                    startActivity(intent);
                } else if (mScaleFactor < 0.7f) {
                    Intent intent = new Intent(EditProfileActivity.this, TempChosenProfileActivity.class);
                    View view = mEditProfilePageAdapter.getRegisteredFragment(mViewPager.getCurrentItem()).getView();
                    view.setDrawingCacheBackgroundColor(Color.BLACK);
                    view.setDrawingCacheEnabled(true);
                    view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
                    view.buildDrawingCache();
                    Bitmap cache = view.getDrawingCache(true);
                    storeImage(cache);
                    intent.putExtra(TempChosenProfileActivity.MODE_ENABLE_SELECT, false);
                    intent.putExtra("TEST_BITMAP", pictureFile.getAbsolutePath());
                    startActivity(intent);
                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mEditProfilePageAdapter = new EditProfilePageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mEditProfilePageAdapter);
        mViewPager.setCurrentItem(1);
    }

    private void storeImage(Bitmap image) {
        pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            int oldWidth = image.getWidth();
            int oldHeight = image.getHeight();
            int newWidth = 760;
            int newHeight = 480;

            float scaleWidth = ((float) newWidth) / oldWidth;
            float scaleHeight = ((float) newHeight) / oldHeight;

            Matrix matrix = new Matrix();
            FileOutputStream fos = new FileOutputStream(pictureFile);
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap _bitmapScaled = Bitmap.createBitmap(image, 0, 0,  oldWidth, oldHeight, matrix, true);
            _bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 40, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
