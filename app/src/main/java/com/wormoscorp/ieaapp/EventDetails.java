package com.wormoscorp.ieaapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class EventDetails extends AppCompatActivity {
    TextView eventTitleTv, eventDateTv, eventTimeTv, eventDescriptionTv, joinEventYes, joinEventNo,eventMemberTextTv,eventPhotoTextTv;
    RecyclerView eventMembersRv,eventPhotoRv;
    FirebaseRecyclerOptions<EventMemberItemModel> options;
    FirebaseRecyclerOptions<PastEventPhotoModel> pastEvenPhotoOptions;
    PastEventPhotoAdapter pastEventPhotoAdapter;
    EventMemberItemAdapter eventMemberItemAdapter;
    DatabaseReference eventsRef;
    ImageView eventDetailImg;
    AppCompatButton addMyselfBtn, eventDetailsBackButton,joinNowBtn;
    Dialog addMyselfDialog;
    FirebaseAuth mAuth;
    String EventItemKey,EventType;
    CardView eventChat;
    String[] userEmail = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        EventItemKey = getIntent().getStringExtra("EventItemKey");
        EventType = getIntent().getStringExtra("EventType");
        Log.d(("ItemKey"), "onCreate: " + EventItemKey+EventType);

        eventTitleTv = findViewById(R.id.event_title_txt);
        eventDateTv = findViewById(R.id.event_date_txt);
        eventTimeTv = findViewById(R.id.event_time_txt);
        eventDescriptionTv = findViewById(R.id.event_description_txt);
        eventMembersRv = findViewById(R.id.event_members_rv);
        eventDetailImg = findViewById(R.id.event_detail_img);
        addMyselfBtn = findViewById(R.id.event_add_myself_btn);
        joinNowBtn = findViewById(R.id.join_now_btn);
        eventDetailsBackButton = findViewById(R.id.events_detail_back_btn);
        eventMemberTextTv = findViewById(R.id.event_members);
        eventChat = findViewById(R.id.eventDetail_chatBtn);
        eventPhotoRv = findViewById(R.id.event_photos_rv);
        eventPhotoTextTv = findViewById(R.id.event_photos);

        addMyselfDialog = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            eventMembersRv.setVisibility(View.GONE);
            joinNowBtn.setVisibility(View.GONE);
            eventMemberTextTv.setVisibility(View.GONE);
            addMyselfBtn.setVisibility(View.GONE);
            eventChat.setVisibility(View.GONE);
        }
        if(EventType.equals("Past Events")){
            eventMembersRv.setVisibility(View.GONE);
            setMargins(eventMembersRv,0,1,0,0);
            joinNowBtn.setVisibility(View.GONE);
            setMargins(eventChat,0,0,0,25);
            eventMemberTextTv.setVisibility(View.GONE);
            setMargins(eventMemberTextTv,0,1,0,0);
            addMyselfBtn.setVisibility(View.GONE);
            eventPhotoRv.setVisibility(View.VISIBLE);
            eventPhotoTextTv.setVisibility(View.VISIBLE);
        }

        eventPhotoRv.setLayoutManager(new MembersDirectory.WrapContentLinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        pastEvenPhotoOptions = new FirebaseRecyclerOptions.Builder<PastEventPhotoModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Past Events/" + EventItemKey + "/image_uris"), PastEventPhotoModel.class)
                .build();

        pastEventPhotoAdapter = new PastEventPhotoAdapter(pastEvenPhotoOptions);
        eventPhotoRv.setAdapter(pastEventPhotoAdapter);
        pastEventPhotoAdapter.startListening();

        eventsRef = FirebaseDatabase.getInstance().getReference().child(EventType);
        eventsRef.child(EventItemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventTitleTv.setText(Objects.requireNonNull(snapshot.child("title").getValue()).toString());
                eventTimeTv.setText(Objects.requireNonNull(snapshot.child("time").getValue()).toString());
                eventDateTv.setText(Objects.requireNonNull(snapshot.child("date").getValue()).toString());
                eventDescriptionTv.setText(Objects.requireNonNull(snapshot.child("description").getValue()).toString());

                Glide.with(getApplicationContext())
                        .load(Objects.requireNonNull(snapshot.child("imgUrl").getValue()).toString())
                        .error(R.drawable.iea_logo)
                        .placeholder(R.drawable.iea_logo)
                        .into(eventDetailImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        eventMembersRv.setLayoutManager(new MemberDirectoryDetail.WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        options = new FirebaseRecyclerOptions.Builder<EventMemberItemModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Events/" + EventItemKey + "/members"), EventMemberItemModel.class)
                .build();

        eventMemberItemAdapter = new EventMemberItemAdapter(options);
        eventMembersRv.setAdapter(eventMemberItemAdapter);


        joinNowBtn.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View addMyselfView = inflater.inflate(R.layout.join_event_popup, null);

            joinEventYes = addMyselfView.findViewById(R.id.join_event_yes);
            joinEventNo = addMyselfView.findViewById(R.id.join_event_no);

            addMyselfDialog.setContentView(addMyselfView);
            addMyselfDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addMyselfDialog.show();

            joinEventYes.setOnClickListener(v -> {

                userEmail[0] = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                DatabaseReference eventMembersReference = FirebaseDatabase.getInstance().getReference().child("Events/" + EventItemKey + "/members/" + userEmail[0].replaceAll("\\.", "%7"));

                final String[] userImgUrl = new String[1];
                final String[] token = new String[1];
                FirebaseDatabase.getInstance().getReference("Registered Users/" + Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.", "%7")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userImgUrl[0] = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                        token[0] = Objects.requireNonNull(snapshot.child("user_token").getValue()).toString();

                        HashMap<String, Object> memberData = new HashMap<>();
                        memberData.put("email", userEmail[0]);
                        memberData.put("imageUrl", userImgUrl[0]);
                        memberData.put("user_token",token[0]);
                        Log.d("TAG", "onDataChange: "+token[0]);

                        eventMembersReference.updateChildren(memberData).addOnSuccessListener(o ->{
                                            Toast.makeText(EventDetails.this, "You have been added", Toast.LENGTH_SHORT).show();
                                        }

                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(EventDetails.this, "You could not be added", Toast.LENGTH_SHORT).show());
                        addMyselfDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });

            joinEventNo.setOnClickListener(v -> addMyselfDialog.dismiss());
        });

        addMyselfBtn.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View addMyselfView = inflater.inflate(R.layout.join_event_popup, null);

            joinEventYes = addMyselfView.findViewById(R.id.join_event_yes);
            joinEventNo = addMyselfView.findViewById(R.id.join_event_no);

            addMyselfDialog.setContentView(addMyselfView);
            addMyselfDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addMyselfDialog.show();

            joinEventYes.setOnClickListener(v -> {

                userEmail[0] = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                DatabaseReference eventMembersReference = FirebaseDatabase.getInstance().getReference().child("Events/" + EventItemKey + "/members/" + userEmail[0].replaceAll("\\.", "%7"));

                final String[] userImgUrl = new String[1];
                final String[] token = new String[1];
                FirebaseDatabase.getInstance().getReference("Registered Users/" + Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.", "%7")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userImgUrl[0] = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                        token[0] = Objects.requireNonNull(snapshot.child("user_token").getValue()).toString();

                        HashMap<String, Object> memberData = new HashMap<>();
                        memberData.put("email", userEmail[0]);
                        memberData.put("imageUrl", userImgUrl[0]);
                        memberData.put("user_token",token[0]);
                        Log.d("TAG", "onDataChange: "+token[0]);

                        eventMembersReference.updateChildren(memberData).addOnSuccessListener(o ->{
                                            Toast.makeText(EventDetails.this, "You have been added", Toast.LENGTH_SHORT).show();
                                        }

                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(EventDetails.this, "You could not be added", Toast.LENGTH_SHORT).show());
                        addMyselfDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });

            joinEventNo.setOnClickListener(v -> addMyselfDialog.dismiss());
        });

        eventDetailsBackButton.setOnClickListener(view -> finish());

        eventChat.setOnClickListener(v -> {
            final String[] eventChatKey = new String[1];

            eventsRef.child(EventItemKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(!snapshot.child("EventChatKey").exists()){
                        eventChatKey[0] = eventTitleTv.getText().toString()+eventDateTv.getText().toString();
                        HashMap<String,Object> eventMap = new HashMap<>();
                        eventMap.put("EventChatKey",eventChatKey[0]);
                        eventsRef.child(EventItemKey).updateChildren(eventMap).addOnSuccessListener(s->{
                            startActivity(new Intent(EventDetails.this,EventChatSession.class).putExtra("chatKey",eventChatKey[0])
                                    .putExtra("eventItemKey",EventItemKey).putExtra("key","0").putExtra("eventType",EventType));
                            Log.d("one2", "onDataChange: ");
                        }).addOnFailureListener(f -> {
                            Toast.makeText(EventDetails.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        });
                    } else if(snapshot.child("EventChatKey").exists()){
                        eventChatKey[0] = snapshot.child("EventChatKey").getValue().toString();
                        startActivity(new Intent(EventDetails.this,EventChatSession.class).putExtra("chatKey",eventChatKey[0])
                                .putExtra("eventItemKey",EventItemKey).putExtra("key","1").putExtra("eventType",EventType));
                        Log.d("one", "onDataChange: ");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventMemberItemAdapter.startListening();
        pastEventPhotoAdapter.startListening();
    }


    @Override
    protected void onResume() {
        super.onResume();
        eventMemberItemAdapter.startListening();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        eventMemberItemAdapter.stopListening();
//    }


    class EventMemberItemAdapter extends FirebaseRecyclerAdapter<EventMemberItemModel, com.wormoscorp.ieaapp.EventDetails.EventMemberItemAdapter.EventMemberItemViewHolder> {
        Dialog notComingToEventDialog;

        public EventMemberItemAdapter(@NonNull FirebaseRecyclerOptions<EventMemberItemModel> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull com.wormoscorp.ieaapp.EventDetails.EventMemberItemAdapter.EventMemberItemViewHolder holder, int position, @NonNull EventMemberItemModel model) {
            notComingToEventDialog = new Dialog(EventDetails.this);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Glide.with(getApplicationContext())
                    .load(model.getImageUrl())
                    .circleCrop()
                    .error(R.drawable.iea_logo)
                    .placeholder(R.drawable.iea_logo)
                    .into(holder.recyclerMemberItemImg);

            holder.eventMemberItem.setOnClickListener(view -> {
                TextView coming, notComing;
                if (Objects.requireNonNull(getRef(position).getKey()).equals(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.", "%7"))) {
                    LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    @SuppressLint("InflateParams") View removeFromEventView = inflater.inflate(R.layout.remove_from_event_popup, null);

                    coming = removeFromEventView.findViewById(R.id.remove_from_event_no);
                    notComing = removeFromEventView.findViewById(R.id.remove_from_event_yes);

                    notComingToEventDialog.setContentView(removeFromEventView);
                    notComingToEventDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    notComingToEventDialog.show();

                    coming.setOnClickListener(v -> notComingToEventDialog.dismiss());
                    notComing.setOnClickListener(v -> {
                        FirebaseDatabase.getInstance().getReference("Events/" + EventItemKey + "/members/" + Objects.requireNonNull(mAuth.getCurrentUser().getEmail()).replaceAll("\\.", "%7")).removeValue();
                        Toast.makeText(EventDetails.this, "You have been removed from the list.", Toast.LENGTH_SHORT).show();
                        notComingToEventDialog.dismiss();
                    });
                } else {
                    view.getContext().startActivity(new Intent(view.getContext(), MemberDirectoryDetail.class).putExtra("MemberItemKey", model.getEmail().replaceAll("\\.", "%7")));
                }
            });
        }

        @NonNull
        @Override
        public com.wormoscorp.ieaapp.EventDetails.EventMemberItemAdapter.EventMemberItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View eventItemMemberView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_member_item, parent, false);
            return new com.wormoscorp.ieaapp.EventDetails.EventMemberItemAdapter.EventMemberItemViewHolder(eventItemMemberView);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class EventMemberItemViewHolder extends RecyclerView.ViewHolder {
            ImageView recyclerMemberItemImg;
            View eventMemberItem;

            public EventMemberItemViewHolder(@NonNull View itemView) {
                super(itemView);

                recyclerMemberItemImg = itemView.findViewById(R.id.event_member_item_img);
                eventMemberItem = itemView;
            }

        }
    }
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }


}