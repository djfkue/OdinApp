package com.argonmobile.odinapp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.argonmobile.odinapp.view.ScaleTransformView;


public class FindCameraActivity extends ActionBarActivity {

    private static final String TAG = "FindCameraActivity";

    private ScaleTransformView mScaleTransformView;
    private float mScaleFactor = Float.NaN;

    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.activity_find_camera);


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
                Log.e(TAG, "onScaleEnd....");
                if (mScaleFactor < 1.0f) {
                    Intent intent = new Intent(FindCameraActivity.this, ChosenProfileActivity.class);
                    startActivity(intent);
                    finishAfterTransition();
                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        FindCameraPageAdapter findCameraPageAdapter = new FindCameraPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(findCameraPageAdapter);
        mViewPager.setCurrentItem(1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_camera, menu);
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
