package com.example.ieaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MemberNotificationAdapter extends FirebaseRecyclerAdapter<MemberNotificationModel, MemberNotificationAdapter.MemberNotifcationViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MemberNotificationAdapter(@NonNull FirebaseRecyclerOptions<MemberNotificationModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MemberNotifcationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MemberNotificationModel model) {
        holder.memberNotificationTitle.setText(model.getNotificationTitle());
        holder.memberNotificationContent.setText(model.getNotificationContent());
        holder.memberNotificationDate.setText(model.getNotificationDate());
        holder.memberNotificationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(),MemberNotificationDetail.class);
                i.putExtra("NotificationItemKey", getRef(position).getKey());
                view.getContext().startActivity(i);
            }
        });
    }

    @NonNull
    @Override
    public MemberNotifcationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_notification_item, parent, false);
        return new MemberNotifcationViewHolder(view);
    }

    class MemberNotifcationViewHolder extends RecyclerView.ViewHolder {
        View memberNotificationItem;
        TextView memberNotificationTitle, memberNotificationContent, memberNotificationDate;

        public MemberNotifcationViewHolder(@NonNull View itemView) {
            super(itemView);
            memberNotificationItem = itemView;
            memberNotificationTitle = itemView.findViewById(R.id.member_notification_item_title_tv);
            memberNotificationContent = itemView.findViewById(R.id.member_notification_item_content_tv);
            memberNotificationDate = itemView.findViewById(R.id.member_notification_item_date_tv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
