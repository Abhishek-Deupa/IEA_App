package com.wormoscorp.ieaapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.github.dhaval2404.imagepicker.ImagePicker;
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
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class Grievance extends AppCompatActivity {

    AppCompatButton grievance_back_button, grievance_submit;
    FirebaseDatabase grievanceDb;
    DatabaseReference grievanceReference, grievanceReference2;
    EditText grievanceSubjectEdtTxt, issue;
    AutoCompleteTextView dept;
    CardView myGrievancesBtn;
    Dialog grievanceSubmissionDialog;
    ImageView cameraBtn,cameraIv;
    ActivityResultLauncher<String> mGetImage;
    Uri imageUri;
    StorageReference storageProfilePicReference = FirebaseStorage.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance);

        grievance_back_button = findViewById(R.id.grievance_back_button);
        myGrievancesBtn = findViewById(R.id.my_grievances_btn);
        grievanceSubjectEdtTxt = findViewById(R.id.grievance_subject_edtTxt);
        cameraBtn = findViewById(R.id.issueCamaraBtn);
        cameraIv = findViewById(R.id.issueCamaraIv);

        dropdownInit();



        grievance_back_button.setOnClickListener(view -> finish());

        grievanceSubmissionDialog = new Dialog(this);

        grievance_submit = findViewById(R.id.grievance_submit_btn);
        grievance_submit.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Submitting...");
            grievanceDb = FirebaseDatabase.getInstance();
            issue = findViewById(R.id.issue_input_edtTxt);
            dept = findViewById(R.id.grievance_department_field);
            if (issue.getText().toString().isEmpty()) {
                Toast.makeText(Grievance.this, "Issue cannot be empty", Toast.LENGTH_SHORT).show();
                issue.requestFocus();
            } else if (dept.getText().toString().isEmpty()) {
                Toast.makeText(Grievance.this, "Department cannot be empty", Toast.LENGTH_SHORT).show();
                dept.requestFocus();
            } else if (grievanceSubjectEdtTxt.getText().toString().isEmpty()) {
                Toast.makeText(Grievance.this, "Subject cannot be empty", Toast.LENGTH_SHORT).show();
                grievanceSubjectEdtTxt.requestFocus();
            } else {
                String complainerEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
                String complain = issue.getText().toString();
                String departments = dept.getText().toString();
                String subject = grievanceSubjectEdtTxt.getText().toString();
                String purl;
                if(imageUri!=null){
                    purl = imageUri.toString();
                } else{
                    purl = "";
                }

                grievanceReference = grievanceDb.getReference("Unsolved Grievances");

                String grievanceKey = grievanceReference.push().getKey();

                grievanceReference2 = grievanceDb.getReference("Unresolved Grievances").child(Objects.requireNonNull(complainerEmail)
                        .replaceAll("\\.", "%7")).child(Objects.requireNonNull(grievanceKey));
                if (!purl.equals("")){
                    progressDialog.show();
                    StorageReference productFileRef = storageProfilePicReference.child("Grievance Images/" + mAuth.getCurrentUser().getEmail() + grievanceSubjectEdtTxt.getText().toString()+ dept.getText().toString());
                    productFileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            productFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    GrievanceModel solvedModel = new GrievanceModel(complain, departments, complainerEmail, "Unsolved", subject, uri.toString());
                                    grievanceReference2.setValue(solvedModel);
                                    grievanceReference.child(grievanceKey).setValue(solvedModel);

                                    Toast.makeText(Grievance.this, "We have received your request with id "+grievanceKey, Toast.LENGTH_LONG).show();
                                    confirmationPopup(progressDialog,0);
                                    sendGrievanceNotification(grievanceKey);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Grievance.this, "Please try again", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Grievance.this, "Please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Grievance.this, "Please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    GrievanceModel solvedModel = new GrievanceModel(complain, departments, complainerEmail, "Unsolved", subject, purl);
                    grievanceReference2.setValue(solvedModel);
                    grievanceReference.child(grievanceKey).setValue(solvedModel);
                    Toast.makeText(Grievance.this, "We have received your request with id "+grievanceKey, Toast.LENGTH_LONG).show();
                    confirmationPopup(progressDialog,1);
                    sendGrievanceNotification(grievanceKey);
                }
            }
        });

        myGrievancesBtn.setOnClickListener(view -> startActivity(new Intent(Grievance.this, MyGrievances.class).putExtra("status","All")));

        mGetImage =registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = UUID.randomUUID().toString() + ".jpg";
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(1, 1)
                        .start(Grievance.this);
            }
        });

        cameraBtn.setOnClickListener(v ->{
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Grievance.this);
            LayoutInflater layoutInflater= getLayoutInflater();
            View pickImgview = layoutInflater.inflate(R.layout.image_picker_item,null);
            builder.setCancelable(true);
            builder.setView(pickImgview);
            android.app.AlertDialog alertDialogImg = builder.create();
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
                }

            });
            cameraCardView.setOnClickListener(view -> {
                ImagePicker.with(this)
                        .crop(1f,1f)
                        .cameraOnly()
                        .compress(2048)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(0);
            });
        });
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            imageUri=data.getData();
            cameraIv.setVisibility(View.VISIBLE);
            cameraIv.setImageURI(imageUri);
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data);
            cameraIv.setVisibility(View.VISIBLE);
            cameraIv.setImageURI(imageUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);

        }

    }

    private void sendGrievanceNotification(String key) {
        FirebaseDatabase.getInstance().getReference("Core Member Token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tokenSnapshot : snapshot.getChildren()) {
                    String grievanceUserToken = Objects.requireNonNull(tokenSnapshot.getValue()).toString();

                    FcmNotificationsSender grievanceNotificationSender = new FcmNotificationsSender(grievanceUserToken,
                            "IEA New Grievance Submitted",
                            "New Grievance has been submitted:\n" + grievanceSubjectEdtTxt.getText().toString(),
                            getApplicationContext(),
                            Grievance.this,"grievance","null",key);

                    grievanceNotificationSender.SendNotifications();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dropdownInit();
    }

    public void dropdownInit() {
        String[] grievance_departments = getResources().getStringArray(R.array.grievance_department);
        ArrayAdapter<String> arrayAdapterDepartments = new ArrayAdapter<>(getBaseContext(), R.layout.drop_down_item, grievance_departments);
        AutoCompleteTextView autoCompleteTextViewDepartments = findViewById(R.id.grievance_department_field);
        autoCompleteTextViewDepartments.setAdapter(arrayAdapterDepartments);
    }

    public void confirmationPopup(ProgressDialog progressDialog,int val) {
        if(val == 0) progressDialog.dismiss();
        LayoutInflater inflater = getLayoutInflater();
        View confirmationView = inflater.inflate(R.layout.confirmation_popup, null);
        grievanceSubmissionDialog.setContentView(confirmationView);
        grievanceSubmissionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        grievanceSubmissionDialog.show();

        new Handler().postDelayed(() -> {
            Intent i = new Intent(getApplicationContext(), Grievance.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }, 3000);
    }
    public  Bitmap getimageBitmap(Uri uri) throws IOException {

        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
           bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(Grievance.this.getContentResolver(), uri));
        } else {
           bitmap = MediaStore.Images.Media.getBitmap(Grievance.this.getContentResolver(), uri);
        }
        return  bitmap;
    }

    
}