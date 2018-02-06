package amalhichri.androidprojects.com.adschain.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import amalhichri.androidprojects.com.adschain.fragments.OthersFragment;
import amalhichri.androidprojects.com.adschain.fragments.WalletFragment;
import amalhichri.androidprojects.com.adschain.fragments.ConfigFragment;


public class HomePageTabsAdapter extends FragmentPagerAdapter {

    private int NUM_ITEMS = 3;

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
                /** because to switch fragments inside a tab we need a root FrameLayout,
                 in which we load fragments in each time ( getFragmentManager.replce(root,newFrag) )**/
                return new WalletFragment();
            case 1:
                return new ConfigFragment();
            case 2:
                return new OthersFragment();
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
