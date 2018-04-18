package amalhichri.androidprojects.com.adschain.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.verify.client.VerifyClient;

import amalhichri.androidprojects.com.adschain.R;

/**
 * Created by Amal on 18/04/2018.
 */

public class TwoFactorApplication extends Application {
    private VerifyClient verifyClient;
    private NexmoClient nexmoClient;
    private boolean verified;
    private static final String TAG = "TWOFACTORAPPLICATION";
    public VerifyClient getVerifyClient(boolean verifiedValue) {
        verified = verifiedValue;
        return this.verifyClient;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        acquireVerifyClient();
    }
    public void acquireVerifyClient() {
        Context context = getApplicationContext();
        try {
            this.nexmoClient = new NexmoClient.NexmoClientBuilder()
                    .context(context)
                    .applicationId(getResources().getString(R.string.nexmo_application_id))
                    .sharedSecretKey(getResources().getString(R.string.nexmo_shared_secret))
                    .build();
        } catch (ClientBuilderException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return;
        }
        this.verifyClient = new VerifyClient(nexmoClient);
    }
}