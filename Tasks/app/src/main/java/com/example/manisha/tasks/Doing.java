package com.example.manisha.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

public class Doing extends Fragment {

    ArrayList<Task> doingList = new ArrayList<Task>();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_doing, container, false);
        listView=rootView.findViewById(R.id.listViewDoing);
        mRootRef.child("Doing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doingList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("hello", "onDataChange: ");
                    if (postSnapshot != null) {
                        Task rtask = new Task();
                        rtask.setTaskName(postSnapshot.child("taskName").getValue().toString());
                        rtask.setStatus(postSnapshot.child("status").getValue().toString());
                        rtask.setDeadline(postSnapshot.child("deadline").getValue().toString());
                        rtask.setTaskid(postSnapshot.child("taskid").getValue().toString());
                        Log.d("hello1", rtask.getTaskName());
                        doingList.add(rtask);
                    }
                }
                Adapterclass adapterclass = new Adapterclass(getActivity(), doingList);
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

            //textViewName.setText(sources.name);
            textViewName.setText(task.getTaskName());
            textViewDate.setText(task.getDeadline());
            String status = task.getStatus();

            imageView.setImageResource(R.drawable.doing);
            imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMessage(task,position);
                        task.setStatus("done");
                        writeNewUserDone(task.getTaskName(),task.getDeadline());
                    }
                });
            return listViewItem;
        }
    }

    private void writeNewUserDone(String taskname, String deadline) {

        String id = mRootRef.child("Done").push().getKey();
        //Log.d("hello", id);
        Task user = new Task(id, taskname, deadline, "Done");
        mRootRef.child("Done").child(user.getTaskid()).setValue(user);
    }
    private void deleteMessage(Task msg, int position) {
        String removekey = msg.getTaskid();
        String child="";
        if(msg.getStatus().equals("todo")) {
            child = "ToDo";
            //toDoList.remove(position);
        }
        else if(msg.getStatus().equals("doing")) {
            child = "Doing";
            doingList.remove(position);
        }
        else if(msg.getStatus().equals("done")) {
            child = "Done";
            //doneList.remove(position);
        }
        mRootRef.child(child).child(removekey).removeValue();
        //toDoList.remove(position);
    }
}
