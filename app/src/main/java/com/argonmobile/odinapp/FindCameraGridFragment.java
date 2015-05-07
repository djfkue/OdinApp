package com.argonmobile.odinapp;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.argonmobile.odinapp.dummy.ImageAdapter;
import com.argonmobile.odinapp.model.CameraInfo;
import com.argonmobile.odinapp.model.EditProfileModel;
import com.argonmobile.odinapp.model.WindowInfoModel;
import com.argonmobile.odinapp.protocol.command.Command;
import com.argonmobile.odinapp.protocol.command.GetInputInfoResponse;
import com.argonmobile.odinapp.protocol.command.Request;
import com.argonmobile.odinapp.protocol.command.RequestFactory;
import com.argonmobile.odinapp.protocol.connection.CommandListener;
import com.argonmobile.odinapp.protocol.connection.ConnectionManager;
import com.argonmobile.odinapp.protocol.connection.ControlConnection;
import com.argonmobile.odinapp.protocol.deviceinfo.InputInfo;
import com.argonmobile.odinapp.protocol.deviceinfo.WindowInfo;
import com.argonmobile.odinapp.protocol.image.ImageUpdater;
import com.argonmobile.odinapp.view.CheckedFrameLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindCameraGridFragment extends Fragment {

    private static final String TAG = "FindCameraGridFragment";
    private static final int MSG_ON_GET_INPUT_INFO_LIST = 0x01;
    private static final boolean DEBUG_MOCK = false;

    private ArrayList<InputInfo> mInputInfos = new ArrayList<>();

    private ImageUpdater imageUpdater = new ImageUpdater();

    private Handler mHandler;

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == MSG_ON_GET_INPUT_INFO_LIST) {
                mInputInfos.clear();
                List<InputInfo> inputInfos = (List<InputInfo>) msg.obj;

                Log.e(TAG, "============== getCount: " + inputInfos.size());
                for (InputInfo inputInfo : inputInfos) {
                    mInputInfos.add(inputInfo);
                }
                mInputAdapter.notifyDataSetChanged();

                //gridview.setAdapter(mInputAdapter);
                //updateInputInfoAdapter();
            }
            return true;
        }
    };

    private InputAdapter mInputAdapter;
    private ImageAdapter mImageAdapter;
    private GridView gridview;
    private TextView mTextView;

    public FindCameraGridFragment() {
        // Required empty public constructor
        mHandler = new Handler(callback);
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DEBUG_MOCK) {
            ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
            con.addCommandListener(commandListener);
            { // test get input info
                Request req = RequestFactory.createGetInputInfoRequest();
                con.sendCommand(req);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_camera_grid, container, false);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);

        mInputAdapter = new InputAdapter(rootView.getContext());

        if (DEBUG_MOCK) {
            mImageAdapter = new ImageAdapter(rootView.getContext());
            gridview.setAdapter(mImageAdapter);
        } else {
            gridview.setAdapter(mInputAdapter);
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG_MOCK) {
                    mImageAdapter.toggleItemChecked(position);
                    if (mImageAdapter.isItemChecked(position)) {
                        if (mInputAdapter.isItemChecked(position)) {
                            CameraInfo cameraInfo = new CameraInfo(view.getTop(),
                                    view.getLeft(),
                                    view.getWidth(),
                                    view.getHeight(),
                                    mInputInfos.get(position).inputIndex,
                                    R.drawable.sample_0);
                            EditProfileModel.getInstance().addCameraInfo(cameraInfo);
                        } else {
                            EditProfileModel.getInstance().removeCameraInfo(position);
                        }
                    }
                } else {
                    mInputAdapter.toggleItemChecked(position);
                    if (mInputAdapter.isItemChecked(position)) {
                        CameraInfo cameraInfo = new CameraInfo(view.getTop(),
                                view.getLeft(),
                                view.getWidth(),
                                view.getHeight(),
                                mInputInfos.get(position).inputIndex,
                                R.drawable.sample_0);
                        EditProfileModel.getInstance().addCameraInfo(cameraInfo);
                    } else {
                        EditProfileModel.getInstance().removeCameraInfo(position);
                    }
                }
            }
        });


        mTextView = (TextView) rootView.findViewById(R.id.title_view);

        return rootView;
    }

    @Override
    public void onResume () {
        super.onResume();
    }

    @Override
    public void onDetach() {
        Log.e("TD_TRACE", "onDetach....................");

        super.onDetach();
    }

    @Override
    public void onStop () {
        super.onStop();
        if (!DEBUG_MOCK) {
            for (InputInfo inputInfo : mInputInfos) {
                ConnectionManager.defaultManager.stopJpgTransport(new byte[]{(byte) inputInfo.inputIndex});
            }
            ControlConnection con = ConnectionManager.defaultManager.getControlConnection();
            con.removeCommandListener(commandListener);
            {
                List<WindowInfo> windowInfos = WindowInfoModel.getInstance().windowInfos;
                for (WindowInfo windowInfo : windowInfos) {
                    Log.e("TD_TRACE", "send close window request");
                    Command req = RequestFactory.createCloseWindowRequest(windowInfo.windowId);
                    con.sendCommand(req);
                }
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
            if(cmd instanceof GetInputInfoResponse) {

                GetInputInfoResponse r = (GetInputInfoResponse) cmd;

                Message message = new Message();
                message.what = MSG_ON_GET_INPUT_INFO_LIST;

                message.obj = r.inputInfos;

                mHandler.sendMessage(message);

                for (InputInfo ii : r.inputInfos) {
                    Log.i(TAG, "get input info, ii:" + ii);
                }
//                ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
//                        (short)480, (short)270, new byte[]{0x00});
            }
        }
    };


    private void updateInputInfoAdapter() {

    }

    private class InputAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;

        private SparseBooleanArray mCheckStates;
        private int mCheckedItemCount;

        public InputAdapter(Context c) {
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

        @Override
        public int getCount() {
            return mInputInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mInputInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mInputInfos.get(position).inputIndex;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.camera_grid_item, parent, false);
            }
            //final float scale = mContext.getResources().getDisplayMetrics().density;
            //convertView.setLayoutParams(new GridView.LayoutParams((int)(220 * scale), (int)(135 * scale)));

            CheckedFrameLayout checkedLayout = (CheckedFrameLayout) convertView.findViewById(R.id.checked_frame);

            boolean shouldBeChecked = false;
            ArrayList<CameraInfo> cameraInfos = EditProfileModel.getInstance().getCameraInfoArrayList();

            for (CameraInfo cameraInfo : cameraInfos) {
                if (cameraInfo.getId() == mInputInfos.get(position).inputIndex) {
                    shouldBeChecked = true;
                    break;
                }
            }

            checkedLayout.setChecked(isItemChecked(position) || shouldBeChecked);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.camera_view);
            //imageView.setImageResource(R.drawable.sample_0);
            imageUpdater.subscribe(mInputInfos.get(position).inputIndex, imageView);
            ConnectionManager.defaultManager.startJpgTransport(imageUpdater,
                    (short)480, (short)270, new byte[]{(byte) mInputInfos.get(position).inputIndex});
            return convertView;
        }
    }

}
