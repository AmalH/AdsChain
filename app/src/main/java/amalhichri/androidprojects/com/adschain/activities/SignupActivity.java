package amalhichri.androidprojects.com.adschain.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.linkedin.platform.LISessionManager;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.models.User;
import amalhichri.androidprojects.com.adschain.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends Activity {

    static boolean isFacebook= false;
    private LoginManager mLoginManager;
    private AccessTokenTracker mAccessTokenTracker;
    private CallbackManager mFacebookCallbackManager;
    private User userFromLinkedIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        //initializing facebook api
        facebookApiInit();

        // material editTexts error msg
        ((EditText) findViewById(R.id.emailSignupTxt)).setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                    ((EditText) findViewById(R.id.emailSignupTxt)).setError("Password is incorrect.");
                return false;
            }
        });
        ((EditText) findViewById(R.id.emailSignupTxt)).setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override public void onFocusChange(    View v,    boolean hasFocus){
                if (hasFocus) ((EditText) findViewById(R.id.emailSignupTxt)).setError(null);
            }
        }
        );
    }


    /** Sign up  **/
    public void signUp(View v) {


        /** simple data validation ... */
        if (((EditText) findViewById(R.id.emailSignupTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.emailSignupTxt)).setError("Email missing !");
            return;
        }
        if (((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.pswSignupTxt)).setError("Password missing");
            return;
        }
        if (!(((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().isEmpty())
                && (((EditText) findViewById(R.id.pswSignupTxt)).getText().toString().length() < 6)) {
            ((EditText) findViewById(R.id.pswSignupTxt)).setError("Password should be at least 6 characters");
            return;
        }
        if (((EditText) findViewById(R.id.fullNameTxt)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.fullNameTxt)).setError("Full name missing");
            return;
        }
        if (!(((EditText) findViewById(R.id.fullNameTxt)).getText().toString().isEmpty())
                && !(isFullName(((EditText) findViewById(R.id.fullNameTxt)).getText().toString()))) {
            ((EditText) findViewById(R.id.fullNameTxt)).setError("Please provide your full name");
            return;
        }
        //authenticate user + add it to firebase DB ..
        Statics.signUp(((EditText) findViewById(R.id.emailSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.pswSignupTxt)).getText().toString(),
                ((EditText) findViewById(R.id.fullNameTxt)).getText().toString(),
                "",
                SignupActivity.this);
    }



    /** sign up with facebook **/
    public void signUpWithFacebook(View v) {
        isFacebook = true;
        if (AccessToken.getCurrentAccessToken() != null) {
            mLoginManager.logOut();
        } else {
            mAccessTokenTracker.startTracking();
            mLoginManager.logInWithReadPermissions(SignupActivity.this, Arrays.asList("public_profile"));
        }
    }
    // to initialize facebook api + retrieve user info
    private void facebookApiInit() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        mLoginManager = LoginManager.getInstance();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken) {
            }
        };

        final LoginButton loginButton = findViewById(R.id.facebookSignInBtn);
        mFacebookCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email","public_profile");
        loginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                isFacebook=true;
                final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    Log.d("OBJECT",object.toString());
                                    //if facebook account is based on phone number / or containes no email
                                    if(object.isNull("email")){
                                        SignupActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(SignupActivity.this, "Sign up failed! \n Please provide a Facebook account with email!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else
                                        Statics.signUp(object.getString("email"),String.valueOf(object.getInt("id")),
                                                object.getString("first_name")+" "+object.getString("last_name"),object.getJSONObject("picture").getJSONObject("data").getString("url") ,SignupActivity.this);
                                } catch (JSONException e) {
                                  Log.d("ERROR",e.getMessage());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,picture");
                request.setParameters(parameters);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GraphResponse gResponse =request.executeAndWait();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Log.d("ERROR",e.getMessage());
                }
                Log.d("Test",request.toString());
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
                Log.d("ERROR",error.toString());
            }
        });


    }

    /** onActivityResult **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            LISessionManager.getInstance(getApplicationContext())
                    .onActivityResult(this,
                            requestCode, resultCode, data);
            isFacebook=false;

        //if(!(FirebaseAuth.getInstance().getCurrentUser()==null))
            //startActivity(new Intent(SignupActivity.this, HomeActivity.class));
    }


    /** for calligraphy lib usage **/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    /*** helper methods ***/
    private boolean isFullName(String s){
        int j=0;
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)==' ')
                j++;
        }
        if (j==1)
           return true;
        else
            return false;
    }

    private void setErrorText(EditText editText,String errorMsg){

    }


}
