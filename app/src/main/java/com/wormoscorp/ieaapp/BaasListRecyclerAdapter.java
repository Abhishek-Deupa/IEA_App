package com.wormoscorp.ieaapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class BaasListRecyclerAdapter extends FirebaseRecyclerAdapter<BaasListRecyclerModel, BaasListRecyclerAdapter.BaasListRecyclerViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BaasListRecyclerAdapter(@NonNull FirebaseRecyclerOptions<BaasListRecyclerModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BaasListRecyclerViewHolder holder, int position, @NonNull BaasListRecyclerModel model) {

        Glide.with(holder.baasListRecycleIv.getContext())
                .load(model.getProductImageUrl())
                .placeholder(R.drawable.iea_logo)
                .error(R.drawable.iea_logo)
                .into(holder.baasListRecycleIv);

        holder.baasListRecyclerView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), BaasMemberProfile.class);
            intent.putExtra("BaasItemKey", model.getOwnerEmail());
            Log.d("item key", ""+model.getOwnerEmail());
            view.getContext().startActivity(intent);
        });
    }

    @NonNull
    @Override
    public BaasListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.baas_list_recycler_item,parent,false);
        return new BaasListRecyclerViewHolder(view);
    }

    public class BaasListRecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView baasListRecycleIv;
        View baasListRecyclerView;

        public BaasListRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            baasListRecycleIv = (ImageView) itemView.findViewById(R.id.baas_list_recycler_iv);
            baasListRecyclerView = itemView;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
