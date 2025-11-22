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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView totalTravelledTextView;
    private TextView pendingTravelsTextView;
    private TextView usernameTextView;
    private ImageView profileImageView;
    private SharedViewModel sharedViewModel;
    private Uri newImageUri = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        totalTravelledTextView = view.findViewById(R.id.TotalTravelled);
        pendingTravelsTextView = view.findViewById(R.id.PendingTravels);
        usernameTextView = view.findViewById(R.id.Username);
        profileImageView = view.findViewById(R.id.profileImage);
        Button logoutButton = view.findViewById(R.id.Logoutbtn);
        FloatingActionButton fabEditProfile = view.findViewById(R.id.fabEditProfile);

        updateUsername();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                profileImageView.setImageURI(uri);
            }
        });

        new ViewModelProvider(requireActivity()).get(CompletedTravelsViewModel.class).getCompletedTravels().observe(getViewLifecycleOwner(), travels -> {
            totalTravelledTextView.setText("TOTAL PLACES TRAVELED: " + travels.size());
        });

        new ViewModelProvider(requireActivity()).get(PendingTravelsViewModel.class).getPendingTravels().observe(getViewLifecycleOwner(), travels -> {
            pendingTravelsTextView.setText("PENDING TRAVELS: " + travels.size());
        });

        fabEditProfile.setOnClickListener(v -> showEditProfileDialog());

        logoutButton.setOnClickListener(v -> {
             Drawable icon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_dialog_alert);
            if (icon != null) {
                DrawableCompat.setTint(icon.mutate(), Color.RED);
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(icon)
                    .show();
        });

        return view;
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        final ImageView editProfileImage = dialogView.findViewById(R.id.editProfileImage);
        final EditText editUsername = dialogView.findViewById(R.id.editUsername);
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            editUsername.setText(user.getDisplayName());
            sharedViewModel.getSelectedImageUri().observe(getViewLifecycleOwner(), uri -> {
                if (uri != null) {
                    editProfileImage.setImageURI(uri);
                }
            });
        }

        editProfileImage.setOnClickListener(v -> openImageChooser());

        builder.setTitle("Edit Profile")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newUsername = editUsername.getText().toString().trim();
                    updateUserProfile(newUsername, newImageUri);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
    
    private void updateUserProfile(String newUsername, Uri newImageUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder();
        if (!newUsername.isEmpty()) {
            profileUpdatesBuilder.setDisplayName(newUsername);
        }
        if (newImageUri != null) {
            sharedViewModel.setSelectedImageUri(newImageUri);
        }
        
        UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                updateUsername(); // Refresh the username display
            }
        });
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
            newImageUri = data.getData();
        }
    }

    private void updateUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.trim().isEmpty()) {
                usernameTextView.setText(displayName);
            } else if (user.getEmail() != null) {
                String email = user.getEmail();
                usernameTextView.setText(email.split("@")[0]);
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
        updateUsername();
    }
}
