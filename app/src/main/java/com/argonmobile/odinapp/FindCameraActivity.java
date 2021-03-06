package com.argonmobile.odinapp;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.view.ScaleTransformView;


public class FindCameraActivity extends ActionBarActivity {

    private static final String TAG = "FindCameraActivity";
    public static final String CAMERA_INFO_LIST = "CAMERA_INFO_LIST";

    public static boolean sClearInput = false;

    private ScaleTransformView mScaleTransformView;
    private float mScaleFactor = Float.NaN;

    private ViewPager mViewPager;
    private FindCameraPageAdapter mFindCameraPageAdapter;

    private PopupMenu mPopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.activity_find_camera);

        sClearInput = getIntent().getBooleanExtra("CLEAR_INPUT", false);

        if (sClearInput) {
            Log.e("TD_TRACE", "clear input infos......");
            EditProfileModel.getInstance().clearCameraInfoArrayList();
        }

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
                if (mScaleFactor < 0.9f) {
                    Intent intent = new Intent(FindCameraActivity.this, EditProfileActivity.class);
                    //Bundle bundle = new Bundle();
                    //bundle.putParcelableArrayList(CAMERA_INFO_LIST, EditProfileModel.getInstance().getCameraInfoArrayList());
//                    intent.putExtra(CAMERA_INFO_LIST,bundle);
                    //intent.putExtras(bundle);
                    startActivity(intent);

                    // Override transitions: we don't want the normal window animation in addition
                    // to our custom one
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mFindCameraPageAdapter = new FindCameraPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFindCameraPageAdapter);
        mViewPager.setCurrentItem(1);

        ImageButton popupButton = (ImageButton) findViewById(R.id.popup_button);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu.show();
            }
        });

        mPopupMenu = new PopupMenu(this, popupButton);
        mPopupMenu.inflate(R.menu.menu_find_camera);

        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(FindCameraActivity.this, EditProfileActivity.class);
                startActivity(intent);

                // Override transitions: we don't want the normal window animation in addition
                // to our custom one
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_find_camera, menu);
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
