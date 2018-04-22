package amalhichri.androidprojects.com.adschain.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amalhichri.androidprojects.com.adschain.R;

/**
 * Created by lj on 3/28/2017.
 */

public class RvSearchDemoHolder extends RecyclerView.ViewHolder {

    private final TextView tvName;

    public RvSearchDemoHolder(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
    }

    public void bind(String name) {
        tvName.setText(name);
    }
}
