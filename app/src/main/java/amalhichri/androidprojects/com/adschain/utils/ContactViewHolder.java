package amalhichri.androidprojects.com.adschain.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import amalhichri.androidprojects.com.adschain.R;

/**
 * Created by lj on 3/28/2017.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvName,tvNum;
    private final CheckBox cntcChbx;

    public ContactViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvNum =  itemView.findViewById(R.id.tvNum);
        cntcChbx = itemView.findViewById(R.id.ctcCkBx);
    }

    public void bind(String name,String num) {
        tvName.setText(name);
        tvNum.setText(num);
    }
}
