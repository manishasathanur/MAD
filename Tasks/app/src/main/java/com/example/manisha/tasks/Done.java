package com.example.manisha.tasks;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Done extends Fragment{
    ListView listView;
    ArrayList<Task> doneList = new ArrayList<Task>();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_done, container, false);
        //ArrayList<Task> doneList = new ArrayList<Task>();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        listView = rootView.findViewById(R.id.listViewDone);

        mRootRef.child("Done").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doneList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("hello", "onDataChange: ");
                    if (postSnapshot != null) {
                        Task rtask = new Task();
                        rtask.setTaskName(postSnapshot.child("taskName").getValue().toString());
                        rtask.setStatus(postSnapshot.child("status").getValue().toString());
                        rtask.setDeadline(postSnapshot.child("deadline").getValue().toString());
                        rtask.setTaskid(postSnapshot.child("taskid").getValue().toString());
                        Log.d("hello1", rtask.getTaskName());
                        doneList.add(rtask);
                        }
                    }
                Adapterclass adapterclass = new Adapterclass(getActivity(), doneList);
                listView.setAdapter(adapterclass);
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rootView;
    }


    public class Adapterclass extends ArrayAdapter<Task> {
        private Activity context;
        private ArrayList<Task> list;

        public Adapterclass(Activity context, List<Task> list) {
            super(context, R.layout.list_adapter, list);
            this.context = context;
            this.list = (ArrayList<Task>) list;
        }

        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            final Task task = getItem(position);
            View listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.list_adapter, null, true);

            TextView textViewName = listViewItem.findViewById(R.id.textViewName);
            final ImageView imageView = listViewItem.findViewById(R.id.imageView);
            TextView textViewDate = listViewItem.findViewById(R.id.textViewDate);
            //textViewName.setText(sources.name);run chey error chubinchu

            textViewName.setText(task.getTaskName());
            textViewDate.setText(task.getDeadline());
            String status = task.getStatus();

                imageView.setImageResource(R.drawable.delete);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder;
                        alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Are you sure you want to delete this Task?");
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Task obj=list.get(position);
                                deleteMessage(task,position);
                                return;
                            }
                        });
                        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        //deleteMessage(task,position);
                    }
                });

            return listViewItem;
        }


    }

    private void deleteMessage(Task msg, int position) {
        String removekey = msg.getTaskid();
        String child="";
        mRootRef.child("Done").child(removekey).removeValue();
        doneList.remove(position);
       Adapterclass adapterclass = new Adapterclass(getActivity(),doneList);
        listView.setAdapter(adapterclass);
    }
}
