package com.example.iptproject;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CompletedTravelsViewModel extends ViewModel {

    private static final String TAG = "CompletedTravelsVM";
    private final MutableLiveData<List<Travel>> completedTravels = new MutableLiveData<>(new ArrayList<>());
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    public CompletedTravelsViewModel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            // Point to the 'completed_travels' node under the specific user's ID
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("completed_travels");
            attachDatabaseReadListener();
        } else {
            Log.e(TAG, "User is not logged in. Cannot fetch data.");
        }
    }

    public LiveData<List<Travel>> getCompletedTravels() {
        return completedTravels;
    }

    private void attachDatabaseReadListener() {
        if (eventListener == null) {
            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Travel> travels = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Travel travel = snapshot.getValue(Travel.class);
                        if (travel != null) {
                            travel.setId(snapshot.getKey()); // Store the Firebase key
                            travels.add(travel);
                        }
                    }
                    completedTravels.setValue(travels);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            };
            databaseReference.addValueEventListener(eventListener);
        }
    }

    public void addCompletedTravel(Travel travel) {
        if (databaseReference != null) {
            String travelId = databaseReference.push().getKey();
            if (travelId != null) {
                databaseReference.child(travelId).setValue(travel);
            }
        }
    }

    public void updateCompletedTravel(Travel travel) {
        if (databaseReference != null && travel.getId() != null) {
            databaseReference.child(travel.getId()).setValue(travel);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (databaseReference != null && eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}
