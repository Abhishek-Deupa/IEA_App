package com.wormoscorp.ieaapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class UserProfile extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    String userEmailConverted, userCompanyNameStr,userProfileNameStr;
    Uri resultUri, pdfUri;
    ProgressDialog productUploadProgressDialog;
    Bitmap imageBitmap;

    {
        assert userEmail != null;
        userEmailConverted = userEmail.replaceAll("\\.", "%7");
    }

    DatabaseReference ref = database.getReference("Registered Users/" + userEmailConverted);

    ImageView userProfileImage, logoutImg;
    TextView userProfileName, userMembershipDate, userMembershipExpiryDate, logoutTv,renewalText;
    EditText userContactNumberEdtTxt, userDateOfBirthEdtTxt, userEmailEdtTxt, userCompanyNameEdtTxt, userAddressEdtTxt;
    AppCompatButton saveProfileBtn, userProfileBackBtn, uploadBrochureBtn;
    ActivityResultLauncher<String> mGetContent, mGetPdf;
    TextInputEditText userBioEditText;
    CardView renewalImg;
    String membershipType;
    StorageReference storageProfilePicReference;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        userProfileImage = findViewById(R.id.user_profile_image);
        renewalText = findViewById(R.id.renewal_text);
        renewalImg = findViewById(R.id.renewal_img);
        userProfileName = findViewById(R.id.user_profile_name);
        userMembershipDate = findViewById(R.id.user_membership_date);
        userContactNumberEdtTxt = findViewById(R.id.user_profile_contactNumber_edtTxt);
        userDateOfBirthEdtTxt = findViewById(R.id.user_profile_dateOfBirth_edtTxt);
        userEmailEdtTxt = findViewById(R.id.user_profile_email_edtTxt);
        userCompanyNameEdtTxt = findViewById(R.id.user_profile_company_name_edtTxt);
        userAddressEdtTxt = findViewById(R.id.user_profile_address_edtTxt);
        saveProfileBtn = findViewById(R.id.user_profile_save_button);
        userBioEditText = findViewById(R.id.user_bio_input_edttxt);
        userMembershipExpiryDate = findViewById(R.id.expiry_dateId);
        userProfileBackBtn = findViewById(R.id.userProfile_back_button);
        uploadBrochureBtn = findViewById(R.id.upload_brochure_btn);
        logoutImg = findViewById(R.id.logout_img);
        logoutTv = findViewById(R.id.logout_text);
        productUploadProgressDialog = new ProgressDialog(this);

        userProfileBackBtn.setOnClickListener(view -> {
            finish();
        });

        renewalImg.setOnClickListener(v -> startActivity(new Intent(UserProfile.this,payment.class).putExtra("renewal","1")
                .putExtra("email",mAuth.getCurrentUser().getEmail()).putExtra("memberfee",membershipType)
                .putExtra("name",userProfileNameStr).putExtra("cname",userCompanyNameStr)));
        renewalText.setOnClickListener(v -> startActivity(new Intent(UserProfile.this,payment.class).putExtra("renewal","1")
                .putExtra("email",mAuth.getCurrentUser().getEmail()).putExtra("memberfee",membershipType)
                .putExtra("name",userProfileNameStr).putExtra("cname",userCompanyNameStr)));


        storageProfilePicReference = FirebaseStorage.getInstance().getReference();


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfileNameStr = Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                userProfileName.setText(userProfileNameStr);

                String userMembershipIdStr = Objects.requireNonNull(dataSnapshot.child("member_id").getValue()).toString();
                membershipType = Objects.requireNonNull(dataSnapshot.child("memberfee").getValue()).toString();

                String userMembershipDateStr = Objects.requireNonNull(dataSnapshot.child("date_of_membership").getValue()).toString();
                userMembershipDate.setText(userMembershipDateStr);
                String userExpiryDate = yearincrementer(userMembershipDateStr,365);
                userMembershipExpiryDate.setText(userExpiryDate);
                int date = dateCompare(yearincrementer(userExpiryDate,-7));
                if(date == 1){
                    renewalImg.setVisibility(View.VISIBLE);
                    renewalText.setVisibility(View.VISIBLE);
                }
                Log.d("datetag", "onDataChange: "+date+"e+"+userExpiryDate);

                String userContactNumberStr = Objects.requireNonNull(dataSnapshot.child("phone_number").getValue()).toString();
                userContactNumberEdtTxt.setText(userContactNumberStr);

                String userDOBStr = Objects.requireNonNull(dataSnapshot.child("date_of_birth").getValue()).toString();
                if (dataSnapshot.child("date_of_birth").getValue().toString().equals("")) {
                    userDateOfBirthEdtTxt.setText(userDOBStr);
                } else {
                    userDateOfBirthEdtTxt.setText(userDOBStr);
                    userDateOfBirthEdtTxt.setFocusable(false);
                    userDateOfBirthEdtTxt.setTextColor(getResources().getColor(R.color.grey));
                }

                String userEmailStr = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                userEmailEdtTxt.setText(userEmailStr);


                userCompanyNameStr = Objects.requireNonNull(dataSnapshot.child("company_name").getValue()).toString();
                userCompanyNameEdtTxt.setText(userCompanyNameStr);

                String userAddressStr = Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString();
                userAddressEdtTxt.setText(userAddressStr);

                String userBioStr = Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                userBioEditText.setText(userBioStr);

                String corePictureUrl = Objects.requireNonNull(dataSnapshot.child("purl").getValue()).toString();

                if(imageBitmap == null && resultUri==null){
                    Glide.with(getApplicationContext())
                            .load(corePictureUrl)
                            .placeholder(R.drawable.iea_logo)
                            .circleCrop()
                            .error(R.drawable.iea_logo)
                            .into(userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        saveProfileBtn.setOnClickListener(view -> {
            String userContactNumberStr = userContactNumberEdtTxt.getText().toString();
            String userDOBStr = userDateOfBirthEdtTxt.getText().toString();
            String userAddressStr = userAddressEdtTxt.getText().toString();
            String userBioStr = userBioEditText.getText().toString();

            updateData(userContactNumberStr, userDOBStr, userAddressStr, userBioStr);

            if (resultUri != null) {
                uploadImageToFirebase(resultUri);
            } else if(imageBitmap!=null) {
                Uri camImg = getimageUri(UserProfile.this,imageBitmap);
                uploadImageToFirebase(camImg);
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(1, 1)
                        .start(UserProfile.this);
            }
        });



        userProfileImage.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
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

            galleryCardView.setOnClickListener(view1 -> {
                if (!checkStoragePermission()) {
                    requestStoragePermission();

                } else {
                    mGetContent.launch("image/*");
                    alertDialogImg.dismiss();
                    imageBitmap = null;
                }

            });
            cameraCardView.setOnClickListener(view1 -> {
                if (!checkCameraPermission()) {
                    requestCameraPermission();

                } else {
                    PickImagefromcamera();
                    resultUri = null;
                    alertDialogImg.dismiss();
                }
            });
        });


        mGetPdf = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                pdfUploadDialog = new ProgressDialog(UserProfile.this);
                pdfUploadDialog.setMessage("Uploading");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users/" + userEmailConverted);

                pdfUploadDialog.show();
                pdfUri = result;
                StorageReference pdfUploadRef = FirebaseStorage.getInstance().getReference().child("Product Brochure/" + userCompanyNameStr + " Brochure" + ".pdf");

                if (pdfUri != null) {
                    pdfUploadRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pdfUploadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap UserData = new HashMap();
                                    UserData.put("brochure_url", uri.toString());

                                    databaseReference.updateChildren(UserData).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Toast.makeText(UserProfile.this, "Brochure Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            pdfUploadDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UserProfile.this, "Failed to Upload Brochure", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    pdfUploadDialog.dismiss();
                    Toast.makeText(UserProfile.this, "File not selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        uploadBrochureBtn.setOnClickListener(view -> {

            mGetPdf.launch("application/pdf");

        });


        logoutImg.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(UserProfile.this, LandingPage.class).setFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        logoutTv.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(UserProfile.this, LandingPage.class).setFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

    }


    ProgressDialog pdfUploadDialog;

    private void updateData(String userContactNumberStr, String userDOBStr, String userAddressStr, String userBioStr) {
        HashMap UserData = new HashMap();
        UserData.put("phone_number", userContactNumberStr);
        UserData.put("date_of_birth", userDOBStr);
        UserData.put("address", userAddressStr);
        UserData.put("description", userBioStr);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users/" + userEmailConverted);
        databaseReference.updateChildren(UserData).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UserProfile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Failed to Update Data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            userProfileImage.setImageURI(resultUri);
        }else if (resultCode == RESULT_OK && requestCode == 3) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            userProfileImage.setImageBitmap(imageBitmap);
            Log.d("TAG23", imageBitmap.toString()+data.getExtras());
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageProfilePicReference.child("User Profile Pictures/" + mAuth.getCurrentUser().getEmail().toString() + "ProfilePicture");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .placeholder(R.drawable.iea_logo)
                                .circleCrop()
                                .error(R.drawable.iea_logo)
                                .into(userProfileImage);

                        HashMap UserData = new HashMap();
                        UserData.put("purl", uri.toString());

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users/" + userEmailConverted);
                        databaseReference.updateChildren(UserData).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(UserProfile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserProfile.this, "Failed to Update Data", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(UserProfile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                    }

                });
                String fileReference = String.valueOf(fileRef.getDownloadUrl());
                Log.d("downloadUrl", fileReference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Failed to Update Profile Picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String yearincrementer(String date,int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(Objects.requireNonNull(sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, day);
        date = sdf.format(c.getTime());
        Log.d("dateis", "yearincrementer: "+date);
        return date;
    }
    public static int dateCompare(String date) {
        int catalog_outdated =0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Date strDate = null;
        try {
            strDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (new Date().after(strDate)) {
            catalog_outdated = 1;
        }
        return catalog_outdated;
    }


    private void PickImagefromcamera() {
        Intent fromcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),"userprofile" );
        startActivityForResult(fromcamera, 3);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    private boolean checkStoragePermission() {
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res2;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    public Uri getimageUri(Context context, Bitmap bitimage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitimage, "Title", null);
        return Uri.parse(path);
    }


}