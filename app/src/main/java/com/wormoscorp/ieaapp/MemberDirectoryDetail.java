package com.wormoscorp.ieaapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberDirectoryDetail extends AppCompatActivity {
    ImageView memberProfileImage;
    TextView memberProfileName,
            memberCompanyName, memberAddress, memberPhoneno, memberMail, memberInfoText, memberInfoDetails;
    AppCompatButton memberProfileBackBtn, downloadBrochureBtn, moreProductButton;
    RecyclerView memberProductRecyclerView;
    String memberEmailStr, memberBrochureLink, memberAddressStr, memberPhoneStr,pdfUrl;
    MemberProductAdapter memberProductAdapter;
    CircleImageView memberEmailImg, memberPhoneImg, memberAddressImg;
    CardView memberChatCV;
    Dialog MemberinfoDialog;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();



    FirebaseRecyclerOptions<MemberProductModel> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_directory_detail);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Registered Users");

        MemberinfoDialog = new Dialog(this);
        memberAddress = findViewById(R.id.nullphonenotext);
        memberPhoneno = findViewById(R.id.nullphonenotext);
        memberProfileImage = findViewById(R.id.member_profile_image);
        memberProfileName = findViewById(R.id.member_profile_name);
        moreProductButton = findViewById(R.id.moreProduct_button);
        memberMail = findViewById(R.id.nullemailtext);
        memberEmailImg = findViewById(R.id.Member_mail_image);
        memberPhoneImg = findViewById(R.id.Member_phone_image);
        memberAddressImg = findViewById(R.id.Member_address_image);
        memberChatCV = findViewById(R.id.member_directory_chatBtn);



        memberEmailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                View PopupView = inflater.inflate(R.layout.member_info_popup, null);

                memberInfoText = PopupView.findViewById(R.id.memberInfo_text);
                memberInfoDetails = PopupView.findViewById(R.id.memberinfo_details);


                memberInfoText.setText("Email");
                memberInfoDetails.setText(memberEmailStr);
                Linkify.addLinks(memberInfoDetails, Linkify.EMAIL_ADDRESSES);
                memberInfoDetails.setLinksClickable(true);

                MemberinfoDialog.setContentView(PopupView);
                MemberinfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                MemberinfoDialog.show();
            }
        });
        memberAddressImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                View PopupView = inflater.inflate(R.layout.member_info_popup, null);


                memberInfoText = PopupView.findViewById(R.id.memberInfo_text);
                memberInfoDetails = PopupView.findViewById(R.id.memberinfo_details);

                memberInfoText.setText("Address");

                memberInfoDetails.setText(memberAddressStr);

                MemberinfoDialog.setContentView(PopupView);
                MemberinfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                MemberinfoDialog.show();

            }
        });
        memberPhoneImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View PopupView = inflater.inflate(R.layout.member_info_popup, null);

                memberInfoText = PopupView.findViewById(R.id.memberInfo_text);
                memberInfoDetails = PopupView.findViewById(R.id.memberinfo_details);


                memberInfoText.setText("Contact Number");
                memberInfoDetails.setText(memberPhoneStr);
                Linkify.addLinks(memberInfoDetails, Linkify.PHONE_NUMBERS);
                memberInfoDetails.setLinksClickable(true);

                MemberinfoDialog.setContentView(PopupView);
                MemberinfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                MemberinfoDialog.show();

            }
        });

        memberCompanyName = findViewById(R.id.member_company_name);

        memberProfileBackBtn = findViewById(R.id.memberDetail_back_button);
        downloadBrochureBtn = findViewById(R.id.downloadBrochure_button);

        memberProfileBackBtn.setOnClickListener(view -> finish());


        String coreItemKey = getIntent().getStringExtra("MemberItemKey");

        ref.child(coreItemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String memberMembershipDateStr = Objects.requireNonNull(snapshot.child("date_of_membership").getValue()).toString();
                    String memberMembershipIdStr = Objects.requireNonNull(snapshot.child("member_id").getValue()).toString();
                    memberEmailStr = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                    String memberCompanyNameStr = Objects.requireNonNull(snapshot.child("company_name").getValue()).toString();
                    memberAddressStr = Objects.requireNonNull(snapshot.child("address").getValue()).toString();
                    String memberNameStr = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    String memberPictureUrl = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                    memberPhoneStr = Objects.requireNonNull(snapshot.child("phone_number").getValue()).toString();
                    memberBrochureLink = Objects.requireNonNull(snapshot.child("brochure_url").getValue()).toString();


                    memberCompanyName.setText(memberCompanyNameStr);
                    memberAddress.setText(memberAddressStr);
                    memberPhoneno.setText(memberPhoneStr);
                    memberMail.setText(memberEmailStr);

                    memberProfileName.setText(memberNameStr);


                    Glide.with(getApplicationContext())
                            .load(memberPictureUrl)
                            .placeholder(R.drawable.iea_logo)
                            .circleCrop()
                            .error(R.drawable.iea_logo)
                            .into(memberProfileImage);

                    if(memberEmailStr.equals(mAuth.getCurrentUser().getEmail())){
                        memberChatCV.setVisibility(View.GONE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        memberProductRecyclerView = (RecyclerView) findViewById(R.id.memberProductRecycleView);
        memberProductRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        options = new FirebaseRecyclerOptions.Builder<MemberProductModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Products by Member")
                        .child(coreItemKey.replaceAll("\\.", "%7")), MemberProductModel.class)
                .build();
        memberProductAdapter = new MemberProductAdapter(options);
        memberProductRecyclerView.setAdapter(memberProductAdapter);


        moreProductButton.setOnClickListener(view -> {
            startActivity(new Intent(MemberDirectoryDetail.this,BaasMemberProfile.class).putExtra("BaasItemKey",memberEmailStr.replaceAll("\\.","%7")));
        });

        downloadBrochureBtn.setOnClickListener(view -> {
            try {
                pdfUrl = URLEncoder.encode(memberBrochureLink,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Uri uri = Uri.parse(pdfUrl);
            if (!uri.toString().equals("")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/gview?embedded=true&url=" + uri));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Brochure hasn't been uploaded yet", Toast.LENGTH_LONG).show();
            }

        });

        memberChatCV.setOnClickListener(v->{
            final String[] chatKey = new String[1];
            String ownerEmailConverted = memberEmailStr.replaceAll("\\.","%7");
            ref.child(memberEmailStr.replaceAll("\\.","%7")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(!snapshot.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).exists()){
                        chatKey[0] = ownerEmailConverted+mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7");
                        HashMap<String,Object> ownerMap = new HashMap<>();
                        ownerMap.put(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7"),chatKey[0]);
                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put(ownerEmailConverted,chatKey[0]);
                        ref.child(ownerEmailConverted).updateChildren(ownerMap);
                        ref.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).updateChildren(userMap).addOnSuccessListener(s->{
                            startActivity(new Intent(MemberDirectoryDetail.this,ChatSession.class).putExtra("chatKey",chatKey[0])
                                    .putExtra("ownerEmail",memberEmailStr).putExtra("chatType","user").putExtra("key","0"));
                            Log.d("one2", "onDataChange: ");
                        }).addOnFailureListener(f -> {
                            Toast.makeText(MemberDirectoryDetail.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        });
                    } else if(snapshot.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).exists()){
                        chatKey[0] = snapshot.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).getValue().toString();
                        startActivity(new Intent(MemberDirectoryDetail.this,ChatSession.class).putExtra("chatKey",chatKey[0])
                                .putExtra("ownerEmail",memberEmailStr).putExtra("chatType","user").putExtra("key","1"));
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
        memberProductAdapter.startListening();
    }

    public static class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "Recycler View error");
            }
        }
    }
}