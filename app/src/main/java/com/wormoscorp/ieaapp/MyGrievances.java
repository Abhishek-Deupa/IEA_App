package com.wormoscorp.ieaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MyGrievances extends AppCompatActivity {

    RecyclerView myGrievancesRecyclerView;
    AppCompatButton myGrievancesBackButton;
    MyGrievancesAdapter myGrievancesAdapter;
    FirebaseRecyclerOptions<MyGrievanceModel> options;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public String activityStatusValue;
    String notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_grievances);

        Intent intent = getIntent();
        activityStatusValue = intent.getStringExtra("status");
        notify = getIntent().getStringExtra("notify");
        myGrievancesRecyclerView = findViewById(R.id.my_grievances_recyclerView);
        LinearLayoutManager linearLayoutManager = new MembersDirectory.WrapContentLinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myGrievancesRecyclerView.setLayoutManager(linearLayoutManager);
        myGrievancesBackButton = findViewById(R.id.my_grievances_back_button);

        switch (activityStatusValue) {
            case "Active":
                options = new FirebaseRecyclerOptions.Builder<MyGrievanceModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Unsolved Grievances").orderByChild("email").startAt(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).endAt(mAuth.getCurrentUser().getEmail() + "\uf8ff"), MyGrievanceModel.class)
                        .build();
                break;
            case "Solved":
                options = new FirebaseRecyclerOptions.Builder<MyGrievanceModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Solved Grievance").child(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.", "%7")), MyGrievanceModel.class)
                        .build();
                break;
            default:
                options = new FirebaseRecyclerOptions.Builder<MyGrievanceModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Unresolved Grievances").child(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.", "%7")), MyGrievanceModel.class)
                        .build();
                break;
        }

        myGrievancesAdapter = new MyGrievancesAdapter(options);
        myGrievancesRecyclerView.setAdapter(myGrievancesAdapter);
        myGrievancesBackButton.setOnClickListener(view -> {
            if(notify!=null){
                startActivity(new Intent(this,explore_menu.class));
            }
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myGrievancesAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(notify!=null){
            startActivity(new Intent(this,explore_menu.class));
            finish();
        } else{finish();}
    }

}
