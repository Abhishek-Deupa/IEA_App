package com.example.ieaapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class BaasMemberProfile extends AppCompatActivity {
    ImageView companyLogoIv;
    TextView baasMemberProfileCompanyName,baasNoOfProduct;
    AppCompatButton baasMemberProfileViewBrochure, baasMemberProfileContactUs, baasMemberBackBtn;
    RecyclerView baasMemberRecyclerView;
    FirebaseRecyclerOptions<MemberProductModel> options;
    BaasProductAdapter baasListRecyclerAdapter;
    String memberBrochureLink, ownerEmail, ownerEmailConverted, ownerContactNumber, ownerContactEmail,pdfUrl;
    Dialog baasMemberContactDialog;
    ActivityResultLauncher<String> mGetContent,mGetPdf;
    Uri companyLogoUri,pdfUri;
    FirebaseAuth mAuth;
    Bitmap imageBitmap;
    CardView addProductCv,uploadBrochureCv,newItemCv,memberchatCv;
    ProgressDialog pdfUploadDialog;
    final DatabaseReference registryDataRef = FirebaseDatabase.getInstance().getReference("Registered Users");




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baas_member_profile);
        ownerEmail = getIntent().getStringExtra("BaasItemKey");
        ownerEmailConverted = ownerEmail.replaceAll("\\.", "%7");
        baasMemberProfileCompanyName = findViewById(R.id.baas_member_profile_company_name);
        baasMemberProfileViewBrochure = findViewById(R.id.baas_member_profile_view_brochure_btn);
        baasMemberProfileContactUs = findViewById(R.id.baas_member_profile_contact_us_btn);
        baasMemberBackBtn = findViewById(R.id.baas_member_back_button);
        companyLogoIv = findViewById(R.id.company_logo_iv);
        addProductCv = findViewById(R.id.baas_addProductIcon);
        uploadBrochureCv = findViewById(R.id.baas_uploadBrochure);
        newItemCv = findViewById(R.id.baas_newitem);
        baasNoOfProduct = findViewById(R.id.baas_no_of_product_info);
        memberchatCv= findViewById(R.id.baas_chatBtn);
        baasMemberContactDialog = new Dialog(this);
        pdfUploadDialog= new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        DatabaseReference ProductdataRef = FirebaseDatabase.getInstance().getReference().child("Products by Member/" + ownerEmailConverted);
        ProductdataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long noOfProduct = snapshot.getChildrenCount();
                baasNoOfProduct.setText(String.valueOf("No of product: "+ noOfProduct));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        DatabaseReference ownerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Registered Users/" + ownerEmailConverted);

        ownerDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberBrochureLink = Objects.requireNonNull(snapshot.child("brochure_url").getValue()).toString();

                ownerContactNumber = Objects.requireNonNull(snapshot.child("phone_number").getValue()).toString();
                ownerContactEmail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();

                if(companyLogoUri==null){
                    Glide.with(getApplicationContext())
                            .load(Objects.requireNonNull(snapshot.child("company_logo").getValue()).toString())
                            .placeholder(R.drawable.iea_logo)
                            .circleCrop()
                            .error(R.drawable.iea_logo)
                            .into(companyLogoIv);
                }


                baasMemberProfileCompanyName.setText(Objects.requireNonNull(snapshot.child("company_name").getValue()).toString());
                if (!mAuth.getCurrentUser().getEmail().equals(ownerContactEmail)) {
                    addProductCv.setVisibility(View.GONE);
                    uploadBrochureCv.setVisibility(View.GONE);
                    newItemCv.setVisibility(View.GONE);
                    memberchatCv.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addProductCv.setOnClickListener(view -> startActivity(new Intent(BaasMemberProfile.this,UploadProduct.class)));

        baasMemberRecyclerView = findViewById(R.id.baas_member_rv);

        GridLayoutManager gridLayoutManager =new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        
        baasMemberRecyclerView.setLayoutManager(gridLayoutManager);

        options = new FirebaseRecyclerOptions.Builder<MemberProductModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Products by Member/" + ownerEmailConverted), MemberProductModel.class)
                .build();
        //Collections.reverse(Collections.singletonList(options));

        baasListRecyclerAdapter = new BaasProductAdapter(options);
        baasMemberRecyclerView.setAdapter(baasListRecyclerAdapter);

        baasListRecyclerAdapter.startListening();

        baasMemberProfileViewBrochure.setOnClickListener(view -> {
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

        baasMemberProfileContactUs.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View contactView = inflater.inflate(R.layout.core_member_contact_popup, null);

            TextView baasMemberContactNumber = contactView.findViewById(R.id.core_member_phone_number);
            TextView baasMemberContactEmail = contactView.findViewById(R.id.core_member_email);

            baasMemberContactEmail.setText(ownerContactEmail);
            baasMemberContactNumber.setText(ownerContactNumber);

            baasMemberContactDialog.setContentView(contactView);
            baasMemberContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            baasMemberContactDialog.show();
        });

        baasMemberBackBtn.setOnClickListener(view -> finish());

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = UUID.randomUUID().toString() + ".jpg";
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(1, 1)
                        .start(BaasMemberProfile.this, 2);
            }
        });
        Log.d("ownerEmail", ownerEmail + " " + mAuth.getCurrentUser().getEmail());
        if (ownerEmail.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail().replaceAll("\\.", "%7"))) {
            companyLogoIv.setClickable(true);

            companyLogoIv.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(BaasMemberProfile.this);
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

                    ImagePicker.with(this)
                            .crop()
                            .cameraOnly()//Crop image(Optional), Check Customization for more option
                            .compress(2048)			//Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            .start(5);
                });
            });
        } else {
            companyLogoIv.setClickable(false);
        }


        mGetPdf = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                pdfUploadDialog = new ProgressDialog(BaasMemberProfile.this);
                pdfUploadDialog.setMessage("Uploading");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users/" + mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7"));
                pdfUploadDialog.show();
                pdfUri = result;
                StorageReference pdfUploadRef = FirebaseStorage.getInstance().getReference().child("Product Brochure/" + baasMemberProfileCompanyName.getText().toString() + " Brochure" + ".pdf");

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
                                            Toast.makeText(BaasMemberProfile.this, "Brochure Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            pdfUploadDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(BaasMemberProfile.this, "Failed to Upload Brochure", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    pdfUploadDialog.dismiss();
                    Toast.makeText(BaasMemberProfile.this, "File not selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        uploadBrochureCv.setOnClickListener(view -> {
            mGetPdf.launch("application/pdf");
        });

        memberchatCv.setOnClickListener(v ->{
            final String[] chatKey = new String[1];

                registryDataRef.child(ownerEmailConverted).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(!snapshot.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).exists()){
                            chatKey[0] = ownerEmailConverted+mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7");
                            HashMap<String,Object> ownerMap = new HashMap<>();
                            ownerMap.put(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7"),chatKey[0]);
                            HashMap<String,Object> userMap = new HashMap<>();
                            userMap.put(ownerEmailConverted,chatKey[0]);
                            registryDataRef.child(ownerEmailConverted).updateChildren(ownerMap);
                            registryDataRef.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).updateChildren(userMap).addOnSuccessListener(s->{
                                startActivity(new Intent(BaasMemberProfile.this,ChatSession.class).putExtra("chatKey",chatKey[0])
                                        .putExtra("ownerEmail",ownerEmail).putExtra("chatType","user").putExtra("key","0"));
                                Log.d("one2", "onDataChange: ");
                            }).addOnFailureListener(f -> {
                                Toast.makeText(BaasMemberProfile.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                            });
                        } else if(snapshot.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).exists()){
                            chatKey[0] = snapshot.child(mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7")).getValue().toString();
                            startActivity(new Intent(BaasMemberProfile.this,ChatSession.class).putExtra("chatKey",chatKey[0])
                                    .putExtra("ownerEmail",ownerEmail).putExtra("chatType","user").putExtra("key","1"));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            companyLogoUri = UCrop.getOutput(data);
            uploadCompanyLogo(companyLogoUri);
        }else if (resultCode == RESULT_OK && requestCode == 0) {
            File file = new File(Environment.getExternalStorageDirectory(),"companyLogo" );
            Uri uri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            Log.d("TAG", "onActivityResult: "+uri.toString());
            companyLogoIv.setImageURI(uri);
            uploadCompanyLogo(uri);
        } else if (resultCode == RESULT_OK && requestCode == 5) {
            companyLogoUri = data.getData();
            uploadCompanyLogo(companyLogoUri);
        }
    }

    private void uploadCompanyLogo(Uri companyLogoUri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Company Logos/" + mAuth.getCurrentUser().getEmail() + "CompanyLogo");
        fileRef.putFile(companyLogoUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            HashMap LogoData = new HashMap();
            LogoData.put("company_logo", uri.toString());

            DatabaseReference companyLogoRef = FirebaseDatabase.getInstance().getReference("Registered Users/" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()
                    .replaceAll("\\.", "%7"));
            companyLogoRef.updateChildren(LogoData).addOnSuccessListener(o -> {
                progressDialog.dismiss();
                startActivity(new Intent(BaasMemberProfile.this,BaasMemberProfile.class).putExtra("BaasItemKey",ownerEmail).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(BaasMemberProfile.this, "Company Logo has been updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(f ->{
            progressDialog.dismiss();
            Toast.makeText(BaasMemberProfile.this, "Please try again", Toast.LENGTH_SHORT).show();
            });

        }).addOnFailureListener(f ->{
            progressDialog.dismiss();
            Toast.makeText(BaasMemberProfile.this, "Please try again", Toast.LENGTH_SHORT).show();
        })).addOnFailureListener(f ->{
            progressDialog.dismiss();
            Toast.makeText(BaasMemberProfile.this, "Please try again", Toast.LENGTH_SHORT).show();
        });

    }


    private void PickImagefromcamera() {
        Intent fromcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),"companyLogo" );
        Uri uri= FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        fromcamera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(fromcamera, 0);
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

    @Override
    protected void onResume() {
        super.onResume();
        baasListRecyclerAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        baasListRecyclerAdapter.startListening();
    }


    class NpaGridLayoutManager extends GridLayoutManager {

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public NpaGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }
    }
}

