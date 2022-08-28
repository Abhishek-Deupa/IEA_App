package com.example.ieaapp;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class BaasProductAdapter extends FirebaseRecyclerAdapter<MemberProductModel, BaasProductAdapter.memberProductViewHolder>{


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BaasProductAdapter(@NonNull FirebaseRecyclerOptions<MemberProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull memberProductViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MemberProductModel model) {

        holder.baasProductName.setText(model.getProductTitle());
        Glide.with(holder.baasProductImg.getContext())
                .load(model.getProductImageUrl())
                .placeholder(R.drawable.iea_logo)
                .error(R.drawable.iea_logo)
                .into(holder.baasProductImg);
        holder.baasProductPrice.setText("\u20B9"+model.getProductPrice());
        Log.d("random3", model.getOwnerEmail()+" "+FirebaseAuth.getInstance().getCurrentUser().getEmail());

        holder.memberProductCardView.setOnClickListener(view -> {
            if(model.getOwnerEmail().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail().replaceAll("\\.","%7"))){
                Intent intent = new Intent(view.getContext(),memberProductedit.class);
                intent.putExtra("EditItemKey", getRef(position).getKey());
                Log.d("random1", "halaluyeah");
                view.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(view.getContext(), MemberProductDetail.class);
                intent.putExtra("memberProductKey", getRef(position).getKey());
                Log.d("random2", "hakunamatata");
                view.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public memberProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.baas_product_item, parent, false);
        return new memberProductViewHolder(view);
    }

    class memberProductViewHolder extends RecyclerView.ViewHolder {

        ImageView baasProductImg;
        TextView baasProductName, baasProductPrice;
        View memberProductCardView;

        public memberProductViewHolder(@NonNull View itemView) {

            super(itemView);
            baasProductImg = itemView.findViewById(R.id.baas_product_img);
            baasProductName = itemView.findViewById(R.id.baas_product_name);
            baasProductPrice = itemView.findViewById(R.id.baas_product_price);
            memberProductCardView = itemView;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}