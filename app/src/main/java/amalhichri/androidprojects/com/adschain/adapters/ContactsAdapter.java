package amalhichri.androidprojects.com.adschain.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.models.Contact;
import amalhichri.androidprojects.com.adschain.utils.ContactViewHolder;


public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {
    private ArrayList<String> countries;
    private ArrayList<String> nums;
    private ArrayList<String> countriesCopy;
    private ArrayList<String> numsCopy;
    private LayoutInflater inflater;

    private ArrayList<Contact> contacts;
    private ArrayList<Contact> contactsCopy;



    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        this.contacts=contacts;
        this.contactsCopy = new ArrayList<>();
        contactsCopy.addAll(contacts);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        final SharedPreferences sendToListShp = parent.getContext().getSharedPreferences("sendToList",0);
        final SharedPreferences.Editor editor = sendToListShp.edit();
        ((CheckBox)v.findViewById(R.id.ctcCkBx)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                   // Toast.makeText(parent.getContext(), "Checked "+((TextView)v.findViewById(R.id.tvNum)).getText().toString(), Toast.LENGTH_LONG).show();
                    editor.putString("phoneNumber",((TextView)v.findViewById(R.id.tvNum)).getText().toString());
                    editor.commit();
                }if(!isChecked){
                }
            }
        });
        ContactViewHolder h = new ContactViewHolder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.bind(contacts.get(position).getNom(),contacts.get(position).getNum());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void filter(CharSequence sequence) {
        ArrayList<Contact> temp = new ArrayList<>();
        if (!TextUtils.isEmpty(sequence)) {
            for (Contact c: contacts) {
                if ((c.getNum().toLowerCase().startsWith(sequence.toString()))) {
                    temp.add(c);
                }
            }
        } else {
            temp.addAll(contactsCopy);
        }
        contacts.clear();
        contacts.addAll(temp);
        notifyDataSetChanged();
        temp.clear();
    }
}

