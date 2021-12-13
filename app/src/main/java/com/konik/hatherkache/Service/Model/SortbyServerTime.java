package com.konik.hatherkache.Service.Model;

import java.util.Comparator;

//This class connected with PB_OrderList Class
public class SortbyServerTime implements Comparator<PB_OrderList_Model> {
    // Used for sorting in ascending order of
    // roll number
    public int compare(PB_OrderList_Model a, PB_OrderList_Model b) {
        long dlTime = a.diTime.getTime() - b.diTime.getTime();
        return (int) (dlTime);
    }
}
