package com.example.manisha.maps;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsDisplayActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback  {

    private GoogleMap mMap;
    ArrayList<Places> placesArrayList = new ArrayList<Places>();
    DatabaseReference mRootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_display);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle bundle=getIntent().getBundleExtra(ProfileActivity.EXTRA);
        Trip trip= (Trip) bundle.getSerializable(ProfileActivity.TRIP);
        placesArrayList=trip.getPlaces();
    }

    @Override
    public void onMapLoaded() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int j=0;j<placesArrayList.size();j++){
            builder.include(new LatLng(Double.parseDouble(placesArrayList.get(j).getLat()),Double.parseDouble(placesArrayList.get(j).getLng())));
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,12);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("heyy", placesArrayList.size()+"");
        mMap = googleMap;

        if (placesArrayList.size() > 0) {
            for (int i = 0; i < placesArrayList.size(); i++) {
                Log.d("hello1", placesArrayList.get(i).getLat());
                LatLng ltLn = new LatLng(Double.parseDouble(placesArrayList.get(i).getLat()), Double.parseDouble(placesArrayList.get(i).getLng()));
                mMap.addMarker((new MarkerOptions()).position(ltLn).title(placesArrayList.get(i).getName()));
            }
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(placesArrayList.get(0).getLat()), Double.parseDouble(placesArrayList.get(0).getLng())), 12.0f));
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            mMap.setOnMapLoadedCallback((GoogleMap.OnMapLoadedCallback) MapsDisplayActivity.this);
        }

    }
}

