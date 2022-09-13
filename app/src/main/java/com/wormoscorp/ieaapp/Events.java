package com.wormoscorp.ieaapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class Events extends AppCompatActivity {
    RecyclerView upcomingEventRecyclerView,pastEventRecyclerView;
    FirebaseRecyclerOptions<UpcomingEventModel> options,pastoptions;
    UpcomingEventAdapter upcomingEventAdapter;
    PastEventAdapter pastEventAdapter;
    EditText eventsSearchTv;
    AppCompatButton eventsBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        eventsBackButton = findViewById(R.id.events_back_button);

        upcomingEventRecyclerView = findViewById(R.id.upcoming_events_rv);
        pastEventRecyclerView = findViewById(R.id.past_events_rv);
        eventsSearchTv = findViewById(R.id.search_events_edtTxt);
        upcomingEventRecyclerView.setLayoutManager(new MemberDirectoryDetail.WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        pastEventRecyclerView.setLayoutManager(new MemberDirectoryDetail.WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        options = new FirebaseRecyclerOptions.Builder<UpcomingEventModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Events"), UpcomingEventModel.class)
                .build();
        pastoptions  = new FirebaseRecyclerOptions.Builder<UpcomingEventModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Past Events"), UpcomingEventModel.class)
                .build();

        pastEventAdapter = new PastEventAdapter(pastoptions);
        pastEventRecyclerView.setAdapter(pastEventAdapter);


        upcomingEventAdapter = new UpcomingEventAdapter(options);
        upcomingEventRecyclerView.setAdapter(upcomingEventAdapter);

        eventsSearchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(eventsSearchTv.getText().toString())){
                    options = new FirebaseRecyclerOptions.Builder<UpcomingEventModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Events").orderByChild("lowercase_title").startAt(eventsSearchTv.getText().toString()).endAt(eventsSearchTv.getText().toString()+"\uf8ff"), UpcomingEventModel.class)
                            .build();
                    pastoptions = new FirebaseRecyclerOptions.Builder<UpcomingEventModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Past Events").orderByChild("lowercase_title").startAt(eventsSearchTv.getText().toString()).endAt(eventsSearchTv.getText().toString()+"\uf8ff"), UpcomingEventModel.class)
                            .build();
                    pastEventAdapter = new PastEventAdapter(pastoptions);
                    pastEventAdapter.startListening();
                    pastEventRecyclerView.setAdapter(pastEventAdapter);
                    upcomingEventAdapter = new UpcomingEventAdapter(options);
                    upcomingEventAdapter.startListening();
                    upcomingEventRecyclerView.setAdapter(upcomingEventAdapter);
                } else {
                    options = new FirebaseRecyclerOptions.Builder<UpcomingEventModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Events"), UpcomingEventModel.class)
                            .build();
                    pastoptions  = new FirebaseRecyclerOptions.Builder<UpcomingEventModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Past Events"), UpcomingEventModel.class)
                            .build();
                }
                pastEventAdapter = new PastEventAdapter(pastoptions);
                pastEventAdapter.startListening();
                pastEventRecyclerView.setAdapter(pastEventAdapter);
                upcomingEventAdapter = new UpcomingEventAdapter(options);
                upcomingEventAdapter.startListening();
                upcomingEventRecyclerView.setAdapter(upcomingEventAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eventsBackButton.setOnClickListener(view -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        upcomingEventAdapter.startListening();
        pastEventAdapter.startListening();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        upcomingEventAdapter.stopListening();
//    }
}