package com.konik.hatherkache.Service.Model;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class PB_OrderList_Model {

    public  @ServerTimestamp Date diTime = new Date();;
    private int diTotal_Money = 0;
    private String uid_order = "NO";//its not on server as document, it the path key
    private String uid_cart = "NO";
    private String uid_buyer = "NO";
    private String uid_rider = "NO";
    private String uid_master = "NO";
    private String uid_report = "NO";
    private String Level2UID = "NO";
    private String Level3UID = "NO";

    private String dsNote = "NO";
    private String dsDeliveryHour = "NO";
    private String dsPaymentStatus = "NO";
    private String dsCompleteLevel = "NO";
    private String dsExtra = "NO";

    public PB_OrderList_Model() {
    }

    public PB_OrderList_Model(Date diTime, int diTotal_Money, String uid_order, String uid_cart, String uid_buyer, String uid_rider, String uid_master, String uid_report, String level2UID, String level3UID, String dsNote, String dsDeliveryHour, String dsPaymentStatus, String dsCompleteLevel, String dsExtra) {
        this.diTime = diTime;
        this.diTotal_Money = diTotal_Money;
        this.uid_order = uid_order; //its not on server as document, it the path key
        this.uid_cart = uid_cart;
        this.uid_buyer = uid_buyer;
        this.uid_rider = uid_rider;
        this.uid_master = uid_master;
        this.uid_report = uid_report;
        Level2UID = level2UID;
        Level3UID = level3UID;
        this.dsNote = dsNote;
        this.dsDeliveryHour = dsDeliveryHour;
        this.dsPaymentStatus = dsPaymentStatus;
        this.dsCompleteLevel = dsCompleteLevel;
        this.dsExtra = dsExtra;
    }

    public Date getDiTime() {
        return diTime;
    }

    public int getDiTotal_Money() {
        return diTotal_Money;
    }

    public String getUid_order() {
        return uid_order;
    }

    public String getUid_cart() {
        return uid_cart;
    }

    public String getUid_buyer() {
        return uid_buyer;
    }

    public String getUid_rider() {
        return uid_rider;
    }

    public String getUid_master() {
        return uid_master;
    }

    public String getUid_report() {
        return uid_report;
    }

    public String getLevel2UID() {
        return Level2UID;
    }

    public String getLevel3UID() {
        return Level3UID;
    }

    public String getDsNote() {
        return dsNote;
    }

    public String getDsDeliveryHour() {
        return dsDeliveryHour;
    }

    public String getDsPaymentStatus() {
        return dsPaymentStatus;
    }

    public String getDsCompleteLevel() {
        return dsCompleteLevel;
    }

    public String getDsExtra() {
        return dsExtra;
    }
}
