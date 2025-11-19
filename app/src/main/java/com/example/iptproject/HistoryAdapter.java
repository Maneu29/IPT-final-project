package com.example.iptproject;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Travel> travels;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
    }

    public HistoryAdapter(List<Travel> travels, OnItemClickListener listener) {
        this.travels = travels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Travel travel = travels.get(position);
        holder.tvCity.setText(travel.getCity());
        holder.tvPlace.setText(travel.getPlace());
        holder.tvCaption.setText(travel.getCaption());

        if (travel.getPhotoUri() != null) {
            holder.imgPhoto.setImageURI(Uri.parse(travel.getPhotoUri()));
        }

        if (listener != null) {
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(holder.getAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return travels.size();
    }

    public void setTravels(List<Travel> travels) {
        this.travels = travels;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvCity, tvPlace, tvCaption;
        ImageButton btnEdit;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgHistoryPhoto);
            tvCity = itemView.findViewById(R.id.tvHistoryCity);
            tvPlace = itemView.findViewById(R.id.tvHistoryPlace);
            tvCaption = itemView.findViewById(R.id.tvHistoryCaption);
            btnEdit = itemView.findViewById(R.id.btnEdit); // Ensure this ID is in item_history.xml
        }
    }
}
