package amalhichri.androidprojects.com.adschain.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.activities.MainActivity;
import amalhichri.androidprojects.com.adschain.utils.Statics;


public class OthersFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_others_fragment, container, false);
        (v.findViewById(R.id.logoutTxt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.auth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        return v;
    }
}
