package com.wormoscorp.ieaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MembershipRenewal extends AppCompatActivity {

    AppCompatButton renewalBackBtn,renewalPayBtn;
    EditText renewalName,renewalEmail,renewalCompanyName;
    DatabaseReference registryRef = FirebaseDatabase.getInstance().getReference("Registered Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_renewal);

        renewalBackBtn = findViewById(R.id.renewal_back_button);
        renewalPayBtn = findViewById(R.id.renewal_proceed_to_pay_btn);
        renewalName = findViewById(R.id.renewal_full_name);
        renewalCompanyName = findViewById(R.id.renewal_companyname);
        renewalEmail = findViewById(R.id.renewalEmail);

        renewalBackBtn.setOnClickListener(v-> finish());
        renewalPayBtn.setOnClickListener(v ->{
            if (TextUtils.isEmpty(renewalName.getText().toString())) {
                renewalName.setError("Email cannot be empty!");
                renewalName.requestFocus();
            } else if (TextUtils.isEmpty(renewalEmail.getText().toString())) {
                renewalEmail.setError("Password cannot be empty!");
                renewalEmail.requestFocus();
            }else if (TextUtils.isEmpty(renewalCompanyName.getText().toString())) {
                renewalCompanyName.setError("Password cannot be empty!");
                renewalCompanyName.requestFocus();
            } else {
                registryRef.child(renewalEmail.getText().toString().replaceAll("\\.","%7")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String membershipType = Objects.requireNonNull(snapshot.child("memberfee").getValue()).toString();
                            startActivity(new Intent(MembershipRenewal.this,payment.class).putExtra("renewal","2")
                                    .putExtra("email",renewalEmail.getText().toString()).putExtra("memberfee",membershipType)
                                     .putExtra("name",renewalName.getText().toString()).putExtra("cname",renewalCompanyName.getText().toString()));
                        }else {
                            Toast.makeText(MembershipRenewal.this, "Enter Correct Email", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}