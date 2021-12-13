package com.konik.hatherkache.Service.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MAP_UserLocationModel implements Parcelable {
    private GeoPoint geoPoint;
    private @ServerTimestamp Date timestamp;
    private String uid = "NA";
    private String avater = "123";
    private int view = 0;
    private String name = "NA";
    private String address = "NA";

    public MAP_UserLocationModel() {
    }

    public MAP_UserLocationModel(GeoPoint geoPoint, Date timestamp, String uid, String avater, int view, String name, String address) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.uid = uid;
        this.avater = avater;
        this.view = view;
        this.name = name;
        this.address = address;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public void setView(int view) {
        this.view = view;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    public String getAvater() {
        return avater;
    }

    public int getView() {
        return view;
    }

    /////PARCLEABLE Implementaion Auto
    protected MAP_UserLocationModel(Parcel in) {
        uid = in.readString();
        avater = in.readString();
        view = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(avater);
        dest.writeInt(view);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MAP_UserLocationModel> CREATOR = new Creator<MAP_UserLocationModel>() {
        @Override
        public MAP_UserLocationModel createFromParcel(Parcel in) {
            return new MAP_UserLocationModel(in);
        }

        @Override
        public MAP_UserLocationModel[] newArray(int size) {
            return new MAP_UserLocationModel[size];
        }
    };

}
