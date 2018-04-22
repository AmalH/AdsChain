package amalhichri.androidprojects.com.adschain.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Amal on 25/02/2018.
 */

public class  SMSService extends JobService {

    JobParameters params;
    DoItTask doIt;

    private static ArrayList<String> sendTo;


    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        sendTo = new ArrayList<String>(Arrays.asList(params.getExtras().getStringArray("selectedContacts")));
        //Toast.makeText(this, "Value1 :  "+  params.getExtras().getStringArray("selectedContacts").length, Toast.LENGTH_LONG).show();
        doIt = new DoItTask();
        doIt.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(this, "TestService : System calling to stop the job here", Toast.LENGTH_LONG).show();
        if (doIt != null)
            doIt.cancel(true);
        return false;
    }

    private class DoItTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(),"Clean up the task here and call jobFinished...",Toast.LENGTH_LONG).show();
            jobFinished(params, false);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
           // Toast.makeText(this, "Value1 :  "+  params.getExtras().getStringArray("selectedContacts").length, Toast.LENGTH_LONG).show();
            sendSms();
            return null;
        }
    }

    // send sms in background
    private void sendSms()
    {
       // Toast.makeText(getApplicationContext(), "Value1 :  "+  sendTo.toString(), Toast.LENGTH_LONG).show();

        Log.d("TEST TEST",sendTo.toString());
        if(isSimExists())
        {
            try
            {
                final PendingIntent sentPI = PendingIntent.getBroadcast(getBaseContext(), 0, new Intent("SMS_SENT"), 0);
               for(int i=0;i<sendTo.size();i++){
                   SmsManager.getDefault().sendTextMessage(sendTo.get(i), null,   "error", sentPI, null);
                   getApplicationContext().registerReceiver(new BroadcastReceiver() {
                       @Override
                       public void onReceive(Context arg0, Intent arg1) {
                           int resultCode = getResultCode();
                           switch (resultCode) {
                               case Activity.RESULT_OK:
                                   Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_LONG).show();
                                   break;
                               case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                   Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_LONG).show();
                                   break;
                               case SmsManager.RESULT_ERROR_NO_SERVICE:
                                   Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_LONG).show();
                                   break;
                               case SmsManager.RESULT_ERROR_NULL_PDU:
                                   Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_LONG).show();
                                   break;
                               case SmsManager.RESULT_ERROR_RADIO_OFF:
                                   Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_LONG).show();
                                   break;
                           }
                       }
                   }, new IntentFilter("SMS_SENT"));
               }


            }
            catch (Exception e)
            {
                Toast.makeText(this, e.getMessage()+"!\n"+"Failed to send SMS", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,  "error " + "Cannot send SMS", Toast.LENGTH_LONG).show();
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


}
