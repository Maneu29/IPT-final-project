package com.example.iptproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private PendingTravelsViewModel pendingTravelsViewModel;
    private CompletedTravelsViewModel completedTravelsViewModel;
    private TextView totalTravelledTextView;
    private TextView pendingTravelsTextView;
    private TextView usernameTextView;
    private ImageView profileImageView;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        totalTravelledTextView = view.findViewById(R.id.TotalTravelled);
        pendingTravelsTextView = view.findViewById(R.id.PendingTravels);
        usernameTextView = view.findViewById(R.id.Username);
        profileImageView = view.findViewById(R.id.profileImage);
        Button logoutButton = view.findViewById(R.id.Logoutbtn);

        updateUsername();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                profileImageView.setImageURI(uri);
            }
        });

        pendingTravelsViewModel = new ViewModelProvider(requireActivity()).get(PendingTravelsViewModel.class);
        completedTravelsViewModel = new ViewModelProvider(requireActivity()).get(CompletedTravelsViewModel.class);

        completedTravelsViewModel.getCompletedTravels().observe(getViewLifecycleOwner(), travels -> {
            totalTravelledTextView.setText("TOTAL PLACES TRAVELED: " + travels.size());
        });

        pendingTravelsViewModel.getPendingTravels().observe(getViewLifecycleOwner(), travels -> {
            pendingTravelsTextView.setText("PENDING TRAVELS: " + travels.size());
        });

        profileImageView.setOnClickListener(v -> openImageChooser());

        logoutButton.setOnClickListener(v -> {
            // Get the original drawable and apply a tint
            Drawable icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_dialog_alert);
            if (icon != null) {
                // Use mutate() to avoid affecting other instances of the same drawable
                DrawableCompat.setTint(icon.mutate(), Color.RED);
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        // Sign out the user
                        FirebaseAuth.getInstance().signOut();
                        // Redirect to login screen
                        Intent intent = new Intent(getActivity(), login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(icon) // Set the tinted drawable
                    .show();
        });

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            sharedViewModel.setSelectedImageUri(imageUri);
        }
    }

    // Helper method to safely update username
    private void updateUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.trim().isEmpty()) {
                // Optional: show only first name
                String firstName = displayName.trim().split(" ")[0];
                usernameTextView.setText(firstName);
            } else if (user.getEmail() != null) {
                // Fallback to email (or just the part before @)
                String email = user.getEmail();
                String nameFromEmail = email.split("@")[0];
                usernameTextView.setText(nameFromEmail);
            } else {
                usernameTextView.setText("Traveler");
            }
        } else {
            usernameTextView.setText("Guest");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUsername();  // Refresh in case name changed
    }
}
