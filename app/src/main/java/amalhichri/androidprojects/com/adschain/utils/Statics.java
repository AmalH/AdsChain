package amalhichri.androidprojects.com.adschain.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import amalhichri.androidprojects.com.adschain.activities.HomeActivity;
import amalhichri.androidprojects.com.adschain.activities.LoginActivity;
import amalhichri.androidprojects.com.adschain.models.User;

/**
 * Created by Amal on 30/11/2017.
 */

public class Statics {

    /**
     * to keep all static references
     * to avoid instanciating them 2+ times in diffrents activitis/fragments
     */

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference("users");
    public static DatabaseReference dealTable = FirebaseDatabase.getInstance().getReference("deals");


    public static void signUp(final String email, String password, final String fullName, final String pictureUrl,final String twoFactorAuthOn, final Activity activity) {
        Statics.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // add user to database
                        Log.d("Test","Facebook to firebase success");
                        User userToAdd = new User();
                        userToAdd.setEmailAddress(email);
                        userToAdd.setTwoFactorAuthOn(twoFactorAuthOn);
                        String[] splited = fullName.split("\\s+");
                        userToAdd.setFirstName(splited[0]);
                        userToAdd.setLastName(splited[1]);
                        userToAdd.setPictureUrl(pictureUrl);
                        usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userToAdd).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Failure",e.getMessage());
                            }
                        });
                        Toast.makeText(activity, "Successfully signed to AdsChain!", Toast.LENGTH_LONG).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ERROR:",e.getMessage());
            }
        });
    }

    /*public static void signIn(String email, String password, final Activity activity) {
        Statics.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener
                (activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           //Toast.makeText(activity, "logged in", Toast.LENGTH_LONG).show();
                            activity.startActivity(new Intent(activity, HomeActivity.class));
                        } else {
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }*/

    public static void validateSecurityCode(String code, final String userId, final Context context, final EditText codeTxt, final TextView errorTxt){
        String codeValidationUrl="https://api.authy.com/protected/json/verify/"+code+"/"+userId+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
        //JSONObject obj = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,null,
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
                        Toast.makeText(context,
                                "You typed a wrong code!",
                                Toast.LENGTH_LONG).show();
                        codeTxt.setText("");
                        errorTxt.setVisibility(View.VISIBLE);
                        //Log.e("ERROR! ",error.getMessage());
                    }
                });
        (AppSingleton.getInstance(context).getRequestQueue()).add(jsObjRequest);
    }


    public static void validateSecurityCode(String code, final String userId, final Context context){
        String codeValidationUrl="https://api.authy.com/protected/json/verify/"+code+"/"+userId+"?api_key=CCb8fPiHfTdFp332cefjTuRjgMNprVOx";
        //JSONObject obj = new JSONObject();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,null,
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
                        Toast.makeText(context,
                                "You typed a wrong code!",
                                Toast.LENGTH_LONG).show();
                    }
                });
        (AppSingleton.getInstance(context).getRequestQueue()).add(jsObjRequest);
    }

}