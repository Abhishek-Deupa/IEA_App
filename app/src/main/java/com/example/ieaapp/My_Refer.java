package com.example.ieaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class My_Refer extends AppCompatActivity {

    AppCompatButton ReferBackbtn ;
    RecyclerView myReferRv;
    MyReferAdapter myReferAdapter;
    FirebaseRecyclerOptions<Refermodelclass> options;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_refer);

        ReferBackbtn = findViewById(R.id.my_refer_back_button);
        ReferBackbtn.setOnClickListener(view -> finish());

        myReferRv = (RecyclerView)findViewById(R.id.my_refer_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myReferRv.setLayoutManager(linearLayoutManager);

        options = new FirebaseRecyclerOptions.Builder<Refermodelclass>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Refer by Member")
                        .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7")),Refermodelclass.class).build();

        myReferAdapter = new MyReferAdapter(options);
        myReferRv.setAdapter(myReferAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myReferAdapter.startListening();
    }
}