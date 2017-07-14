package com.arifsubroto.itcevent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.arifsubroto.itcevent.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageEventActivity extends Activity {

    public static final String ARTIST_NAME = "net.simplifiedcoding.firebasedatabaseexample.artistname";
    public static final String ARTIST_ID = "net.simplifiedcoding.firebasedatabaseexample.artistid";

    DatabaseReference mDatabase;

    ListView listViewEvents;
    List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        mDatabase= FirebaseDatabase.getInstance().getReference("events");


        //Get View
        listViewEvents = (ListView) findViewById(R.id.listViewEvents);

        events = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot thisEvent : dataSnapshot.getChildren()) {
                    events.add(thisEvent.getValue(Event.class));
                    Log.d("UUUUUUUUUUUUUUUUUUUUUUUUUUU", "user:" + thisEvent);
                }
                EventList eventListAdapter = new EventList(ManageEventActivity.this, events);
                listViewEvents.setAdapter(eventListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
