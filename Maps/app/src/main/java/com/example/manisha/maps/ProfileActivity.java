package com.example.manisha.maps;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    static final  int REQ=100;
    static final String TRIP="trip",EXTRA="extra";

    ImageView imageViewPic,imageViewAddTrip;
    ListView listViewTrips;
    TextView textViewUserName;
    GoogleSignInClient googleSignInClient;
    ImageView buttonSignout;
    DatabaseReference mRootRef;
    ArrayList<Trip> listTrips=new ArrayList<Trip>();
    ArrayList<String> listTripNames=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        setTitle("Profile");
        String imageUrl=getIntent().getExtras().getString(MainActivity.IMAGEURL);
        String userName=getIntent().getExtras().getString(MainActivity.USERNAME);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        imageViewPic=findViewById(R.id.imageViewPic);
        imageViewAddTrip=findViewById(R.id.imageViewAddTrip);
        listViewTrips=findViewById(R.id.listViewTrips);
        textViewUserName=findViewById(R.id.textViewUserName);
        buttonSignout=findViewById(R.id.buttonSignout);

        textViewUserName.setText(userName);
        if(imageUrl.equals("none")){
            imageViewPic.setImageResource(R.drawable.common_google_signin_btn_icon_light);
        }
        else
            Glide.with(this).load(imageUrl).into(imageViewPic);
        buttonSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        imageViewAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,AddTripActivity.class);
                startActivityForResult(intent,REQ);
            }
        });

        mRootRef.child("Trip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listTrips.clear();
                listTripNames.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot != null) {
                        Trip trip = new Trip();
                        trip.setTripName(postSnapshot.child("tripName").getValue().toString());
                        trip.setCity(postSnapshot.child("city").getValue().toString());
                        trip.setTripId(postSnapshot.child("tripId").getValue().toString());
                        trip.setDay(postSnapshot.child("day").getValue().toString());

                        GenericTypeIndicator<List<Places>> t = new GenericTypeIndicator<List<Places>>() {};
                        trip.setPlaces((ArrayList<Places>) postSnapshot.child("places").getValue(t));
                        listTrips.add(trip);
                        listTripNames.add(trip.tripName);
                    }
                }
                ArrayAdapter<String> itemsAdapter =new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1, listTripNames);
                listViewTrips.setAdapter(itemsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listViewTrips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder;
                alertDialogBuilder=new AlertDialog.Builder(ProfileActivity.this);
                alertDialogBuilder.setTitle("Delete Trip");
                alertDialogBuilder.setMessage("Are you sure you want to delete?");
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name=listTripNames.get(position);
                        deleteMessage(name,position);
                    }
                });
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog=alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

          listViewTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip listObj=listTrips.get(position);
                Log.d("hello", listObj.tripId);
                Intent intent=new Intent(ProfileActivity.this,MapsDisplayActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable(TRIP,listObj);
                intent.putExtra(EXTRA,bundle);
                startActivity(intent);
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //mGoogleSignInClient = GoogleSignIn.getClient(ListViewDisp.this, gso);
        googleSignInClient.signOut()
                .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void deleteMessage(String msg, int position) {
        Trip trip=listTrips.get(position);
        String removekey = trip.tripId;
        mRootRef.child("Trip").child(removekey).removeValue();
        listTripNames.remove(position);
        listTrips.remove(position);
        ArrayAdapter<String> itemsAdapter =new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1, listTripNames);
        listViewTrips.setAdapter(itemsAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ) {
            if (resultCode == 1) {

            }
            }
        }
}
