package amalhichri.androidprojects.com.adschain.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.models.Contact;
import amalhichri.androidprojects.com.adschain.utils.RvSearchDemoHolder;


public class ContactsAdapter extends RecyclerView.Adapter<RvSearchDemoHolder> {
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
    public RvSearchDemoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RvSearchDemoHolder(inflater.inflate(R.layout.item_country, parent, false));
    }

    @Override
    public void onBindViewHolder(RvSearchDemoHolder holder, int position) {
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
        //nums.clear();
        //nums.addAll(tempNums);
        notifyDataSetChanged();
        temp.clear();
        //tempNums.clear();
    }
}

