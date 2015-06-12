package com.argonmobile.odinapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.argonmobile.odinapp.model.PlanInfoModel;
import com.argonmobile.odinapp.model.Profile;
import com.argonmobile.odinapp.model.ProfileStack;
import com.argonmobile.odinapp.protocol.command.Command;
import com.argonmobile.odinapp.protocol.command.GetPlanListResponse;
import com.argonmobile.odinapp.protocol.command.Request;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.connection.CommandListener;
import com.argonmobile.odinapp.protocol.connection.ConnectionManager;
import com.argonmobile.odinapp.protocol.connection.ControlConnection;
import com.argonmobile.odinapp.protocol.deviceinfo.PlanInfo;
import com.argonmobile.odinapp.util.ReferenceCountedTrigger;
import com.argonmobile.odinapp.view.RecentsView;
import com.argonmobile.odinapp.view.TaskView;
import com.argonmobile.odinapp.view.ViewAnimation;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentProfileFragment extends Fragment {
    private static final String TAG = "RecentProfileFragment";
    private static final int MSG_ON_GET_PLAN_LIST = 0x01;

    private RecentsConfiguration mConfig;
    private RecentsView mRecentsView;

    public List<PlanInfo> mPlanInfos = new ArrayList<PlanInfo>();

    private Handler mHandler;

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == MSG_ON_GET_PLAN_LIST) {
                mPlanInfos.clear();
                List<PlanInfo> planInfos = (List<PlanInfo>) msg.obj;

                Log.e(TAG, "============== getCount: " + planInfos.size());
                for (PlanInfo planInfo : planInfos) {
                    mPlanInfos.add(planInfo);
                }
                updateRecentTasks();
            }
            return true;
        }
    };


    CommandListener commandListener = new CommandListener() {
        @Override
        public void onSentCommand(Command cmd) {
            Log.i(TAG, "onSentCommand:" + cmd.command);
        }

        @Override
        public void onReceivedCommand(Command cmd) {
            Log.i(TAG, "onReceivedCommand:" + cmd);
            if(cmd instanceof GetPlanListResponse) {

                GetPlanListResponse r = (GetPlanListResponse) cmd;
                Log.i(TAG, "GetPlanWindowListResponse list sg window count:" + r.planCount);
                PlanInfoModel.getInstance().planInfos = r.planInfos;


                Message message = new Message();
                message.what = MSG_ON_GET_PLAN_LIST;

                message.obj = r.planInfos;

                mHandler.sendMessage(message);

                for (PlanInfo ps : r.planInfos) {
                    Log.i(TAG, "get plan window list sg:" + ps);
                }

//                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
//                        (short)480, (short)270, new byte[]{0x00});
            }
        }
    };



    public RecentProfileFragment() {
        // Required empty public constructor
        mHandler = new Handler(callback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize the loader and the configuration
        mConfig = RecentsConfiguration.reinitialize(getActivity());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recent_profile, container, false);

        mRecentsView = (RecentsView) rootView.findViewById(R.id.recents_view);
        mRecentsView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mRecentsView.setCallbacks(new RecentsView.RecentsViewCallbacks() {
            @Override
            public void onTaskViewClicked(TaskView tv, Profile t) {
                Log.e("SD_TRACE", "click profle: " + t.activityLabel);
                int[] screenLocation = new int[2];
                tv.getLocationOnScreen(screenLocation);

                ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
                Request req = RequestFactory.createInvokePlanRequest(t.key.id);
                con.sendCommand(req);

                Intent intent = new Intent(getActivity(), ChosenProfileActivity.class);
                intent.
//                        putExtra(ViewProfileFragment.PACKAGE_NAME + ".left", screenLocation[0]).
//                        putExtra(ViewProfileFragment.PACKAGE_NAME + ".top", screenLocation[1]).
//                        putExtra(ViewProfileFragment.PACKAGE_NAME + ".width", tv.getWidth()).
//                        putExtra(ViewProfileFragment.PACKAGE_NAME + ".height", tv.getHeight()).
                        putExtra(ViewProfileFragment.ENABLE_ANIMATION, true).
                        putExtra(ViewProfileFragment.PROFILE_NAME, t.activityLabel);
                       // putExtra(ViewProfileFragment.PROFILE_ID, R.drawable.profile_1);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
        con.addCommandListener(commandListener);
        {
            Request req = RequestFactory.createGetPlanListRequest();
            con.sendCommand(req);
        }

        //updateRecentTasks();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Mark Recent as visible

        ReferenceCountedTrigger t = new ReferenceCountedTrigger(getActivity(), null, null, null);
        mRecentsView.startEnterRecentsAnimation(new ViewAnimation.TaskViewEnterContext(t));
    }


    void updateRecentTasks() {

//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.profile_1);

        ArrayList<ProfileStack> stacks = new ArrayList<>();
        ProfileStack mockStack = new ProfileStack();

        for (int i = 0; i < mPlanInfos.size(); i++) {
            Profile profile = new Profile();
            profile.isLaunchTarget = true;
            profile.key = new Profile.TaskKey();
            profile.key.id = mPlanInfos.get(i).index;

            profile.activityLabel = mPlanInfos.get(i).planName;
            mockStack.addTask(profile);
        }

        // Load all the tasks
//
//        ArrayList<ProfileStack> stacks = new ArrayList<>();
//        ProfileStack mockStack = new ProfileStack();
//        Profile mockProfile1 = new Profile();
//        mockProfile1.isLaunchTarget = true;
//        mockProfile1.key = new Profile.TaskKey();
//        mockProfile1.key.id = 0;
//        mockProfile1.activityLabel = "Profile 0";
//        mockProfile1.thumbnail = bm;
//        mockStack.addTask(mockProfile1);
//
//        Profile mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 1;
//        mockProfile2.activityLabel = "Profile 1";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 2;
//        mockProfile2.activityLabel = "Profile 2";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 3;
//        mockProfile2.activityLabel = "Profile 3";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 4;
//        mockProfile2.activityLabel = "Profile 4";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 5;
//        mockProfile2.activityLabel = "Profile 5";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 6;
//        mockProfile2.activityLabel = "Profile 6";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 7;
//        mockProfile2.activityLabel = "Profile 7";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 8;
//        mockProfile2.activityLabel = "Profile 8";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);
//
//        mockProfile2 = new Profile();
//        mockProfile2.isLaunchTarget = true;
//        mockProfile2.key = new Profile.TaskKey();
//        mockProfile2.key.id = 9;
//        mockProfile2.activityLabel = "Profile 9";
//        mockProfile2.thumbnail = bm;
//        mockStack.addTask(mockProfile2);

        stacks.add(mockStack);
        if (!stacks.isEmpty()) {
            mRecentsView.setTaskStacks(stacks);
        }
        mConfig.launchedWithNoRecentTasks = false;

        // Mark the task that is the launch target
        int taskStackCount = stacks.size();
        if (mConfig.launchedToTaskId != -1) {
            for (int i = 0; i < taskStackCount; i++) {
                ProfileStack stack = stacks.get(i);
                ArrayList<Profile> tasks = stack.getTasks();
                int taskCount = tasks.size();
                for (int j = 0; j < taskCount; j++) {
                    Profile t = tasks.get(j);
                    if (t.key.id == mConfig.launchedToTaskId) {
                        t.isLaunchTarget = true;
                        break;
                    }
                }
            }
        }
    }

}
