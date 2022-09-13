package com.wormoscorp.ieaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LandingPage extends AppCompatActivity {

    AppCompatButton requestForRegistration, existingMember, exploreUs;
    FirebaseAuth mAuth;
    RecyclerView sliderCoreMemberRecyclerView;
    sliderLandingPageAdapter sliderAdapter;
    FirebaseRecyclerOptions<CoreMemberModel> option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing_page);


        requestForRegistration = findViewById(R.id.request_for_membership_btn);
        existingMember = findViewById(R.id.existing_member_btn);
        exploreUs = findViewById(R.id.explore_btn);
        mAuth = FirebaseAuth.getInstance();


        requestForRegistration.setOnClickListener(view -> startActivity(new Intent(LandingPage.this, Registration.class)));

        exploreUs.setOnClickListener(view -> startActivity(new Intent(LandingPage.this, explore_us.class)));

        existingMember.setOnClickListener(view -> startActivity(new Intent(LandingPage.this, Login.class)));


        sliderCoreMemberRecyclerView = (RecyclerView) findViewById(R.id.sliderCoreMemberRecyclerview);
        sliderCoreMemberRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        option = new FirebaseRecyclerOptions.Builder<CoreMemberModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Core Teams Member").limitToFirst(3), CoreMemberModel.class)
                .build();

        sliderAdapter = new sliderLandingPageAdapter(option);
        sliderCoreMemberRecyclerView.setAdapter(sliderAdapter);


    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public WrapContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("TAG", "meet a IOOBE in RecyclerView");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sliderAdapter.startListening();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LandingPage.this, explore_menu.class));
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        sliderAdapter.stopListening();
//    }

}