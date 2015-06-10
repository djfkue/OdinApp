package com.argonmobile.odinapp;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.argonmobile.odinapp.protocol.IPCameraParser;
import com.argonmobile.odinapp.protocol.deviceinfo.IPCameraNode;
import com.argonmobile.odinapp.protocol.deviceinfo.Node;
import com.argonmobile.odinapp.view.WheelWidget;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchCameraFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private WheelWidget mWheel;
    private MyAdapter myAdapter;

    // for mock only
    private Node mRootNode;
    private Node mCurrentNode;

    public SearchCameraFragment() {
        // Required empty public constructor
        // TODO: get ip camera info
        String ipCameraInfo = "<IPCLists><雨花区 Name=\"T130758898957037250\" Type=\"Folder\"><板桥派出所 Name=\"T130758899048902505\" Type=\"Folder\"><凤台南路 Name=\"T130758899147408139\" Type=\"Folder\"><XX路口 Name=\"T130758899260354599\" Type=\"Folder\"><监控摄像机1 Name=\"T130758899418983672\" Type=\"IPC\"><IP地址 Name=\"T130758899530720063\" Type=\"SUBIPC\" /><用户名 Name=\"T130758899626645550\" Type=\"SUBIPC\" /><密码 Name=\"T130758899674258273\" Type=\"SUBIPC\" /><端口 Name=\"T130758899725821222\" Type=\"SUBIPC\" /></监控摄像机1></XX路口></凤台南路></板桥派出所><软件园派出所 Name=\"T130758899850548356\" Type=\"Folder\" /></雨花区></IPCLists>";
        ArrayList<IPCameraNode> cameraNodes = new ArrayList<IPCameraNode>();
        mRootNode = IPCameraParser.parserIPCameras(ipCameraInfo, cameraNodes);
        mCurrentNode = mRootNode;
    }


    TextView currentSelection, fakeSelection;
    int stackedPos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_camera, container, false);
        mWheel = (WheelWidget)rootView.findViewById(R.id.wheel);
        mWheel.setCacheColorHint(Color.TRANSPARENT);
        currentSelection = (TextView) rootView.findViewById(R.id.current_selection);
        currentSelection.setOnClickListener(this);
        fakeSelection = (TextView) rootView.findViewById(R.id.fake_selection);
        myAdapter = new MyAdapter(getActivity(), getActivity().getLayoutInflater());
        mWheel.setAdapter(myAdapter);
        mWheel.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i("TEst", "state:" + scrollState);
                if(scrollState == SCROLL_STATE_IDLE) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWheel.scrollToCenterOfItem();
                        }
                    });
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.i("TEst", "visibleItemCount:" + visibleItemCount);
            }
        });

        mWheel.setOnItemClickListener(this);
        currentSelection.setText(null);

        final View searchCamera = rootView.findViewById(R.id.search_camera_view);
        searchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCamera.animate().alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        searchCamera.setVisibility(View.GONE);
                    }
                }).start();
            }
        });

        View searchTrigger = rootView.findViewById(R.id.search_tap_view);
        searchTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCamera.setAlpha(1);
                searchCamera.setTranslationY(-searchCamera.getHeight());
                searchCamera.setVisibility(View.VISIBLE);
                searchCamera.animate().translationY(0).start();
            }
        });


        return rootView;
    }

    private class MyAdapter extends BaseAdapter {
        private Context ctx;
        private LayoutInflater mInflater;
        public MyAdapter(Context context, LayoutInflater inflater)//, String[] data)
        {
            ctx = context;
            mInflater = inflater;
        }
        @Override
        public int getCount() {
            // How many items are in the data set represented by this Adapter.(在此适配器中所代表的数据集中的条目数)
            return (mCurrentNode != null) ? (mCurrentNode.mChildNodes.size() + 8) : 0;
        }

        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.(获取数据集中与指定索引对应的数据项)
            if(position < 4 || position >= (getCount() - 4)) return null;
            return (mCurrentNode != null) ? mCurrentNode.mChildNodes.values().toArray()[position - 4] : null;
        }

        @Override
        public long getItemId(int position) {
            // Get the row id associated with the specified position in the list.(取在列表中与指定索引对应的行id)
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            Node item = (Node)getItem(position);
            return item != null && !item.mIsLeafNode;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get a View that displays the data at the specified position in the data set.
            View view = convertView;
            if(view == null) {
                view = mInflater.inflate(R.layout.wheel_item_2, parent, false);
            }
            TextView content = (TextView) view.findViewById(R.id.content);
            Node item = (Node)getItem(position);
            content.setText(item != null ? item.mDisplayName : null);
            return view;
        }

    }

    @Override
    public void onClick(View view) {
        if((mCurrentNode!= null) && (mRootNode != null) && (mCurrentNode != mRootNode)) {
            {
                fakeSelection.setText(currentSelection.getText());
                fakeSelection.setVisibility(View.VISIBLE);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(ObjectAnimator.ofFloat(fakeSelection, "translationX",
                                currentSelection.getLeft() - fakeSelection.getLeft(), 0F),
                        ObjectAnimator.ofFloat(fakeSelection, "translationY", currentSelection.getTop() - fakeSelection.getTop(),  0F),
                        ObjectAnimator.ofFloat(fakeSelection, "alpha", 1.0f, 0.0f));
                set.setDuration(480);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fakeSelection.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                set.start();
            }
            {
                ObjectAnimator anim = ObjectAnimator.ofFloat(mWheel, "alpha",
                        1.0f, 0.0f);
                anim.setDuration(480);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCurrentNode = mCurrentNode.mParentNode.get();
                        currentSelection.setText(mCurrentNode != mRootNode ? mCurrentNode.mDisplayName : null);
                        myAdapter.notifyDataSetChanged();
                        mWheel.setSelection(stackedPos - 4);
                        ObjectAnimator anim = ObjectAnimator.ofFloat(mWheel, "alpha",
                                0f, 1.0f);
                        anim.setDuration(200);
                        anim.start();
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                anim.start();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Node item = (Node)myAdapter.getItem(position);
        if(item != null && !item.mIsLeafNode) {
            stackedPos = position;
            {
                fakeSelection.setText(item.mDisplayName);
                fakeSelection.setVisibility(View.VISIBLE);
                Log.i("xx", "fakeSelection.getTop():" + fakeSelection.getTop());
                AnimatorSet set = new AnimatorSet();
                set.playTogether(ObjectAnimator.ofFloat(fakeSelection, "translationX", 0F, currentSelection.getLeft() - fakeSelection.getLeft()),
                        ObjectAnimator.ofFloat(fakeSelection, "translationY", 0F, currentSelection.getTop() - fakeSelection.getTop()),
                        ObjectAnimator.ofFloat(fakeSelection, "alpha", 0.0f, 1.0f));
                set.setDuration(350);
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fakeSelection.setVisibility(View.INVISIBLE);
                        currentSelection.setText(item != mRootNode ? item.mDisplayName : null);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                set.start();
            }
            {
                ObjectAnimator anim = ObjectAnimator.ofFloat(mWheel, "alpha",
                        1.0f, 0.0f);
                anim.setDuration(200);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCurrentNode = item;
                        myAdapter.notifyDataSetChanged();
                        ObjectAnimator anim = ObjectAnimator.ofFloat(mWheel, "alpha",
                                0f, 1.0f);
                        anim.setDuration(200);
                        anim.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                anim.start();
            }
        }
    }
}
