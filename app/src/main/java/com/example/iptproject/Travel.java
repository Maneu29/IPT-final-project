// Travel.java
package com.example.iptproject;

public class Travel {
    private String city;
    private String place;
    private String photoUri;      // null if not completed yet
    private String caption;       // null if not completed

    public Travel(String city, String place) {
        this.city = city;
        this.place = place;
    }

    public Travel(String city, String place, String photoUri, String caption) {
        this.city = city;
        this.place = place;
        this.photoUri = photoUri;
        this.caption = caption;
    }

    // Getters & Setters
    public String getCity() { return city; }
    public String getPlace() { return place; }
    public String getPhotoUri() { return photoUri; }
    public String getCaption() { return caption; }
    public boolean isCompleted() { return photoUri != null; }

    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public void setCaption(String caption) { this.caption = caption; }
}