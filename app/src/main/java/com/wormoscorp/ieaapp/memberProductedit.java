package com.wormoscorp.ieaapp;

import android.Manifest;
import android.app.AlertDialog;
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
import java.util.HashMap;
import java.util.UUID;

public class memberProductedit extends AppCompatActivity {

    ImageView Productimg;
    TextView yesbtn, nobtn;
    EditText productName, productDesc, productPrice;
    AppCompatButton editproductbackbutton;
    CardView Addproductbtn, RemoveProductBtn;
    ActivityResultLauncher<String> mGetProductImage;
    Uri productImageUri;
    String productKey, productPurlStr;
    DatabaseReference databaseReference, productReference;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final String ownerEmailConverted = mAuth.getCurrentUser().getEmail().replaceAll("\\.", "%7");
    StorageReference storageProfilePicReference;
    Bitmap imageBitmap;
    String productPriceStr, productNameStr, productDescStr;
    Dialog confirmationDialog;

    {
        storageProfilePicReference = FirebaseStorage.getInstance().getReference();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_productedit);

        productKey = getIntent().getStringExtra("EditItemKey");
        Log.d("keydG", "onCreate: " + productKey + " " + imageBitmap);
        Productimg = findViewById(R.id.edit_product_image_iv);
        productName = findViewById(R.id.edit_title_edtTxt);
        productDesc = findViewById(R.id.edit_description_edtTxt);
        productPrice = findViewById(R.id.edit_price_edtTxt);
        Addproductbtn = findViewById(R.id.upload_product_btn);
        RemoveProductBtn = findViewById(R.id.remove_product_btn);
        editproductbackbutton = findViewById(R.id.editProduct_back_button);
        editproductbackbutton.setOnClickListener(view -> finish());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products by Member").child(ownerEmailConverted);
        databaseReference.child(productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    productNameStr = snapshot.child("productTitle").getValue().toString();
                    productPriceStr = snapshot.child("productPrice").getValue().toString();
                    productDescStr = snapshot.child("productDescription").getValue().toString();
                    productPurlStr = snapshot.child("productImageUrl").getValue().toString();

                    productName.setText(productNameStr);
                    productPrice.setText("\u20B9" + productPriceStr);
                    productDesc.setText(productDescStr);
                    if (productImageUri == null) {
                        Glide.with(getApplicationContext())
                                .load(productPurlStr)
                                .placeholder(R.drawable.iea_logo)
                                .error(R.drawable.iea_logo)
                                .into(Productimg);
                    }
                } catch (Exception e) {
                    Log.e("error", "onDataChange: ", e);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        RemoveProductBtn.setOnClickListener(v -> {
            confirmationDialog = new Dialog(memberProductedit.this);
            LayoutInflater inflater = getLayoutInflater();
            View confirmationView = inflater.inflate(R.layout.are_you_sure_popup, null);
            yesbtn = confirmationView.findViewById(R.id.yesbtn);
            nobtn = confirmationView.findViewById(R.id.nobtn);
            confirmationDialog.setContentView(confirmationView);
            confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmationDialog.show();
            nobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmationDialog.dismiss();
                }
            });
            yesbtn.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    try {
                        productReference = FirebaseDatabase.getInstance().getReference().child("Products").child(productKey);
                        databaseReference.child(productKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                productReference.removeValue();
                                confirmationDialog.dismiss();
                                startActivity(new Intent(memberProductedit.this, BaasMemberProfile.class).putExtra("BaasItemKey", ownerEmailConverted).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        });

                    } catch (Exception e) {
                        Log.d("error", ": " + e);
                    }
                }
            });
        });

        Addproductbtn.setOnClickListener(v -> {
            if (productImageUri != null) {
                uploadEditedProduct(productImageUri, databaseReference, productKey);
            } else {
                uploadEditedProductStr(productPurlStr, databaseReference, productKey);

            }
        });

        mGetProductImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                String destinationUri = UUID.randomUUID().toString() + ".jpg";
                UCrop.of(result, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                        .withAspectRatio(5, 6)
                        .start(memberProductedit.this, 2);
                Productimg.setPadding(0, 0, 0, 0);
            }
        });

        Productimg.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(memberProductedit.this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View pickImgview = layoutInflater.inflate(R.layout.image_picker_item, null);
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
                    mGetProductImage.launch("image/*");
                    alertDialogImg.dismiss();
                    imageBitmap = null;
                }

            });
            cameraCardView.setOnClickListener(view1 -> {
                ImagePicker.with(this)
                        .cameraOnly()
                        .crop(5f, 6f)                    //Crop image(Optional), Check Customization for more option
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start(3);
            });
        });

    }

    public void uploadEditedProduct(Uri productUri, DatabaseReference ref, String key) {
        try {

            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Updating...");
            dialog.setCancelable(false);
            dialog.show();
            HashMap<String, Object> productData = new HashMap<>();
            if (productPrice.getText().toString().isEmpty()) {
                productData.put("productPrice", "--");
            } else {
                productData.put("productPrice", productPrice.getText().toString().substring(1));
            }
            productData.put("productTitle", productName.getText().toString());
            productData.put("productDescription", productDesc.getText().toString());
            StorageReference productFileRef = storageProfilePicReference.child("Product Images/" + mAuth.getCurrentUser().getEmail() + productName.getText().toString());
            productFileRef.putFile(productUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    productFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            productData.put("productImageUrl", uri.toString());
                            ref.child(key).updateChildren(productData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if (!productNameStr.equals(productName.getText().toString())) {
                                        StorageReference oldProductImgRef = FirebaseStorage.getInstance().getReferenceFromUrl(productPurlStr);
                                        oldProductImgRef.delete();
                                    }
//                                    imageBitmap=null;
//                                    productImageUri =null;
                                    dialog.dismiss();
                                    startActivity(new Intent(memberProductedit.this, memberProductedit.class).putExtra("EditItemKey", productKey).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    Toast.makeText(memberProductedit.this, "Product updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(memberProductedit.this, "Product updating failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(memberProductedit.this, "Product updating failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "uploadEditedProduct: ", e);
        }

    }

    public void uploadEditedProductStr(String purlStr, DatabaseReference ref, String key) {
        HashMap<String, Object> productData = new HashMap<>();
        if (productPrice.getText().toString().isEmpty()) {
            productData.put("productPrice", "--");
        } else {
            productData.put("productPrice", productPrice.getText().toString().substring(1));
        }
        productData.put("productTitle", productName.getText().toString());
        productData.put("productDescription", productDesc.getText().toString());
        ref.child(key).updateChildren(productData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(memberProductedit.this, "Product updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(memberProductedit.this, "Product updating failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 2) {
            productImageUri = UCrop.getOutput(data);
            Productimg.setImageURI(productImageUri);
        } else if (resultCode == RESULT_OK && requestCode == 3) {
            productImageUri = data.getData();
            Productimg.setImageURI(productImageUri);

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
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermission() {
        boolean res1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean res2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

    public Bitmap getimageBitmap(Uri uri) throws IOException {

        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(memberProductedit.this.getContentResolver(), uri));
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(memberProductedit.this.getContentResolver(), uri);
        }
        return bitmap;
    }

}