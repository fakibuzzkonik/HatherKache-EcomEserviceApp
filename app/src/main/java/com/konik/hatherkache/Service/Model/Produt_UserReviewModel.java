package com.konik.hatherkache.Service.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Produt_UserReviewModel {
    private @ServerTimestamp Date  user_time = null;
    private String user_uid = "NO";
    private String user_review  = "NO";
    private Double user_rating  = 0.5;
    private String admin_reply  = "NO";
    private String admin_id = "NO";

    public Produt_UserReviewModel() {
    }

    public Produt_UserReviewModel(Date user_time, String user_uid, String user_review, Double user_rating, String admin_reply, String admin_id) {
        this.user_time = user_time;
        this.user_uid = user_uid;
        this.user_review = user_review;
        this.user_rating = user_rating;
        this.admin_reply = admin_reply;
        this.admin_id = admin_id;
    }

    public Date getUser_time() {
        return user_time;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public String getUser_review() {
        return user_review;
    }

    public Double getUser_rating() {
        return user_rating;
    }

    public String getAdmin_reply() {
        return admin_reply;
    }

    public String getAdmin_id() {
        return admin_id;
    }


}

