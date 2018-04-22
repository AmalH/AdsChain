package amalhichri.androidprojects.com.adschain.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.models.Contact;

public class ContactAdapter extends  RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements Filterable{

    private Context context;
    private List<Contact> contactList;
    private List<Contact> contactListFiltered;
    private ContactsAdapterListener listener;
    private int lastPosition = -1;
    SharedPreferences sendToShPfs;
    SharedPreferences.Editor spEditor;


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txt_contact ,txt_num;
        CheckBox ck;
        public ContactViewHolder(View itemView) {
            super(itemView);
            txt_contact = itemView.findViewById(R.id.txt_name);
            txt_num = itemView.findViewById(R.id.txt_num);
            ck =  itemView.findViewById(R.id.ck);
        }
    }

    public ContactAdapter(Context context, List<Contact> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
        this.sendToShPfs = context.getSharedPreferences("sendToList",0);
        this.spEditor = sendToShPfs.edit();
    }

    public void filterList(ArrayList<Contact> filteredList) {
        this.contactList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ContactAdapter.ContactViewHolder h =new ContactAdapter.ContactViewHolder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, int position) {
        holder.txt_contact.setText(contactList.get(position).getNom());
        holder.txt_num.setText(contactList.get(position).getNum());
        //holder.ck.setChecked(contactList.get(position).isEtat());
        final int p = position;
        holder.ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if(isChecked){
                       contactList.get(p).setEtat(isChecked);
                       spEditor.putString(contactList.get(p).getNom(),contactList.get(p).getNum());
                       spEditor.commit();
                   }
                   if(!isChecked){
                       spEditor.remove(contactList.get(p).getNom());
                       spEditor.commit();
                   }

               }
          }
        );
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.enter_from_bottom_item);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<Contact> filteredList = new ArrayList<>();
                    for (Contact row : contactList) {
                        //row.getNom().toLowerCase().contains(charString.toLowerCase()) ||
                        if ( row.getNum().contains(charSequence)) {
                            filteredList.add(row);
                            Toast.makeText(context, "Selected: " + row.getNum(), Toast.LENGTH_LONG).show();
                        }
                    }
                    contactListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }




public interface ContactsAdapterListener {
        void onContactSelected(Contact contact);
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }
}
