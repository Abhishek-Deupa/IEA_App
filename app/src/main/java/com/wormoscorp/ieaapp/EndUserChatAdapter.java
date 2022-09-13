package com.wormoscorp.ieaapp;

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

public class EndUserChatAdapter extends FirebaseRecyclerAdapter<EndUserChatModel, EndUserChatAdapter.EndUserChatViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public EndUserChatAdapter(@NonNull FirebaseRecyclerOptions<EndUserChatModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EndUserChatViewHolder holder, int position, @NonNull EndUserChatModel model) {
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
                    holder.endUserChat.setText(model.getMessage());
                    Log.d("one4", "onDataChange: ");
                }else{
                    holder.endUserIV.setVisibility(View.GONE);
                    holder.endUserCV.setVisibility(View.GONE);
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
    public EndUserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.end_msg_item, parent,false);
        return new EndUserChatViewHolder(view);
    }

    static class EndUserChatViewHolder extends RecyclerView.ViewHolder{
        TextView endUserChat,endUserName,userChat;
        CardView userCV,endUserCV;
        ImageView userIV,endUserIV;
        public EndUserChatViewHolder(@NonNull View itemView) {
            super(itemView);
            endUserChat = (TextView) itemView.findViewById(R.id.endUserText);
            endUserName = (TextView) itemView.findViewById(R.id.endUserName);
            userChat = (TextView) itemView.findViewById(R.id.userChatText);
            userCV =(CardView) itemView.findViewById(R.id.userchatCV);
            endUserCV =(CardView) itemView.findViewById(R.id.enduserchatCV);
            userIV = (ImageView)  itemView.findViewById(R.id.userChatIV);
            endUserIV = (ImageView)  itemView.findViewById(R.id.endUserChatIV);

        }
    }
}
