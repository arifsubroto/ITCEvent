package com.arifsubroto.itcevent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.arifsubroto.itcevent.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailEventActivity extends Activity {

    DatabaseReference mDatabaseEventX, mDatabaseEvent;

    ListView listViewGuests;
    List<String> guests;

    Intent intent;

    private TextView textViewNama, textViewDeskripsi, textViewWaktu, textViewTempat, textViewBiaya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        intent = getIntent();
        mDatabaseEventX = FirebaseDatabase.getInstance().getReference("eventUser").child(intent.getStringExtra("EVENT_ID"));
        mDatabaseEvent = FirebaseDatabase.getInstance().getReference("events");

        //Get View
        listViewGuests = (ListView) findViewById(R.id.listViewGuests);
        textViewNama = (TextView) findViewById(R.id.textViewName);
        textViewDeskripsi = (TextView) findViewById(R.id.textViewDeskripsi);
        textViewWaktu = (TextView) findViewById(R.id.textViewWaktu);
        textViewTempat = (TextView) findViewById(R.id.textViewTempat);
        textViewBiaya = (TextView) findViewById(R.id.textViewBiaya);

        guests = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseEventX.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                guests.clear();
                for (DataSnapshot thisGuest : dataSnapshot.getChildren()) {
                    guests.add(thisGuest.getKey());
                }
                GuestList guestListAdapter = new GuestList(DetailEventActivity.this, guests);
                listViewGuests.setAdapter(guestListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = mDatabaseEvent.orderByKey().equalTo(intent.getStringExtra("EVENT_ID"));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot thisEvent : dataSnapshot.getChildren()) {
                    Event event = thisEvent.getValue(Event.class);
                    textViewNama.setText(event.getNama());
                    textViewDeskripsi.setText(event.getDeskripsi());
                    textViewWaktu.setText("Waktu    : "+event.getWaktu());
                    textViewTempat.setText("Tempat : "+event.getTempat());
                    textViewBiaya.setText("Biaya      : "+event.getFee());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
