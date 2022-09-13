package com.wormoscorp.ieaapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpcomingEventAdapter extends FirebaseRecyclerAdapter<UpcomingEventModel, UpcomingEventAdapter.UpcomingEventViewHolder>  {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UpcomingEventAdapter(@NonNull FirebaseRecyclerOptions<UpcomingEventModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UpcomingEventViewHolder holder, int position, @NonNull UpcomingEventModel model) {
        if(UserProfile.dateCompare(model.getDate())==1){
            PastEventModel pastEventModel = new PastEventModel(model.getDate(), model.getDescription(), model.getImgUrl(), model.getTitle().toLowerCase(), model.getLocation(), model.getWeekday(), model.getTime(), model.getTitle());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Past Events");
            databaseReference.child(getRef(position).getKey()).setValue(pastEventModel).addOnSuccessListener(s->{
                DatabaseReference upcomingRef = getRef(position);
                upcomingRef.removeValue();
            });
        }else{
            holder.upcomingEventTitle.setText(model.getTitle());
            holder.upcomingEventDate.setText(model.getDate());
            holder.upcomingEventTime.setText(model.getTime());

            Glide.with(holder.upcomingEventImg.getContext())
                    .load(model.getImgUrl())
                    .placeholder(R.drawable.iea_logo)
                    .error(R.drawable.iea_logo)
                    .into(holder.upcomingEventImg);

            holder.upcomingEventItem.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), EventDetails.class);
                intent.putExtra("EventItemKey", getRef(position).getKey());
                intent.putExtra("EventType", "Events");
                view.getContext().startActivity(intent);
            });
        }

    }

    @NonNull
    @Override
    public UpcomingEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View upcomingEventView = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_event_item, parent, false);
        return new UpcomingEventViewHolder(upcomingEventView);
    }

    public class UpcomingEventViewHolder extends RecyclerView.ViewHolder {
        ImageView upcomingEventImg;
        TextView upcomingEventTitle, upcomingEventTime, upcomingEventDate;
        View upcomingEventItem;

        public UpcomingEventViewHolder(@NonNull View itemView) {
            super(itemView);

            upcomingEventImg = itemView.findViewById(R.id.upcoming_events_item_img);
            upcomingEventTime = itemView.findViewById(R.id.upcoming_event_item_time);
            upcomingEventTitle = itemView.findViewById(R.id.upcoming_event_item_title);
            upcomingEventDate = itemView.findViewById(R.id.upcoming_event_item_date);
            upcomingEventItem = itemView;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
