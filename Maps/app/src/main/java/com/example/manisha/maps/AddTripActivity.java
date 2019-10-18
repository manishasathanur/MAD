package com.example.manisha.maps;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTripActivity extends AppCompatActivity {
    EditText editTextTripName, editTextDate;
    int checkedCount=0;
    ImageView imageViewAddTrip;
    ProgressDialog pd;
    double latitute, longitude;
    ListView listViewPlaces;
    TextView textViewPlace;
    ListObj listObj=new ListObj();
    DatabaseReference mRootRef ;
    AutoCompleteTextView editTextCity;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ArrayList<ListObj> placeNameList=new ArrayList<ListObj>();
    ArrayList<com.example.manisha.maps.Places> arrayListPlacestoTrip=new ArrayList<com.example.manisha.maps.Places>();
    ArrayList<com.example.manisha.maps.Places> placesArrayList = new ArrayList<com.example.manisha.maps.Places>();
    private static LatLngBounds latLngBounds = new LatLngBounds(new LatLng(32.6393, -117.004304), new LatLng(44.901184, -67.32254));
    Trip trip=new Trip();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_add_trip);
        setTitle("Add Trip");

        editTextDate = findViewById(R.id.editTextDate);
        textViewPlace=findViewById(R.id.textViewPlaceSelected);
        listViewPlaces=findViewById(R.id.listViewPlaces);
        editTextTripName=findViewById(R.id.editTextTripName);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(AddTripActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1)
                                + "/" + String.valueOf(year);
                        editTextDate.setText(date);
                        trip.setDay(date);
                        trip.setTripName(editTextTripName.getText()+"");
                        trip.setTripId("id");
                    }
                }, yy, mm, dd);
                datePicker.show();
            }


        });


        editTextCity = findViewById(R.id.autoCompleteTextViewCity);

        //AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.	TYPE_FILTER_CITIES).build();

        editTextCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("US").setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setFilter(typeFilter).build(AddTripActivity.this);
                    //Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(AddTripActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });


       listViewPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListObj listObj=placeNameList.get(position);
                placeNameList.add(position,listObj);
                //placeNameList.add(listObj);
                Adapterclass adapterclass = new Adapterclass(AddTripActivity.this, placeNameList);
                listViewPlaces.setAdapter(adapterclass);

            }
        });
        imageViewAddTrip=findViewById(R.id.imageViewAddPlace);
        imageViewAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((editTextTripName.getText().toString()).equals("")){
                    editTextTripName.setError("Enter a name for your trip");
                }
                else if((editTextDate.getText().toString()).equals("")){
                    editTextDate.setError("Set date for your trip");
                }
                else if((editTextCity.getText().toString()).equals("")){
                    editTextCity.setError("Select a city ");
                }
                else if(checkedCount==0){
                    Toast.makeText(AddTripActivity.this,"Select atleast one place",Toast.LENGTH_SHORT).show();
                }
                else if (checkedCount < 16) {
                    for (ListObj listObj : placeNameList) {
                        if ((listObj.getStatus().equals("checked"))) {
                            int index = placeNameList.indexOf(listObj);
                            com.example.manisha.maps.Places place = placesArrayList.get(index);
                            arrayListPlacestoTrip.add(place);
                        }
                    }
                    trip.setPlaces(arrayListPlacestoTrip);
                    writeNewUser(trip);
                    int resultCode = 1;
                    setResult(resultCode);
                    finish();
                }
                else
                    Toast.makeText(AddTripActivity.this,"Trip cannot have more then 15 places",Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void writeNewUser(Trip trip) {
            String id = mRootRef.child("Trip").push().getKey();
            trip.setTripId(id);
            mRootRef.child("Trip").child(trip.getTripId()).setValue(trip);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(AddTripActivity.this, data);
                latitute = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                editTextCity.setText(place.getName());
                trip.setCity(place.getName()+"");
                String url;

                url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?query=restaurants&location=+" + latitute + "," + longitude + "&radius=1500&key=AIzaSyCpe3pladjfvm1DSItJHLB9PizGXhqh8K4";
                new JsonTask().execute(url);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(AddTripActivity.this, data);
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        String temp;

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(AddTripActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            temp = buffer.toString();
            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
            placeNameList.clear();
            placesArrayList.clear();
            ArrayList<com.example.manisha.maps.Places> temporary=(ArrayList) parseGoogleParse(temp);
            for(int index=1;index<temporary.size();index++){
                com.example.manisha.maps.Places obj=temporary.get(index);
                ListObj listObj=new ListObj();
                listObj.setName(obj.name);
                listObj.setStatus("unchecked");
                placeNameList.add(listObj);
                placesArrayList.add(obj);
            }

            Adapterclass adapterclass = new Adapterclass(AddTripActivity.this, placeNameList);
            listViewPlaces.setAdapter(adapterclass);
            //addPlaces(venuesList);
        }
    }


    private static ArrayList<com.example.manisha.maps.Places> parseGoogleParse(final String response) {
        ArrayList placesList = new ArrayList();
        try {
            JSONObject root = new JSONObject(response);
            JSONArray JSONresults = root.getJSONArray("results");

            for (int i = 0; i < JSONresults.length(); i++) {
                com.example.manisha.maps.Places places = new com.example.manisha.maps.Places();
                JSONObject placeJSON = JSONresults.getJSONObject(i);
                places.setName(placeJSON.getString("name"));
                JSONObject geometry = placeJSON.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                places.setLat(location.getString("lat"));
                places.setLng(location.getString("lng"));
                //placeNamesList.add(places.getName());
                placesList.add(places);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return placesList;
    }

    public class Adapterclass extends ArrayAdapter<ListObj> {
        private Activity context;
        private ArrayList<ListObj> list;
        CheckBox checkBox;

        public Adapterclass(Activity context, List<ListObj> list) {
            super(context, R.layout.list_adapter, list);
            this.context = context;
            this.list = (ArrayList<ListObj>) list;
        }

        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            listObj=getItem(position);
            View listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.list_adapter, null, true);
            checkBox=listViewItem.findViewById(R.id.checkBox);
            TextView textViewName = listViewItem.findViewById(R.id.textViewPlaceName);
            textViewName.setText(listObj.getName()+"");
            String status = listObj.getStatus();

            if (status.equals("unchecked")) {
                checkBox.setChecked(false);
            }

            else if (status.equals("checked")) {
                checkBox.setChecked(true);
            }

            checkBox.setTag(position);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (Integer) buttonView.getTag();
                    if(isChecked){
                        checkedCount++;
                        //listObj.setStatus("checked");
                        placeNameList.get(position).setStatus("checked");
                        //checkBox.setChecked(true);
                    }
                    else {
                        checkedCount--;
                        placeNameList.get(position).setStatus("unchecked");
                        //checkBox.setChecked(false);
                    }
                }
            });

            return listViewItem;
        }
    }
}




