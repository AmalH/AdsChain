package amalhichri.androidprojects.com.adschain.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;

import amalhichri.androidprojects.com.adschain.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SplashActivity extends Activity  {

    private  Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /** if user is logged in --> homeActivity **/
        SharedPreferences twoauthprefs = getSharedPreferences("2FA", 0);
        String twoauthStatus = twoauthprefs.getString("status", "");
        if ((FirebaseAuth.getInstance().getCurrentUser() != null) && (twoauthStatus.equals("finished")))
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

        else{
            animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.motto_slide);
            (findViewById(R.id.motto)).startAnimation(animation);

            /** splash screen **/
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(2500);
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
        }

    /*****************************************************************************
     * * Utils
     * **************************************************************************/

    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
