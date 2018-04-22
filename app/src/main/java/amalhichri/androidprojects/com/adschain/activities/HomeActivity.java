package amalhichri.androidprojects.com.adschain.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.adapters.HomePageTabsAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {


    private static TabLayout tablayout;
    private static ViewPager vpPager;
    private HomePageTabsAdapter adapter;
    private static ColorMatrixColorFilter filter;
    private ColorMatrix matrix;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager =getSupportFragmentManager();

        /** will be used to change tab icons colors on select/deselect */
        matrix = new ColorMatrix();
        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);

        /** setting the actionBar **/
        getSupportActionBar().hide();

        /*** setting the tabsLayout ***/
        adapter = new HomePageTabsAdapter(getSupportFragmentManager());
        vpPager = findViewById(R.id.viewpager);
        vpPager.setAdapter(adapter);
        vpPager.setCurrentItem(0);
        vpPager.setOffscreenPageLimit(2);
        tablayout=findViewById(R.id.tabsLayout);
       tablayout.post(new Runnable() {
           @Override
           public void run() {
               setIndicator(tablayout,40,40);
           }
       });
        tablayout.setupWithViewPager(vpPager);
        setUpTabIcons(tablayout);


        /** change title in actionBar depending on tabSelected **/
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch(tablayout.getSelectedTabPosition()) {
                    case 0:
                        tab.getIcon().clearColorFilter();
                        break;
                    case 1:
                        tab.getIcon().clearColorFilter();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(filter);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }





    /*** helper methods ***/
    private static void allTabIconsToDeselected(TabLayout tablayout){
        for(int i=1;i<2;i++){
            tablayout.getTabAt(i).getIcon().setColorFilter(filter);
        }
    }
    private static void setUpTabIcons(TabLayout tbs){
        tbs.getTabAt(0).setIcon(R.drawable.ic_wallet);
        tbs.getTabAt(1).setIcon(R.drawable.ic_configurations);
        allTabIconsToDeselected(tbs);
    }
    /** to prevent back to loginActivity **/
    @Override
    public void onBackPressed() {
           fragmentManager.popBackStack();
    }

    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setIndicator (TabLayout tabs,int leftDip,int rightDip){
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

}