package com.konik.hatherkache.Service.Model;

import java.util.ArrayList;
import java.util.List;

public class Level_E_Model {
        private String L5UID = "NA";
        private String L5Name = "NA";
        private String L5From = "NA";

        private List<String> L5PhotoUrl = new ArrayList<String>();
        private String L5Bio = "NA";
        private String L5Address = "NA";
        private String L5Search = "NA";

        private String L5Privacy = "NA";
        private String L5PaymentMode = "NA";
        private String L5UIDoCreator = "NA";
        private String L5UIDofL3 = "NA";
        private String L5UIDofL4 = "NA";
        private String L5UIDofL2 = "NA";
        private String L5BarKeyword = "NA";
        private String L5Extra = "NA";
        private String L5Phone = "NA";

        private int  L5iRating = 0;
        private int  L5iViews = 0;
        private int  L5iOrders = 0;
        private int  L5iQuantity = 0;
        private int  L5iQuantityLimit = 0;
        private int  L5iPrice = 0;
        private int  L5iPriceDiscount = 0;
        private int  L5iLoved = 0;
    //Bar Key, ExtraKey, Creator id, Department Uid, SubCategory UID Not added, PhotoURL Array
        private int  L5iPriority = 0;
        private long  L5iRegDate = 0;

    public Level_E_Model() {

    }

    public Level_E_Model(String l5UID, String l5Name, String l5From, List<String> l5PhotoUrl,
                         String l5Bio, String l5Address, String l5Search, String l5Privacy,
                         String l5PaymentMode, String l5UIDoCreator, String l5UIDofL3,

                         String l5UIDofL4, String l5UIDofL2, String l5BarKeyword,
                         String l5Extra, String l5Phone, int l5iRating, int l5iViews,
                         int l5iOrders, int l5iQuantity, int l5iQuantityLimit, int l5iPrice,

                         int l5iPriceDiscount, int l5iLoved, int l5iPriority, long l5iRegDate) {
        L5UID = l5UID;
        L5Name = l5Name;
        L5From = l5From;
        L5PhotoUrl = l5PhotoUrl;
        L5Bio = l5Bio;
        L5Address = l5Address;
        L5Search = l5Search;
        L5Privacy = l5Privacy;
        L5PaymentMode = l5PaymentMode;
        L5UIDoCreator = l5UIDoCreator;
        L5UIDofL3 = l5UIDofL3;
        L5UIDofL4 = l5UIDofL4;
        L5UIDofL2 = l5UIDofL2;
        L5BarKeyword = l5BarKeyword;
        L5Extra = l5Extra;
        L5Phone = l5Phone;
        L5iRating = l5iRating;
        L5iViews = l5iViews;
        L5iOrders = l5iOrders;
        L5iQuantity = l5iQuantity;
        L5iQuantityLimit = l5iQuantityLimit;
        L5iPrice = l5iPrice;
        L5iPriceDiscount = l5iPriceDiscount;
        L5iLoved = l5iLoved;
        L5iPriority = l5iPriority;
        L5iRegDate = l5iRegDate;
    }

    public void setL5UID(String l5UID) {
        L5UID = l5UID;
    }

    public String getL5UID() {
        return L5UID;
    }

    public String getL5Name() {
        return L5Name;
    }

    public String getL5From() {
        return L5From;
    }

    public List<String> getL5PhotoUrl() {
        return L5PhotoUrl;
    }

    public String getL5Bio() {
        return L5Bio;
    }

    public String getL5Address() {
        return L5Address;
    }

    public String getL5Search() {
        return L5Search;
    }

    public String getL5Privacy() {
        return L5Privacy;
    }

    public String getL5PaymentMode() {
        return L5PaymentMode;
    }

    public String getL5UIDoCreator() {
        return L5UIDoCreator;
    }

    public String getL5UIDofL3() {
        return L5UIDofL3;
    }

    public String getL5UIDofL4() {
        return L5UIDofL4;
    }

    public String getL5UIDofL2() {
        return L5UIDofL2;
    }

    public String getL5BarKeyword() {
        return L5BarKeyword;
    }

    public String getL5Extra() {
        return L5Extra;
    }

    public String getL5Phone() {
        return L5Phone;
    }

    public int getL5iRating() {
        return L5iRating;
    }

    public int getL5iViews() {
        return L5iViews;
    }

    public int getL5iOrders() {
        return L5iOrders;
    }

    public int getL5iQuantity() {
        return L5iQuantity;
    }

    public int getL5iQuantityLimit() {
        return L5iQuantityLimit;
    }

    public int getL5iPrice() {
        return L5iPrice;
    }

    public int getL5iPriceDiscount() {
        return L5iPriceDiscount;
    }

    public int getL5iLoved() {
        return L5iLoved;
    }

    public int getL5iPriority() {
        return L5iPriority;
    }

    public long getL5iRegDate() {
        return L5iRegDate;
    }
}
