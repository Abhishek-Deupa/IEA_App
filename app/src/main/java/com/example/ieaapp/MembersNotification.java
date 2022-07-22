package com.example.ieaapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MembersNotification extends AppCompatActivity {
    RecyclerView memberNotificationRv;
    MemberNotificationAdapter memberNotificationAdapter;
    FirebaseRecyclerOptions<MemberNotificationModel> options;
    AppCompatButton memberNotificationBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_notification);
        memberNotificationRv = findViewById(R.id.member_notification_rv);
        memberNotificationBackBtn = findViewById(R.id.member_notification_back_icon);

        memberNotificationRv.setLayoutManager(new LinearLayoutManager(this));

        options = new FirebaseRecyclerOptions.Builder<MemberNotificationModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", "%7")), MemberNotificationModel.class)
                .build();

        Log.d("email", FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", "%7"));

        memberNotificationAdapter = new MemberNotificationAdapter(options);
        memberNotificationRv.setAdapter(memberNotificationAdapter);


        memberNotificationBackBtn.setOnClickListener(view -> finish());


    }


    @Override
    public void onStart() {
        super.onStart();
        memberNotificationAdapter.startListening();
    }

}

