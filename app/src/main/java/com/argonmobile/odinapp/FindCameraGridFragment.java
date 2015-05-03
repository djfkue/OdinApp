package com.argonmobile.odinapp;


import android.animation.TimeInterpolator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.ImageAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.protocol.command.Command;
import com.argonmobile.odinapp.protocol.command.GetInputInfoResponse;
import com.argonmobile.odinapp.protocol.command.GetOutputInfoResponse;
import com.argonmobile.odinapp.protocol.command.GetPlanListResponse;
import com.argonmobile.odinapp.protocol.command.GetPlanWindowInfoResponse;
import com.argonmobile.odinapp.protocol.command.GetPlanWindowListResponse;
import com.argonmobile.odinapp.protocol.command.GetWindowStructureResponse;
import com.argonmobile.odinapp.protocol.command.Request;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.connection.CommandListener;
import com.argonmobile.odinapp.protocol.connection.ConnectionManager;
import com.argonmobile.odinapp.protocol.connection.ControlConnection;
import com.argonmobile.odinapp.protocol.deviceinfo.InputInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.OutputInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.PlanInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.ScreenGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindCameraGridFragment extends Fragment {

    private static final String TAG = "FindCameraGridFragment";
//    private static final int ANIM_DURATION = 500;
//
//    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
//    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();

//    private OnExitAnimationListener mOnExitAnimationListner;

    private ImageAdapter mImageAdapter;
    private GridView gridview;
    private TextView mTextView;

    public FindCameraGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
        con.addCommandListener(commandListener);
        { // test get input info
            Request req = RequestFactory.createGetInputInfoRequest();
            con.sendCommand(req);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_camera_grid, container, false);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);

        mImageAdapter = new ImageAdapter(rootView.getContext());

        gridview.setAdapter(mImageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mImageAdapter.toggleItemChecked(position);
                if (mImageAdapter.isItemChecked(position)) {
                    CameraInfo cameraInfo = new CameraInfo(view.getTop(), view.getLeft(), view.getWidth(), view.getHeight(), position, mImageAdapter.mThumbIds[position]);
                    EditProfileModel.getInstance().addCameraInfo(cameraInfo);
                } else {
                    EditProfileModel.getInstance().removeCameraInfo(position);
                }
            }
        });

        mTextView = (TextView) rootView.findViewById(R.id.title_view);

        return rootView;
    }

    CommandListener commandListener = new CommandListener() {
        @Override
        public void onSentCommand(Command cmd) {
            Log.i(TAG, "onSentCommand:" + cmd.command);
        }

        @Override
        public void onReceivedCommand(Command cmd) {
            Log.i(TAG, "onReceivedCommand:" + cmd);
            if(cmd instanceof GetInputInfoResponse) {

                GetInputInfoResponse r = (GetInputInfoResponse) cmd;
                for (InputInfo ii : r.inputInfos) {
                    Log.i(TAG, "get input info, ii:" + ii);
                }
//                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
//                        (short)480, (short)270, new byte[]{0x00});
            }
        }
    };

}
