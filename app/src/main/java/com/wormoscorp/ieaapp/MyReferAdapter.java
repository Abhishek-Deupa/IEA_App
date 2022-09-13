package com.wormoscorp.ieaapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MyReferAdapter extends FirebaseRecyclerAdapter<Refermodelclass,MyReferAdapter.MyReferViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MyReferAdapter(@NonNull FirebaseRecyclerOptions<Refermodelclass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyReferViewHolder holder, int position, @NonNull Refermodelclass model) {
        holder.myReferName.setText(model.getName());
        holder.myReferCompanyName.setText(model.getCompanyname());
        holder.myReferStatus.setText(model.getStatus());

        switch (model.getStatus()){
            case "Unsolved":
                holder.referStatusColorTv.setBackgroundColor(Color.parseColor("#000000"));
                break;
            case "Reviewed":
                holder.referStatusColorTv.setBackgroundColor(Color.parseColor("#48A14D"));
                break;
            case "Rejected":
                holder.referStatusColorTv.setBackgroundColor(Color.parseColor("#96271f"));
                break;
        }

    }

    @NonNull
    @Override
    public MyReferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_grievance_item, parent,false);
        return new MyReferViewHolder(view);
    }

    static class  MyReferViewHolder extends RecyclerView.ViewHolder{

        View myReferView;
        TextView myReferName, myReferCompanyName, myReferStatus, referStatusColorTv;

        public MyReferViewHolder(@NonNull View itemView) {
            super(itemView);

            myReferName = (TextView) itemView.findViewById(R.id.my_grievances_dept_tv);
            myReferCompanyName = (TextView) itemView.findViewById(R.id.my_grievances_desc_tv);
            myReferStatus = (TextView) itemView.findViewById(R.id.my_grievances_status_tv);
            referStatusColorTv = itemView.findViewById(R.id.grievance_status_color_tv);
            myReferView = itemView;
        }
    }
}