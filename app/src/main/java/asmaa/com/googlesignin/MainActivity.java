package asmaa.com.googlesignin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity" ;
    private FirebaseAuth auth;
    private SignInButton googlebutton;
    private GoogleApiClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance ();
        googlebutton=findViewById(R.id.sign_in_button );



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient = new GoogleApiClient.Builder ( this )
        .enableAutoManage ( this, new GoogleApiClient.OnConnectionFailedListener () {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            }
        } )
          .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
          .build ();

        googlebutton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                signIn ();
            }
        });


    }


    /*@Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

    }*/


    private void signIn() {
       //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Intent signInIntent= Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

                GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent ( data );
                if (result.isSuccess ()){
                    GoogleSignInAccount account=result.getSignInAccount ();
                    firebaseAuthWithGoogle(account);
                    Toast.makeText ( this, "please wait,while we are getting your auth result..", Toast.LENGTH_SHORT ).show ();
                }
                else {
                    Toast.makeText ( this, "cannot get auth result", Toast.LENGTH_SHORT ).show ();
                }

        }
    }


   /* private void handleSignInResult( GoogleSignInResult result) {
        Log.d (TAG, "handleSignInResult:" + result.isSuccess());


        if (result.isSuccess()){

            GoogleSignInAccount acct = result.getSignInAccount ();


        }}*/






    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            SendUserToMainActivity ();


                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message=task.getException ().toString ();
                            SendUserToLoginActivity ();
                            Toast.makeText ( MainActivity.this, "not Authentacted:"+message, Toast.LENGTH_SHORT ).show ();

                        }

                    }
                });

    }

    private void SendUserToMainActivity(){
        Intent mainintent=new Intent ( this,Main2Activity.class );
        mainintent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( mainintent );
        finish ();
    }


    private void SendUserToLoginActivity(){
        Intent mainintent=new Intent ( this,MainActivity.class );
        mainintent.addFlags ( Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( mainintent );
        finish ();
    }

    /*@Override
    public void onClick(View v) {
        signIn ();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d (TAG,"onconnectionfailed:" + connectionResult);
    }*/
}
