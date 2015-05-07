package com.argonmobile.odinapp;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.ChosenProfileImageAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.model.ScreenStructure;
import com.argonmobile.odinapp.model.WindowInfoModel;
import com.argonmobile.odinapp.protocol.command.CloseWindowCommand;
import com.argonmobile.odinapp.protocol.command.Command;
import com.argonmobile.odinapp.protocol.command.CommandDefs;
import com.argonmobile.odinapp.protocol.command.CreateWindowCommand;
import com.argonmobile.odinapp.protocol.command.GetInputInfoResponse;
import com.argonmobile.odinapp.protocol.command.GetPlanWindowListResponse;
import com.argonmobile.odinapp.protocol.command.MoveWindowCommand;
import com.argonmobile.odinapp.protocol.command.Notification;
import com.argonmobile.odinapp.protocol.command.Request;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.connection.CommandListener;
import com.argonmobile.odinapp.protocol.connection.ConnectionManager;
import com.argonmobile.odinapp.protocol.connection.ControlConnection;
import com.argonmobile.odinapp.protocol.deviceinfo.InputInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.ScreenGroup;
import com.argonmobile.odinapp.protocol.deviceinfo.WindowInfo;
import com.argonmobile.odinapp.protocol.image.ImageUpdater;
import com.argonmobile.odinapp.util.ScaleFactorCaculator;
import com.argonmobile.odinapp.view.FreeProfileLayoutView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ViewProfileFragment extends Fragment {

    private static final String TAG = "ViewProfileFragment";
    public static final String ENABLE_ANIMATION = "enable_animation";
    public static final String PROFILE_NAME = "profile_name";
    public static final String PROFILE_ID = "profile_id";
    public static final String PACKAGE_NAME = "com.argonmobile.odinapp";

    private static final int ANIM_DURATION = 380;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    private static final int MSG_ON_GET_PLAN_WINDOW_LIST = 0x01;

    private OnFragmentInteractionListener mListener;

    private ColorDrawable mBackground;
    private boolean mEnableAnimation = false;
    private String mProfileName;

    private ChosenProfileImageAdapter mImageAdapter;
    private GridView gridview;
    private TextView mTextView;
    private View mRootView;

    private int mProfileId;

    private ImageUpdater imageUpdater;

    private FreeProfileLayoutView mEditProfileLayoutView;
    private ArrayList<WindowInfo> mWindowInfos = new ArrayList<>();
    private Handler mHandler;

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == MSG_ON_GET_PLAN_WINDOW_LIST) {
                mWindowInfos.clear();
                List<WindowInfo> windowInfos = (List<WindowInfo>) msg.obj;

                Log.e(TAG, "============== getCount: " + windowInfos.size());
                for (WindowInfo windowInfo : windowInfos) {
                    mWindowInfos.add(windowInfo);
                }

                updateWindowInfos();

                //gridview.setAdapter(mInputAdapter);
                //updateInputInfoAdapter();
            }
            return true;
        }
    };

    private void updateWindowInfos() {

        if (mEditProfileLayoutView != null && getActivity() != null) {
            mEditProfileLayoutView.removeAllViews();
            imageUpdater = new ImageUpdater();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (WindowInfo windowInfo : mWindowInfos) {
                int windowTop = windowInfo.top;
                int windowLeft = windowInfo.left;
                int windowWidth = windowInfo.width;
                int windowHeight = windowInfo.height;

                ScreenGroup screenGroup = ScreenStructure.getInstance().screenGroups[0];
                float screenWidth = screenGroup.horizontalCount * 1920.0f;
                float screenHeight = screenGroup.verticalCount * 1080.0f;

                int height = mEditProfileLayoutView.getMeasuredHeight();
                int width = (int) (height * ( screenWidth / screenHeight ));

                int deviceWindowTop = ScaleFactorCaculator.getDeviceWindowTop(windowTop,
                        mEditProfileLayoutView.getMeasuredWidth(),
                        mEditProfileLayoutView.getMeasuredHeight());
                int deviceWindowLeft = ScaleFactorCaculator.getDeviceWindowLeft(windowLeft,
                        width,
                        mEditProfileLayoutView.getMeasuredHeight());

                Log.e(TAG, "screenWindowWidth: " + windowWidth);
                Log.e(TAG, "deviceScreenWidth: " + mEditProfileLayoutView.getMeasuredWidth());
                int deviceWindowWidth = ScaleFactorCaculator.getDeviceWindowWidth(windowWidth,
                        width,
                        mEditProfileLayoutView.getMeasuredHeight());

                Log.e(TAG, "deviceWindowWidth: " + deviceWindowWidth);

                int deviceWindowHeight = ScaleFactorCaculator.getDeviceWindowHeight(windowHeight,
                        mEditProfileLayoutView.getMeasuredWidth(),
                        mEditProfileLayoutView.getMeasuredHeight());

                View child = inflater.inflate(R.layout.camera_grid_item, mEditProfileLayoutView, false);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(deviceWindowWidth, deviceWindowHeight);
                layoutParams.leftMargin = deviceWindowLeft;
                layoutParams.topMargin = deviceWindowTop;
                layoutParams.width = deviceWindowWidth;
                layoutParams.height = deviceWindowHeight;

                ImageView imageView = (ImageView) child.findViewById(R.id.camera_view);
                imageView.setImageResource(R.drawable.sample_0);

                mEditProfileLayoutView.addView(child, layoutParams);

                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ControlInputActivity.class);
                        startActivity(intent);
                    }
                });

                imageUpdater.subscribe(windowInfo.inputIndex, imageView);
                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
                        (short) 240, (short) 180, new byte[]{(byte) windowInfo.inputIndex});

                CameraInfo cameraInfo = new CameraInfo(deviceWindowTop,
                        deviceWindowLeft,
                        deviceWindowWidth,
                        deviceWindowHeight,
                        windowInfo.inputIndex,
                        R.drawable.sample_0);
                EditProfileModel.getInstance().addCameraInfo(cameraInfo);
//
//                imageUpdater.subscribe(0, imageView);
//                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
//                        (short) 480, (short) 270, new byte[]{0});
            }
        }
    }

    CommandListener commandListener = new CommandListener() {
        @Override
        public void onSentCommand(Command cmd) {
            Log.i(TAG, "onSentCommand:" + cmd.command);
        }

        @Override
        public void onReceivedCommand(Command cmd) {
            Log.i(TAG, "onReceivedCommand:" + cmd);
            if(cmd instanceof GetPlanWindowListResponse) {

                GetPlanWindowListResponse r = (GetPlanWindowListResponse) cmd;
                Log.i(TAG, "GetPlanWindowListResponse list sg window count:" + r.windowCount);
                WindowInfoModel.getInstance().windowInfos = r.windowInfos;


                Message message = new Message();
                message.what = MSG_ON_GET_PLAN_WINDOW_LIST;

                message.obj = r.windowInfos;

                mHandler.sendMessage(message);

                for (WindowInfo ws : r.windowInfos) {
                    Log.i(TAG, "get plan window list sg:" + ws);
                }

//                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
//                        (short)480, (short)270, new byte[]{0x00});
            }

            if (cmd instanceof MoveWindowCommand) {
                ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
                {
                    Request req = RequestFactory.createGetPlanWindowListRequest();
                    con.sendCommand(req);
                }
            }

            if (cmd instanceof CreateWindowCommand || cmd instanceof CloseWindowCommand) {
                ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
                {
                    Request req = RequestFactory.createGetPlanWindowListRequest();
                    con.sendCommand(req);
                }

            }
        }
    };


    public ViewProfileFragment() {
        // Required empty public constructor
        mHandler = new Handler(callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mEnableAnimation = getArguments().getBoolean(ENABLE_ANIMATION, false);
            mProfileId = getArguments().getInt(PROFILE_ID, 0);
        }

        ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
        con.addCommandListener(commandListener);
        {
            Request req = RequestFactory.createGetPlanWindowListRequest();
            con.sendCommand(req);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_view_profile_default, container, false);

        mEditProfileLayoutView = (FreeProfileLayoutView) mRootView.findViewById(R.id.camera_container);

        mTextView = (TextView) mRootView.findViewById(R.id.title_view);

        mBackground = new ColorDrawable(Color.BLACK);
        mRootView.setBackground(mBackground);

        if (mProfileId != 0 && mEnableAnimation) {
            mTextView.setVisibility(View.GONE);
            mRootView.setBackgroundResource(mProfileId);
        }

        if (savedInstanceState == null) {
            //if (mEnableAnimation) {
                mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (mEnableAnimation) {
                            runEnterAnimation();
                        }

                        ScreenGroup screenGroup = ScreenStructure.getInstance().screenGroups[0];

                        float screenWidth = screenGroup.horizontalCount * 1920.0f;
                        float screenHeight = screenGroup.verticalCount * 1080.0f;

                        boolean isLandScape = screenWidth > screenHeight ? true : false;

                        mEditProfileLayoutView.setHScreenCount(screenGroup.horizontalCount);
                        mEditProfileLayoutView.setVScreenCount(screenGroup.verticalCount);

                        //if (isLandScape) {
                        int height = mEditProfileLayoutView.getMeasuredHeight();
                        int width = (int) (height * ( screenWidth / screenHeight ));

                        Log.e(TAG, "editProfileLayoutWidth: " + width);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mEditProfileLayoutView.getLayoutParams();

                        layoutParams.width = width;
                        mEditProfileLayoutView.setLayoutParams(layoutParams);
                        mEditProfileLayoutView.postInvalidate();
                        return true;
                    }
                });
            //}
        }

        return mRootView;
    }
/*
    private void createProfileLayout() {
        ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < cameraInfos.size(); i++) {
            //View cameraView = mGridView.getChildAt(i);
            CameraInfo cameraInfo = cameraInfos.get(i);
            View child = inflater.inflate(R.layout.camera_grid_item, mEditProfileLayoutView, false);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cameraInfo.getWidth(), cameraInfo.getHeight());
            layoutParams.leftMargin = cameraInfo.getLeft();
            layoutParams.topMargin = cameraInfo.getTop();
            ImageView imageView = (ImageView)child.findViewById(R.id.camera_view);
            imageView.setImageResource(cameraInfo.getBitmap());
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ControlInputActivity.class);
                    startActivity(intent);
                }
            });
            mEditProfileLayoutView.addView(child, layoutParams);
        }
    }
*/
    private void runEnterAnimation() {
        if (getArguments() != null) {
            Log.e(TAG, "runEnterAnimation...........");
            final long duration = (long) (ANIM_DURATION);

            final int top = getArguments().getInt(PACKAGE_NAME + ".top");
            final int left = getArguments().getInt(PACKAGE_NAME + ".left");
            final int width = getArguments().getInt(PACKAGE_NAME + ".width");
            final int height = getArguments().getInt(PACKAGE_NAME + ".height");
            Log.e(TAG, "runEnterAnimation........... top: " + top);
            mProfileName = getArguments().getString(PROFILE_NAME);
            mTextView.setText(mProfileName);
            // Figure out where the thumbnail and full size versions are, relative
            // to the screen and each other
            int[] screenLocation = new int[2];
            getView().getLocationOnScreen(screenLocation);
            int leftDelta = left - screenLocation[0];
            int topDelta = top - screenLocation[1];

            // Scale factors to make the large version the same size as the thumbnail
            float widthScale = (float) width / getView().getWidth();
            float heightScale = (float) height / getView().getHeight();

            getView().setPivotX(0);
            getView().setPivotY(0);
            getView().setScaleX(widthScale);
            getView().setScaleY(heightScale);
            getView().setTranslationX(leftDelta);
            getView().setTranslationY(topDelta);

            // Animate scale and translation to go from thumbnail to full size
            getView().animate().setDuration(duration).
                    scaleX(1).scaleY(1).
                    translationX(0).translationY(0).
                    setInterpolator(sDecelerator).withLayer();

        }
    }

//    public void performUpdateProfileModel() {
//        if (!mEnableAnimation) {
//            for (int i = 0; i < gridview.getChildCount(); i++) {
//                View view = gridview.getChildAt(i);
//                int[] screenLocation = new int[2];
//                view.getLocationOnScreen(screenLocation);
//                CameraInfo cameraInfo = new CameraInfo(view.getTop(), view.getLeft(), view.getWidth(), view.getHeight(), i, mImageAdapter.mThumbIds[i]);
//                EditProfileModel.getInstance().addCameraInfo(cameraInfo);
//            }
//        }
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateWindowInfos();
    }

    @Override
    public void onStop () {
        super.onStop();
        for (WindowInfo windowInfo : mWindowInfos) {
            ConnectionManager.defaultManager.stopJpgTransport(new byte[]{(byte) windowInfo.inputIndex});
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
