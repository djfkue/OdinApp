package com.argonmobile.odinapp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.argonmobile.odinapp.view.ScaleTransformView;


public class TempChosenProfileActivity extends ActionBarActivity {

    private static final String TAG = "TempChosenProfileActivity";

    public static final String MODE_ENABLE_SELECT = "mode_enable_select";

    private static final int ANIM_DURATION = 500;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    private static final String ENABLE_ANIMATION = "enable_animation";
    private static final String PROFILE_NAME = "profile_name";
    private static final String PACKAGE_NAME = "com.argonmobile.odinapp";

    private ScaleTransformView mScaleTransformView;
    private float mScaleFactor = Float.NaN;
    private boolean mEnableSelect;
    private ColorDrawable mBackground;
    private TextView mProfileOne;
    private TextView mProfileTwo;
    private TextView mProfileThree;
    private TextView mProfileFour;
    private TextView mProfileFive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_temp_chosen_profile);

        mEnableSelect = getIntent().getBooleanExtra(MODE_ENABLE_SELECT, true);

        mProfileOne = (TextView) findViewById(R.id.profile_1);
        mProfileOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChosenProfileActivity(mProfileOne);
            }
        });

        mProfileTwo = (TextView) findViewById(R.id.profile_2);
        mProfileTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChosenProfileActivity(mProfileTwo);
            }
        });

        mProfileThree = (TextView) findViewById(R.id.profile_3);
        mProfileThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChosenProfileActivity(mProfileThree);
            }
        });

        mProfileFour = (TextView) findViewById(R.id.profile_4);
        mProfileFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChosenProfileActivity(mProfileFour);
            }
        });

        mProfileFive = (TextView) findViewById(R.id.profile_5);
        mProfileFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChosenProfileActivity(mProfileFive);
            }
        });

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
                    startChosenProfileActivity(mProfileOne);
                } else if (mScaleFactor > 1.1f){
                    Intent intent = new Intent(TempChosenProfileActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            }
        });

        mScaleTransformView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mScaleTransformView.getViewTreeObserver().removeOnPreDrawListener(this);

                runEnterAnimation();

                return true;
            }
        });

        mBackground = new ColorDrawable(Color.BLACK);
        mScaleTransformView.setBackground(mBackground);
    }

    private void runEnterAnimation() {

        final long duration = (long) (ANIM_DURATION);

        int[] viewLocation = new int[2];
        mProfileOne.getLocationOnScreen(viewLocation);
        int deltaLeft = 0 - mProfileOne.getMeasuredWidth() - viewLocation[0];
        int deltaTop = 0 - mProfileOne.getMeasuredHeight() - viewLocation[1];
        mProfileOne.setTranslationX(deltaLeft);
        mProfileOne.setTranslationY(deltaTop);

        mProfileOne.animate().setDuration(duration).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).withLayer().withEndAction(new Runnable() {
            @Override
            public void run() {
                if (mEnableSelect == false) {
                    startChosenProfileActivity(mProfileOne);
                }
            }
        });

        mProfileTwo.getLocationOnScreen(viewLocation);
        deltaLeft = mScaleTransformView.getWidth() - viewLocation[0];
        deltaTop = 0 - mProfileTwo.getMeasuredHeight() - viewLocation[1];
        mProfileTwo.setTranslationX(deltaLeft);
        mProfileTwo.setTranslationY(deltaTop);
        mProfileTwo.animate().setDuration(duration).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).withLayer();

        mProfileThree.getLocationOnScreen(viewLocation);
        deltaLeft = -mProfileThree.getWidth() - viewLocation[0];
        deltaTop = mScaleTransformView.getMeasuredHeight() + mProfileThree.getMeasuredHeight() - viewLocation[1];
        mProfileThree.setTranslationX(deltaLeft);
        mProfileThree.setTranslationY(deltaTop);
        mProfileThree.animate().setDuration(duration).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).withLayer();

        mProfileFour.getLocationOnScreen(viewLocation);
        deltaLeft = 0;
        deltaTop = mScaleTransformView.getMeasuredHeight() + mProfileFour.getMeasuredHeight() - viewLocation[1];
        mProfileFour.setTranslationX(deltaLeft);
        mProfileFour.setTranslationY(deltaTop);
        mProfileFour.animate().setDuration(duration).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).withLayer();

        mProfileFive.getLocationOnScreen(viewLocation);
        deltaLeft = mScaleTransformView.getWidth() - viewLocation[0];
        deltaTop = mScaleTransformView.getMeasuredHeight() + mProfileFive.getMeasuredHeight() - viewLocation[1];
        mProfileFive.setTranslationX(deltaLeft);
        mProfileFive.setTranslationY(deltaTop);
        mProfileFive.animate().setDuration(duration).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).withLayer();

    }

    private void startChosenProfileActivity(TextView view) {
        Intent intent = new Intent(TempChosenProfileActivity.this, ChosenProfileActivity.class);

        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);

        intent.
                putExtra(PACKAGE_NAME + ".left", screenLocation[0]).
                putExtra(PACKAGE_NAME + ".top", screenLocation[1]).
                putExtra(PACKAGE_NAME + ".width", view.getWidth()).
                putExtra(PACKAGE_NAME + ".height", view.getHeight()).
                putExtra(ENABLE_ANIMATION, true).
                putExtra(PROFILE_NAME, view.getText());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_temp_chosen_profile, menu);
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
