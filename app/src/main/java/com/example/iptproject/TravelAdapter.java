package com.example.iptproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {

    private Context context;
    private List<Travel> travelList;

    public TravelAdapter(Context context, List<Travel> travelList) {
        this.context = context;
        this.travelList = travelList;
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
    }

    @Override
    public int getItemCount() {
        return travelList.size();
    }

    public static class TravelViewHolder extends RecyclerView.ViewHolder {
        TextView city, place;

        public TravelViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.tvCity);
            place = itemView.findViewById(R.id.tvPlace);
        }
    }
}
