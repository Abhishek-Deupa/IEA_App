package com.example.ieaapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BAAS extends AppCompatActivity {

    RecyclerView baasListRecyclerView;
    FirebaseRecyclerOptions<BaasListModel> options;
    FirebaseRecyclerOptions<MemberProductModel> productOptions;
    BaasListAdapter baasListAdapter;
    MemberProductAdapter baasProductAdapter;
    AppCompatButton baasBackBtn, baasFilterOkayBtn;
    EditText searchBaasEdtTxt;
    AutoCompleteTextView baasIndustrySearchTv;
    CardView baasFilterIconCv, wormosCv;
    CircleImageView companyLogo;
    Dialog baasFilterPopup;
    RadioGroup baasFilterRadioGroup;
    RadioButton baasFilterRadioButton;
    TextView baasRadioIdHolder;
    TextInputLayout baasFilterSearchOutbox;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference storageCompanyLogoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baas);
        baasBackBtn = findViewById(R.id.baas_back_button);
        searchBaasEdtTxt = findViewById(R.id.search_baas_edtTxt);
        baasIndustrySearchTv = findViewById(R.id.baas_industry_search_autocomplete);
        baasListRecyclerView = findViewById(R.id.baas_list_rv);
        baasFilterIconCv = findViewById(R.id.baas_filter_cv);
        baasRadioIdHolder = findViewById(R.id.baas_radio_id_holder);
        baasFilterSearchOutbox = findViewById(R.id.baas_filter_search_outbox);
        wormosCv = findViewById(R.id.WormosCv);
        companyLogo = findViewById(R.id.baasCompanyLogoIv);

        baasFilterPopup = new Dialog(this);

        storageCompanyLogoReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageCompanyLogoReference.child("Company Logos/" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail() + "CompanyLogo");
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext())
                .load(uri)
                .placeholder(R.drawable.iea_logo)
                .circleCrop()
                .error(R.drawable.iea_logo)
                .into(companyLogo));
        companyLogo.setOnClickListener(view -> startActivity(new Intent(BAAS.this, BaasMemberProfile.class).putExtra("BaasItemKey", Objects.requireNonNull(mAuth.getCurrentUser().getEmail()).replaceAll("\\.", "%7"))));

        baasRadioIdHolder.setText(String.valueOf(R.id.search_by_industry_type_rBtn));

        WrapContentLinearLayoutManager2 wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager2(this);


        baasListRecyclerView.setLayoutManager(wrapContentLinearLayoutManager);


        options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                .build();

        baasListAdapter = new BaasListAdapter(options);
        baasListRecyclerView.setAdapter(baasListAdapter);


        baasBackBtn.setOnClickListener(view -> finish());

        dropdownInit();

        baasIndustrySearchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                        .build();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (baasIndustrySearchTv.getText().toString().equals("Clear Selection")) {
                    baasIndustrySearchTv.setText("");
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                            .build();

                } else if (baasIndustrySearchTv.getText().toString().equals("")) {
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                            .build();
                } else {
                    options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("industry_type").equalTo(baasIndustrySearchTv.getText().toString()), BaasListModel.class)
                            .build();
                }
                baasListAdapter = new BaasListAdapter(options);
                baasListRecyclerView.setAdapter(baasListAdapter);
                //baasListAdapter.startListening();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        baasFilterIconCv.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.baas_filter_popup, null);

            baasFilterRadioGroup = view1.findViewById(R.id.baas_filter_popup_radio_group);
            baasFilterRadioButton = view1.findViewById(Integer.parseInt(baasRadioIdHolder.getText().toString()));
            baasFilterRadioGroup.check(Integer.parseInt(baasRadioIdHolder.getText().toString()));
            baasFilterOkayBtn = view1.findViewById(R.id.baas_filter_popup_okay_btn);

            baasFilterPopup.setContentView(view1);
            baasFilterPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            baasFilterPopup.show();

            baasFilterOkayBtn.setOnClickListener(view2 -> {
                baasRadioIdHolder.setText(String.valueOf(baasFilterRadioGroup.getCheckedRadioButtonId()));
                baasFilterRadioButton = view1.findViewById(Integer.parseInt(baasRadioIdHolder.getText().toString()));
                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                        .build();
                baasListAdapter = new BaasListAdapter(options);
                baasListRecyclerView.setAdapter(baasListAdapter);
                baasListAdapter.startListening();
                Log.d("RadioBtn", baasFilterRadioButton.getText().toString());
                if (baasFilterRadioButton.getText().toString().equals("Industry Type")) {
                    baasFilterSearchOutbox.setVisibility(View.VISIBLE);
                    searchBaasEdtTxt.setVisibility(View.GONE);

                    baasIndustrySearchTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                    .build();
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (baasIndustrySearchTv.getText().toString().equals("Clear Selection")) {
                                baasIndustrySearchTv.setText("");
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                        .build();

                            } else if (baasIndustrySearchTv.getText().toString().isEmpty()) {
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                        .build();
                            } else {
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("industry_type").equalTo(baasIndustrySearchTv.getText().toString()), BaasListModel.class)
                                        .build();
                            }
                            baasListAdapter = new BaasListAdapter(options);
                            baasListRecyclerView.setAdapter(baasListAdapter);
                            baasListAdapter.startListening();
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else if (baasFilterRadioButton.getText().toString().equals("Company Name")) {
                    baasFilterSearchOutbox.setVisibility(View.GONE);
                    searchBaasEdtTxt.setVisibility(View.VISIBLE);
                    searchBaasEdtTxt.setHint("Search by Company Name");

                    searchBaasEdtTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                    .build();
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (TextUtils.isEmpty(searchBaasEdtTxt.getText().toString())) {
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), BaasListModel.class)
                                        .build();
                            } else {
                                String companyNameLC = searchBaasEdtTxt.getText().toString().toLowerCase(Locale.ROOT);
                                options = new FirebaseRecyclerOptions.Builder<BaasListModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("company_name_lowercase").startAt(companyNameLC).endAt(companyNameLC + "\uf8ff"), BaasListModel.class)
                                        .build();

                            }
                            baasListAdapter = new BaasListAdapter(options);
                            baasListRecyclerView.setAdapter(baasListAdapter);
                            baasListAdapter.startListening();
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else if (baasFilterRadioButton.getText().toString().equals("Product Name")) {
                    baasFilterSearchOutbox.setVisibility(View.GONE);
                    searchBaasEdtTxt.setVisibility(View.VISIBLE);
                    searchBaasEdtTxt.setHint("Search by Product Name");

                    productOptions = new FirebaseRecyclerOptions.Builder<MemberProductModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Products"), MemberProductModel.class)
                            .build();

                    baasProductAdapter = new MemberProductAdapter(productOptions);
                    baasListRecyclerView.setAdapter(baasProductAdapter);
                    baasProductAdapter.startListening();

                    searchBaasEdtTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            productOptions = new FirebaseRecyclerOptions.Builder<MemberProductModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Products"), MemberProductModel.class)
                                    .build();

                            baasProductAdapter = new MemberProductAdapter(productOptions);
                            baasListRecyclerView.setAdapter(baasProductAdapter);
                            baasProductAdapter.startListening();
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String searchText = searchBaasEdtTxt.getText().toString().toLowerCase(Locale.ROOT);
                            productOptions = new FirebaseRecyclerOptions.Builder<MemberProductModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Products")
                                            .orderByChild("productTitleLowerCase").startAt(searchText).endAt(searchText + "\uf8ff"), MemberProductModel.class)
                                    .build();

                            baasProductAdapter = new MemberProductAdapter(productOptions);
                            baasListRecyclerView.setAdapter(baasProductAdapter);
                            baasProductAdapter.startListening();
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }
                baasFilterPopup.dismiss();
            });
        });

        wormosCv.setOnClickListener(view -> startActivity(new Intent(BAAS.this, WormosDetail.class)));
    }

    public void dropdownInit() {
        String[] departments = getResources().getStringArray(R.array.department_search);
        ArrayAdapter<String> arrayAdapterDepartments = new ArrayAdapter<>(getBaseContext(), R.layout.drop_down_item, departments);
        baasIndustrySearchTv.setAdapter(arrayAdapterDepartments);
    }

    @Override
    protected void onStart() {
        super.onStart();
        baasListAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dropdownInit();
        baasListAdapter.startListening();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        baasListAdapter.stopListening();
//    }
        public static class WrapContentLinearLayoutManager2 extends LinearLayoutManager {
            public WrapContentLinearLayoutManager2(Context context) {
        super(context);
    }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("TAG", "Recycler view error");
                }
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        }
}