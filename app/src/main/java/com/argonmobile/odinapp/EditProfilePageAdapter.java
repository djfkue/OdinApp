package com.argonmobile.odinapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by argon on 3/11/15.
 */
public class EditProfilePageAdapter extends FragmentStatePagerAdapter {

    SparseArray<Fragment> mRegisteredFragments = new SparseArray<Fragment>();

    public EditProfilePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        Fragment fragment = null;
        if (i == 0) {
            fragment = new DemoObjectFragment();
        }

        if (i == 1) {
            fragment = new EditProfileFragment();
        }
        if (i == 2) {
            fragment = new DemoObjectFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
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
                    R.layout.fragment_search_camera, container, false);
//            Bundle args = getArguments();
//            ((TextView) rootView.findViewById(R.id.text_view)).setText(
//                    Integer.toString(args.getInt(ARG_OBJECT)));
            rootView.setBackgroundResource(R.color.primary_dark_material_dark);
            return rootView;
        }
    }
}
