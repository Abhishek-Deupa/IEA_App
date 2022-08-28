package com.example.ieaapp;

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

public class MembersDirectoryAdapter extends FirebaseRecyclerAdapter<MembersDirectoryModel, MembersDirectoryAdapter.MemberDirectoryViewHolder> {



    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MembersDirectoryAdapter(@NonNull FirebaseRecyclerOptions<MembersDirectoryModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MemberDirectoryViewHolder holder, int position, @NonNull MembersDirectoryModel model) {
        holder.memberDirectoryName.setText(model.getName());
        holder.memberDirectoryCompanyName.setText(model.getCompany_name());

        holder.memberDirectoryView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MemberDirectoryDetail.class);
            intent.putExtra("MemberItemKey", getRef(position).getKey());
            view.getContext().startActivity(intent);
        });

        Glide.with(holder.memberDirectoryProfileImg.getContext())
                .load(model.getPurl())
                .placeholder(R.drawable.iea_logo)
                .circleCrop()
                .error(R.drawable.iea_logo)
                .into(holder.memberDirectoryProfileImg);

    }

    @NonNull
    @Override
    public MemberDirectoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_directory_item,parent,false);
        return new MemberDirectoryViewHolder(view);
    }

    class MemberDirectoryViewHolder extends RecyclerView.ViewHolder{

        View memberDirectoryView;
        ImageView memberDirectoryProfileImg;
        TextView memberDirectoryName, memberDirectoryCompanyName;

        public MemberDirectoryViewHolder(@NonNull View itemView) {
            super(itemView);

            memberDirectoryProfileImg = (ImageView) itemView.findViewById(R.id.members_directory_profile_picture);
            memberDirectoryName = (TextView) itemView.findViewById(R.id.members_directory_name);
            memberDirectoryCompanyName = (TextView) itemView.findViewById(R.id.members_directory_company_name);
            memberDirectoryView = itemView;
        }
    }

}
