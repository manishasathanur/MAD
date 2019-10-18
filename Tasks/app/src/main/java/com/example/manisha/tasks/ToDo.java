package com.example.manisha.tasks;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



public class ToDo extends Fragment {
    ListView listView;

    ArrayList<Task> toDoList = new ArrayList<Task>();
    ArrayList<Task> doingList = new ArrayList<Task>();
    ArrayList<Task> doneList = new ArrayList<Task>();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_todo, container, false);

        final EditText editTextDeadline = rootView.findViewById(R.id.editTextDeadline);
        final EditText editTextTask = rootView.findViewById(R.id.editTextTaskName);
        //ImageView imageView = rootView.findViewById(R.id.imageViewStatus);
        Button buttonCreate = rootView.findViewById(R.id.buttonAdd);
        listView = rootView.findViewById(R.id.listViewTodo);


        editTextDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1)
                                + "/" + String.valueOf(year);
                        editTextDeadline.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }


        });

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String task=editTextTask.getText()+"";
                if ((editTextTask.getText().toString()).equals("")) {
                    editTextTask.setError("Value cannot be null");
                    Toast.makeText(getActivity(), "Please enter task name ", Toast.LENGTH_SHORT).show();
                } else if ((editTextDeadline.getText().toString()).equals("")) {
                    editTextDeadline.setError("Set a deadline for your task");
                    Toast.makeText(getActivity(), "Please set a deadline", Toast.LENGTH_SHORT).show();
                } else {
                    String name = editTextTask.getText().toString();
                    String deadline = editTextDeadline.getText().toString();
                    editTextDeadline.setText("");
                    editTextTask.setText("");
                    writeNewUser(name, deadline);
                }
            }
        });

        mRootRef.child("ToDo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                toDoList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot != null) {
                        Task rtask = new Task();
                        rtask.setTaskName(postSnapshot.child("taskName").getValue().toString());
                        rtask.setStatus(postSnapshot.child("status").getValue().toString());
                        rtask.setDeadline(postSnapshot.child("deadline").getValue().toString());
                        rtask.setTaskid(postSnapshot.child("taskid").getValue().toString());
                        toDoList.add(rtask);

                    }
                }
                Adapterclass adapterclass = new Adapterclass(getActivity(), toDoList);
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
            textViewName.setText(task.getTaskName());
            textViewDate.setText(task.getDeadline());
            String status = task.getStatus();

            if (status.equals("todo")) {
                imageView.setImageResource(R.drawable.todo);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMessage(task,position);
                        task.setStatus("doing");
                        doingList.add(task);
                        writeNewUserDoing(task.getTaskName(),task.getDeadline());

                    }
                });

            } else if (status.equals("doing")) {
                imageView.setImageResource(R.drawable.doing);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMessage(task,position);
                        task.setStatus("done");
                        doneList.add(task);
                        writeNewUserDone(task.getTaskName(),task.getDeadline());
                    }
                });
            }
            else if(status.equals("done")){
                imageView.setImageResource(R.drawable.delete);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMessage(task,position);
                    }
                });
            }

            return listViewItem;
        }
    }

    private void deleteMessage(Task msg, int position) {
        String removekey = msg.getTaskid();
        String child="";
        if(msg.getStatus().equals("todo")) {
            child = "ToDo";
            toDoList.remove(position);
        }
        else if(msg.getStatus().equals("doing")) {
            child = "Doing";
            doingList.remove(position);
        }
        else if(msg.getStatus().equals("done")) {
            child = "Done";
            doneList.remove(position);
        }
        mRootRef.child(child).child(removekey).removeValue();
        //toDoList.remove(position);
        Adapterclass adapterclass = new Adapterclass(getActivity(), toDoList);
        listView.setAdapter(adapterclass);
    }

    private void writeNewUser(String taskname, String deadline) {

        String id = mRootRef.child("ToDo").push().getKey();
        //Log.d("hello", id);
        Task user = new Task(id, taskname, deadline, "todo");
        mRootRef.child("ToDo").child(user.getTaskid()).setValue(user);
    }
    private void writeNewUserDoing(String taskname, String deadline) {

        String id = mRootRef.child("Doing").push().getKey();
        //Log.d("hello", id);
        Task user = new Task(id, taskname, deadline, "doing");
        mRootRef.child("Doing").child(user.getTaskid()).setValue(user);
    }
    private void writeNewUserDone(String taskname, String deadline) {

        String id = mRootRef.child("Done").push().getKey();
        //Log.d("hello", id);
        Task user = new Task(id, taskname, deadline, "Done");
        mRootRef.child("Done").child(user.getTaskid()).setValue(user);
    }

}
