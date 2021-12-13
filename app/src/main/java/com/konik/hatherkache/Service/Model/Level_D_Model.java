package com.konik.hatherkache.Service.Model;

public class Level_D_Model {
    private String L4Uid;
    private String L4Name;
    private String L4PhotoUrl;
    private String L4Bio;
    private String L4Search;
    private String L4Creator;
    private String L4Extra;
    private int L4iPrivacy;
    private int L4iPriority;
    private int L4iViewCount;
    private int L4iTotalProducts;

    public Level_D_Model() {
    }

    public Level_D_Model(String l4Uid, String l4Name, String l4PhotoUrl, String l4Bio, String l4Search, String l4Creator, String l4Extra, int l4iPrivacy, int l4iPriority, int l4iViewCount, int l4iTotalProducts) {
        L4Uid = l4Uid;
        L4Name = l4Name;
        L4PhotoUrl = l4PhotoUrl;
        L4Bio = l4Bio;
        L4Search = l4Search;
        L4Creator = l4Creator;
        L4Extra = l4Extra;
        L4iPrivacy = l4iPrivacy;
        L4iPriority = l4iPriority;
        L4iViewCount = l4iViewCount;
        L4iTotalProducts = l4iTotalProducts;
    }

    public String getL4Uid() {
        return L4Uid;
    }

    public String getL4Name() {
        return L4Name;
    }

    public String getL4PhotoUrl() {
        return L4PhotoUrl;
    }

    public String getL4Bio() {
        return L4Bio;
    }

    public String getL4Search() {
        return L4Search;
    }

    public String getL4Creator() {
        return L4Creator;
    }

    public String getL4Extra() {
        return L4Extra;
    }

    public int getL4iPrivacy() {
        return L4iPrivacy;
    }

    public int getL4iPriority() {
        return L4iPriority;
    }

    public int getL4iViewCount() {
        return L4iViewCount;
    }

    public int getL4iTotalProducts() {
        return L4iTotalProducts;
    }
}
