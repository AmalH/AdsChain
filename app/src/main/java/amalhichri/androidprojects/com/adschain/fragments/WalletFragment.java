package amalhichri.androidprojects.com.adschain.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import amalhichri.androidprojects.com.adschain.R;


public class WalletFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wallet_fragment, container, false);
        ((WebView)(v.findViewById(R.id.chartWebView))).loadUrl("file:///android_asset/WalletChart.html");
        ((WebView)(v.findViewById(R.id.chartWebView))).getSettings().setJavaScriptEnabled(true);
        return v;
    }
}
