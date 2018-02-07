package amalhichri.androidprojects.com.adschain.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import amalhichri.androidprojects.com.adschain.R;


public class ConfigFragment extends Fragment {

    private ArrayAdapter<String> spinnerAdapter;
    private List<String> options= new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_config_fragment, container, false);
       options.add("a wish list");options.add("contacts I choose");
       spinnerAdapter = new ArrayAdapter<>(getContext(),R.layout.customspinner_view, options);
       spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)v.findViewById(R.id.contactsLimitSpinner)).setAdapter(spinnerAdapter);
        return v;
    }
}
