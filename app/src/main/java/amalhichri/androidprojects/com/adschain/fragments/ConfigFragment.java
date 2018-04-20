package amalhichri.androidprojects.com.adschain.fragments;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.activities.MainActivity;
import amalhichri.androidprojects.com.adschain.adapters.ContactAdapter;
import amalhichri.androidprojects.com.adschain.models.Contact;
import amalhichri.androidprojects.com.adschain.utils.SMSService;
import amalhichri.androidprojects.com.adschain.utils.Statics;

import static android.content.Context.JOB_SCHEDULER_SERVICE;


public class ConfigFragment extends Fragment implements ContactAdapter.ContactsAdapterListener{

    private static List<Contact> contacts;
    private  RecyclerView rcv_cotact;
    private int smsNbLimit;
    private ContactAdapter contactAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.contacts = new ArrayList<>();
        fetchContacts();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_config_fragment, container, false);

        rcv_cotact = view.findViewById(R.id.rcv_contact);

/**------------------------ Choosing contacts ------------------------**/
        ((RadioButton)view.findViewById(R.id.limitedContactsChkBx)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // disable the first checkbox
                    /** give contacts permission if not granted **/
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                        int hasWriteContactsPermission = getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS);
                        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED  ) { //|| hasWriteSmsPermission != PackageManager.PERMISSION_GRANTED //|| hasWriteStatePermission != PackageManager.PERMISSION_GRANTED
                            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS }, //Manifest.permission.READ_PHONE_STATE
                                    1);
                            return;
                        }
                       // int hasWriteStatePermission =  getContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                    }
                    /** fetch contacts and show list **/
                    updateListWithContacts();
                    ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout1)).collapse();
                    ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout2)).expand();
                    ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout3)).collapse();
                }
            }
        });


        ((RadioButton)view.findViewById(R.id.unlimitedContactsChkBx)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                }
            }
        });

        (view.findViewById(R.id.saveContatcsBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout1)).expand();
                ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout2)).collapse();
                ((ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout3)).expand();
            }
        });

        /** filter on choosing contacts **/
        ((EditText)view.findViewById(R.id.edt_fltr)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final ArrayList<Contact> filteredList = new ArrayList<>();
                for (int i = 0; i < ConfigFragment.contacts.size(); i++) {
                    Toast.makeText(getContext(), "nums:  !"+ConfigFragment.contacts.get(i), Toast.LENGTH_LONG).show();
                  /*if (ConfigFragment.ConfigFragment.contacts.get(i).contains(s.toString())) {
                        filteredList.add(ConfigFragment.contacts.get(i));
                    }
                    contactAdapter.filterList(filreredList());
                    */
                }
               // contactAdapter.getFilter().filter(s.toString());
              //  Toast.makeText(getContext(), "adapter:  !"+contactAdapter.getFilter().toString(), Toast.LENGTH_LONG).show();
            }
        });
/**-------------------------- Setting SMS nb limit -------------------------- **/

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
                        Toast.makeText(getContext(), "will start sending !", Toast.LENGTH_LONG).show();
                        ComponentName componentName = new ComponentName(getContext(), SMSService.class);
                        JobInfo jobInfo = new JobInfo.Builder(1, componentName).setPeriodic(5000).build(); // setPeriodic(10000)
                        jobScheduler.schedule(jobInfo);
                    /** call turning on sending sms in background **/
                }
                if(!checked){
                    jobScheduler.cancelAll();
                    Toast.makeText(getContext(), "pending jobs:" +jobScheduler.getAllPendingJobs().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        /**-------------------------- Signout  --------------------------**/
        (view.findViewById(R.id.logoutTvw)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.auth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return view;

    }




/**-------------------------- Helper methods  --------------------------**/

    private void updateListWithContacts(){
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rcv_cotact.setLayoutManager(llm);
        contactAdapter= new ContactAdapter(getContext(),ConfigFragment.contacts ,this);
        rcv_cotact.setAdapter(contactAdapter);
    }
    public void fetchContacts() {
        String phoneNumber ;
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
                    ConfigFragment.contacts.add(c);
                }
            }
        }
    }
    @Override
    public void onContactSelected(Contact contact) {
        Toast.makeText(getContext(), "Selected: " + contact.getNom() + ", " + contact.getNum(), Toast.LENGTH_LONG).show();
    }
}
