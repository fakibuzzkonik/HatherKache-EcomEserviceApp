package com.konik.hatherkache;

public interface RecylerviewCartClickInterface {

    void onWholeItemClick(int position);
    void onDeleteItemClick(int position, int qunatity, int price);
    void onPlusBtnClick(int position, int qunatity, int price);
    void onMinusBtnClick(int position, int qunatity, int price, boolean fridge);
    //implement to main class. add two method
    //declared recylerviewClickInterface on Adapter Class
    //add this argument to constructor
    //
    //
}


