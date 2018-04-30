package amalhichri.androidprojects.com.adschain.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

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
import amalhichri.androidprojects.com.adschain.activities.QRCodeActivity;
import amalhichri.androidprojects.com.adschain.models.User;

public class TwoFactoAuthAppDialog extends Dialog {

    private Context context;
    private static String email,password;
    private String username, phoneNumber, countryCode,addUserUrl,addedUserId;
    private int style;

    public TwoFactoAuthAppDialog (Context context,int style) {
        super(context);
        this.context=context;
        this.style = style;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twofactorauth_app_ui);


        /*************************************************************************************************
         *                       2FA using Authenticator app on another device *
         *  **********************************************************************************************/

        (findViewById(R.id.authAppOnOtherDevice)).setOnClickListener(new View.OnClickListener() {
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
                                            //Toast.makeText(getApplicationContext(), "Res: "+addedUserId, Toast.LENGTH_LONG).show();
                                            /** 3.Call the Authy API to generate a QRCode:  will be handled in QRCodeActivity **/
                                            // and pass it to next activity
                                            Intent qrCodeIntent = new Intent( getContext().getApplicationContext(), QRCodeActivity.class);
                                            qrCodeIntent.putExtra("userId",addedUserId);
                                            getContext().startActivity(qrCodeIntent);
                                            /** 4. validating user provided key: will be handled in QRCodeActivity**/
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
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
            }
        });
    }


}