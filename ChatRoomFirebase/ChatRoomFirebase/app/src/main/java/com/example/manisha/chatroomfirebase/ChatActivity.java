package com.example.manisha.chatroomfirebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static android.content.Intent.createChooser;


public class ChatActivity extends AppCompatActivity {

    StorageReference storageRef, imageRef;
    private static final int Selected = 100;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    Uri uriImage=null;
    ImageView addImage;



    DatabaseReference dbref, ref;
    FirebaseDatabase database;

    FirebaseAuth mAuth;
    Adapterclass messageAdapter;
    ArrayList<Message> messagesList = new ArrayList<Message>();
    String key, fullname, ukey;
    ListView listView;
    public static final int REQUEST_CODE = 1234;
    ImageView imageViewpicture;

    boolean imagePresent=false;
    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Chat Room");
        mAuth = FirebaseAuth.getInstance();
        storageRef = storage.getReference();
        String urlImage;

        TextView textViewUname = findViewById(R.id.textViewUname);
        listView = findViewById(R.id.listview);
        ImageView imageViewLogout = findViewById(R.id.imageViewLogout);
        final Message message = new Message();
        Button button = findViewById(R.id.buttonAdd);
        addImage = findViewById(R.id.buttonImage);
        final EditText editTextMessage = findViewById(R.id.editTextAddMessage);
        key = getIntent().getExtras().getString(SignupActivity.KEY);
        fullname = getIntent().getExtras().getString(SignupActivity.FULLNAME);
        ukey = getIntent().getExtras().getString(SignupActivity.UKEY);

        textViewUname.setText(fullname);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePresent=true;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Selected);

            }
        });

        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });


        ref = FirebaseDatabase.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((editTextMessage.getText() + "").equals("")){
                    Toast.makeText(getBaseContext(),"Please enter a message",Toast.LENGTH_SHORT).show();
                }
                else {
                    String id = ref.child("message").push().getKey();
                    Message message1 = new Message();
                    message1.setName(fullname);
                    message1.setKey(key);
                    message1.setText(editTextMessage.getText() + "");
                    message1.setTime(Calendar.getInstance().getTime() + "");
                    message1.setId(id);
                    message1.setUrl("NA");
                    ref.child("message").child(id).setValue(message1);
                    if (imagePresent == true)
                        UploadImage(message1);
                }
            }
        });

        ref.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot != null) {
                        Message msg = new Message();
                        msg.setName(postSnapshot.child("name").getValue() + "");
                        msg.setTime(postSnapshot.child("time").getValue() + "");
                        msg.setKey(postSnapshot.child("key").getValue() + "");
                        msg.setId(postSnapshot.child("id").getValue() + "");
                        msg.setText(postSnapshot.child("text").getValue() + "");
                        msg.setUrl(postSnapshot.child("url").getValue()+"");
                        Log.d("hello", msg.getTime());

                        messagesList.add(msg);
                    }
                }
                Adapterclass adapterclass = new Adapterclass(ChatActivity.this, messagesList);
                listView.setAdapter(adapterclass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Selected:
                if (resultCode == RESULT_OK) {
                    uriImage = data.getData();
                    addImage.setBackgroundColor(Color.rgb(255, 255, 255));
                    addImage.setImageURI(uriImage);

                }
        }
    }

    public void UploadImage(final Message msg) {
        Message message = msg;
        imageRef = storageRef.child("myphotos/"+UUID.randomUUID()+".png");
        uploadTask = imageRef.putFile(uriImage);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String urlIMAGE = downloadUri.toString();
                    Log.d("hello", urlIMAGE);
                    String uploadId = ref.push().getKey();
                    ref.child("message").child(msg.getId()).child("url").setValue(urlIMAGE);


                }
                else
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();

            }
        });

    }

            public class Adapterclass extends ArrayAdapter<Message> {
                private Activity context;
                private ArrayList<Message> list;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

                public Adapterclass(Activity context, List<Message> list) {
                    super(context, R.layout.list_adapter, list);
                    this.context = context;
                    this.list = (ArrayList<Message>) list;
                }

                @Override
                public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    final Message msg = list.get(position);
                    LayoutInflater inflater = context.getLayoutInflater();
                    Message sources = getItem(position);
                    View listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.list_adapter, null, true);

                    TextView textViewName = listViewItem.findViewById(R.id.textViewName);
                    TextView textViewDate = listViewItem.findViewById(R.id.textViewDate);
                    TextView textViewMessage = listViewItem.findViewById(R.id.textViewMessage);
                    Button buttonDelete = listViewItem.findViewById(R.id.buttonDelete);
                    imageViewpicture = listViewItem.findViewById(R.id.imageViewMesg);

                    textViewName.setText(sources.getName());
                    textViewMessage.setText(sources.getText());
                    //imageViewpicture.setVisibility(View.VISIBLE);
                    Log.d("hello", sources.getUrl());
                    if((sources.getUrl()).equals("NA")){
                        imageViewpicture.setVisibility(View.GONE);
                    }
                    else{
                        imageViewpicture.setVisibility(View.VISIBLE);
                        Picasso.get().load(sources.getUrl()).into(imageViewpicture);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    Date date=new Date();
                    try {
                        date = sdf.parse(sources.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String prettyTime = new PrettyTime().format(date);
                    textViewDate.setText(prettyTime);

                    if(mAuth.getUid().equals(sources.getKey())){
                        buttonDelete.setVisibility(View.VISIBLE);
                    }
                    else
                        buttonDelete.setVisibility(View.INVISIBLE);
                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteMessage(msg, position);
                        }
                    });
                    return listViewItem;
                }

            }

            private void deleteMessage(Message msg, int position) {
                //String removekey=msg.getId();
                //DatabaseReference newref=database.getReference();
                /*ref.child(removekey).removeValue();
                messagesList.remove(position);
                messageAdapter.notifyDataSetChanged();*/

                String removekey = msg.getId();
                ref.child("message").child(removekey).removeValue();
                messagesList.remove(position);
                Adapterclass adapterclass = new Adapterclass(ChatActivity.this, messagesList);
                listView.setAdapter(adapterclass);

            }
        }

