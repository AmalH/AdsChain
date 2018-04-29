package amalhichri.androidprojects.com.adschain.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amalhichri.androidprojects.com.adschain.R;

/**
 * Created by lj on 3/28/2017.
 */

public class ContactHolder extends RecyclerView.ViewHolder {

    private final TextView tvName,tvNum;

    public ContactHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvNum =  itemView.findViewById(R.id.tvNum);
    }

    public void bind(String name,String num) {
        tvName.setText(name);
        tvNum.setText(num);
    }
}
