package amalhichri.androidprojects.com.adschain.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.utils.RvSearchDemoHolder;


public class ContactsAdapter extends RecyclerView.Adapter<RvSearchDemoHolder> {
    private ArrayList<String> countries;
    private ArrayList<String> countriesCopy;
    private LayoutInflater inflater;


    public ContactsAdapter(Context context, ArrayList<String> countries) {
        this.countries = countries;
        this.countriesCopy = new ArrayList<>();
        countriesCopy.addAll(countries);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RvSearchDemoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RvSearchDemoHolder(inflater.inflate(R.layout.item_country, parent, false));
    }

    @Override
    public void onBindViewHolder(RvSearchDemoHolder holder, int position) {
        holder.bind(countries.get(position));
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public void filter(CharSequence sequence) {
        ArrayList<String> temp = new ArrayList<>();
        if (!TextUtils.isEmpty(sequence)) {
            for (String s : countries) {
                if (s.toLowerCase().contains(sequence)) {
                    temp.add(s);
                }
            }
        } else {
            temp.addAll(countriesCopy);
        }
        countries.clear();
        countries.addAll(temp);
        notifyDataSetChanged();
        temp.clear();
    }
}

