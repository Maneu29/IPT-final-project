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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private CompletedTravelsViewModel viewModel;
    private List<Travel> completedTravels = new ArrayList<>(); // Keep a local copy
    private Dialog mCurrentDialog;
    private SharedViewModel sharedViewModel;
    private ImageView profileImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profileImageView = view.findViewById(R.id.profileImage);

        historyAdapter = new HistoryAdapter(completedTravels, this);
        historyRecyclerView.setAdapter(historyAdapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CompletedTravelsViewModel.class);
        viewModel.getCompletedTravels().observe(getViewLifecycleOwner(), travels -> {
            completedTravels.clear();
            completedTravels.addAll(travels);
            historyAdapter.notifyDataSetChanged();
        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                profileImageView.setImageURI(uri);
            }
        });

        updateUsername(view);

        return view;
    }

    private void updateUsername(View view) {
        TextView usernameTextView = view.findViewById(R.id.Username);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.trim().isEmpty()) {
                String firstName = displayName.trim().split(" ")[0];
                usernameTextView.setText(firstName);
            } else if (user.getEmail() != null) {
                usernameTextView.setText(user.getEmail().split("@")[0]);
            } else {
                usernameTextView.setText("User");
            }
        } else {
            usernameTextView.setText("Guest");
        }
    }

    @Override
    public void onEditClick(int position) {
        Travel travelToEdit = completedTravels.get(position);

        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_mark_done);
        mCurrentDialog = dialog;
        dialog.setOnDismissListener(d -> mCurrentDialog = null);

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

        tvCityName.setText(travelToEdit.getCity() + "\n" + travelToEdit.getPlace().toUpperCase());

        if (travelToEdit.getPhotoUri() != null) {
            imgPhoto.setImageURI(Uri.parse(travelToEdit.getPhotoUri()));
            tvAttach.setVisibility(View.GONE);
        }
        imgPhoto.setTag(travelToEdit.getPhotoUri());

        etCaption.setText(travelToEdit.getCaption());
        btnDone.setText("Save");

        View.OnClickListener attachListener = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 200);
        };
        tvAttach.setOnClickListener(attachListener);
        imgPhoto.setOnClickListener(attachListener);

        btnDone.setOnClickListener(v -> {
            String newCaption = etCaption.getText().toString().trim();
            String newPhotoUri = (String) imgPhoto.getTag();

            travelToEdit.setCaption(newCaption);
            travelToEdit.setPhotoUri(newPhotoUri);

            viewModel.updateCompletedTravel(travelToEdit);
            dialog.dismiss();
            Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 200) {
                Uri uri = data.getData();
                if (mCurrentDialog != null) {
                    ImageView img = mCurrentDialog.findViewById(R.id.imgAttachedPhoto);
                    TextView tvAttach = mCurrentDialog.findViewById(R.id.tvAttachText);
                    if (img != null) {
                        img.setImageURI(uri);
                        img.setTag(uri.toString());
                        if (tvAttach != null) {
                            tvAttach.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }
}
