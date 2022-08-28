package com.example.ieaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PastEventPhotoAdapter extends FirebaseRecyclerAdapter<PastEventPhotoModel, PastEventPhotoAdapter.PastEventPhotoViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PastEventPhotoAdapter(@NonNull FirebaseRecyclerOptions<PastEventPhotoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PastEventPhotoViewHolder holder, int position, @NonNull PastEventPhotoModel model) {
        Glide.with(holder.pastEventItemIv.getContext())
                .load(model.getImage_uri())
                .error(R.drawable.iea_logo)
                .into(holder.pastEventItemIv);
    }

    @NonNull
    @Override
    public PastEventPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View photoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_event_photo_item,parent,false);
        return new PastEventPhotoViewHolder(photoView);
    }

    public class PastEventPhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView pastEventItemIv;

        public PastEventPhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            pastEventItemIv = itemView.findViewById(R.id.past_event_photo_item);
        }
    }
}