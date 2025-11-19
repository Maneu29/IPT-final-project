package com.example.iptproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class CompletedTravelsViewModel extends ViewModel {
    private final MutableLiveData<List<Travel>> completedTravels = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Travel>> getCompletedTravels() {
        return completedTravels;
    }

    public void addCompletedTravel(Travel travel) {
        List<Travel> currentList = completedTravels.getValue();
        if (currentList != null) {
            currentList.add(0, travel); // Add to the top of the list
            completedTravels.setValue(currentList);
        }
    }

    public void updateCompletedTravel(int position, String newCaption, String newPhotoUri) {
        List<Travel> currentList = completedTravels.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            Travel travel = currentList.get(position);
            travel.setCaption(newCaption);
            if (newPhotoUri != null) { // Only update photo if a new one is provided
                travel.setPhotoUri(newPhotoUri);
            }
            completedTravels.setValue(currentList); // This will trigger the observer
        }
    }
}
