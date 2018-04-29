package amalhichri.androidprojects.com.adschain.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import amalhichri.androidprojects.com.adschain.R;

public class Enable2FAdialog extends Dialog { //implements android.view.View.OnClickListener {


    public Activity c;
    public Dialog d;
    public Button yes, no;

    public Enable2FAdialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.enable_2fa_dialog_ui);
      //  yes = (Button) findViewById(R.id.btn_yes);
     //   no = (Button) findViewById(R.id.btn_no);
        //yes.setOnClickListener(this);
        //no.setOnClickListener(this);
    }

   /* @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }*/
}
