package com.example.ieaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class GrievanceDetails extends AppCompatActivity {
    String statusStr, userEmailReplaced, grievanceKeyStr, imageUrl = "";
    AutoCompleteTextView grievanceDetailsDepartmentField;
    EditText grievanceDetailsSubjectEdtTxt, getGrievanceDetailsIssueInputEdtTxt;
    AppCompatButton grievanceDetailsSubmitBtn;
    DatabaseReference grievanceItemRef, grievanceReference, grievanceReference2;
    FirebaseAuth mAuth;
    ImageView grievanceDetailsIv;
    ProgressDialog progressDialog;
    FirebaseDatabase grievanceDb;
    StorageReference storageProfilePicReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance_details);

        Intent intent = getIntent();
        statusStr = intent.getExtras().getString("status");
        grievanceKeyStr = intent.getExtras().getString("grievanceKey");
        grievanceDetailsDepartmentField = findViewById(R.id.grievance_details_department_field);
        grievanceDetailsSubjectEdtTxt = findViewById(R.id.grievance_details_subject_edtTxt);
        getGrievanceDetailsIssueInputEdtTxt = findViewById(R.id.grievance_details_issue_input_edtTxt);
        grievanceDetailsSubmitBtn = findViewById(R.id.grievance_details_submit_btn);
        grievanceDetailsIv = findViewById(R.id.grievance_details_iv);
        mAuth = FirebaseAuth.getInstance();
        userEmailReplaced = Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()).replaceAll("\\.", "%7");
        grievanceItemRef = FirebaseDatabase.getInstance().getReference(("Unresolved Grievances/" + userEmailReplaced+"/"+grievanceKeyStr));

        if (statusStr.equals("Rejected"))
            grievanceDetailsSubmitBtn.setVisibility(View.VISIBLE);

        Log.d("grievanceItemRef", String.valueOf(grievanceItemRef));

        grievanceItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                grievanceDetailsDepartmentField.setText(Objects.requireNonNull(snapshot.child("department").getValue()).toString());
                grievanceDetailsSubjectEdtTxt.setText(Objects.requireNonNull(snapshot.child("subject").getValue()).toString());
                getGrievanceDetailsIssueInputEdtTxt.setText(Objects.requireNonNull(snapshot.child("complain").getValue()).toString());
                if (snapshot.child("purl").exists()) {

                    imageUrl = Objects.requireNonNull(snapshot.child("purl").getValue()).toString();
                    if(!imageUrl.isEmpty()){
                        grievanceDetailsIv.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext())
                                .load(Objects.requireNonNull(snapshot.child("purl").getValue()).toString())
                                .placeholder(R.drawable.iea_logo)
                                .error(R.drawable.iea_logo)
                                .into(grievanceDetailsIv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        grievanceDetailsSubmitBtn.setOnClickListener(view -> {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Submitting...");

            grievanceDb = FirebaseDatabase.getInstance();

            String complainerEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
            String complain = getGrievanceDetailsIssueInputEdtTxt.getText().toString();
            String departments = grievanceDetailsDepartmentField.getText().toString();
            String subject = grievanceDetailsSubjectEdtTxt.getText().toString();

            grievanceReference = grievanceDb.getReference("Unsolved Grievances");

            String grievanceKey = grievanceReference.push().getKey();

            grievanceReference2 = grievanceDb.getReference("Unresolved Grievances").child(Objects.requireNonNull(complainerEmail)
                    .replaceAll("\\.", "%7")).child(Objects.requireNonNull(grievanceKey));

            if (!imageUrl.equals("")){
                progressDialog.show();
                GrievanceModel solvedModel = new GrievanceModel(complain, departments, complainerEmail, "Unsolved", subject, imageUrl);
                grievanceReference2.setValue(solvedModel);
                grievanceReference.child(grievanceKey).setValue(solvedModel);

                Toast.makeText(GrievanceDetails.this, "We have received your request with id "+grievanceKey, Toast.LENGTH_LONG).show();
                sendGrievanceNotification(grievanceKey);
            } else {
                GrievanceModel solvedModel = new GrievanceModel(complain, departments, complainerEmail, "Unsolved", subject, imageUrl);
                grievanceReference2.setValue(solvedModel);
                grievanceReference.child(grievanceKey).setValue(solvedModel);
                Toast.makeText(GrievanceDetails.this, "We have received your request with id "+grievanceKey, Toast.LENGTH_LONG).show();
                sendGrievanceNotification(grievanceKey);
            }
        });
    }

    private void sendGrievanceNotification(String Key) {
        FirebaseDatabase.getInstance().getReference("Core Member Token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                    String grievanceUserToken = Objects.requireNonNull(tokenSnapshot.getValue()).toString();

                    FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(grievanceUserToken,
                            "IEA New Grievance Submitted",
                            "New Grievance has been submitted:\n" + grievanceDetailsSubjectEdtTxt.getText().toString(),
                            getApplicationContext(),
                            GrievanceDetails.this,
                            "grievance",
                            "null",
                            Key);

                    grievanceNotificationSender.SendNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}