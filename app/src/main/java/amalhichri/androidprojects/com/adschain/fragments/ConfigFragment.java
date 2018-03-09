package amalhichri.androidprojects.com.adschain.fragments;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.adapters.ContactAdapter;
import amalhichri.androidprojects.com.adschain.models.Contact;
import amalhichri.androidprojects.com.adschain.utils.SMSService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;


public class ConfigFragment extends Fragment {

    List<Contact> contacts;
    private  RecyclerView rcv_cotact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_config_fragment, container, false);

        rcv_cotact = view.findViewById(R.id.rcv_contact);
        ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout)).collapse();

/**------------------------ Choosing contacts ------------------------**/
        ((CheckBox)view.findViewById(R.id.limitedContactsChkBx)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    /** give contacts permission if not granted **/
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                        int hasWriteContactsPermission = getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS);
                        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED  ) { //|| hasWriteSmsPermission != PackageManager.PERMISSION_GRANTED //|| hasWriteStatePermission != PackageManager.PERMISSION_GRANTED
                            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS }, //Manifest.permission.READ_PHONE_STATE
                                    1);
                            return;
                        }
                       //int hasWriteStatePermission =  getContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                    }
                    /** fetch contacts and show list **/
                    fetchContacts();
                    ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout)).expand();
                }
            }
        });
        (view.findViewById(R.id.unlimitedContactsChkBx)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        (view.findViewById(R.id.saveContatcsBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout)).collapse();
            }
        });
        /** filter on choosing contacts **/
        ((EditText)view.findViewById(R.id.edt_fltr)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    String n = String.valueOf(((EditText)view.findViewById(R.id.edt_fltr)).getText().toString().charAt(0));

                    if (s.length() == 1) {
                        if (n.equals("9") || n.equals("2") || n.equals("5") || n.equals("4")) {
                            //Toast.makeText(getContext(), "chaged :: n="+n, Toast.LENGTH_SHORT).show();
                            setFilter(n);
                        }
                    } else {
                        if (n.equals("9") || n.equals("2") || n.equals("5") || n.equals("4")) {
                            n += String.valueOf(((EditText)view.findViewById(R.id.edt_fltr)).getText().toString().charAt(1));
                            setFilter(n);
                        }
                    }
                }
            }
        });

/**-------------------------- Setting SMS nb limit --------------------------**/
        (view.findViewById(R.id.unlimitedSmsChkBx)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        (view.findViewById(R.id.limitedSmsChkBx)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

/**-------------------------- Turning sending off/on  --------------------------**/
        ((Switch)view.findViewById(R.id.stopSdingSms)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
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
                    }
                    /** sending sms , this is just a test, will configure it with number of sms/contacts **/
                    if(((TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName().equals("Orange Tn"))
                    {
                        Toast.makeText(getContext(), "will start sending !", Toast.LENGTH_LONG).show();
                        ComponentName componentName = new ComponentName(getContext(), SMSService.class);
                        JobInfo jobInfo = new JobInfo.Builder(1, componentName).setPeriodic(5000).build(); // setPeriodic(10000)
                        jobScheduler.schedule(jobInfo);
                    }
                    else{
                        Toast.makeText(getContext(), "You have no free SMS plans !", Toast.LENGTH_LONG).show();
                    }
                    /** call turning on sending sms in background **/
                }
                if(!checked){
                    jobScheduler.cancelAll();
                    Toast.makeText(getContext(), "pending jobs:" +jobScheduler.getAllPendingJobs().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
/**-------------------------- Helper methods  --------------------------**/

    public void fetchContacts() {
        contacts = new ArrayList<>();
        String phoneNumber = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                Contact c = new Contact();
                if (hasPhoneNumber > 0) {
                    output.append("\n First Name:" + name);
                    c.setEtat(true);
                    c.setNom(name);
                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number:" + phoneNumber);
                        c.setNum(phoneNumber);
                    }
                    phoneCursor.close();
                    contacts.add(c);
                }

            }
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            rcv_cotact.setLayoutManager(llm);
            ContactAdapter adp = new ContactAdapter(contacts , getContext());
            rcv_cotact.setAdapter(adp);

        }

    }
    private void setFilter(String f){
        Toast.makeText(getContext(), "f in filter = "+f, Toast.LENGTH_SHORT).show();
        for (Contact c: contacts) {
            if(f.length() == 1){
                if(String.valueOf(c.getNum().charAt(0)).equals(String.valueOf(f.charAt(0)))){
                    c.setEtat(true);
                    //Toast.makeText(getContext(), "chaged :: ok", Toast.LENGTH_SHORT).show();
                }else{
                    c.setEtat(false);
                    //Toast.makeText(getContext(), "chaged :: NO", Toast.LENGTH_SHORT).show();
                }
            }else{
                if(String.valueOf(c.getNum().charAt(0)).equals(String.valueOf(f.charAt(0))) && String.valueOf(c.getNum().charAt(1)).equals(String.valueOf(f.charAt(1)))){
                    c.setEtat(true);
                }else{
                    c.setEtat(false);
                }
            }
        }
        rcv_cotact.getAdapter().notifyDataSetChanged();

    }

    private void startSmsProcess(){

    }
}
