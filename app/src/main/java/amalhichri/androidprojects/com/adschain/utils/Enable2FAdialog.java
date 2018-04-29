package amalhichri.androidprojects.com.adschain.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.activities.HomeActivity;
import amalhichri.androidprojects.com.adschain.models.User;

public class Enable2FAdialog extends Dialog {

    private static String email,password;
    private String username, phoneNumber, countryCode,addUserUrl,addedUserId;
    private Activity c;

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



        /*************************************************************************************************
         *                      2FA using text messages *
         *  **********************************************************************************************/
        (findViewById(R.id.smsOptionLyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Statics.usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /** 1.Get user's creds! phone number included.. **/
                        email = (dataSnapshot.getValue(User.class)).getEmailAddress();
                        username = (dataSnapshot.getValue(User.class)).getFirstName()+" "+(dataSnapshot.getValue(User.class)).getLastName();
                       // password= (dataSnapshot.getValue(User.class)).getPassword();
                        phoneNumber ="54821200";
                        countryCode="216";
                        addUserUrl  = "https://api.authy.com/protected/json/users/new?user[email]="+email
                                +"&user[cellphone]="+phoneNumber
                                +"&user[country_code]="+countryCode+"&api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";

                        /** 2.Add the user to the Authy API **/
                        JSONObject obj = new JSONObject();
                        // post call for Authy api to add a user | response contains the added user's id
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,addUserUrl,obj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Gson gson = new Gson();
                                        try {
                                            /** get the returned id **/
                                            JsonObject addedUser = gson.fromJson(response.getString("user"),JsonObject.class);
                                            addedUserId = (addedUser.get("id")).getAsString();
                                            // Toast.makeText(getApplicationContext(), "Res: "+addedUserId, Toast.LENGTH_LONG).show();
                                            /** 3.call the Authy API to send a code through sms **/
                                            /** 4.call the Authy API to validate code provided by user [embedded in sendSecurityCodeTo method **/
                                            sendSecurityCodeTo(addedUserId);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("ERROR! ","ee: "+error.getMessage());
                                    }
                                });
                        (AppSingleton.getInstance(getContext()).getRequestQueue()).add(jsObjRequest);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
                //countryCode =((com.hbb20.CountryCodePicker)findViewById(R.id.countryCodePicker)).getSelectedCountryCode();
                }
        });

        /*************************************************************************************************
         *                       2FA using Authenticator app *
         *  **********************************************************************************************/

        (findViewById(R.id.authAppOptionLyt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                (new TwoFactoAuthAppDialog(getContext())).show();
            }
        });
    }

    /*************************************************************************************************
     *                       helpers *
     *  **********************************************************************************************/


    private void sendSecurityCodeTo(final String userId){
        JSONObject obj = new JSONObject();
        String getCodeSMS="https://api.authy.com/protected/json/sms/"+userId+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx&force=true";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,getCodeSMS,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dismiss();
                            if(response.getString("success").equals("true"));
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                            alertDialog.setTitle("Validate security code");
                            alertDialog.setMessage("Enter the code you received in sms");

                            final EditText input = new EditText(getContext());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            alertDialog.setView(input);
                            alertDialog.setPositiveButton("Validate",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            /** call authy api to validate code provided by the user **/
                                            validateSecurityCode(input.getText().toString(),userId,username,getContext());
                                        }
                                    });

                            alertDialog.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            alertDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR! ",error.getMessage());
                    }
                });
        (AppSingleton.getInstance(getContext()).getRequestQueue()).add(jsObjRequest);
    }

    public static void validateSecurityCode(String code, final String userId, final String username, final Context context){
        String codeValidationUrl="https://api.authy.com/protected/json/verify/"+code+"/"+userId+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
        JSONObject obj = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if((response.getString("token")).equals("is valid"))
                                context.startActivity(new Intent(context, HomeActivity.class));
                            else
                                Toast.makeText(context, "You typed a wrong code!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR! ",error.getMessage());
                    }
                });
        (AppSingleton.getInstance(context).getRequestQueue()).add(jsObjRequest);
    }
}
