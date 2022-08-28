package com.example.ieaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventChatSession extends AppCompatActivity {
    AppCompatButton eventChatBackBtn;
    CircleImageView eventChatSendBtn,eventChatIcon;
    EditText eventChatText;
    TextView eventChatName;
    RecyclerView eventChatRV;
    FirebaseRecyclerOptions<EndUserChatModel> options;
    EventChatAdapter eventChatAdapter;
    String eventItemKey,key,notify,chatKey,EventType,senderName,colors,eventNameStr;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference eventChatData = FirebaseDatabase.getInstance().getReference("Events Chat");
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_chat_session);
        eventChatBackBtn= findViewById(R.id.eventchat_session_back_button);
        eventChatSendBtn = findViewById(R.id.eventchat_session_send_button);
        eventChatIcon = findViewById(R.id.eventchat_session_user_image);
        eventChatText = findViewById(R.id.eventMsgText);
        eventChatName = findViewById(R.id.eventchat_name_text);
        eventChatRV = findViewById(R.id.eventchatSessionRV);
        eventItemKey=getIntent().getStringExtra("eventItemKey");
        key = getIntent().getStringExtra("key");
        notify = getIntent().getStringExtra("notify");
        if(key != null){
            if(key.equals("0")){
                finish();
            }
        }

        eventChatBackBtn.setOnClickListener(view -> {
            if(notify!=null){
                startActivity(new Intent(this,explore_menu.class));
                finish();
            } else{finish();}
        });
        chatKey = getIntent().getStringExtra("chatKey");
        EventType = getIntent().getStringExtra("eventType");

        databaseReference.child(EventType).child(eventItemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventNameStr= Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                String userPurlStr= Objects.requireNonNull(snapshot.child("imgUrl").getValue()).toString();
                eventChatName.setText(eventNameStr);
                Glide.with(getApplicationContext())
                        .load(userPurlStr)
                        .placeholder(R.drawable.iea_logo)
                        .circleCrop()
                        .error(R.drawable.iea_logo)
                        .into(eventChatIcon);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("Registered Users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.","%7")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    senderName = snapshot.child("name").getValue().toString();
                    colors = snapshot.child("color").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        eventChatData.child(chatKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    position = (int) snapshot.getChildrenCount();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        eventChatRV.setLayoutManager(linearLayoutManager);


        options = new FirebaseRecyclerOptions.Builder<EndUserChatModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Events Chat").child(chatKey), EndUserChatModel.class)
                .build();

        eventChatAdapter = new EventChatAdapter(options);
        eventChatRV.setAdapter(eventChatAdapter);

        eventChatSendBtn.setOnClickListener(view -> {
            eventChatSender(eventChatData,chatKey,eventChatAdapter);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        eventChatAdapter.startListening();

    }


    public void eventChatSender(DatabaseReference databaseReference,String key,EventChatAdapter adapter){
        EndUserChatModel chatData =new EndUserChatModel(FirebaseAuth.getInstance().getCurrentUser().getEmail(),eventChatText.getText().toString(),senderName,colors);
        if(!eventChatText.getText().toString().isEmpty()){
            String pushkey = databaseReference.child(key).push().getKey();
            databaseReference.child(key).child(pushkey).setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    eventChatRV.smoothScrollToPosition(adapter.getItemCount());
                    sendEventNotification(eventChatText.getText().toString(),senderName,eventNameStr,eventItemKey);
                    eventChatText.setText("");
                }
            });
        }
    }

    public void sendEventNotification(String message,String senderName,String eventName,String eventKey){
        DatabaseReference eventMembersReference = FirebaseDatabase.getInstance().getReference().child("Events/" + eventKey + "/members");
        eventMembersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                    String grievanceUserToken = Objects.requireNonNull(tokenSnapshot.child("user_token").getValue()).toString();
                    Log.d("noti", "onDataChange: "+grievanceUserToken);
                    FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(grievanceUserToken,
                            senderName+"-"+eventName,
                            message,
                            getApplicationContext(),
                            EventChatSession.this,
                            "eventChatSession",
                            eventKey,
                            chatKey,
                            EventType);

                    grievanceNotificationSender.SendNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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