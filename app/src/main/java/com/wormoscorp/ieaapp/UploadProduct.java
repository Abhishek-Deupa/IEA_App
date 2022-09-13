package com.wormoscorp.ieaapp;

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

import android.Manifest;
import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.UUID;

public class UploadProduct extends AppCompatActivity {

    AppCompatButton productUploadBackBtn;
    ImageView productImg;
    EditText productName,productDescription,productPrice;
    Uri resultUri;
    Bitmap imageBitmap;
    CardView saveBtn;
    ActivityResultLauncher<String> mGetImage;
    ProgressDialog productUploadProgressDialog;
    final FirebaseAuth mAuth =FirebaseAuth.getInstance();
    StorageReference storageProfilePicReference;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);
        productUploadBackBtn= findViewById(R.id.uploadProduct_back_button);
        productImg = findViewById(R.id.productImg);
        productName = findViewById(R.id.productName);
        productPrice= findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.baas_productDescription);
        saveBtn = findViewById(R.id.uploadProductSaveBtn);
        productUploadProgressDialog = new ProgressDialog(this);
        Log.d("TAG", "onCreate: ");

        storageProfilePicReference = FirebaseStorage.getInstance().getReference();

        productUploadBackBtn.setOnClickListener(view -> finish());

        mGetImage =registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(5, 6)
                        .start(UploadProduct.this);
            }
        });

        productImg.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadProduct.this);
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
                ImagePicker.with(this)
                        .crop(5f,6f)
                        .cameraOnly()
                        .maxResultSize(620, 620)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(5);
            });
        });

        saveBtn.setOnClickListener(view -> {
            if (productName.getText().toString().isEmpty()) {
                productName.setError("Enter product title");
                productName.requestFocus();
            } else if (productDescription.getText().toString().isEmpty()) {
                productDescription.setError("Enter product description");
                productDescription.requestFocus();
            } else if (resultUri == null ) {
                Toast.makeText(this, "Select a product image", Toast.LENGTH_SHORT).show();
                productImg.requestFocus();
            } else {
                uploadProductImage(resultUri);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 5) {
            resultUri = data.getData();
            productImg.setImageURI(resultUri);
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
            productImg.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);

        }
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



    private void uploadProductImage(Uri productImageUri) {
        final String[] productPriceStr = new String[1];
        final String[] userContactNumber = new String[1];
        productUploadProgressDialog.setMessage("Uploading Product");
        productUploadProgressDialog.setCancelable(false);
        productUploadProgressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered Users/" + mAuth.getCurrentUser().getEmail().replaceAll("\\.","%7"));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userContactNumber[0] = snapshot.child("phone_number").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        StorageReference productFileRef = storageProfilePicReference.child("Product Images/" + mAuth.getCurrentUser().getEmail() + productName.getText().toString());
        productFileRef.putFile(productImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                productFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference productReferenceByUser = FirebaseDatabase.getInstance().getReference().child("Products by Member")
                                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));
                        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Products");
                        String productKey = productReferenceByUser.push().getKey();

                        String productTitleStr = productName.getText().toString();
                        String productDescriptionStr = productDescription.getText().toString();
                        if(productPrice.getText().toString().isEmpty()){
                            productPriceStr[0] = "--";
                        } else {
                            productPriceStr[0] = productPrice.getText().toString();
                        }


                        ProductModel newProduct = new ProductModel(uri.toString(), productTitleStr, productDescriptionStr, productPriceStr[0], mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7"));
                        productReferenceByUser.child(productKey).setValue(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ProductDetailsModel newProductDetail = new ProductDetailsModel(mAuth.getCurrentUser().getEmail(),
                                        userContactNumber[0], productDescriptionStr, uri.toString(), productPriceStr[0],
                                        productTitleStr, productTitleStr.toLowerCase());
                                productReference.child(productKey).setValue(newProductDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        startActivity(new Intent(getApplicationContext(), UploadProduct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        productUploadProgressDialog.dismiss();
                                        Toast.makeText(UploadProduct.this, "Your product has been added", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        productUploadProgressDialog.dismiss();
                                        Toast.makeText(UploadProduct.this, "Product could not be added", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                productUploadProgressDialog.dismiss();
                                Toast.makeText(UploadProduct.this, "Product could not be added", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                productUploadProgressDialog.dismiss();
                                Toast.makeText(UploadProduct.this, "Product could not be added", Toast.LENGTH_SHORT).show();
                            }
                        });;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        productUploadProgressDialog.dismiss();
                        Toast.makeText(UploadProduct.this, "Product could not be added", Toast.LENGTH_SHORT).show();
                    }
                });;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                productUploadProgressDialog.dismiss();
                Toast.makeText(UploadProduct.this, "Product could not be added", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public  Bitmap getimageBitmap(Uri uri) throws IOException {

        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(UploadProduct.this.getContentResolver(), uri));
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(UploadProduct.this.getContentResolver(), uri);
        }
        return  bitmap;
    }
}