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


public class EditProfileActivity extends ActionBarActivity {

    private static final String TAG = "EditProfileActivity";

    private ScaleTransformView mScaleTransformView;
    private float mScaleFactor = Float.NaN;

    private ViewPager mViewPager;
    private EditProfilePageAdapter mEditProfilePageAdapter;

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
                Log.e(TAG, "onScaleEnd....");
                if (mViewPager.getCurrentItem() != 1) {
                    return;
                }
                if (mScaleFactor < 1.0f && mScaleFactor > 0.7f) {
                    Intent intent = new Intent(EditProfileActivity.this, TempChosenProfileActivity.class);
                    intent.putExtra(TempChosenProfileActivity.MODE_ENABLE_SELECT, true);
                    startActivity(intent);
                } else if (mScaleFactor > 1.1f){
                    Intent intent = new Intent(EditProfileActivity.this, FindCameraActivity.class);
                    startActivity(intent);
                } else if (mScaleFactor < 0.7f) {
                    Intent intent = new Intent(EditProfileActivity.this, TempChosenProfileActivity.class);
                    intent.putExtra(TempChosenProfileActivity.MODE_ENABLE_SELECT, false);
                    startActivity(intent);
                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mEditProfilePageAdapter = new EditProfilePageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mEditProfilePageAdapter);
        mViewPager.setCurrentItem(1);
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
