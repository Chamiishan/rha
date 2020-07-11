package roadcondition.cynsore.cyient.com.cynsore.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ij39559 on 1/10/2019.
 */

public class VerificationPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public VerificationPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}