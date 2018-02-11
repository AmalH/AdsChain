package amalhichri.androidprojects.com.adschain.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

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
    private String SimState = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager =getSupportFragmentManager();

        /** sart sending sms in background **/
        final int PERMISSION_REQUEST_CODE = 1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            } else {
                sendSms();
            }
        }

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
                    case 2:
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
        for(int i=1;i<3;i++){
            tablayout.getTabAt(i).getIcon().setColorFilter(filter);
        }
    }
    private static void setUpTabIcons(TabLayout tbs){
        tbs.getTabAt(0).setIcon(R.drawable.ic_wallet_tab2);
        tbs.getTabAt(1).setIcon(R.drawable.ic_configurations_tab1);
        tbs.getTabAt(2).setIcon(R.drawable.ic_others);
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


    /***  sending sms **/
    private void sendSms()
    {
        if(isSimExists())
        {
            try
            {
                String SENT      = "SMS_SENT";
                final PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
                final SmsManager smsMgr = SmsManager.getDefault();
                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                        public void run() {
                            smsMgr.sendTextMessage("+21654821200", null, "hello", sentPI, null);
                        }
                        }, 8000);
                registerReceiver(new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context arg0, Intent arg1)
                    {
                        int resultCode = getResultCode();
                        switch (resultCode)
                        {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS sent",Toast.LENGTH_LONG).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  Toast.makeText(getBaseContext(), "Generic failure",Toast.LENGTH_LONG).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:       Toast.makeText(getBaseContext(), "No service",Toast.LENGTH_LONG).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:         Toast.makeText(getBaseContext(), "Null PDU",Toast.LENGTH_LONG).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:        Toast.makeText(getBaseContext(), "Radio off",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }, new IntentFilter(SENT));


            }
            catch (Exception e)
            {
                Toast.makeText(this, e.getMessage()+"!\n"+"Failed to send SMS", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this, SimState+ " " + "Cannot send SMS", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
            return true;
        else {
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT: // SimState =
                    // "No Sim Found!";
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: // SimState =
                    // "Network Locked!";
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED: // SimState =
                    // "PIN Required to access SIM!";
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED: // SimState =
                    // "PUK Required to access SIM!";
                    // // Personal
                    // Unblocking Code
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN: // SimState =
                    // "Unknown SIM State!";
                    break;
            }
            return false;
        }
    }
    // For receiving sms

    class SMSReceiver extends BroadcastReceiver
    {
        private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent != null && intent.getAction() != null && ACTION.compareToIgnoreCase(intent.getAction()) == 0)
            {
                // Sms Received Your code here
            }
        }
    }

}