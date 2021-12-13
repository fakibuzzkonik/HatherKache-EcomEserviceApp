package com.konik.hatherkache.Service.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PB_Cart_Model {
    
    private String ProductUID = "NA";
    private String ProductName = "NA";
    private String ProductPHOTO = "NA";
    private String ProductCreatorUID = "NA";
    private String Level2UID = "NA";
    private String Level3UID = "NA";

    private int ProductPrice = 0;
    private int  ProductQunatity = 0;
    private @ServerTimestamp Date ProductTime;


    public PB_Cart_Model() {
    }


    public PB_Cart_Model(String productUID, String productName, String productPHOTO, String productCreatorUID, String level2UID, String level3UID, int productPrice, int productQunatity, Date productTime) {
        ProductUID = productUID;
        ProductName = productName;
        ProductPHOTO = productPHOTO;
        ProductCreatorUID = productCreatorUID;
        Level2UID = level2UID;
        Level3UID = level3UID;
        ProductPrice = productPrice;
        ProductQunatity = productQunatity;
        ProductTime = productTime;
    }

    public String getLevel2UID() {
        return Level2UID;
    }

    public Date getProductTime() {
        return ProductTime;
    }

    public String getProductUID() {
        return ProductUID;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getProductPHOTO() {
        return ProductPHOTO;
    }

    public String getProductCreatorUID() {
        return ProductCreatorUID;
    }

    public String getLevel3UID() {
        return Level3UID;
    }

    public int getProductPrice() {
        return ProductPrice;
    }

    public int getProductQunatity() {
        return ProductQunatity;
    }

    public void setProductUID(String productUID) {
        ProductUID = productUID;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setProductPHOTO(String productPHOTO) {
        ProductPHOTO = productPHOTO;
    }

    public void setProductCreatorUID(String productCreatorUID) {
        ProductCreatorUID = productCreatorUID;
    }

    public void setLevel2UID(String level2UID) {
        Level2UID = level2UID;
    }

    public void setLevel3UID(String level3UID) {
        Level3UID = level3UID;
    }

    public void setProductPrice(int productPrice) {
        ProductPrice = productPrice;
    }

    public void setProductQunatity(int productQunatity) {
        ProductQunatity = productQunatity;
    }

    public void setProductTime(Date productTime) {
        ProductTime = productTime;
    }
}
