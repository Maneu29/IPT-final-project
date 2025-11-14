package com.example.iptproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.iptproject.TravelAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerViewTravels;
    TravelAdapter adapter;
    List<Travel> travelList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewTravels = view.findViewById(R.id.recyclerViewTravels);
        recyclerViewTravels.setLayoutManager(new LinearLayoutManager(getContext()));

        // Example data
        travelList.add(new Travel("PARIS", "France"));
        travelList.add(new Travel("TOKYO", "Japan"));
        travelList.add(new Travel("SYDNEY", "Australia"));
        travelList.add(new Travel("BALI", "Indonesia"));

        adapter = new TravelAdapter(getContext(), travelList);
        recyclerViewTravels.setAdapter(adapter);

        FloatingActionButton fabAdd = view.findViewById(R.id.add);
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add travel clicked!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
