package com.wormoscorp.ieaapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

public class MembersDirectory extends AppCompatActivity {

    RecyclerView memberDirectoryRecyclerView;
    AppCompatButton memberDirectoryBackButton, memberDirectoryFilterIcon, memberDirectoryPopupOkayBtn;
    MembersDirectoryAdapter memberDirectoryAdapter;
    FirebaseRecyclerOptions<MembersDirectoryModel> options;
    AutoCompleteTextView memberSearchTextView;
    TextView noMemberTv, memberDirectoryIdHolder;
    Dialog memberDirectorySearchPopup;
    RadioGroup memberDirectoryRadioGroup;
    RadioButton searchByMemberNameRBtn;
    TextInputLayout memberSearchOutbox;
    EditText memberDirectorySearchNameEdtTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_directory);

        memberDirectoryRecyclerView = (RecyclerView) findViewById(R.id.members_directory_recycler_view);
        WrapContentLinearLayoutManager wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(getApplicationContext());
        memberDirectoryRecyclerView.setLayoutManager(wrapContentLinearLayoutManager);
        memberDirectoryBackButton = findViewById(R.id.members_directory_back_button);
        memberSearchTextView = findViewById(R.id.member_search_autocomplete);
        noMemberTv = findViewById(R.id.no_member_tv);
        memberDirectoryFilterIcon = findViewById(R.id.member_directory_filter_icon);
        memberDirectorySearchPopup = new Dialog(this);
        memberSearchOutbox = findViewById(R.id.member_search_outbox);
        memberDirectorySearchNameEdtTxt = findViewById(R.id.member_directory_search_edtTxt);
        memberDirectoryIdHolder = findViewById(R.id.member_directory_id_holder); //holds id of checked popup radio button
        memberDirectoryIdHolder.setText(String.valueOf(R.id.search_by_product_type_rBtn));


        options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                .build();

        memberSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                        .build();
                memberDirectoryAdapter = new MembersDirectoryAdapter(options);
                memberDirectoryRecyclerView.setAdapter(memberDirectoryAdapter);
                memberDirectoryAdapter.startListening();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (memberSearchTextView.getText().toString().equals("Clear Selection")) {
                    memberSearchTextView.setText("");
                    options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                            .build();

                } else if (memberSearchTextView.getText().toString().equals("")) {
                    options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                            .build();
                } else {
                    options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("industry_type").equalTo(memberSearchTextView.getText().toString()), MembersDirectoryModel.class)
                            .build();
                }
                memberDirectoryAdapter = new MembersDirectoryAdapter(options);
                memberDirectoryRecyclerView.setAdapter(memberDirectoryAdapter);
                memberDirectoryAdapter.startListening();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        memberDirectoryBackButton.setOnClickListener(view -> finish());

       memberDirectoryAdapter = new MembersDirectoryAdapter(options);
       memberDirectoryRecyclerView.setAdapter(memberDirectoryAdapter);

        memberDirectoryFilterIcon.setOnClickListener(view -> {
            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.member_directory_filter_popup, null);

            memberDirectoryRadioGroup = view1.findViewById(R.id.member_directory_popup_radio_group);
            searchByMemberNameRBtn = view1.findViewById(Integer.parseInt(memberDirectoryIdHolder.getText().toString()));
            memberDirectoryPopupOkayBtn = view1.findViewById(R.id.member_directory_popup_okay_btn);
            memberDirectorySearchPopup.setContentView(view1);
            memberDirectorySearchPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            memberDirectorySearchPopup.show();
            memberDirectoryRadioGroup.check(Integer.parseInt(memberDirectoryIdHolder.getText().toString()));

            memberDirectoryPopupOkayBtn.setOnClickListener(v -> {
                memberDirectoryIdHolder.setText(String.valueOf(memberDirectoryRadioGroup.getCheckedRadioButtonId()));
                searchByMemberNameRBtn = view1.findViewById(Integer.parseInt(memberDirectoryIdHolder.getText().toString()));
                if (searchByMemberNameRBtn.getText().toString().equals("Member Name")) {
                    Log.d("Visibility", "Working");
                    memberSearchOutbox.setVisibility(View.GONE);
                    memberDirectorySearchNameEdtTxt.setVisibility(View.VISIBLE);
                    memberDirectorySearchNameEdtTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                                    .build();
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (memberDirectorySearchNameEdtTxt.getText().toString().equals("")) {
                                options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                                        .build();

                            } else {
                                String firstLetterCapital = memberDirectorySearchNameEdtTxt.getText().toString().substring(0, 1).toUpperCase() + memberDirectorySearchNameEdtTxt.getText().toString().substring(1);
                                options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("name").startAt(firstLetterCapital).endAt(firstLetterCapital + "\uf8ff"), MembersDirectoryModel.class)
                                        .build();

                                Log.d("options", memberDirectorySearchNameEdtTxt.getText().toString());
                            }
                            memberDirectoryAdapter = new MembersDirectoryAdapter(options);
                            memberDirectoryRecyclerView.setAdapter(memberDirectoryAdapter);
                            memberDirectoryAdapter.startListening();
                            Log.d("options", String.valueOf(memberDirectoryAdapter));
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else {
                    memberDirectorySearchNameEdtTxt.setVisibility(View.GONE);
                    memberSearchOutbox.setVisibility(View.VISIBLE);
                    memberSearchTextView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                                    .build();
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (memberSearchTextView.getText().toString().equals("Clear Selection")) {
                                memberSearchTextView.setText("");
                                options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                                        .build();

                            } else {
                                options = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users").orderByChild("industry_type").equalTo(memberSearchTextView.getText().toString()), MembersDirectoryModel.class)
                                        .build();
                            }
                            memberDirectoryAdapter = new MembersDirectoryAdapter(options);
                            memberDirectoryRecyclerView.setAdapter(memberDirectoryAdapter);
                            memberDirectoryAdapter.startListening();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }

                memberDirectorySearchPopup.dismiss();
                Log.d("filtervalue", memberDirectoryIdHolder.getText().toString());
                Log.d("Visibility", searchByMemberNameRBtn.getText().toString());
            });
        });
    }

    public void dropdownInit() {
        String[] departments = getResources().getStringArray(R.array.department_search);
        ArrayAdapter<String> arrayAdapterDepartments = new ArrayAdapter<>(getBaseContext(), R.layout.drop_down_item, departments);
        memberSearchTextView.setAdapter(arrayAdapterDepartments);
    }

    @Override
    public void onResume() {
        super.onResume();
        dropdownInit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        memberDirectoryAdapter.startListening();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        memberDirectoryAdapter.stopListening();
//    }

     static class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }
         public WrapContentLinearLayoutManager(Context context,int orientation,boolean reverseLayout) {
             super(context,orientation,reverseLayout);
         }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "Recycler view error");
            }
        }
    }


}