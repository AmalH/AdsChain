package amalhichri.androidprojects.com.adschain.fragments;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.activities.MainActivity;
import amalhichri.androidprojects.com.adschain.adapters.ContactsAdapter;
import amalhichri.androidprojects.com.adschain.models.Contact;
import amalhichri.androidprojects.com.adschain.utils.SMSService;
import amalhichri.androidprojects.com.adschain.utils.Statics;

import static android.content.Context.JOB_SCHEDULER_SERVICE;


public class ConfigFragment extends Fragment{

    private static ArrayList<Contact> contacts;
    private  RecyclerView rcv_cotact;
    private int smsNbLimit;
    private JobScheduler jobScheduler;
    private ExpandableRelativeLayout expandableLayout1,expandableLayout2,expandableLayout3;
    private boolean isSelecedContacts=false;
    private boolean isAllContacts=false;
    private EditText searchEdtTxt;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.contacts = new ArrayList<>();
        jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        isSelecedContacts=false;
        isAllContacts=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_config_fragment, container, false);

        expandableLayout1 = (view.findViewById(R.id.expandableLayout1));
        expandableLayout2 =(view.findViewById(R.id.expandableLayout2));
        expandableLayout3 = (view.findViewById(R.id.expandableLayout3));
        searchEdtTxt = (view.findViewById(R.id.etSearch));


        rcv_cotact = view.findViewById(R.id.rvItems);

        (view.findViewById(R.id.saveContatcsBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout1)).expand();
                ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout2)).collapse();
                ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout3)).expand();
            }
        });

/**------------------------------------------- Choosing contacts --------------------------------------------------------**/

        ((RadioButton)view.findViewById(R.id.limitedContactsChkBx)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isSelecedContacts=true;
                    isAllContacts=false;
                    /** give contacts permission if not granted **/
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                        int hasWriteContactsPermission = getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS);
                        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED  ) { //|| hasWriteSmsPermission != PackageManager.PERMISSION_GRANTED //|| hasWriteStatePermission != PackageManager.PERMISSION_GRANTED
                            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS }, //Manifest.permission.READ_PHONE_STATE
                                    1);
                            return;
                        }
                        else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED  ){
                            /** fetch contacts and show list **/
                            fetchContacts();
                            // updateListWithContacts();
                            expandableLayout1.collapse();
                            expandableLayout2.expand();
                            expandableLayout3.collapse();
                        }
                       // int hasWriteStatePermission =  getContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                    }
                }
            }
        });

        ((RadioButton)view.findViewById(R.id.unlimitedContactsChkBx)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isSelecedContacts=false;
                    isAllContacts=true;
                    /** give contacts permission if not granted **/
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                        int hasWriteContactsPermission = getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS);
                        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED  ) { //|| hasWriteSmsPermission != PackageManager.PERMISSION_GRANTED //|| hasWriteStatePermission != PackageManager.PERMISSION_GRANTED
                            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS }, //Manifest.permission.READ_PHONE_STATE
                                    0);
                            return;
                        }
                        else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED  ){
                            prepareSendingToAll();
                        }
                    }
                }
            }
        });


/**------------------------------------------------ Setting SMS nb limit -------------------------------------------------- **/

        (view.findViewById(R.id.unlimitedSmsChkBx)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsNbLimit=-1;
            }
        });

        (view.findViewById(R.id.limitedSmsChkBx)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // ((view.findViewById(R.id.limitedSmsEditTxt))).requestFocus();
                if(!(((EditText)(view.findViewById(R.id.limitedSmsEditTxt))).getText().toString()).equals(""))
                    smsNbLimit=Integer.parseInt(((EditText)(view.findViewById(R.id.limitedSmsEditTxt))).getText().toString());
            }
        });


/**---------------------------------------------- Turning sending off/on  --------------------------------------------------**/
        ((Switch)view.findViewById(R.id.stopSdingSms)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if(checked){
                    /** give sms permission if not granted **/
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                        int hasWriteSmsPermission =  getContext().checkSelfPermission(Manifest.permission.SEND_SMS);
                        if (hasWriteSmsPermission != PackageManager.PERMISSION_GRANTED  ) {
                            requestPermissions(new String[] {Manifest.permission.SEND_SMS },
                                    2);
                            return;
                        }
                        else if (hasWriteSmsPermission == PackageManager.PERMISSION_GRANTED  ){
                            sendingOnOff();
                        }
                    }
                }
                if(!checked){
                    jobScheduler.cancelAll();
                    Toast.makeText(getContext(), "Stopped sending SMSs" +jobScheduler.getAllPendingJobs().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        /**-------------------------------------------------------------- Signout  ------------------------------------------**/
        (view.findViewById(R.id.logoutTvw)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.auth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            /** reading contacts permission **/
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareSendingToAll();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /** fetch contacts and show list **/
                    fetchContacts();
                    // updateListWithContacts();
                    expandableLayout1.collapse();
                    expandableLayout2.expand();
                    expandableLayout3.collapse();

                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            /** sending sms permission **/
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  sendingOnOff();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


/**-------------------------- Helper methods  --------------------------**/


   private void prepareSendingToAll(){
      SharedPreferences sendToShPfs = getContext().getSharedPreferences("sendToListAll",0);
      SharedPreferences.Editor editor= sendToShPfs.edit();
       String phoneNumber ;
       Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
       String _ID = ContactsContract.Contacts._ID;
       String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
       String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
       Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
       String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
       String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

       ContentResolver contentResolver = getContext().getContentResolver();
       Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
       if (cursor.getCount() > 0) {
           while (cursor.moveToNext()) {
               String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
               String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
               int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
               Contact c = new Contact();
               if (hasPhoneNumber > 0) {
                   c.setNom(name);
                   Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                   while (phoneCursor.moveToNext()) {
                       phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                       c.setNum(phoneNumber);
                   }
                   phoneCursor.close();
                   //ConfigFragment.contacts.add(c);
               }
               //sendToAll.add(c.getNum());
               editor.putString(c.getNum(),c.getNum());
               editor.commit();
           }
       }
       Toast.makeText(getContext(), "All contacts: "+cursor.getCount() , Toast.LENGTH_LONG).show();
   }

   private void sendingOnOff(){
       /** pass selected contacts to jobScheduler **/
       PersistableBundle bundle = new PersistableBundle();
       /** if it's all contacts **/
       /*if(isSelecedContacts){*/
           Log.d("Gonna Crush :D","hh");

           List<String> sendTo = new ArrayList<>();
           SharedPreferences sendToListShp = getContext().getSharedPreferences("sendToList",0);
           Map<String,?> keys = sendToListShp.getAll();
           for(Map.Entry<String,?> entry : keys.entrySet()){
               sendTo.add(entry.getValue().toString());
           }
           //bundle.clear();
           Toast.makeText(getContext(),"Selected: "+sendTo.toArray(new String[sendTo.size()]).toString(),Toast.LENGTH_LONG).show();
           bundle.putStringArray("selectedContacts",sendTo.toArray(new String[sendTo.size()]));
       //}

       /** if it's selected contacts
       else if(isAllContacts){
                                   List<String> sendToAll = new ArrayList<>();
                                    SharedPreferences sendToListShpAll = getContext().getSharedPreferences("sendToListAll",0);
                                    Map<String,?> keys = sendToListShpAll.getAll();
                                    for(Map.Entry<String,?> entry : keys.entrySet()){
                                        sendToAll.add(entry.getValue().toString());
                                    }
                                    //bundle.clear();
                                    bundle.putStringArray("selectedContacts",sendToAll.toArray(new String[sendToAll.size()]));
                                    Log.d("TEST TEST ","hh"+bundle.getStringArray("selectedContacts").toString());
       }**/
       /** sending sms , this is just a test, will configure it with number of sms/contacts **/
       Toast.makeText(getContext(), "sms sent 2", Toast.LENGTH_LONG).show();
       /** ------------------------- TEST TEST ----------------------------- **/
       ComponentName componentName = new ComponentName(getContext(), SMSService.class);
       JobInfo jobInfo = new JobInfo.Builder(1, componentName)
               .setExtras(bundle)
               .setPeriodic(5000).build(); // setPeriodic(10000)
       jobScheduler.schedule(jobInfo);
   }

   private void fetchContacts(){

       rcv_cotact.setLayoutManager(new LinearLayoutManager(getContext()));

       ArrayList<String> countryList = new ArrayList<>();
       ArrayList<String> numsList = new ArrayList<>();


       String phoneNumber ;
       Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
       String _ID = ContactsContract.Contacts._ID;
       String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
       String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
       Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
       String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
       String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

       ContentResolver contentResolver = getContext().getContentResolver();
       Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

       if (cursor.getCount() > 0) {
           while (cursor.moveToNext()) {
               String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
               String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
               int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
               Contact c = new Contact();
               if (hasPhoneNumber > 0) {
                   c.setNom(name);
                   Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                   while (phoneCursor.moveToNext()) {
                       phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                       c.setNum(phoneNumber);
                   }
                   phoneCursor.close();
                   ConfigFragment.contacts.add(c);
               }
           }
       }

       for (Contact c:ConfigFragment.contacts) {
           countryList.add(c.getNom());
           numsList.add(c.getNum());
       }
       final ContactsAdapter adapter = new ContactsAdapter(getContext(), ConfigFragment.contacts);
       rcv_cotact.setAdapter(adapter);

       searchEdtTxt.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               adapter.filter(s);
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

   }
}
