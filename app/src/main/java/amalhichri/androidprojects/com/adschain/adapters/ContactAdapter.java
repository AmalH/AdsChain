package amalhichri.androidprojects.com.adschain.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.models.Contact;

/**
 * Created by Worm-root on 29/01/2018.
 */

public class ContactAdapter extends  RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    List<Contact> data;
    Context ctx;
    boolean mode;
    private int lastPosition = -1;

    public ContactAdapter(List<Contact> data, Context ctx ) {
        this.data = data;
        this.ctx = ctx;

    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        ContactAdapter.ContactViewHolder h =new ContactAdapter.ContactViewHolder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, int position) {
        holder.txt_contact.setText(data.get(position).getNom());
        holder.txt_num.setText(data.get(position).getNum());
        holder.ck.setChecked(data.get(position).isEtat());
        final int p = position;
        holder.ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   data.get(p).setEtat(isChecked);
               }
          }
        );
        setAnimation(holder.itemView, position);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.enter_from_bottom_item);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }




    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        CardView cv ;
        TextView txt_contact , txt_num;
        CheckBox ck;

        public ContactViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            txt_contact = (TextView) itemView.findViewById(R.id.txt_name);
            txt_num = (TextView) itemView.findViewById(R.id.txt_num);
            ck = (CheckBox) itemView.findViewById(R.id.ck);
        }



    }
}
