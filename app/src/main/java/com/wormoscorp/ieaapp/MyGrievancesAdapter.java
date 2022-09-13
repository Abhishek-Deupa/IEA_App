package com.wormoscorp.ieaapp;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MyGrievancesAdapter extends FirebaseRecyclerAdapter<MyGrievanceModel, MyGrievancesAdapter.MyGrievanceViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MyGrievancesAdapter(@NonNull FirebaseRecyclerOptions<MyGrievanceModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyGrievanceViewHolder holder, int position, @NonNull MyGrievanceModel model) {
        holder.myGrievanceDept.setText(model.getDepartment());
        holder.myGrievanceDesc.setText(model.getComplain());
        String grievanceStatus = String.valueOf(model.getStatus());
        holder.myGrievanceStatus.setText(grievanceStatus);

        switch (model.getStatus()){
            case "Unsolved":
                holder.grievanceStatusColorTv.setBackgroundColor(Color.parseColor("#000000"));
                break;
            case "On Progress":
                holder.grievanceStatusColorTv.setBackgroundColor(Color.parseColor("#ED944D"));
                break;
            case "Solved":
                holder.grievanceStatusColorTv.setBackgroundColor(Color.parseColor("#48A14D"));
                break;
            case "Rejected":
                holder.grievanceStatusColorTv.setBackgroundColor(Color.parseColor("#96271f"));
                break;
            case "Under Review":
                holder.grievanceStatusColorTv.setBackgroundColor(Color.parseColor("#FEFF9E"));
                break;
        }

        holder.myGrievanceView.setOnClickListener(view -> openGrievanceDetails(position, view, grievanceStatus));
    }

    private void openGrievanceDetails(int position, View view, String status) {
        Intent intent = new Intent(view.getContext(), GrievanceDetails.class);
        intent.putExtra("status", status);
        intent.putExtra("grievanceKey", getRef(position).getKey());
        view.getContext().startActivity(intent);
    }


    @NonNull
    @Override
    public MyGrievanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_grievance_item, parent,false);
        return new MyGrievanceViewHolder(view);
    }

    class MyGrievanceViewHolder extends RecyclerView.ViewHolder{

        View myGrievanceView;
        TextView myGrievanceDept, myGrievanceDesc, myGrievanceStatus, grievanceStatusColorTv;

        public MyGrievanceViewHolder(@NonNull View itemView) {
            super(itemView);

            myGrievanceDept = itemView.findViewById(R.id.my_grievances_dept_tv);
            myGrievanceDesc = itemView.findViewById(R.id.my_grievances_desc_tv);
            myGrievanceStatus = itemView.findViewById(R.id.my_grievances_status_tv);
            grievanceStatusColorTv = itemView.findViewById(R.id.grievance_status_color_tv);
            myGrievanceView = itemView;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}