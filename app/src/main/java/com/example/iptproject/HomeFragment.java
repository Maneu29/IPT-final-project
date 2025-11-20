package com.example.iptproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment implements TravelAdapter.OnItemClickListener {

    private RecyclerView recyclerViewTravels;
    private TravelAdapter adapter;
    private PendingTravelsViewModel pendingTravelsViewModel;
    private CompletedTravelsViewModel completedTravelsViewModel;
    private Dialog mMarkAsDoneDialog;
    private ImageView profileImageView;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewTravels = view.findViewById(R.id.recyclerViewTravels);
        recyclerViewTravels.setLayoutManager(new LinearLayoutManager(getContext()));

        pendingTravelsViewModel = new ViewModelProvider(requireActivity()).get(PendingTravelsViewModel.class);
        completedTravelsViewModel = new ViewModelProvider(requireActivity()).get(CompletedTravelsViewModel.class);
        profileImageView = view.findViewById(R.id.profileImage);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                profileImageView.setImageURI(uri);
            }
        });

        pendingTravelsViewModel.getPendingTravels().observe(getViewLifecycleOwner(), travels -> {
            adapter = new TravelAdapter(getContext(), travels, this);
            recyclerViewTravels.setAdapter(adapter);
        });

        ImageView fabAdd = view.findViewById(R.id.add);
        fabAdd.setOnClickListener(v -> showAddTravelDialog());

        TextView usernameTextView = view.findViewById(R.id.username);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            usernameTextView.setText(user.getDisplayName());
        } else if (user != null && user.getEmail() != null) {
            usernameTextView.setText(user.getEmail());
        }

        return view;
    }

    private void showAddTravelDialog() {
        showTravelDialog(null);
    }

    @Override
    public void onEditClick(int position) {
        Travel travelToEdit = pendingTravelsViewModel.getPendingTravels().getValue().get(position);
        showTravelDialog(travelToEdit);
    }

    @Override
    public void onDeleteClick(int position) {
        Travel travelToDelete = pendingTravelsViewModel.getPendingTravels().getValue().get(position);
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Destination")
                .setMessage("Are you sure you want to delete " + travelToDelete.getCity() + ", " + travelToDelete.getPlace() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    pendingTravelsViewModel.removeTravel(travelToDelete);
                    Toast.makeText(getContext(), travelToDelete.getCity() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showTravelDialog(Travel existingTravel) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_travel);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText etCity = dialog.findViewById(R.id.etCity);
        EditText etPlace = dialog.findViewById(R.id.etPlace);
        Button btnDone = dialog.findViewById(R.id.btnDone);
        ImageButton btnClose = dialog.findViewById(R.id.btnCloseDialog);

        boolean isEdit = (existingTravel != null);

        if (isEdit) {
            etCity.setText(existingTravel.getCity());
            etPlace.setText(existingTravel.getPlace());
        }

        btnDone.setOnClickListener(v -> {
            String city = etCity.getText().toString().trim().toUpperCase();
            String place = etPlace.getText().toString().trim();

            if (city.isEmpty() || place.isEmpty()) {
                Toast.makeText(getContext(), "Please fill both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit) {
                existingTravel.setCity(city);
                existingTravel.setPlace(place);
                pendingTravelsViewModel.updateTravel(existingTravel);
                Toast.makeText(getContext(), "Updated!", Toast.LENGTH_SHORT).show();
            } else {
                pendingTravelsViewModel.addTravel(new Travel(city, place));
                Toast.makeText(getContext(), "Added " + city, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onDoneClick(int position) {
        showMarkAsDoneDialog(pendingTravelsViewModel.getPendingTravels().getValue().get(position));
    }

    private void showMarkAsDoneDialog(Travel travel) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_mark_done);
        mMarkAsDoneDialog = dialog;
        dialog.setOnDismissListener(d -> mMarkAsDoneDialog = null);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvCityName = dialog.findViewById(R.id.tvCityName);
        ImageView imgPhoto = dialog.findViewById(R.id.imgAttachedPhoto);
        EditText etCaption = dialog.findViewById(R.id.etCaption);
        TextView tvAttach = dialog.findViewById(R.id.tvAttachText);
        Button btnDone = dialog.findViewById(R.id.btnFinalDone);
        ImageButton btnClose = dialog.findViewById(R.id.btnCloseDialog);

        tvCityName.setText(travel.getCity() + "\n" + travel.getPlace().toUpperCase());

        tvAttach.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        imgPhoto.setOnClickListener(v -> tvAttach.performClick());

        btnDone.setOnClickListener(v -> {
            String caption = etCaption.getText().toString().trim();
            String photoUri = (String) imgPhoto.getTag();

            if (photoUri == null) {
                Toast.makeText(getContext(), "Please attach a photo", Toast.LENGTH_SHORT).show();
                return;
            }

            travel.setPhotoUri(photoUri);
            travel.setCaption(caption.isEmpty() ? "It was an amazing experience!" : caption);

            pendingTravelsViewModel.moveTravelToCompleted(travel, completedTravelsViewModel);

            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavigationView);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.history);
            }

            Toast.makeText(getContext(), travel.getCity() + " marked as done!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Dialog dialog = mMarkAsDoneDialog;
            if (dialog != null) {
                ImageView img = dialog.findViewById(R.id.imgAttachedPhoto);
                if (img != null) {
                    img.setImageURI(uri);
                    img.setTag(uri.toString());
                    TextView tvAttach = dialog.findViewById(R.id.tvAttachText);
                    if (tvAttach != null) {
                        tvAttach.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
