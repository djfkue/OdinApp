package com.argonmobile.odinapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.argonmobile.odinapp.view.ScaleTransformView;


public class ChosenProfileActivity extends ActionBarActivity implements ViewProfileFragment.OnFragmentInteractionListener{

    private final static String TAG = "ChosenProfileActivity";

    public static final String ENABLE_ANIMATION = "enable_animation";
    public static final String PROFILE_NAME = "profile_name";
    public static final String PACKAGE_NAME = "com.argonmobile.odinapp";

    private ScaleTransformView mScaleTransformView;

    private float mScaleFactor = Float.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chosen_profile);

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
                if (mScaleFactor < 0.7f) {
                    Intent intent = new Intent(ChosenProfileActivity.this, FindCameraActivity.class);
                    startActivity(intent);
                }
            }
        });

        if (savedInstanceState == null) {
            Fragment fragment = new ViewProfileFragment();
            if (getIntent().getBooleanExtra(ENABLE_ANIMATION, false)) {

                Bundle bundle = getIntent().getExtras();
                final int top = bundle.getInt(PACKAGE_NAME + ".top");
                final int left = bundle.getInt(PACKAGE_NAME + ".left");
                final int width = bundle.getInt(PACKAGE_NAME + ".width");
                final int height = bundle.getInt(PACKAGE_NAME + ".height");

                fragment.setArguments(bundle);
            }
            //ProfileFragment profileFragment = ProfileFragment.newInstance("test", "test2");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choosen_profile, menu);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chosen_profile, container, false);
            return rootView;
        }
    }
}
