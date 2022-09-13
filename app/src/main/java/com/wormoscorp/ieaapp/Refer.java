package com.wormoscorp.ieaapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Refer extends AppCompatActivity {

    EditText nameEdtTxt, company_nameEdtTxt, contact_numberEdtTxt, email_addressEdtTxt;
    AppCompatButton referBackButton, referButton;
    String name, company_name, contact_number;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    CardView myReferBtn;
    final String status = "In Review";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Refering...");

        referBackButton = findViewById(R.id.refer_back_button);
        referButton = findViewById(R.id.refer_button);
        nameEdtTxt = findViewById(R.id.refer_name_editText);
        company_nameEdtTxt = findViewById(R.id.refer_company_name_editText);
        contact_numberEdtTxt = findViewById(R.id.refer_contact_number_editText);
        email_addressEdtTxt = findViewById(R.id.refer_email_address_editText);
        myReferBtn = findViewById(R.id.my_refer_btn);

        myReferBtn.setOnClickListener(view -> {
            startActivity(new Intent(Refer.this,My_Refer.class));
        });


        referBackButton.setOnClickListener(view -> finish());

        referButton.setOnClickListener(v -> {
            String email_address;
            if (TextUtils.isEmpty(nameEdtTxt.getText().toString())) {
                nameEdtTxt.setError("Name cannot be empty!");
                nameEdtTxt.requestFocus();
            } else if (TextUtils.isEmpty(company_nameEdtTxt.getText().toString())) {
                company_nameEdtTxt.setError("Company name cannot be empty!");
                company_nameEdtTxt.requestFocus();
            } else if (TextUtils.isEmpty(contact_numberEdtTxt.getText().toString())) {
                contact_numberEdtTxt.setError("Contact number cannot be empty!");
                contact_numberEdtTxt.requestFocus();
            } else {
                progressDialog.show();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Refer");
                DatabaseReference databaseReferencebyuser = FirebaseDatabase.getInstance().getReference().child("Refer by Member");
                String referKey = databaseReference.push().getKey();
                name = nameEdtTxt.getText().toString();
                company_name = company_nameEdtTxt.getText().toString();
                contact_number = contact_numberEdtTxt.getText().toString();
                if(email_addressEdtTxt.getText().toString().isEmpty()){
                    email_address = "";
                }else {
                    email_address = email_addressEdtTxt.getText().toString();
                }

                Refermodelclass newrefer = new Refermodelclass(name,email_address,contact_number,company_name,mAuth.getCurrentUser().getEmail(),status);
                databaseReference.child(referKey).setValue(newrefer).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        databaseReferencebyuser.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7"))
                                .child(referKey).setValue(newrefer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        sendEmail();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Refer.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Refer.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @SuppressLint("IntentReset")
    protected void sendEmail() {
        name = nameEdtTxt.getText().toString();
        company_name = company_nameEdtTxt.getText().toString();
        contact_number = contact_numberEdtTxt.getText().toString();
        String email_address = email_addressEdtTxt.getText().toString();
        Log.i("Send email", "");

        String[] TO = {"wormoscorporation@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "IEA Member Referral");
        if(email_address.isEmpty()){
            emailIntent.putExtra(Intent.EXTRA_TEXT, "New Member Referral\n\nThis link is shared by: " + name);
        } else {
            emailIntent.putExtra(Intent.EXTRA_TEXT, "New Member Referral\n\nThis link is shared by: " + name);
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Refer.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}