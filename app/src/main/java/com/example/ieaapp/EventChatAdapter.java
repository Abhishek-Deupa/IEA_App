package com.example.ieaapp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class EventChatAdapter extends FirebaseRecyclerAdapter<EndUserChatModel, EventChatAdapter.EventChatViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public EventChatAdapter(@NonNull FirebaseRecyclerOptions<EndUserChatModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventChatViewHolder holder, int position, @NonNull EndUserChatModel model) {
        DatabaseReference databaseReference = getRef(position);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue().toString();
                String UserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                Log.d("TAG", "onDataChange: "+UserEmail+email);
                if(!email.equals(UserEmail)){
                    holder.userIV.setVisibility(View.GONE);
                    holder.userCV.setVisibility(View.GONE);
                    holder.endUserIV.setVisibility(View.VISIBLE);
                    holder.endUserCV.setVisibility(View.VISIBLE);
                    holder.endUserName.setVisibility(View.VISIBLE);
                    holder.endUserName.setTextColor(Integer.parseInt(model.getColor()));
                    holder.endUserChat.setText(model.getMessage());
                    holder.endUserName.setText(model.getName());

                    Log.d("one4", "onDataChange: ");
                }else{
                    holder.endUserIV.setVisibility(View.GONE);
                    holder.endUserCV.setVisibility(View.GONE);
                    holder.endUserName.setVisibility(View.GONE);
                    holder.userIV.setVisibility(View.VISIBLE);
                    holder.userCV.setVisibility(View.VISIBLE);
                    holder.userChat.setText(model.getMessage());
                    Log.d("one5", "onDataChange: ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @NonNull
    @Override
    public EventChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg_item, parent,false);
        return new EventChatViewHolder(view);
    }

    static class EventChatViewHolder extends RecyclerView.ViewHolder{
        TextView endUserChat,endUserName,userChat;
        CardView userCV,endUserCV;
        ImageView userIV,endUserIV;
        public EventChatViewHolder(@NonNull View itemView) {
            super(itemView);
            endUserChat = (TextView) itemView.findViewById(R.id.eventUserText);
            endUserName = (TextView) itemView.findViewById(R.id.eventUserName);
            userChat = (TextView) itemView.findViewById(R.id.eventChatText);
            userCV =(CardView) itemView.findViewById(R.id.eventchatCV);
            endUserCV =(CardView) itemView.findViewById(R.id.eventuserchatCV);
            userIV = (ImageView)  itemView.findViewById(R.id.eventChatIV);
            endUserIV = (ImageView)  itemView.findViewById(R.id.eventUserChatIV);

        }
    }
}
