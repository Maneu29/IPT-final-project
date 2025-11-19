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

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private CompletedTravelsViewModel viewModel;
    private List<Travel> completedTravels = new ArrayList<>(); // Keep a local copy
    private Dialog mCurrentDialog; // To hold a reference to the currently open dialog
    private int mEditingPosition = -1; // To know which item is being edited

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pass this fragment as listener
        historyAdapter = new HistoryAdapter(completedTravels, this);
        historyRecyclerView.setAdapter(historyAdapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CompletedTravelsViewModel.class);

        viewModel.getCompletedTravels().observe(getViewLifecycleOwner(), travels -> {
            completedTravels.clear();
            completedTravels.addAll(travels);
            historyAdapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onEditClick(int position) {
        mEditingPosition = position;
        Travel travelToEdit = completedTravels.get(position);

        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_mark_done);
        mCurrentDialog = dialog;
        dialog.setOnDismissListener(d -> {
            mCurrentDialog = null;
            mEditingPosition = -1;
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.TOP);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvCityName = dialog.findViewById(R.id.tvCityName);
        ImageView imgPhoto = dialog.findViewById(R.id.imgAttachedPhoto);
        EditText etCaption = dialog.findViewById(R.id.etCaption);
        TextView tvAttach = dialog.findViewById(R.id.tvAttachText);
        Button btnDone = dialog.findViewById(R.id.btnFinalDone);
        ImageButton btnClose = dialog.findViewById(R.id.btnCloseDialog);

        // --- Pre-fill the dialog with existing data ---
        if (tvCityName != null) {
            String city = travelToEdit.getCity();
            String place = travelToEdit.getPlace().toUpperCase();
            String fullText = city + "\n" + place;

            SpannableString spannableString = new SpannableString(fullText);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, city.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.8f), city.length() + 1, fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvCityName.setText(spannableString);
        }

        if (travelToEdit.getPhotoUri() != null) {
            imgPhoto.setImageURI(Uri.parse(travelToEdit.getPhotoUri()));
            tvAttach.setVisibility(View.GONE); // Hide "Attach" text if there's already a photo
        }
        imgPhoto.setTag(travelToEdit.getPhotoUri()); // Set the initial URI

        etCaption.setText(travelToEdit.getCaption());
        btnDone.setText("Save"); // Change button text to indicate an edit

        // --- Handle Clicks ---
        View.OnClickListener attachListener = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 200); // Use a different request code for editing
        };
        tvAttach.setOnClickListener(attachListener);
        imgPhoto.setOnClickListener(attachListener);

        btnDone.setOnClickListener(v -> {
            String newCaption = etCaption.getText().toString().trim();
            String newPhotoUri = (String) imgPhoto.getTag();

            viewModel.updateCompletedTravel(mEditingPosition, newCaption, newPhotoUri);
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
            if (requestCode == 200) { // Check for the edit request code
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
