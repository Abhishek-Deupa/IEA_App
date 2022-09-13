package com.wormoscorp.ieaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    RecyclerView splashRV;
    MembersDirectoryAdapter memberDirectoryAdapter2;
    FirebaseRecyclerOptions<MembersDirectoryModel> options3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashRV = findViewById(R.id.splash_RV);
        MembersDirectory.WrapContentLinearLayoutManager wrapContentLinearLayoutManager = new MembersDirectory.WrapContentLinearLayoutManager(getApplicationContext());
        splashRV.setLayoutManager(wrapContentLinearLayoutManager);
        options3 = new FirebaseRecyclerOptions.Builder<MembersDirectoryModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Registered Users"), MembersDirectoryModel.class)
                .build();
        memberDirectoryAdapter2 =  new MembersDirectoryAdapter(options3);
        splashRV.setAdapter(memberDirectoryAdapter2);

        if (getIntent().getExtras()!=null) {
            if (getIntent().getExtras() != null) {
                for (String key : getIntent().getExtras().keySet()) {
                    Object value = getIntent().getExtras().get(key);
                    Log.d("Activity onResume", "Key: " + key + " Value: " + value);
                }
            }
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = activityHandler();
                startActivity(i);
                finish();
            }
        },4100);
    }

    @Override
    protected void onStart() {
        super.onStart();
        memberDirectoryAdapter2.startListening();
    }

    public Intent activityHandler(){
        Intent act = null;
        try{
            if(getIntent().getExtras()!=null){
                if(getIntent().getExtras().getString("activity").equals("grievance")){
                    act = new Intent(MainActivity.this,MyGrievances.class).putExtra("GrievanceItemKey",getIntent().getExtras().getString("ownerKey")).
                            putExtra("notify","1");
                    return act;
                } else if(getIntent().getExtras().getString("activity").equals("userChatSession")){
                    act = new Intent(MainActivity.this,ChatSession.class).putExtra("chatKey", getIntent().getExtras().getString("chatKey"))
                            .putExtra("ownerEmail",getIntent().getExtras().getString("ownerKey")).
                            putExtra("notify","1");
                    return act;
                } else if(getIntent().getExtras().getString("activity").equals("eventChatSession")){
                    act = new Intent(MainActivity.this,EventChatSession.class).putExtra("chatKey",getIntent().getExtras().getString("chatKey"))
                            .putExtra("eventItemKey",getIntent().getExtras().getString("ownerKey"))
                            .putExtra("eventType",getIntent().getExtras().getString("eventType")).
                            putExtra("notify","1");;
                    return act;
                } else {
                    act = new Intent(MainActivity.this, MembersNotification.class);
                    return act;
                }
            } else {
                act = new Intent(MainActivity.this, LandingPage.class);
                return act;
            }
        }catch (Exception e){
            e.printStackTrace();
            act = new Intent(MainActivity.this, LandingPage.class);
            return act;
        }

    }


}