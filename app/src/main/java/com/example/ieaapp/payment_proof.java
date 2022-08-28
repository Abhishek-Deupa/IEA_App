package com.example.ieaapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class payment_proof extends AppCompatActivity {

    ImageView proof_img;
    AppCompatButton insert_btn, payment_proofbackbtn, upload_btn;
    String fullname, email, companyName, Department, phoneNo, Turnover, memberfees, amountleft, paymentMethod, nameOfReceiver = "",gstNo="",renew;
    FirebaseDatabase memberDirectoryRoot;
    DatabaseReference memberDirectoryRef;
    private StorageReference memberstorageRef;
    Bitmap imageBitmap;
    EditText paymentReceiverName;
    TextView paymentReceiverHeading;
    Dialog registrarionConfirmationDialog;
    ProgressDialog dialog;
    ActivityResultLauncher<String> mGetImage;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_proof);
        memberstorageRef = FirebaseStorage.getInstance().getReference();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");

        proof_img = findViewById(R.id.proof_img);
        insert_btn = findViewById(R.id.insert_proof_img_btn);
        payment_proofbackbtn = findViewById(R.id.paymentproof_back_button);
        upload_btn = findViewById(R.id.proofupload_btn);
        paymentReceiverName = findViewById(R.id.receiver_name_editxt);
        paymentReceiverHeading = findViewById(R.id.name_of_receiver_text);

        Intent intent = getIntent();
        renew = intent.getStringExtra("renewal");
        email = intent.getStringExtra("email");
        memberfees = intent.getStringExtra("memberfee");
        amountleft = intent.getStringExtra("costleft");
        paymentMethod = intent.getStringExtra("paymentMethod");
        fullname = intent.getStringExtra("name");
        companyName = intent.getStringExtra("cname");
        if(renew.equals("0")){
            phoneNo = intent.getStringExtra("phoneno");
            Department = intent.getStringExtra("department");
            Turnover = intent.getStringExtra("annual_turn");
            gstNo = intent.getStringExtra("GstNo");
        }




        if (paymentMethod.equals("Physical Method Via Cash")) {
            paymentReceiverName.setVisibility(View.VISIBLE);
            paymentReceiverHeading.setVisibility(View.VISIBLE);
        }

        payment_proofbackbtn.setOnClickListener(view -> {
            finish();
        });

        mGetImage =registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(1, 1)
                        .start(payment_proof.this);
            }
        });

        insert_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(payment_proof.this);
                LayoutInflater layoutInflater= getLayoutInflater();
                View pickImgview = layoutInflater.inflate(R.layout.image_picker_item,null);
                builder.setCancelable(true);
                builder.setView(pickImgview);
                AlertDialog alertDialogImg = builder.create();
                Window window = alertDialogImg.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                alertDialogImg.show();
                window.setAttributes(wlp);

                CardView cameraCardView = pickImgview.findViewById(R.id.chooseCamera);
                CardView galleryCardView = pickImgview.findViewById(R.id.chooseGallery);

                galleryCardView.setOnClickListener(view -> {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();

                    } else {
                        mGetImage.launch("image/*");
                        alertDialogImg.dismiss();
                        imageBitmap = null;
                    }

                });
                cameraCardView.setOnClickListener(view -> {

                        ImagePicker.with(payment_proof.this)
                                .crop(1f,1f)
                                .cameraOnly()
                                .start(0);

                });
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            Boolean sendNotification = true;

            @Override
            public void onClick(View v) {

                if (!paymentReceiverName.getText().toString().isEmpty()) {
                    nameOfReceiver = paymentReceiverName.getText().toString();
                }
                if (paymentMethod.equals("Physical Method Via Cash")) {
                    if (paymentReceiverName.getText().toString().isEmpty()) {
                        paymentReceiverName.setError("Enter receiver name");
                        paymentReceiverName.requestFocus();
                    } else {
                        if (sendNotification) {
                            dialog.show();
                            imguploader(imageBitmap,dialog,imageUri);
                            sendNotification = false;
                        }

                    }
                } else {
                    if (sendNotification) {
                        dialog.show();
                        imguploader(imageBitmap,dialog,imageUri);
                        sendNotification = false;
                    }
                }

            }
        });


    }



//    private void PickImagefromcamera() {
//        Intent fromcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File file = new File(Environment.getExternalStorageDirectory(),"paymentpic" );
//        Uri uri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
//        fromcamera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(fromcamera, 0);
//    }
//
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void requestCameraPermission() {
//        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//    }
//
    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
    }
//
//    private boolean checkCameraPermission() {
//        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        return res1 && res2;
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            imageUri = data.getData();
            proof_img.setImageURI(imageUri);

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data);
            proof_img.setImageURI(imageUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);

        }

    }

    public void imguploader(Bitmap imageBitmap,ProgressDialog dialog,Uri uri) {

        memberDirectoryRoot = FirebaseDatabase.getInstance();
        memberDirectoryRef = memberDirectoryRoot.getReference("Temp Registry");

        if(uri != null){
            if(renew.equals("0")){
                ImgdataHaldler(dialog,uri);
            } else if(renew.equals("1")) {
                renewalHandler(dialog,uri,renew);
            } else if(renew.equals("2")){
                renewalHandler(dialog,uri,renew);
            }


        } else {
            dialog.dismiss();
            Toast.makeText(payment_proof.this, "Please Upload Image", Toast.LENGTH_SHORT).show();
        }

    }

    public void ImgdataHaldler(ProgressDialog dialog,Uri proofimg_uri){
        StorageReference urirefence = memberstorageRef.child("paymentproof/" + UUID.randomUUID().toString());
        urirefence.putFile(proofimg_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                urirefence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserRegistrationHelperClass userRegistrationHelperClass = new UserRegistrationHelperClass(fullname, email, phoneNo, companyName, Department, Turnover, uri.toString(), amountleft, memberfees, nameOfReceiver,gstNo);
                        memberDirectoryRef.child(email.replaceAll("\\.", "%7")).setValue(userRegistrationHelperClass);

                        final long[] registrationCount = new long[1];

                        FirebaseDatabase.getInstance().getReference("Temp Registry").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                registrationCount[0] = snapshot.getChildrenCount();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        FirebaseDatabase.getInstance().getReference("Core Member Token").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                                    String grievanceUserToken = Objects.requireNonNull(tokenSnapshot.getValue()).toString();

                                    FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(grievanceUserToken,
                                            "IEA New Registration",
                                            "New registration application has been submitted. (" + registrationCount[0] + ")",
                                            getApplicationContext(),
                                            payment_proof.this,
                                            "memberapproval",
                                            email.replaceAll("\\.","%7"),
                                            "null");

                                    grievanceNotificationSender.SendNotifications();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(payment_proof.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        confirmationPopup();

                    }
                });

            }
        });
    }

    public void renewalHandler(ProgressDialog dialog,Uri proofimg_uri,String renewalOption){
        StorageReference urirefence = memberstorageRef.child("RenewalProof/" + UUID.randomUUID().toString());
        urirefence.putFile(proofimg_uri).addOnSuccessListener(s ->{
            urirefence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    UserRenewalHelper userRenewalHelper = new UserRenewalHelper(uri.toString(),email,memberfees,amountleft,fullname,companyName,nameOfReceiver);
                    DatabaseReference renewalRef = FirebaseDatabase.getInstance().getReference("Renewal Registry");
                    renewalRef.child(email.replaceAll("\\.","%7")).setValue(userRenewalHelper).addOnSuccessListener(s -> {

                        FirebaseDatabase.getInstance().getReference("Core Member Token").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                                    String grievanceUserToken = Objects.requireNonNull(tokenSnapshot.getValue()).toString();

                                    FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(grievanceUserToken,
                                            "IEA Membership Renewal",
                                            "New renewal application has been submitted.",
                                            getApplicationContext(),
                                            payment_proof.this,
                                            "memberrenewal",
                                            email.replaceAll("\\.","%7"),
                                            "null");

                                    grievanceNotificationSender.SendNotifications();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(payment_proof.this, "Renewal Submitted", Toast.LENGTH_LONG).show();
                        if(renewalOption.equals("2")){
                            startActivity(new Intent(payment_proof.this,LandingPage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }else {
                            startActivity(new Intent(payment_proof.this,UserProfile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }

                        dialog.dismiss();

                    }).addOnFailureListener(f -> {
                        Toast.makeText(payment_proof.this, "Please try again", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            }).addOnFailureListener(f -> {

            });
        }).addOnFailureListener(f -> {
            Toast.makeText(payment_proof.this, "Please try again", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    public void confirmationPopup() {
        registrarionConfirmationDialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View confirmationView = inflater.inflate(R.layout.confirmation_popup, null);
        registrarionConfirmationDialog.setContentView(confirmationView);
        registrarionConfirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        registrarionConfirmationDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), LandingPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
    public  Bitmap getimageBitmap(Uri uri) throws IOException {

        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(payment_proof.this.getContentResolver(), uri));
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(payment_proof.this.getContentResolver(), uri);
        }
        return  bitmap;
    }

}