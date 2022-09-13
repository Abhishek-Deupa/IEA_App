package com.wormoscorp.ieaapp;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class BaasListAdapter extends FirebaseRecyclerAdapter<BaasListModel, BaasListAdapter.BaasListViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BaasListAdapter(@NonNull FirebaseRecyclerOptions<BaasListModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public BaasListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.baas_list_item, parent, false);
        return new BaasListViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull BaasListViewHolder holder, int position, @NonNull BaasListModel model) {
        holder.baasListCompanyName.setText(model.getCompany_name());
        holder.baasListIndustryType.setText(model.getIndustry_type());
        holder.baasListCl.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        holder.baasListExpandRecyclerView.setLayoutManager(new MemberDirectoryDetail.WrapContentLinearLayoutManager(holder.baasListExpandRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<BaasListRecyclerModel> options = new FirebaseRecyclerOptions.Builder<BaasListRecyclerModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Products by Member").child(model.getEmail().replaceAll("\\.", "%7")), BaasListRecyclerModel.class)
                .build();

        final Boolean[] productExists = {false};
        options.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                if(snapshot.exists()){
                    productExists[0] = true;
                }
            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onError(@NonNull DatabaseError databaseError) {

            }
        });

        BaasListRecyclerAdapter baasListRecyclerAdapter = new BaasListRecyclerAdapter(options);
        holder.baasListExpandRecyclerView.setAdapter(baasListRecyclerAdapter);
        baasListRecyclerAdapter.startListening();

        holder.baasListView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), BaasMemberProfile.class);
                intent.putExtra("BaasItemKey", getRef(position).getKey());
                Log.d("item key", ""+getRef(position).getKey());
                view.getContext().startActivity(intent);
        });

        holder.baasListExpandBtn.setOnClickListener(view -> {

            if (productExists[0]) {
                if (holder.baasListExpandRecyclerView.getVisibility() == View.GONE) {
                    holder.baasListExpandRecyclerView.setVisibility(View.VISIBLE);
                    holder.baasListExpandBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_arrow_up_24));
                } else {
                    holder.baasListExpandRecyclerView.setVisibility(View.GONE);
                    holder.baasListExpandBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_arrow_down_24));
                }
            } else {
                if (holder.nothingToShowTextView.getVisibility() == View.GONE) {
                    holder.nothingToShowTextView.setVisibility(View.VISIBLE);
                    holder.baasListExpandBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_arrow_up_24));
                } else {
                    holder.nothingToShowTextView.setVisibility(View.GONE);
                    holder.baasListExpandBtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_arrow_down_24));
                }
            }
        });


        Glide.with(holder.baasListImage.getContext())
                .load(model.getCompany_logo())
                .placeholder(R.drawable.iea_logo)
                .circleCrop()
                .error(R.drawable.iea_logo)
                .into(holder.baasListImage);
    }

    class BaasListViewHolder extends RecyclerView.ViewHolder {

        View baasListView;
        ImageView baasListImage;
        TextView baasListCompanyName, baasListIndustryType, nothingToShowTextView;
        AppCompatButton baasListExpandBtn;
        RecyclerView baasListExpandRecyclerView;
        ConstraintLayout baasListCl;


        public BaasListViewHolder(@NonNull View itemView) {
            super(itemView);

            baasListImage = (ImageView) itemView.findViewById(R.id.baas_list_image_iv);
            baasListCompanyName = (TextView) itemView.findViewById(R.id.baas_list_company_name);
            baasListIndustryType = (TextView) itemView.findViewById(R.id.baas_list_industry_type);
            baasListView = itemView;
            baasListExpandBtn = (AppCompatButton) itemView.findViewById(R.id.baas_list_expand_btn);
            baasListExpandRecyclerView = (RecyclerView) itemView.findViewById(R.id.baas_list_expand_rv);
            nothingToShowTextView = (TextView) itemView.findViewById(R.id.nothing_to_show_tv);
            baasListCl = (ConstraintLayout) itemView.findViewById(R.id.baas_list_cl);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
