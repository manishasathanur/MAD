package com.example.manisha.maps;


import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    final static int REQ_CODE=100;
    FirebaseAuth mAuth;
    final static String USERNAME="username",IMAGEURL="imageUrl";
    SignInButton buttonLogin;
    Button buttonSignout;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
         buttonLogin=findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name=user.getDisplayName();
                            String imageUrl="none";
                            if(user.getPhotoUrl()!=null) {
                                imageUrl = user.getPhotoUrl().toString();
                            }
                            Toast.makeText(MainActivity.this, "Signed in sucessfully as "+name, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                            intent.putExtra(USERNAME,name);
                            intent.putExtra(IMAGEURL,imageUrl);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getBaseContext(),"Signin in not sucessfully !!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d("demo", "Google sign in failed: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // include code to handle the case if the user is already logged in,
        // which should take the user to main activity and finish this activity.

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
            Toast.makeText(MainActivity.this, "Authentication Successfull.",
                    Toast.LENGTH_SHORT).show();
            String name=user.getDisplayName();
            String imageUrl="none";
            if(user.getPhotoUrl()!=null) {
                imageUrl = user.getPhotoUrl().toString();
            }
            Toast.makeText(MainActivity.this, "Signed in sucessfully as "+name, Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
            intent.putExtra(USERNAME,name);
            intent.putExtra(IMAGEURL,imageUrl);
            startActivity(intent);
        }
    }

}

