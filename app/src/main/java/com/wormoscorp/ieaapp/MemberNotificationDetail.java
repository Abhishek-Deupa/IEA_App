package com.wormoscorp.ieaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MemberNotificationDetail extends AppCompatActivity {


    TextView notificationTitle,notificationDescription;
    DatabaseReference databaseReference;
    String ownerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_notification_detail);
        notificationDescription=findViewById(R.id.notification_descripiton_editText);
        notificationTitle= findViewById(R.id.notification_title_editText);
        String notificationKey = getIntent().getStringExtra("NotificationItemKey");

        ownerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().replaceAll("\\.","%7");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification").child(ownerEmail).child(notificationKey);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Titlestr = Objects.requireNonNull(snapshot.child("notificationTitle").getValue()).toString();
                String Descriptionstr = Objects.requireNonNull(snapshot.child("notificationContent").getValue()).toString();

                notificationDescription.setText(Descriptionstr);
                notificationTitle.setText(Titlestr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}