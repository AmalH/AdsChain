package amalhichri.androidprojects.com.adschain.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import amalhichri.androidprojects.com.adschain.fragments.ConfigFragment;
import amalhichri.androidprojects.com.adschain.fragments.WalletFragment;


public class HomePageTabsAdapter extends FragmentPagerAdapter {

    private int NUM_ITEMS = 2;

        public HomePageTabsAdapter(FragmentManager fm) {
            super(fm);
        }

    @Override
    public int getCount() {
        return  NUM_ITEMS ;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ConfigFragment();
            case 1:
                return new WalletFragment();
            default:
                return null;
        }
    }

    /** needed for fragment refresh **/
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
