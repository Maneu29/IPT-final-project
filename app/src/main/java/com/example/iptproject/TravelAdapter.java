package com.example.iptproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {

    private Context context;
    private List<Travel> travelList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onDoneClick(int position);
    }

    public TravelAdapter(Context context, List<Travel> travelList, OnItemClickListener listener) {
        this.context = context;
        this.travelList = travelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_travel, parent, false);
        return new TravelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        Travel travel = travelList.get(position);
        holder.city.setText(travel.getCity());
        holder.place.setText(travel.getPlace());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(holder.getAdapterPosition()));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(holder.getAdapterPosition()));

        // Correctly handle the done button click
        holder.btnDone.setOnClickListener(v -> listener.onDoneClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return travelList.size();
    }

    public static class TravelViewHolder extends RecyclerView.ViewHolder {
        TextView city, place;
        ImageButton btnEdit, btnDelete, btnDone; // btnDone is now declared

        public TravelViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.tvCity);
            place = itemView.findViewById(R.id.tvPlace);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDone = itemView.findViewById(R.id.btnDone); // and initialized
        }
    }
}
