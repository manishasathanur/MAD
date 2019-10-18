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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText editTextFname, editTextLname, editTextPassword, editTextRPassword, editTextEmail;
    Button buttonSignup, buttonCancel;
    FirebaseAuth auth = null;
    DatabaseReference mDatabaseReference;
    final static String FULLNAME = "fullname", KEY = "key", UKEY = "userkey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Sign up");


        auth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        editTextFname = findViewById(R.id.editTextFname);
        editTextLname = findViewById(R.id.editTextLName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextChoosePassword);
        editTextRPassword = findViewById(R.id.editTextRepeatPassword);
        User user = new User();
        user.setFirstName(editTextFname + "");
        user.setLastName(editTextLname + "");
        //user.set

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSignup = findViewById(R.id.buttonSignUp1);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SignupActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }

        });


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em1 = editTextEmail.getText() + "";
                String pwd1 = editTextPassword.getText() + "";

                if (editTextFname.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
                } else if (editTextLname.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
                } else if (editTextFname.getText().toString().equals(editTextLname.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "First Name and Last Name should not be the same", Toast.LENGTH_SHORT).show();
                } else if (editTextEmail.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else if (editTextPassword.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please choose password", Toast.LENGTH_SHORT).show();
                } else if (editTextRPassword.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please Re-enter the password again to verify", Toast.LENGTH_SHORT).show();
                } else if (!(editTextPassword.getText().toString().equals(editTextRPassword.getText().toString()))) {
                    Toast.makeText(SignupActivity.this, "Please enter matching passwords", Toast.LENGTH_SHORT).show();
                } else if (editTextPassword.getText().toString().length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password should be of minimum six characters", Toast.LENGTH_SHORT).show();
                } else if (!(editTextEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                    Log.d("demo", "entered validation");
                    Toast.makeText(SignupActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                } else {

                    auth.createUserWithEmailAndPassword(em1, pwd1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser fuser = auth.getCurrentUser();
                            User user = new User(editTextFname.getText().toString(), editTextLname.getText().toString(), fuser.getUid());
                            String key = mDatabaseReference.push().getKey();
                            user.setUserKey(key);

                            Map<String, Object> post = user.toMap();
                            Map<String, Object> child = new HashMap<>();
                            //child.put("/user/" + auth.getCurrentUser().getUid(), post);
                            //mDatabaseReference.updateChildren(child);
                            // mDatabaseReference.child(user.getUserId()).setValue(user);
                            mDatabaseReference.child(user.getUserKey()).setValue(user);
                            Intent intent = new Intent(SignupActivity.this, ChatActivity.class);
                            intent.putExtra(FULLNAME, user.getFirstName() + user.getLastName());
                            intent.putExtra(UKEY, user.getUserId());
                            intent.putExtra(KEY, user.getUserKey());
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

        });
    }
}
