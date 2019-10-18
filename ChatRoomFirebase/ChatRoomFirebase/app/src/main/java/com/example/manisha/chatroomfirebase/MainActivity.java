package com.example.manisha.chatroomfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin, buttonSignup;
    EditText email, pwd;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
        buttonSignup = findViewById(R.id.buttonSignup);
        buttonLogin = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.loginemail);
        pwd = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //mAuth.signOut();


        if (mAuth.getCurrentUser() != null) {
            String id = mAuth.getCurrentUser().getUid();
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String id = postSnapshot.child("userId").getValue() + "";
                        if (id.equals(mAuth.getCurrentUser().getUid())) {
                            Log.d(" hello", "in if");
                            String fullname = postSnapshot.child("firstName").getValue().toString() + " " + postSnapshot.child("lastName").getValue().toString();
                            ;
                            Log.d("hello", fullname);
                            Intent i = new Intent(MainActivity.this, ChatActivity.class);
                            i.putExtra(SignupActivity.FULLNAME, fullname);
                            i.putExtra(SignupActivity.UKEY, postSnapshot.child("userKey").getValue() + "");
                            //i.putExtra(SignupActivity.KEY,mAuth.getCurrentUser().getUid());
                            i.putExtra(SignupActivity.KEY, id);
                            startActivity(i);
                            finish();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        //FirebaseAuth.getInstance().getCurrentUser();

        //FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em = email.getText().toString().trim();
                String pass = pwd.getText().toString().trim();
                String fullname = null;

                /*if (edemail.getText().toString().length() != 0 && edpwd.getText().toString().length() != 0) {
                    performLogin(edemail.getText().toString(), edpwd.getText().toString());
                } else {
                    Toast.makeText(getBaseContext(), "Please Enter values", Toast.LENGTH_SHORT).show();
                }*/
                if (email.getText().toString().length() != 0 && pwd.getText().toString().length() != 0) {

                    mAuth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //User user=mAuth.getCurrentUser();
                                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                            String id = postSnapshot.child("userId").getValue() + "";
                                            //User usobjval =dataSnapshot.getValue(User.class);
                                            if (id.equals(mAuth.getCurrentUser().getUid())) {
                                                String fullname = postSnapshot.child("firstName").getValue().toString() + " " + postSnapshot.child("lastName").getValue().toString();
                                                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                                                i.putExtra(SignupActivity.FULLNAME, fullname);
                                                i.putExtra(SignupActivity.UKEY, postSnapshot.child("userKey").getValue() + "");
                                                //i.putExtra(SignupActivity.KEY,mAuth.getCurrentUser().getUid());
                                                i.putExtra(SignupActivity.KEY, id);
                                                startActivity(i);
                                                finish();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), " Email and Password fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
