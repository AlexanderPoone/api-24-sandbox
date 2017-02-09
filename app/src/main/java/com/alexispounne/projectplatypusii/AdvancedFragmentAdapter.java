package com.alexispounne.projectplatypusii;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Alexandre on 21/01/2017.
 */
public class AdvancedFragmentAdapter extends FragmentStatePagerAdapter {

    public AdvancedFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return AdvancedFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab " + position;
    }
}