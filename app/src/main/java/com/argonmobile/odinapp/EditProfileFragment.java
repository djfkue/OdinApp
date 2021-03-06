package com.argonmobile.odinapp;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.model.PlanInfoModel;
import com.argonmobile.odinapp.model.ScreenStructure;
import com.argonmobile.odinapp.model.WindowInfoModel;
import com.argonmobile.odinapp.protocol.command.CloseWindowCommand;
import com.argonmobile.odinapp.protocol.command.Command;
import com.argonmobile.odinapp.protocol.command.CommandDefs;
import com.argonmobile.odinapp.protocol.command.CreateWindowCommand;
import com.argonmobile.odinapp.protocol.command.CreateWindowRequest;
import com.argonmobile.odinapp.protocol.command.GetPlanWindowListResponse;
import com.argonmobile.odinapp.protocol.command.MoveWindowCommand;
import com.argonmobile.odinapp.protocol.command.Request;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.connection.CommandListener;
import com.argonmobile.odinapp.protocol.connection.ConnectionManager;
import com.argonmobile.odinapp.protocol.connection.ControlConnection;
import com.argonmobile.odinapp.protocol.deviceinfo.ScreenGroup;
import com.argonmobile.odinapp.protocol.deviceinfo.WindowInfo;
import com.argonmobile.odinapp.protocol.image.ImageUpdater;
import com.argonmobile.odinapp.util.MockSwitch;
import com.argonmobile.odinapp.util.ScaleFactorCaculator;
import com.argonmobile.odinapp.view.CheckedFrameLayout;
import com.argonmobile.odinapp.view.FreeProfileLayoutView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private static final int ANIM_DURATION = 500;

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

    //private GridView mGridView;
    private ColorDrawable mBackground;
    private TextView mTextView;
    private FreeProfileLayoutView mEditProfileLayoutView;
    private CheckedFrameLayout mCurrentChecked;

    private int mCellHeight = 0;
    private int mCellWidth = 0;

    private ImageUpdater imageUpdater = new ImageUpdater();

    private FreeProfileLayoutView.ChildScaleChangeListener mChildScaleChangeListener = new FreeProfileLayoutView.ChildScaleChangeListener() {
        @Override
        public void onChildScaleEnd(View view, int width, int height) {
            syncInputWindow(view, width, height);
        }
    };

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

//                if (!EditProfileActivity.sNeedRelayout) {
//                    updateWindowInfos();
//                }

            }
            return true;
        }
    };

    private static final int MSG_ON_GET_PLAN_WINDOW_LIST = 0x01;


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

                Log.e(TAG, "windowTop: " + windowInfo.top);
                Log.e(TAG, "windowLeft:" + windowInfo.left);
                Log.e(TAG, "windowWidth: " + windowInfo.width);
                Log.e(TAG, "windowHeight: " + windowInfo.height);

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

                final CheckedFrameLayout child = (CheckedFrameLayout) inflater.inflate(R.layout.camera_grid_item, mEditProfileLayoutView, false);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(deviceWindowWidth, deviceWindowHeight);
                layoutParams.leftMargin = deviceWindowLeft;
                layoutParams.topMargin = deviceWindowTop;
                layoutParams.width = deviceWindowWidth;
                layoutParams.height = deviceWindowHeight;

                ImageView imageView = (ImageView) child.findViewById(R.id.camera_view);
                imageView.setImageResource(R.drawable.sample_0);

                mEditProfileLayoutView.addView(child, layoutParams);

                child.setOnDragListener(mCameraDragListener);

                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        child.toggle();
                        v.bringToFront();
                    }
                });

                child.setOnCheckedListener(new CheckedFrameLayout.OnCheckedListener() {
                    @Override
                    public void onChecked(CheckedFrameLayout checkedView, boolean checked) {
                        if (checked == false) {
                            checkedView.setOnTouchListener(null);
                            checkedView.setOnDragListener(mCameraDragListener);
                            if (mCurrentChecked == checkedView) {
                                mCurrentChecked = null;
                            }
                        }
                        if (mCurrentChecked != checkedView && checked) {
                            if (mCurrentChecked != null) {
                                mCurrentChecked.setChecked(false);
                            }
                            mCurrentChecked = checkedView;
                            Log.e(TAG, "current checked: " + mCurrentChecked.toString());

                            mCurrentChecked.setOnDragListener(null);
                        }
                    }
                });

                imageUpdater.subscribe(CommandDefs.PARAM_SIGNAL_IMAGE, windowInfo.inputIndex, imageView);
                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
                        (short) 480, (short) 270, new byte[]{(byte) windowInfo.inputIndex});

                //child.setTag(windowInfo);

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
            Log.i(TAG, "onSentCommand:" + cmd.command + " command payload length: " + cmd.getPayloadLength() + " cmd length: " + cmd.length);
        }

        @Override
        public void onReceivedCommand(Command cmd) {
            Log.i(TAG, "onReceivedCommand:" + cmd);
            if(cmd instanceof GetPlanWindowListResponse) {

                GetPlanWindowListResponse r = (GetPlanWindowListResponse) cmd;
                Log.i(TAG, "GetPlanWindowListResponse list sg window count:" + r.windowCount);
                WindowInfoModel.getInstance().windowInfos = r.windowInfos;

                for (WindowInfo ws : r.windowInfos) {
                    Log.i(TAG, "get plan window list sg:" + ws);
                }

                if (EditProfileActivity.sNeedRelayout) {
                    return;
                }

                Message message = new Message();
                message.what = MSG_ON_GET_PLAN_WINDOW_LIST;

                message.obj = r.windowInfos;

                mHandler.sendMessage(message);

//                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
//                        (short)480, (short)270, new byte[]{0x00});
            }

//            if (cmd instanceof MoveWindowCommand) {
//                ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
//                {
//                    Request req = RequestFactory.createGetPlanWindowListRequest();
//                    con.sendCommand(req);
//                }
//            }

//            if (cmd instanceof CreateWindowCommand || cmd instanceof CloseWindowCommand) {
//                ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
//                {
//                    Request req = RequestFactory.createGetPlanWindowListRequest();
//                    con.sendCommand(req);
//                }
//            }
        }
    };

    public EditProfileFragment() {
        // Required empty public constructor

        mHandler = new Handler(callback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mEditProfileLayoutView = (FreeProfileLayoutView) rootView.findViewById(R.id.edit_container);

        mEditProfileLayoutView.setOnDragListener(mFrameDragListener);
        mEditProfileLayoutView.setOnChildScaleChangeListener(mChildScaleChangeListener);

        mEditProfileLayoutView.enableGesture(true);

        if (savedInstanceState == null) {
            rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    rootView.getViewTreeObserver().removeOnPreDrawListener(this);

                    runEnterAnimation();

                    if (!MockSwitch.MOCK_SWITCH_ON) {
                        ScreenGroup screenGroup = ScreenStructure.getInstance().screenGroups[0];

                        float screenWidth = screenGroup.horizontalCount * 1920.0f;
                        float screenHeight = screenGroup.verticalCount * 1080.0f;

                        boolean isLandScape = screenWidth > screenHeight ? true : false;

                        mEditProfileLayoutView.setVScreenCount(screenGroup.verticalCount);
                        mEditProfileLayoutView.setHScreenCount(screenGroup.horizontalCount);

                        //if (isLandScape) {
                        int height = mEditProfileLayoutView.getMeasuredHeight();
                        mCellHeight = height / screenGroup.verticalCount / 2;
                        Log.d(TAG, "editProfileLayoutHeight: " + height);
                        int width = (int) (height * (screenWidth / screenHeight));

                        mCellWidth = width / screenGroup.horizontalCount / 2;
                        ViewGroup.LayoutParams layoutParams = mEditProfileLayoutView.getLayoutParams();
                        layoutParams.width = width;
                        mEditProfileLayoutView.setLayoutParams(layoutParams);

                    } else {
                        float screenWidth = 3 * 1920.0f;
                        float screenHeight = 3 * 1080.0f;

                        boolean isLandScape = screenWidth > screenHeight ? true : false;

                        mEditProfileLayoutView.setVScreenCount(3);
                        mEditProfileLayoutView.setHScreenCount(3);

                        int height = mEditProfileLayoutView.getMeasuredHeight();
                        mCellHeight = height / 3 / 2;
                        Log.d(TAG, "editProfileLayoutHeight: " + height);
                        int width = (int) (height * (screenWidth / screenHeight));

                        mCellWidth = width / 3 / 2;
                        Log.d(TAG, "editProfileLayoutHeight: " + height);
                        ViewGroup.LayoutParams layoutParams = mEditProfileLayoutView.getLayoutParams();
                        layoutParams.width = width;
                        mEditProfileLayoutView.setLayoutParams(layoutParams);

                    }
                    return true;
                }
            });
        }

        mBackground = new ColorDrawable(Color.BLACK);
        rootView.setBackground(mBackground);

        mTextView = (TextView) rootView.findViewById(R.id.title_view);

        Log.d("TD_TRACE", "onCreateView........");
        if (!MockSwitch.MOCK_SWITCH_ON) {
//            if (EditProfileActivity.sNeedRelayout) {
//                closeAllWindow();
//            }

            ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
            con.addCommandListener(commandListener);
            {
                Request req = RequestFactory.createGetPlanWindowListRequest();
                con.sendCommand(req);
            }
        }
        return rootView;
    }

    public void closeAllWindow() {
        ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
        {
            List<WindowInfo> windowInfos = new ArrayList<WindowInfo>();
            windowInfos.addAll(WindowInfoModel.getInstance().windowInfos);
            for (WindowInfo windowInfo : windowInfos) {
                Log.e("TD_TRACE", "send close window request");
                Command req = RequestFactory.createCloseWindowRequest(windowInfo.windowId);
                con.sendCommand(req);
            }
        }
//        Command req = RequestFactory.createCloseAllWindowRequest();
//        con.sendCommand(req);
    }

    @Override
    public void onStop () {
        super.onStop();

        if (!MockSwitch.MOCK_SWITCH_ON) {
            ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();
            for (CameraInfo cameraInfo : cameraInfos) {
                ConnectionManager.defaultManager.stopJpgTransport(new byte[]{(byte) cameraInfo.getId()});
            }
        }
    }

    private void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION);

        //mTextView.setAlpha(0);

        // Fade in the black background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTextView.setTranslationY(mTextView.getHeight());
                mTextView.animate().setDuration(duration).
                        translationY(0).alpha(1).
                        setInterpolator(sDecelerator).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (!MockSwitch.MOCK_SWITCH_ON) {
                            if (EditProfileActivity.sNeedRelayout) {
                                performUpdateProfileModel();
                                //addWindows();
                            }
                            //else {
                                createEditProfileLayout();
                            //}
                            ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
                            {
                                Request req = RequestFactory.createGetPlanWindowListRequest();
                                con.sendCommand(req);
                            }
                        } else {
                            if (EditProfileActivity.sNeedRelayout) {
                                performUpdateProfileModel();
                                //addWindows();
                            }
                            createEditProfileLayout();
                        }
                    }
                });

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        bgAnim.start();

    }

    public void addWindows() {
        ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
        {
//            List<WindowInfo> windowInfos = WindowInfoModel.getInstance().windowInfos;
//            for (WindowInfo windowInfo : windowInfos) {
//                Log.e("TD_TRACE", "send add window request");
//                Command req = RequestFactory.createCloseWindowRequest(windowInfo.windowId);
//                con.sendCommand(req);
//            }
            ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();

            for (int i = 0; i < cameraInfos.size(); i++) {
                CameraInfo cameraInfo = cameraInfos.get(i);
                Log.e(TAG, "addWindows: " + cameraInfo.toString());
                CreateWindowRequest req = (CreateWindowRequest) RequestFactory.createNewWindowRequest();
                req.windowId = (short) i;
                req.inputIndex = (short) cameraInfo.getId();
                req.userZOrder = (short) i;
                req.input = (byte) cameraInfo.getId();
                req.divideMode = 1;
                req.subInputss = new String[1];
                req.subInputss[0] = null;
                req.url = "";
                req.panelGroupId = 0;
                req.left = (short) cameraInfo.mLeft;
                req.top = (short) cameraInfo.mTop;
                req.width = (short) cameraInfo.mWidth;
                req.height = (short) cameraInfo.mHeight;
                req.leftTop = (short) (((cameraInfo.mTop - 5) / (mCellHeight * 2) + 1) * 16 + ((cameraInfo.mLeft - 5) / (mCellWidth * 2) + 1));
                Log.e("TD_TRACE", "leftTop: " + req.leftTop);
                req.rightBottom = (short) (((cameraInfo.mTop + cameraInfo.mHeight - 5) / (mCellHeight * 2) + 1) * 16 + ((cameraInfo.mLeft + cameraInfo.mWidth - 5) / (mCellWidth * 2) + 1));;
                Log.e("TD_TRACE", "rightBottom: " + req.rightBottom);

                req.left = (short) ScaleFactorCaculator.getScreenWindowLeft(cameraInfo.mLeft, mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());
                req.top = (short) ScaleFactorCaculator.getScreenWindowTop(cameraInfo.mTop, mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());
                req.width = (short) ScaleFactorCaculator.getScreenWindowWidth(cameraInfo.mWidth, mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());
                req.height = (short) ScaleFactorCaculator.getScreenWindowHeight(cameraInfo.mHeight, mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());

                req.isWindowFixed = false;
                req.recycleInterval = 0;
                req.recycleListCount = 1;
                req.recycleIndexes = new short[1];
                req.recycleIndexes[0] = 0;
                con.sendCommand(req);
            }
        }
    }

    public void performUpdateProfileModel() {
        Log.e(TAG, "performUpdateProfileModel");
        ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();

        if (!MockSwitch.MOCK_SWITCH_ON) {
            ScreenGroup screenGroup = ScreenStructure.getInstance().screenGroups[0];

            for (int i = 0; i < cameraInfos.size(); i++) {
                CameraInfo cameraInfo = cameraInfos.get(i);
                cameraInfo.mLeft = (i % (screenGroup.horizontalCount * 2)) * mCellWidth;
                cameraInfo.mTop = (i / (screenGroup.horizontalCount * 2)) * mCellHeight;
                cameraInfo.mWidth = mCellWidth;
                cameraInfo.mHeight = mCellHeight;

            }
        } else {
            for (int i = 0; i < cameraInfos.size(); i++) {
                CameraInfo cameraInfo = cameraInfos.get(i);
                cameraInfo.mLeft = (i % (3 * 2)) * mCellWidth;
                cameraInfo.mTop = (i / (3 * 2)) * mCellHeight;
                cameraInfo.mWidth = mCellWidth;
                cameraInfo.mHeight = mCellHeight;

                Log.e("TALOS", "set left: " + cameraInfo.mLeft);

                Log.e("TALOS", " set top: " + cameraInfo.mTop);
            }
        }

    }

    private void createEditProfileLayout() {
        ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();

        mEditProfileLayoutView.removeAllViews();
        Log.e(TAG, "create edit profile layout: " + cameraInfos.size());
        if (getActivity() == null ) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < cameraInfos.size(); i++) {
            //View cameraView = mGridView.getChildAt(i);
            CameraInfo cameraInfo = cameraInfos.get(i);
            View child = inflater.inflate(R.layout.camera_grid_item, mEditProfileLayoutView, false);

            Log.e("TALOS", "createEditProfileLayout width: " + cameraInfo.getWidth());
            Log.e("TALOS", "createEditProfileLayout height:" + cameraInfo.getHeight());

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(cameraInfo.getWidth(), cameraInfo.getHeight());
            layoutParams.leftMargin = cameraInfo.getLeft();
            layoutParams.topMargin = cameraInfo.getTop();
            Log.e(TAG, "left: " + layoutParams.leftMargin);

            Log.e(TAG, "top: " + layoutParams.topMargin);
            ImageView imageView = (ImageView)child.findViewById(R.id.camera_view);
            imageView.setImageResource(cameraInfo.getBitmap());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            child.setTag(cameraInfo);
            mEditProfileLayoutView.addView(child, layoutParams);

            if (!MockSwitch.MOCK_SWITCH_ON) {
                imageUpdater.subscribe(CommandDefs.PARAM_SIGNAL_IMAGE, cameraInfo.getId(), imageView);
                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
                        (short) 480, (short) 270, new byte[]{(byte) cameraInfo.getId()});
            }
            final CheckedFrameLayout checkedFrameLayout = (CheckedFrameLayout) child.findViewById(R.id.checked_frame);

            checkedFrameLayout.setOnDragListener(mCameraDragListener);

            checkedFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedFrameLayout.toggle();
                    v.bringToFront();
                }
            });

            checkedFrameLayout.setOnCheckedListener(new CheckedFrameLayout.OnCheckedListener() {
                @Override
                public void onChecked(CheckedFrameLayout checkedView, boolean checked) {
                    if (checked == false) {
                        checkedView.setOnTouchListener(null);
                        checkedView.setOnDragListener(mCameraDragListener);
                        if (mCurrentChecked == checkedView) {
                            mCurrentChecked = null;
                        }
                    }
                    if (mCurrentChecked != checkedView && checked) {
                        if (mCurrentChecked != null) {
                            mCurrentChecked.setChecked(false);
                        }
                        mCurrentChecked = checkedView;
                        Log.e(TAG, "current checked: " + mCurrentChecked.toString());

                        mCurrentChecked.setOnDragListener(null);
                    }
                }
            });


        }
    }

    private View.OnDragListener mFrameDragListener = new View.OnDragListener() {

        float mStartX = 0;
        float mStartY = 0;

        @Override
        public boolean onDrag(View v, DragEvent event) {

            int action = event.getAction();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    // do nothing
                    mStartX = event.getX();
                    mStartY = event.getY();

                    Log.e(TAG, "ACTION_DRAG_STARTED................. x: " + mStartX);
                    Log.e(TAG, "ACTION_DRAG_STARTED................. y: " + mStartY);


                }
                case DragEvent.ACTION_DRAG_ENTERED:
//                    mStartX = event.getX();
//                    mStartY = event.getY();

                    Log.e(TAG, "ACTION_DRAG_ENTERED................. x: " + mStartX);
                    Log.e(TAG, "ACTION_DRAG_ENTERED................. y: " + mStartY);

                    break;
                case DragEvent.ACTION_DRAG_EXITED: {
                    Log.e(TAG, "ACTION_DRAG_EXITED ");
                    View view = (View) event.getLocalState();
                    view.bringToFront();
                    view.setVisibility(View.VISIBLE);

                    syncInputWindow(view, view.getWidth(), view.getHeight());
                    break;
                }
                case DragEvent.ACTION_DROP:
                    if (v.getId() == R.id.edit_container) {
                        View view = (View) event.getLocalState();
                        Log.e(TAG, "ACTION_DROP................. x: " + event.getX());
                        Log.e(TAG, "ACTION_DROP................. y: " + event.getY());

                        //mEditProfileLayoutView.getParent().requestDisallowInterceptTouchEvent(false);

                        if ((event.getX() - view.getWidth() / 2 + view.getMeasuredWidth()) > (mEditProfileLayoutView.getX() + mEditProfileLayoutView.getMeasuredWidth())) {
                            view.setX(mEditProfileLayoutView.getX() + mEditProfileLayoutView.getMeasuredWidth() - view.getMeasuredWidth());
                        } else if ((event.getY() - view.getHeight() / 2 + view.getMeasuredHeight() > (mEditProfileLayoutView.getY() + mEditProfileLayoutView.getMeasuredHeight()))) {
                            view.setY(mEditProfileLayoutView.getY() + mEditProfileLayoutView.getMeasuredHeight() - view.getMeasuredHeight());
                        } else if ((event.getX() - view.getWidth() / 2 + view.getMeasuredWidth()) < (mEditProfileLayoutView.getX() + view.getMeasuredWidth())) {
                            view.setX(mEditProfileLayoutView.getX());
                        } else if ((event.getY() - view.getHeight() / 2 + view.getMeasuredHeight() < (mEditProfileLayoutView.getY() + view.getMeasuredHeight()))) {
                            view.setY(mEditProfileLayoutView.getY());
                        }
                        else {
                            view.setX(event.getX() - view.getWidth() / 2);
                            view.setY(event.getY() - view.getHeight() / 2);
                        }

                        view.bringToFront();
                        view.setVisibility(View.VISIBLE);
                        syncInputWindow(view, view.getWidth(), view.getHeight());
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.e(TAG, "ACTION_DRAG_ENDED ");
                    mStartX = 0;
                    mStartY = 0;
                    View view = (View) event.getLocalState();
                    view.bringToFront();
                    view.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void syncInputWindow(View window, int width, int height) {
        if (false) {
            WindowInfo windowInfo = (WindowInfo) window.getTag();

            Log.e(TAG, "sync input window: " + windowInfo.windowId);

            ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
            short windowLeft = (short) ScaleFactorCaculator.getScreenWindowLeft((int) window.getX(), mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());
            short windowTop = (short) ScaleFactorCaculator.getScreenWindowTop((int) window.getY(), mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());
            short windowWidth = (short) ScaleFactorCaculator.getScreenWindowWidth(width, mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());
            short windowHeight = (short) ScaleFactorCaculator.getScreenWindowHeight(height, mEditProfileLayoutView.getWidth(), mEditProfileLayoutView.getHeight());
            short leftTop = (short) ((int) ((window.getY() - 5) / (mCellHeight * 2) + 1) * 16 + (int) ((window.getX() - 5) / (mCellWidth * 2) + 1));

            short rightBottom = (short) ((int) ((window.getY() + height - 5) / (mCellHeight * 2) + 1) * 16 + (int) ((window.getX() + width - 5) / (mCellWidth * 2) + 1));
            ;
            Log.e("TD_TRACE", "rightBottom: " + rightBottom);

            Log.e(TAG, "move windowTop: " + windowTop);
            Log.e(TAG, "move windowLeft: " + windowLeft);
            Log.e(TAG, "move windowWidth: " + windowWidth);
            Log.e(TAG, "move windowHeight:" + windowHeight);
            Command req = RequestFactory.createMoveWindowRequest(windowInfo.windowId, windowInfo.userZOrder, windowLeft, windowTop, windowWidth, windowHeight, leftTop, rightBottom);
            con.sendCommand(req);
        }

        CameraInfo cameraInfo = (CameraInfo) window.getTag();

        Log.e(TAG, "sync input window: " + cameraInfo.toString());
        cameraInfo.mTop = (int) window.getY();
        cameraInfo.mLeft = (int) window.getX();
        cameraInfo.mWidth = width;
        cameraInfo.mHeight = height;
    }

    private View.OnDragListener mCameraDragListener = new View.OnDragListener() {

        float mStartX = 0;
        float mStartY = 0;

        float mDeltaX = 0;
        float mDeltaY = 0;

        @Override
        public boolean onDrag(final View v, DragEvent event) {

            int action = event.getAction();
            Log.e(TAG, "Camera onDrag.................: " + event.toString());
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    // do nothing
                    mStartX = event.getX();
                    mStartY = event.getY();

                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. x: " + mStartX);
                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. y: " + mStartY);

                    int[] screenLocation = new int[2];
                    v.getLocationOnScreen(screenLocation);
                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. v x: " + screenLocation[0]);
                    Log.e(TAG, "Camera ACTION_DRAG_STARTED................. v y: " + screenLocation[1]);

                    break;
                }

                case DragEvent.ACTION_DRAG_LOCATION: {
                    if (mDeltaX != 0) {
                        mDeltaX += event.getX() - mStartX;
                    } else {
                        mDeltaX = event.getX();
                    }
                    if (mDeltaY != 0) {
                        mDeltaY += event.getY() - mStartY;
                    } else {
                        mDeltaY = event.getY();
                    }
                    mStartX = event.getX();
                    mStartY = event.getY();
                    break;
                }

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.e(TAG, "Camera ACTION_DRAG_ENTERED................. x: " + event.getX());
                    Log.e(TAG, "Camera ACTION_DRAG_ENTERED................. y: " + event.getY());
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.e(TAG, "Camera ACTION_DRAG_EXITED ");

                    break;
                case DragEvent.ACTION_DROP:
                    if (v.getId() == R.id.checked_frame) {
                        View view = (View) event.getLocalState();
                        if (view.getWidth() * view.getScaleX() == v.getWidth() * v.getScaleX()) {
                            int[] dropViewScreenLocation = new int[2];
                            v.getLocationOnScreen(dropViewScreenLocation);

                            int[] dragViewScreenLocation = new int[2];
                            view.getLocationOnScreen(dragViewScreenLocation);

                            Log.e(TAG, "Camera ACTION_DROP................. x: " + dropViewScreenLocation[0]);
                            Log.e(TAG, "Camera ACTION_DROP................. y: " + dropViewScreenLocation[1]);

                            float deltaX = dragViewScreenLocation[0] - dropViewScreenLocation[0];
                            float deltaY = dragViewScreenLocation[1] - dropViewScreenLocation[1];

                            v.animate().translationXBy(deltaX).translationYBy(deltaY).withLayer().withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    syncInputWindow(v, v.getWidth(), v.getHeight());
                                }
                            });

                            view.setX(v.getX());
                            view.setY(v.getY());

                            view.bringToFront();
                            view.setVisibility(View.VISIBLE);
                            syncInputWindow(view, view.getWidth(), view.getHeight());
                        } else {
                            return false;
                        }

                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.e(TAG, "Camera ACTION_DRAG_ENDED ");
                    mStartX = 0;
                    mStartY = 0;
                    return false;
                    //break;
                default:
                    break;
            }
            return true;
        }
    };

    public void saveProfile() {
        if (EditProfileActivity.sNeedRelayout) {
            ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
            Request newPlan = RequestFactory.createNewPlanRequest(PlanInfoModel.getInstance().planInfos.size() + 1, "New Plan");
            con.sendCommand(newPlan);
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mScaleFactor;
        private View mView;

        public ScaleListener(View view) {
            mView = view;
            mScaleFactor = 1.0f;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.e(TAG, "..............................");
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));

            mView.setScaleX(mScaleFactor);
            mView.setScaleY(mScaleFactor);

            return true;
        }
    }

    class EditProfileCameraAdapter extends BaseAdapter {
        private final LayoutInflater mInflater;
        private Context mContext;

        private SparseBooleanArray mCheckStates;
        private int mCheckedItemCount;

        public EditProfileCameraAdapter(Context c) {
            mContext = c;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mCheckedItemCount = 0;
            mCheckStates = new SparseBooleanArray();
        }

        public boolean isItemChecked(int position) {
            return mCheckStates.get(position);
        }

        public void toggleItemChecked(int position) {
            boolean oldValue = mCheckStates.get(position);
            mCheckStates.put(position, !oldValue);
            if (oldValue) {
                mCheckedItemCount--;
            } else {
                mCheckedItemCount++;
            }
            notifyDataSetChanged();
        }

        public SparseBooleanArray getCheckedItemPositions() {
            return mCheckStates;
        }

        public int getCheckedItemCount() {
            return mCheckedItemCount;
        }

        public boolean isAllItemChecked() {
            return mCheckedItemCount == getCount();
        }

        public boolean hasItemChecked() {
            return mCheckedItemCount > 0;
        }

        public int getCount() {
            return EditProfileModel.getInstance().getCameraInfoArrayList().size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            CameraInfo cameraInfo = EditProfileModel.getInstance().getCameraInfoArrayList().get(position);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.camera_grid_item, parent, false);
            }
            //final float scale = mContext.getResources().getDisplayMetrics().density;
            //convertView.setLayoutParams(new GridView.LayoutParams((int)(220 * scale), (int)(135 * scale)));

            CheckedFrameLayout checkedLayout = (CheckedFrameLayout) convertView.findViewById(R.id.checked_frame);
            checkedLayout.setChecked(isItemChecked(position));

            ImageView imageView = (ImageView) convertView.findViewById(R.id.camera_view);

            imageView.setImageResource(cameraInfo.getBitmap());

            return convertView;
        }
    }
}
