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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Refer extends AppCompatActivity {

    final String status = "In Review";
    EditText nameEdtTxt, company_nameEdtTxt, contact_numberEdtTxt, email_addressEdtTxt;
    AppCompatButton referBackButton, referButton;
    String name, company_name, contact_number;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    CardView myReferBtn, referWhatsAppCv;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Referring...");

        referBackButton = findViewById(R.id.refer_back_button);
        referButton = findViewById(R.id.refer_button);
        nameEdtTxt = findViewById(R.id.refer_name_editText);
        company_nameEdtTxt = findViewById(R.id.refer_company_name_editText);
        contact_numberEdtTxt = findViewById(R.id.refer_contact_number_editText);
        email_addressEdtTxt = findViewById(R.id.refer_email_address_editText);
        myReferBtn = findViewById(R.id.my_refer_btn);
        referWhatsAppCv = findViewById(R.id.refer_whats_app_cv);

        myReferBtn.setOnClickListener(view -> startActivity(new Intent(Refer.this, My_Refer.class)));


        referBackButton.setOnClickListener(view -> finish());

        //referring on WhatsApp
        referWhatsAppCv.setOnClickListener(view -> referOnWhatsApp());

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
                DatabaseReference databaseReferenceByUser = FirebaseDatabase.getInstance().getReference().child("Refer by Member");
                String referKey = databaseReference.push().getKey();
                name = nameEdtTxt.getText().toString();
                company_name = company_nameEdtTxt.getText().toString();
                contact_number = contact_numberEdtTxt.getText().toString();
                if (email_addressEdtTxt.getText().toString().isEmpty()) {
                    email_address = "";
                } else {
                    email_address = email_addressEdtTxt.getText().toString();
                }

                Refermodelclass newRefer = new Refermodelclass(name, email_address, contact_number, company_name, Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), status);
                databaseReference.child(Objects.requireNonNull(referKey)).setValue(newRefer).addOnSuccessListener(unused -> databaseReferenceByUser.child(Objects.requireNonNull(mAuth.getCurrentUser().getEmail()).replaceAll("\\.", "%7"))
                        .child(referKey).setValue(newRefer).addOnSuccessListener(unused1 -> {
                            progressDialog.dismiss();
                            sendEmail();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(Refer.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                        })).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Refer.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    //function to refer on WhatsApp
    private void referOnWhatsApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String appUrl = "https://play.google.com/store/apps/details?id=com.wormoscorp.ieaapp"; //PlayStore URL of IEA App
        String message = "Download IEA - Industrial Entrepreneurs app from PlayStore and give your business a boost.\n\n" + appUrl; //message sent on WhatsApp

        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");

        intent.putExtra(
                Intent.EXTRA_TEXT,
                message);

        // Checking whether Whatsapp is installed or not
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this,"Please install whatsapp first.",Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intent);
        }
    }

    @SuppressLint("IntentReset")
    private void sendEmail() {
        name = nameEdtTxt.getText().toString();
        company_name = company_nameEdtTxt.getText().toString();
        contact_number = contact_numberEdtTxt.getText().toString();
//        String email_address = email_addressEdtTxt.getText().toString();
        Log.i("Send email", "");

        String[] TO = {"wormoscorporation@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "IEA Member Referral");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "New Member Referral\n\nThis link is shared by: " + name);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Refer.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}