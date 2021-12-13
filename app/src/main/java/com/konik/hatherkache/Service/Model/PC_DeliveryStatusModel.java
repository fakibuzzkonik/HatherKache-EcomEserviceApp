package com.konik.hatherkache.Service.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PC_DeliveryStatusModel {


    public  @ServerTimestamp Date ddDate = new Date();;

    String dsStatusUID = "NA";
    String dsNote = "NA";
    String dsStatus = "NA";

    public PC_DeliveryStatusModel() {

    }

    public void setDsStatusUID(String dsStatusUID) {
        this.dsStatusUID = dsStatusUID;
    }

    public PC_DeliveryStatusModel(Date ddDate,  String dsStatusUID, String dsNote, String dsStatus) {
        this.ddDate = ddDate;

        this.dsStatusUID = dsStatusUID;
        this.dsNote = dsNote;
        this.dsStatus = dsStatus;
    }

    public Date getDdDate() {
        return ddDate;
    }

    public String getDsStatusUID() {
        return dsStatusUID;
    }

    public String getDsNote() {
        return dsNote;
    }

    public String getDsStatus() {
        return dsStatus;
    }

}
