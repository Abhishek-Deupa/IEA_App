package com.wormoscorp.ieaapp;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MemberProductAdapter extends FirebaseRecyclerAdapter<MemberProductModel,MemberProductAdapter.memberProductViewHolder>{


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MemberProductAdapter(@NonNull FirebaseRecyclerOptions<MemberProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull memberProductViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull MemberProductModel model) {

        holder.productName.setText(model.getProductTitle());
        holder.memberProductPrice.setText("\u20B9"+model.getProductPrice());
        Glide.with(holder.productImg.getContext())
                .load(model.getProductImageUrl())
                .placeholder(R.drawable.iea_logo)
                .error(R.drawable.iea_logo)
                .into(holder.productImg);

        holder.memberProductCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.getOwnerEmail().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())){
                    Intent i = new Intent(view.getContext(),memberProductedit.class);
                    i.putExtra("EditItemKey", getRef(position).getKey());
                    view.getContext().startActivity(i);
                }else {
                    Intent intent = new Intent(view.getContext(), MemberProductDetail.class);
                    intent.putExtra("memberProductKey", getRef(position).getKey());
                    view.getContext().startActivity(intent);
                }
            }
        });

    }

    @NonNull
    @Override
    public memberProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_product_item, parent, false);
        return new memberProductViewHolder(view);
    }

    class memberProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImg;
        TextView productName, memberProductPrice;
        View memberProductCardView;

        public memberProductViewHolder(@NonNull View itemView) {

            super(itemView);
            productImg = itemView.findViewById(R.id.memberProductImg);
            productName = itemView.findViewById(R.id.memberProductName);
            memberProductPrice = itemView.findViewById(R.id.member_product_price);
            memberProductCardView = itemView;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}