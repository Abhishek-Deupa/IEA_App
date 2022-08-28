package com.example.ieaapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MemberProductDetail extends AppCompatActivity {
    ImageView memberProductDetailIv;
    TextView memberProductDetailTitleTv, memberProductDetailDescTv;
    AppCompatButton memberContactBtn,productBackBtn;
    DatabaseReference productReference;
    Dialog contactDialog;
    String ownerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_product_detail);
        String productKey = getIntent().getStringExtra("memberProductKey");

        productBackBtn=findViewById(R.id.member_product_detail_back_btn);
        productBackBtn.setOnClickListener(view -> finish());


        FirebaseDatabase.getInstance().getReference("Products/" + productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ownerEmail = snapshot.child("ownerEmail").getValue().toString().replaceAll("\\.", "%7");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        memberProductDetailIv = findViewById(R.id.member_product_detail_img);
        memberProductDetailTitleTv = findViewById(R.id.member_product_detail_title_tv);
        memberProductDetailDescTv = findViewById(R.id.member_product_detail_desc_tv);
        memberContactBtn = findViewById(R.id.member_product_detail_contact_btn);
        contactDialog = new Dialog(this);

        productReference = FirebaseDatabase.getInstance().getReference("Products/" + productKey);

        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberProductDetailTitleTv.setText(Objects.requireNonNull(snapshot.child("productTitle").getValue()).toString());
                memberProductDetailDescTv.setText(Objects.requireNonNull(snapshot.child("productDescription").getValue()).toString());

                Glide.with(MemberProductDetail.this)
                        .load(Objects.requireNonNull(snapshot.child("productImageUrl").getValue()).toString())
                        .error(R.drawable.iea_logo)
                        .placeholder(R.drawable.iea_logo)
                        .into(memberProductDetailIv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        memberContactBtn.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference("Registered Users").child(ownerEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.core_member_contact_popup, null);

                        TextView coreMemberPhoneNumber = view.findViewById(R.id.core_member_phone_number);
                        TextView coreMemberEmail = view.findViewById(R.id.core_member_email);

                        String coreEmail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                        String corePhoneNumber = Objects.requireNonNull(snapshot.child("phone_number").getValue()).toString();

                        coreMemberPhoneNumber.setText(corePhoneNumber);
                        coreMemberEmail.setText(coreEmail);

                        contactDialog.setContentView(view);
                        contactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        contactDialog.show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }
}