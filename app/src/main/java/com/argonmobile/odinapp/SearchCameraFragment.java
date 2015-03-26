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

import com.argonmobile.odinapp.view.WheelWidget;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchCameraFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final static int LEVEL_DISTRICT = 0;
    private final static int LEVEL_STREET = 1;

    private String[] mLocation_streets = new String[] {
            "", "", "", "",
            "天元西路", "龙眠大道", "宏图上水园", "清水停东路", "天地新城天蝎座", "软件大道18号", "将军大道56号", "学院路", "仙林大道",
            "", "", "", ""
    };
    private String[] mLocation_districts = new String[] {
            "", "", "", "",
            "建邺区", "栖霞区", "鼓楼区", "白下区", "浦口区", "雨花区", "江宁区", "雨花区", "玄武区", "六合区", "秦淮区",
            "", "", "", ""
    };

    private WheelWidget mWheel;
    private MyAdapter myAdapter;

    public SearchCameraFragment() {
        // Required empty public constructor
    }


    TextView currentSelection, fakeSelection;
    int mCurrentLevel;
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
        mCurrentLevel = LEVEL_DISTRICT;
        myAdapter = new MyAdapter(getActivity(), getActivity().getLayoutInflater(), mLocation_districts);
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

    private class MyAdapter extends BaseAdapter   {
        private String[] data;
        private Context ctx;
        private LayoutInflater mInflater;
        public MyAdapter(Context context, LayoutInflater inflater, String[] data)
        {
            ctx = context;
            mInflater = inflater;
            this.data = data;
        }
        public void updateData(String[] data) {
            this.data = data;
            this.notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            // How many items are in the data set represented by this Adapter.(在此适配器中所代表的数据集中的条目数)
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the data set.(获取数据集中与指定索引对应的数据项)
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            // Get the row id associated with the specified position in the list.(取在列表中与指定索引对应的行id)
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;//(mWheel.getEnabledItemIndex() == position) ? true : false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get a View that displays the data at the specified position in the data set.
            View view = convertView;
            if(view == null) {
                view = mInflater.inflate(R.layout.wheel_item_2, parent, false);
            }
            TextView content = (TextView) view.findViewById(R.id.content);
            content.setText(data[position]);
            return view;
        }

    }

    @Override
    public void onClick(View view) {
        if(mCurrentLevel == LEVEL_STREET) {
            {
                fakeSelection.setText(currentSelection.getText());
                fakeSelection.setVisibility(View.VISIBLE);
                currentSelection.setText(null);
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
                        myAdapter.updateData(mLocation_districts);
                        mCurrentLevel = LEVEL_DISTRICT;
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
        if(mCurrentLevel == LEVEL_DISTRICT) {
            stackedPos = position;
            {
                fakeSelection.setText(mLocation_districts[position]);
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
                        currentSelection.setText(mLocation_districts[stackedPos]);
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
                        myAdapter.updateData(mLocation_streets);
                        mCurrentLevel = LEVEL_STREET;
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
