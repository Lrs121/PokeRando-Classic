package org.lrsservers.pokerando.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.lrsservers.pokerando.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    int totalTabs;

    public SectionsPagerAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        totalTabs = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       switch (position){
           case 0:
                RomInfoFragment romInfoFragment = new RomInfoFragment();
                return romInfoFragment;
           default:
               return null;
       }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return totalTabs;
    }
}
