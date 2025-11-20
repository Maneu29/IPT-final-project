package com.example.iptproject;

public class Travel {
    private String id; // To store the Firebase push ID
    private String city;
    private String place;
    private String photoUri;
    private String caption;

    // No-argument constructor required for Firebase
    public Travel() {}

    public Travel(String city, String place) {
        this.city = city;
        this.place = place;
    }

    // Getters
    public String getId() { return id; }
    public String getCity() { return city; }
    public String getPlace() { return place; }
    public String getPhotoUri() { return photoUri; }
    public String getCaption() { return caption; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCity(String city) { this.city = city; }
    public void setPlace(String place) { this.place = place; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public void setCaption(String caption) { this.caption = caption; }
}
