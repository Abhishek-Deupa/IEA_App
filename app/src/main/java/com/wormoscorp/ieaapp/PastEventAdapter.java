package com.wormoscorp.ieaapp;

import android.content.Intent;
import android.util.Log;
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

public class PastEventAdapter extends FirebaseRecyclerAdapter<UpcomingEventModel,PastEventAdapter.pastEventViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PastEventAdapter(@NonNull FirebaseRecyclerOptions<UpcomingEventModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull pastEventViewHolder holder, int position, @NonNull UpcomingEventModel model) {
        Log.d("TAG", "onBindViewHolder: past");

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
                intent.putExtra("EventType","Past Events");
                view.getContext().startActivity(intent);
            });
    }

    @NonNull
    @Override
    public pastEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View upcomingEventView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_event_item, parent, false);
        return new pastEventViewHolder(upcomingEventView2);
    }

    static class pastEventViewHolder extends RecyclerView.ViewHolder{
        ImageView upcomingEventImg;
        TextView upcomingEventTitle, upcomingEventTime, upcomingEventDate;
        View upcomingEventItem;

        public pastEventViewHolder(@NonNull View itemView) {
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
