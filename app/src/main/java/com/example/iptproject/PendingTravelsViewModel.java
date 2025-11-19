package com.example.iptproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class PendingTravelsViewModel extends ViewModel {
    private final MutableLiveData<List<Travel>> pendingTravels = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Travel>> getPendingTravels() {
        return pendingTravels;
    }

    public void addTravel(Travel travel) {
        List<Travel> currentList = pendingTravels.getValue();
        if (currentList != null) {
            currentList.add(travel);
            pendingTravels.setValue(currentList);
        }
    }

    public void removeTravel(int position) {
        List<Travel> currentList = pendingTravels.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.remove(position);
            pendingTravels.setValue(currentList);
        }
    }

    public void updateTravel(int position, Travel travel) {
        List<Travel> currentList = pendingTravels.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, travel);
            pendingTravels.setValue(currentList);
        }
    }

    // Method to initialize with some data
    public void initializeData() {
        if (pendingTravels.getValue().isEmpty()) {
            List<Travel> initialList = new ArrayList<>();
            initialList.add(new Travel("PARIS", "France"));
            initialList.add(new Travel("TOKYO", "Japan"));
            initialList.add(new Travel("SYDNEY", "Australia"));
            initialList.add(new Travel("BALI", "Indonesia"));
            pendingTravels.setValue(initialList);
        }
    }
}
