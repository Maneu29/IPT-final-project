package com.example.iptproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private PendingTravelsViewModel pendingTravelsViewModel;
    private CompletedTravelsViewModel completedTravelsViewModel;
    private TextView totalTravelledTextView;
    private TextView pendingTravelsTextView;
    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        totalTravelledTextView = view.findViewById(R.id.TotalTravelled);
        pendingTravelsTextView = view.findViewById(R.id.PendingTravels);
        usernameTextView = view.findViewById(R.id.username);
        Button logoutButton = view.findViewById(R.id.Logoutbtn);

        // Set username from Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            usernameTextView.setText(user.getDisplayName());
        } else if (user != null && user.getEmail() != null) {
            usernameTextView.setText(user.getEmail());
        }

        pendingTravelsViewModel = new ViewModelProvider(requireActivity()).get(PendingTravelsViewModel.class);
        completedTravelsViewModel = new ViewModelProvider(requireActivity()).get(CompletedTravelsViewModel.class);

        completedTravelsViewModel.getCompletedTravels().observe(getViewLifecycleOwner(), travels -> {
            totalTravelledTextView.setText("TOTAL PLACES TRAVELED: " + travels.size());
        });

        pendingTravelsViewModel.getPendingTravels().observe(getViewLifecycleOwner(), travels -> {
            pendingTravelsTextView.setText("PENDING TRAVELS: " + travels.size());
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
