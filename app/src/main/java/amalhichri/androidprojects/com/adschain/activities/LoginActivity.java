package amalhichri.androidprojects.com.adschain.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.EditText;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import amalhichri.androidprojects.com.adschain.R;
import amalhichri.androidprojects.com.adschain.models.User;
import amalhichri.androidprojects.com.adschain.utils.Enable2FAdialog;
import amalhichri.androidprojects.com.adschain.utils.Statics;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity  extends Activity {

    private CallbackManager mFacebookCallbackManager;
    private LoginManager mLoginManager;
    private AccessTokenTracker mAccessTokenTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //initialize facebook sdk
        facebookApiInit();

        (findViewById(R.id.signUpTxtVw)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });


    }

    /*****************************************************************************
     * * Firebase login
     * **************************************************************************/
  public void login(View view) {

      /** simple data validation ... **/
      if (((EditText) findViewById(R.id.emailLoginTxt)).getText().toString().trim().isEmpty()) {
          ((EditText) findViewById(R.id.emailLoginTxt)).setError("email missing");
          (findViewById(R.id.emailLoginTxt)).requestFocus();
          return;
      }
      if(((ShowHidePasswordEditText) findViewById(R.id.pswLoginTxt)).getText().toString().isEmpty()){
          ((ShowHidePasswordEditText)findViewById(R.id.pswLoginTxt)).setError("password missing");
          (findViewById(R.id.pswLoginTxt)).requestFocus();
          return;
      }
      Statics.auth.signInWithEmailAndPassword(((EditText) findViewById(R.id.emailLoginTxt)).getText().toString(), ((ShowHidePasswordEditText) findViewById(R.id.pswLoginTxt)).getText().toString())
              .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                          @Override
                          public void onSuccess(AuthResult authResult) {
                              Statics.usersTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(DataSnapshot dataSnapshot) {
                                      String twoFactorAuthOn  = (dataSnapshot.getValue(User.class)).getTwoFactorAuthOn();
                                      /** if user has activated two factor authentication **/
                                      if(twoFactorAuthOn.equals("true")){
                                          SharedPreferences loggedUserPrefs = getSharedPreferences("2FA",0);
                                          SharedPreferences.Editor e=loggedUserPrefs.edit();
                                          e.putString("status","unfinished");
                                          e.commit();
                                          (new Enable2FAdialog(LoginActivity.this)).show();
                                      }
                                      /** if user hasnt activated two factor authentication just  **/
                                      if(twoFactorAuthOn.equals("false")){
                                          SharedPreferences loggedUserPrefs = getSharedPreferences("2FA",0);
                                          SharedPreferences.Editor e=loggedUserPrefs.edit();
                                          e.putString("status","finished");
                                          e.commit();
                                         LoginActivity.this.startActivity(new Intent( LoginActivity.this, HomeActivity.class));
                                      }
                                  }
                                  @Override
                                  public void onCancelled(DatabaseError databaseError) {
                                      throw databaseError.toException();
                                  }
                              });
                          }
                      }
              )
              .addOnCompleteListener
                      (LoginActivity.this, new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if (!task.isSuccessful()) {
                                  Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                              }
                          }
                      });
  }


    /*****************************************************************************
     * * Password recovery
     * **************************************************************************/
    public void resetPassword(View v) {
        if (((EditText)findViewById(R.id.emailLoginTxt)).getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please provide your email\nto send you password recovery info.", Toast.LENGTH_LONG).show();
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(((EditText)findViewById(R.id.emailLoginTxt)).getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "A password recovery email has been sent to "+((EditText)findViewById(R.id.emailLoginTxt)).getText().toString(), Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(getApplicationContext(), "No such email KotlinLearn database, please provide your email!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /*****************************************************************************
     * * Facebook login
     * **************************************************************************/
    public void loginWithFacebook(View v) {
       if (AccessToken.getCurrentAccessToken() != null) {
            mLoginManager.logOut();
        } else {
           // some call here
            mAccessTokenTracker.startTracking();
            mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile","email"));
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

        final LoginButton loginButton = findViewById(R.id.facebookLoginBtn);
        mFacebookCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions( Arrays.asList("public_profile","email"));
        loginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebook_token", "handleFacebookAccessToken:" + loginResult.getAccessToken());
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                Log.d("CREDENTIALS:",credential.toString());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    Statics.auth.signInWithEmailAndPassword(object.getString("email"),String.valueOf(object.getInt("id")))
                                            .addOnCompleteListener
                                                    (LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                            } else {
                                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                   // Statics.signIn(object.getString("email"),String.valueOf(object.getInt("id")), LoginActivity.this);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
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
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(!(FirebaseAuth.getInstance().getCurrentUser()==null))
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    /*****************************************************************************
     * * Utils
     * **************************************************************************/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}