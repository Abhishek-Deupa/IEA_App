package com.example.ieaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;

public class Login extends AppCompatActivity {

    AppCompatButton loginBackButton, signInButton;
    EditText loginEmail, loginPassword;
    FirebaseAuth mAuth;
    TextView forgotPassIntent, privacyPolicy,renewalText;

    ProgressDialog loginProgressDialog;
    String token;
    DatabaseReference registryDataRef= FirebaseDatabase.getInstance().getReference("Registered Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBackButton = findViewById(R.id.login_back_button);
        signInButton = findViewById(R.id.signin_btn);
        renewalText = findViewById(R.id.login_renewal);

        forgotPassIntent = findViewById(R.id.forgotPassIntent);
        privacyPolicy = findViewById(R.id.login_policy_text);
        forgotPassIntent.setOnClickListener(view -> startActivity(new Intent(Login.this, forgot_password.class)));
        privacyPolicy.setOnClickListener(view -> openPrivacyPolicyLink());
        mAuth = FirebaseAuth.getInstance();

        loginBackButton.setOnClickListener(view -> finish());

        signInButton.setOnClickListener(view -> loginUser());

        renewalText.setOnClickListener( v ->{startActivity(new Intent(Login.this,MembershipRenewal.class));});
    }

    private void openPrivacyPolicyLink() {
        startActivity(new Intent(Login.this, PrivacyPolicy.class));
    }

    private void loginUser() {
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        String login_email = loginEmail.getText().toString().toLowerCase();
        String login_password = loginPassword.getText().toString();


        if (TextUtils.isEmpty(login_email)) {
            loginEmail.setError("Email cannot be empty!");
            loginEmail.requestFocus();
        } else if (TextUtils.isEmpty(login_password)) {
            loginPassword.setError("Password cannot be empty!");
            loginPassword.requestFocus();
        } else {

            loginProgressDialog = new ProgressDialog(Login.this);
            loginProgressDialog.setMessage("Logging you in...");
            loginProgressDialog.show();
            mAuth.signInWithEmailAndPassword(login_email, login_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                public String replacePeriod(String login_email) {
                    return login_email.replaceAll("\\.", "%7");
                }

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        registryDataRef.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String status = Objects.requireNonNull(snapshot.child("status").getValue()).toString();
                                    if(status.equals("blocked")){
                                        renewalText.setVisibility(View.VISIBLE);
                                        Toast.makeText(Login.this, "Your Membership has been expired", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        loginProgressDialog.dismiss();
                                    }else {
                                        loginProgressDialog.dismiss();
                                        Toast.makeText(Login.this, "You are logged in!", Toast.LENGTH_SHORT).show();
                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 -> {
                                            Log.d("Messaging", "onComplete: Messaging On Complete");
                                            token = task1.getResult();
                                            Log.d("Messaging", "Token: " + token);
                                            sendTokenToDatabase(token);
                                        });

                                        startActivity(new Intent(Login.this, explore_menu.class).putExtra("userEmail", replacePeriod(login_email)));
                                        loginProgressDialog.dismiss();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(Login.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Toast.makeText(Login.this, "Login Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        loginProgressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void sendTokenToDatabase(String token) {
        HashMap userToken = new HashMap();
        userToken.put("user_token", token);

        HashMap memberToken = new HashMap();
        memberToken.put(loginEmail.getText().toString().replaceAll("\\.", "%7").toLowerCase(), token);

        FirebaseDatabase.getInstance().getReference().child("Registered Users/" + loginEmail.getText().toString().replaceAll("\\.", "%7").toLowerCase())
                .updateChildren(userToken);

        FirebaseDatabase.getInstance().getReference("Member Directory Token")
                .updateChildren(memberToken);

        String industryTypeRef = "Registered Users/" + loginEmail.getText().toString().replaceAll("\\.", "%7").toLowerCase() + "/industry_type";

        Log.d("IndustryType", industryTypeRef);
        FirebaseDatabase.getInstance().getReference()
                .child(industryTypeRef).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("IndustryType", "Industry Type working");
                        if(snapshot.getValue() != null) {
                            FirebaseDatabase.getInstance().getReference("Industry Notification Token/" + snapshot.getValue().toString())
                                    .updateChildren(memberToken);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}