package com.wormoscorp.ieaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatSession extends AppCompatActivity {
    RecyclerView chatRv;
    AppCompatButton chatBackBtn;
    CircleImageView chatSendBtn, userProfilePic;
    EditText chatText;
    TextView userName;
    DatabaseReference chatData = FirebaseDatabase.getInstance().getReference("Members Chat");
    DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Registered Users");
    String chatKey, ownerEmail, chatType, key, notify, senderName, userToken;
    EndUserChatAdapter chatAdapter;
    FirebaseRecyclerOptions<EndUserChatModel> options;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_session);

        chatRv = (RecyclerView) findViewById(R.id.chatSessionRV);
        chatBackBtn = findViewById(R.id.chat_session_back_button);
        chatSendBtn = findViewById(R.id.chat_session_send_button);
        chatText = findViewById(R.id.userMsgText);

        chatKey = getIntent().getStringExtra("chatKey");
        ownerEmail = getIntent().getStringExtra("ownerEmail");
        userProfilePic = findViewById(R.id.chat_session_user_image);
        userName = findViewById(R.id.userchat_name_text);
        String ownerEmailConverted = ownerEmail.replaceAll("\\.", "%7");
        chatType = getIntent().getStringExtra("chatType");
        key = getIntent().getStringExtra("key");
        notify = getIntent().getStringExtra("notify");
        if (key != null) {
            if (key.equals("0")) {
                finish();
            }
        }

        chatBackBtn.setOnClickListener(view -> {
            if (notify != null) {
                startActivity(new Intent(this, explore_menu.class));
                finish();
            } else {
                finish();
            }
        });


        userData.child(ownerEmailConverted).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("user_token").exists()) {
                    String userNameStr = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String userPurlStr = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                    userToken = Objects.requireNonNull(snapshot.child("user_token").getValue()).toString();
                    userName.setText(userNameStr);
                    Glide.with(getApplicationContext())
                            .load(userPurlStr)
                            .placeholder(R.drawable.iea_logo)
                            .circleCrop()
                            .error(R.drawable.iea_logo)
                            .into(userProfilePic);
                } else {
                    String userNameStr = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String userPurlStr = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                    userName.setText(userNameStr);
                    Glide.with(getApplicationContext())
                            .load(userPurlStr)
                            .placeholder(R.drawable.iea_logo)
                            .circleCrop()
                            .error(R.drawable.iea_logo)
                            .into(userProfilePic);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userData.child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", "%7")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        chatData.child(chatKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    position = (int) snapshot.getChildrenCount();
                    Log.d("TAGr", "onCreate: " + position);
                    if (chatAdapter != null) {
                        chatRv.smoothScrollToPosition(position);
                        Log.d("TAGr", "onCreate: " + chatAdapter.getItemCount());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatRv.setLayoutManager(linearLayoutManager);


        options = new FirebaseRecyclerOptions.Builder<EndUserChatModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Members Chat").child(chatKey), EndUserChatModel.class)
                .build();

        chatAdapter = new EndUserChatAdapter(options);
        chatRv.setAdapter(chatAdapter);

        chatSendBtn.setOnClickListener(v -> {
            chatSender(chatData, chatKey, chatAdapter);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.startListening();

    }


    public void chatSender(DatabaseReference databaseReference, String key, EndUserChatAdapter adapter) {

        EndUserChatModel chatData = new EndUserChatModel(FirebaseAuth.getInstance().getCurrentUser().getEmail(), chatText.getText().toString(), senderName);
        if (!chatText.getText().toString().isEmpty()) {
            String pushkey = databaseReference.child(key).push().getKey();
            databaseReference.child(key).child(pushkey).setValue(chatData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    chatRv.smoothScrollToPosition(adapter.getItemCount());
                    sendNotification(userToken, chatText.getText().toString(), senderName);
                    chatText.setText("");

                }
            });
        }
    }

    public void sendNotification(String Token, String message, String senderName) {
        FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(Token, senderName, message, getApplicationContext(), ChatSession.this, "userChatSession", FirebaseAuth.getInstance().getCurrentUser().getEmail(), chatKey);
        grievanceNotificationSender.SendNotifications();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (notify != null) {
            startActivity(new Intent(this, explore_menu.class));
            finish();
        } else {
            finish();
        }
    }
}