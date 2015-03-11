package com.argonmobile.odinapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by argon on 3/11/15.
 */
public class FindCameraPageAdapter extends FragmentStatePagerAdapter {
    public FindCameraPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment = null;
        if (i == 0) {
            fragment = new SearchCameraFragment();
        }

        if (i == 1) {
            fragment = new FindCameraGridFragment();
        }
        if (i == 2) {
            fragment = new RecentCameraGridFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class DemoObjectFragment extends Fragment {
        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_item_grid, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.text_view)).setText(
                    Integer.toString(args.getInt(ARG_OBJECT)));
            return rootView;
        }
    }
}
