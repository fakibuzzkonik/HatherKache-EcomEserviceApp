package com.konik.hatherkache.Service.Model;

public class Level_C_Model {
     private String L3UID;
     private String L3Name;
     private String L3PhotoUrl;
     private String L3Bio;
     private String L3Creator;
     private int L3iPrivacy;
     private int  L3iPriority;
     private int  L3iViewCount;
     private int  L3iTotalProducts;



    public Level_C_Model(String l3Uid, String l3Name, String l3PhotoUrl, String l3Bio, String l3Creator, int l3iPrivacy, int l3iPriority, int l3iViewCount, int l3iTotalProducts) {
        L3UID = l3Uid;
        L3Name = l3Name;
        L3PhotoUrl = l3PhotoUrl;
        L3Bio = l3Bio;
        L3Creator = l3Creator;
        L3iPrivacy = l3iPrivacy;
        L3iPriority = l3iPriority;
        L3iViewCount = l3iViewCount;
        L3iTotalProducts = l3iTotalProducts;
    }

    public Level_C_Model() {
    }
    public String getL3UID() {
        return L3UID;
    }
    public String getL3Name() {
        return L3Name;
    }

    public String getL3PhotoUrl() {
        return L3PhotoUrl;
    }

    public String getL3Bio() {
        return L3Bio;
    }

    public String getL3Creator() {
        return L3Creator;
    }

    public int getL3iPrivacy() {
        return L3iPrivacy;
    }

    public int getL3iPriority() {
        return L3iPriority;
    }

    public int getL3iViewCount() {
        return L3iViewCount;
    }

    public int getL3iTotalProducts() {
        return L3iTotalProducts;
    }
}
