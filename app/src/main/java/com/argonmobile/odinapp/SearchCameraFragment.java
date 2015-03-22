package com.argonmobile.odinapp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.argonmobile.odinapp.view.WheelWidget;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchCameraFragment extends Fragment {


    private WheelWidget mWheel;
    private MyAdapter myAdapter;

    public SearchCameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_camera, container, false);
        mWheel = (WheelWidget)rootView.findViewById(R.id.wheel);
        mWheel.setCacheColorHint(Color.TRANSPARENT);
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
        return rootView;
    }

    private class MyAdapter extends BaseAdapter {
        private String[] mLocation = new String[] {
                "", "", "", "",
                "天元西路", "龙眠大道", "宏图上水园", "清水停东路", "天地新城天蝎座", "软件大道18号", "将军大道56号", "学院路", "仙林大道",
                "", "", "", ""
        };
        private Context ctx;
        private LayoutInflater mInflater;
        public MyAdapter(Context context, LayoutInflater inflater)
        {
            ctx = context;
            mInflater = inflater;
        }
        @Override
        public int getCount() {
            // How many items are in the data set represented by this Adapter.(在此适配器中所代表的数据集中的条目数)
            return mLocation.length;
        }

        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.(获取数据集中与指定索引对应的数据项)
            return mLocation[position];
        }

        @Override
        public long getItemId(int position) {
            // Get the row id associated with the specified position in the list.(取在列表中与指定索引对应的行id)
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;//(mWheel.getEnabledItemIndex() == position) ? true : false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get a View that displays the data at the specified position in the data set.
            View view = convertView;
            if(view == null) {
                view = mInflater.inflate(R.layout.wheel_item_2, parent, false);
            }
            TextView content = (TextView) view.findViewById(R.id.content);
            content.setText(mLocation[position]);
            return view;
        }

    }

}
