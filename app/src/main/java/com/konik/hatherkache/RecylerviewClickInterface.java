package com.konik.hatherkache;

public interface RecylerviewClickInterface {
    void onItemClick(int position);
    void onItemLongCLick(int postion);
    void onItemClickAuthorID(int position, String AuthorUID);
    //implement to main class. add two method
    //declared recylerviewClickInterface on Adapter Class
    //add this argument to constructor
    //
    //
}
