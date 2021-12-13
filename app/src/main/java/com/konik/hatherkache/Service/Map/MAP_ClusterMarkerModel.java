package com.konik.hatherkache.Service.Map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MAP_ClusterMarkerModel implements ClusterItem {
    //For Setting ICON on User Position
    //Video No 12
    private  LatLng position;
    private String title;
    private String snippet;
    private String avater;
    private int iconPicture;
    private String UserUID;


    public MAP_ClusterMarkerModel() {
    }

    public MAP_ClusterMarkerModel(LatLng position, String title, String snippet, String avater, int iconPicture, String userUID) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.avater = avater;
        this.iconPicture = iconPicture;
        UserUID = userUID;
    }

    public String getAvater() {
        return avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }
}
