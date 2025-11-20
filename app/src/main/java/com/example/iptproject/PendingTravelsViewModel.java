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

public class PendingTravelsViewModel extends ViewModel {

    private static final String TAG = "PendingTravelsVM";
    private final MutableLiveData<List<Travel>> pendingTravels = new MutableLiveData<>(new ArrayList<>());
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    public PendingTravelsViewModel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            // Point to the 'pending_travels' node under the specific user's ID
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("pending_travels");
            attachDatabaseReadListener();
        } else {
            Log.e(TAG, "User is not logged in. Cannot fetch data.");
        }
    }

    public LiveData<List<Travel>> getPendingTravels() {
        return pendingTravels;
    }

    // Attaches a listener that reads data from Firebase and updates the LiveData
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
                    pendingTravels.setValue(travels);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            };
            databaseReference.addValueEventListener(eventListener);
        }
    }

    // Pushes a new travel item to the database
    public void addTravel(Travel travel) {
        if (databaseReference != null) {
            String travelId = databaseReference.push().getKey();
            if (travelId != null) {
                databaseReference.child(travelId).setValue(travel);
            }
        }
    }

    // Removes a travel item from the database using its unique ID
    public void removeTravel(Travel travel) {
        if (databaseReference != null && travel.getId() != null) {
            databaseReference.child(travel.getId()).removeValue();
        }
    }

    // Updates a travel item in the database using its unique ID
    public void updateTravel(Travel travel) {
        if (databaseReference != null && travel.getId() != null) {
            databaseReference.child(travel.getId()).setValue(travel);
        }
    }
    
    // Moves a travel from the pending list to the completed list in the database
    public void moveTravelToCompleted(Travel travel, CompletedTravelsViewModel completedViewModel) {
        if (databaseReference != null && travel.getId() != null) {
            // Add to the completed ViewModel, which will push it to the completed node in Firebase
            completedViewModel.addCompletedTravel(travel);
            // Remove from the pending database node
            removeTravel(travel);
        }
    }

    // Clean up the listener when the ViewModel is destroyed
    @Override
    protected void onCleared() {
        super.onCleared();
        if (databaseReference != null && eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        }
    }
}
