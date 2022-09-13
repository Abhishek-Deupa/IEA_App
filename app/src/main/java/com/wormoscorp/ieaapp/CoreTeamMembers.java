package com.wormoscorp.ieaapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.wormoscorp.ieaapp.CoreMemberAdapter;
import com.wormoscorp.ieaapp.CoreMemberModel;

public class CoreTeamMembers extends AppCompatActivity{

    RecyclerView coreMemberRecyclerView;
    AppCompatButton coreTeamMemberBackButton;
    CoreMemberAdapter coreMemberAdapter;
    FirebaseRecyclerOptions<CoreMemberModel> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_team_members);

        coreMemberRecyclerView = findViewById(R.id.core_member_recycler_view);
        coreMemberRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getApplicationContext()));
        coreTeamMemberBackButton = findViewById(R.id.core_team_member_back_button);

        options = new FirebaseRecyclerOptions.Builder<CoreMemberModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Core Teams Member"), CoreMemberModel.class)
                .build();

        Log.d("kilj", "onCreateView: "+options+"\n"+FirebaseDatabase.getInstance().getReference("Core Teams Member"));

        coreMemberAdapter = new CoreMemberAdapter(options);
        coreMemberRecyclerView.setAdapter(coreMemberAdapter);

        coreTeamMemberBackButton.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        coreMemberAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        coreMemberAdapter.startListening();
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
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
    }


}