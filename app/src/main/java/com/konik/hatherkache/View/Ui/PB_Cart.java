package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.konik.hatherkache.View.Adapter.PB_Cart_Adapter;
import com.konik.hatherkache.Service.Model.PB_Cart_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewCartClickInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PB_Cart extends AppCompatActivity implements RecylerviewCartClickInterface {

    private TextView mTotalText, mTotalTextCount, mDiscountText, mDiscountCount;
    private TextView mPhoneNoText, mAddressText;
    private Button mConfirmBtn;
    private ImageView mAddressEditImageView;

    private RecyclerView mCartRecyclerView;
    List<PB_Cart_Model> listCartItem;
    PB_Cart_Adapter pb_cart_adapter;


    private CollectionReference notebookRef ;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //FirebaseAUTH
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private DocumentReference mCartRef;
    private CollectionReference mCartOrderedref;
    //Variables
    private String dUserUID = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_b__cart);
        mTotalText = (TextView)findViewById(R.id.pb_cart_total_text);
        mTotalTextCount = (TextView)findViewById(R.id.pb_cart_total_text_count);
        mDiscountText = (TextView)findViewById(R.id.pb_cart_discount);
        mDiscountCount = (TextView)findViewById(R.id.pb_cart_discount_count);
        mPhoneNoText = (TextView)findViewById(R.id.pb_cart_user_phone_no);
        mAddressText = (TextView)findViewById(R.id.pb_cart_user_address);
        mAddressEditImageView = (ImageView) findViewById(R.id.pb_cart_user_address_image);
        mConfirmBtn = (Button)findViewById(R.id.pb_cart_confirm_btn);
        mCartRecyclerView = (RecyclerView)findViewById(R.id.pb_cart_recyclerview);
        listCartItem = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user != null){
                    dUserUID = FirebaseAuth.getInstance().getUid();
                    String dsUserName = user.getDisplayName();
                    Toast.makeText(getApplicationContext(),dsUserName+"'s Cart", Toast.LENGTH_SHORT).show();;

                    LoadCartItemList(dUserUID);
                    mUserRef = db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER")
                            .document(dUserUID);
                    mUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                String dsUserPhoneNo = documentSnapshot.getString("phone_no");
                                String dsUserAddress = documentSnapshot.getString("homeAddress");
                                mPhoneNoText.setText(dsUserPhoneNo);
                                mAddressText.setText(dsUserAddress);
                            }
                        }
                    });
                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        mAddressEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get alert_dialog.xml view
                LayoutInflater li = LayoutInflater.from(PB_Cart.this);
                View promptsView = li.inflate(R.layout.popup_edit_address, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        PB_Cart.this);

                // set alert_dialog.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInputPhoneNo = (EditText) promptsView.findViewById(R.id.etUserInputPhoneno);
                final EditText userInputAddress = (EditText) promptsView.findViewById(R.id.etUserInputAddress);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String dsNewPhoneNo = userInputPhoneNo.getText().toString();
                                String dsNewAddress = userInputAddress.getText().toString();
                                mUserRef.update("phone_no",dsNewPhoneNo);
                                mUserRef.update("homeAddress",dsNewAddress);
                                mPhoneNoText.setText(dsNewPhoneNo);
                                mAddressText.setText(dsNewAddress);
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCartRef= db.collection("HatherKacheApp").document("REGISTER")
                        .collection("NORMAL_USER").document(dUserUID);


                Level3UID_ofProduct = listCartItem.get(0).getLevel3UID();
                Level2UID_ofProduct = listCartItem.get(0).getLevel2UID();
                ProccedOrderToServer(Level2UID_ofProduct);


            }
        });

    }
    String Level3UID_ofProduct = "NO";
    String Level2UID_ofProduct = "NO";

    private void ProccedOrderToServer(String level2UID) {
        if(level2UID.equals("HomeServices")){
            SetOrderToServer();
        }else if(level2UID.equals("FoodDelivery")){
            SetOrderToServer();
        }else if(level2UID.equals("GroceryShopping")){
            SetOrderToServer();
        }
    }

    private void SetOrderToServer() {

        String dsMiliSeconds = String.valueOf(System.currentTimeMillis());
        String dsProductUID = listCartItem.get(0).getProductUID();
        //Order Data Creating to Server
        FieldValue ddDate =  FieldValue.serverTimestamp();
        Map<String, Object> note = new HashMap<>();
        note.put("diTime", ddDate);
        note.put("diTotal_Money", diTotalMoney);
        //ERROR on uid cart
        note.put("uid_cart", dsMiliSeconds);    //inside of .collection("Ordered").document("MyOrder");
        note.put("uid_buyer", dUserUID);    //je order dise
        note.put("uid_rider", "NIL");


        //Doctor,Chef->Creator UID //or Shop Name UID //or Specific UID for Grocery //
        //by this uid, we will find user or shop , and his location
        note.put("Level2UID", Level2UID_ofProduct);
        note.put("Level3UID", Level3UID_ofProduct);

        note.put("dsNote", "NIL");
        note.put("dsDeliveryHour", "NIL");
        note.put("dsPaymentStatus", "Pending");
        note.put("dsCompleteLevel", "1");
        note.put("uid_report", "NIL");


        if(Level2UID_ofProduct.equals("HomeServices")){
            if(listCartItem.size() != 1){
                Toast.makeText(getApplicationContext(), "Please Take Single Person", Toast.LENGTH_SHORT).show();
            }else {
                //Error Level 1 Name not Dynamicaly Set
                //ekon Product Information jabe, giye product er crator ke oita collect korbe
                db.collection("HatherKacheApp").document("Sylhet")
                        .collection(Level2UID_ofProduct).document(Level3UID_ofProduct)
                        .collection("AllProducts").document(dsProductUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                                String L5UIDoCreator = documentSnapshot.getString("L5UIDoCreator");
                                if(dUserUID.equals(L5UIDoCreator)){ //jodi order person, r product creator same hoy, taile order proceed hobe na.
                                    Toast.makeText(getApplicationContext(), "Failed! because you are Creator n Buyer", Toast.LENGTH_SHORT).show();
                                }else { //jodi order person, r product creator same na hoy, taile order proceed hobe.
                                    note.put("uid_master", L5UIDoCreator); //uid master means target uid where the worker stayed.
                                    note.put("dsExtra", "NIL");
                                    CartTransferMyCartToNewCart(dsMiliSeconds);
                                    OrderCreatingToServer(note);
                                }
                        }else{
                            Toast.makeText(getApplicationContext(),"Product Not Found",Toast.LENGTH_SHORT);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed to Fetch Product Creator",Toast.LENGTH_SHORT);
                    }
                });
            }
        }else if(Level2UID_ofProduct.equals("FoodDelivery") || Level2UID_ofProduct.equals("GroceryShopping") ){
            db.collection("HatherKacheApp").document("Sylhet")
                    .collection(Level2UID_ofProduct).document(Level3UID_ofProduct).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String L3ShopCreator = documentSnapshot.getString("L3Creator"); //Shop or Resturent Creator UID
                        String ShopsUID = listCartItem.get(0).getLevel3UID();  //shops
                        note.put("uid_master", ShopsUID);//uid master means target uid where the worker stayed.
                        note.put("dsExtra", L3ShopCreator);
                        CartTransferMyCartToNewCart(dsMiliSeconds);
                        OrderCreatingToServer(note);
                    }else{
                        Toast.makeText(getApplicationContext(),"Shop Creator not Found",Toast.LENGTH_SHORT);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed to Fetch Shop Creator",Toast.LENGTH_SHORT);
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Level 2 404", Toast.LENGTH_SHORT).show();
        }



    }

    private void OrderCreatingToServer(Map myNote) {
        //ERROR Change CITY NAME from intent
        //Creating Order Documents to server now.
        db.collection("HatherKacheApp").document("Sylhet")
                .collection("Orders").add(myNote).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String dsOrderUID = documentReference.getId();
                Toast.makeText(PB_Cart.this, "Order Successful. ID = "+dsOrderUID, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PB_Cart.this, UserProfile.class);
                startActivity(intent);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PB_Cart.this, "Order Uplaod Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CartTransferMyCartToNewCart(String MiliSeconds) {
        //Cart Moving from MyCart to OrderedCart
        int diListSize = listCartItem.size();
        for(int i = 0; i<diListSize; i++){

            PB_Cart_Model pb_cart_model = listCartItem.get(i);
            String dsProductDataUID = pb_cart_model.getProductUID();
            String ProductName = pb_cart_model.getProductName();
            String ProductPHOTO = pb_cart_model.getProductPHOTO();
            String ProductCreatorUID = pb_cart_model.getProductCreatorUID();
            //Level3UID = pb_cart_model.getLevel3UID();
            //Level2UID = pb_cart_model.getLevel2UID();
            int ProductPrice = pb_cart_model.getProductPrice();
            int  ProductQunatity = pb_cart_model.getProductQunatity();
            Date ProductTime = pb_cart_model.getProductTime();


            Map<String, Object> product = new HashMap<>();
            product.put("ProductName", ProductName);
            product.put("ProductPHOTO", ProductPHOTO);
            product.put("ProductCreatorUID", ProductCreatorUID);
            product.put("Level3UID", Level3UID_ofProduct);
            product.put("Level2UID", Level2UID_ofProduct);

            product.put("ProductPrice", ProductPrice);
            product.put("ProductQunatity", ProductQunatity);
            product.put("ProductTime", ProductTime);

            mCartRef.collection("Ordered").document("MyOrder").collection(MiliSeconds)
                    .document(dsProductDataUID).set(product);
            mCartRef.collection("MyCart").document(dsProductDataUID).delete();
        }
    }

    private int diTotalMoney = 0;
    private int diTotalDiscount = 0;
    String dsUIDCART = "NO", dsUIDBUYER = "NO", dsUIDMaster = "NO";
    DocumentReference user_data_ref;
    private void LoadCartItemList(String dUserUID) {
        notebookRef = db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER")
                .document(dUserUID).collection("MyCart");
        notebookRef.orderBy("ProductTime", Query.Direction.ASCENDING)
        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"No Items Found ",Toast.LENGTH_SHORT).show();
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Items Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }else {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        PB_Cart_Model pb_cart_model = documentSnapshot.toObject(PB_Cart_Model.class);
                        //messageModel.setDocumentID(documentSnapshot.getId());
                         String ProductUID  = documentSnapshot.getId();

                         String ProductName = pb_cart_model.getProductName();
                         String ProductPHOTO = pb_cart_model.getProductPHOTO();
                         String ProductCreatorUID = pb_cart_model.getProductCreatorUID();
                         String Level2UID = pb_cart_model.getLevel2UID();
                         String Level3UID = pb_cart_model.getLevel3UID();
                         int ProductPrice = pb_cart_model.getProductPrice();
                         int  ProductQunatity = pb_cart_model.getProductQunatity();
                         Date  ProductTime = pb_cart_model.getProductTime();

                        //Level_D_Model(String l4Uid, String l4Name, String l4PhotoUrl, String l4Bio, String l4Search, String l4Creator, String l4Extra, int l4iPrivacy, int l4iPriority, int l4iViewCount, int l4iTotalProducts
                        listCartItem.add(new PB_Cart_Model(ProductUID,ProductName,ProductPHOTO,ProductCreatorUID,Level2UID,Level3UID,ProductPrice,ProductQunatity,ProductTime));
                        int diProductTotalPrice = ProductPrice * ProductQunatity;
                        diTotalMoney = diTotalMoney + diProductTotalPrice;

                    }
                    mTotalTextCount.setText(String.valueOf(diTotalMoney));
                }
                //Collections.reverse(listBook);
                pb_cart_adapter = new PB_Cart_Adapter(PB_Cart.this,listCartItem,PB_Cart.this);
                pb_cart_adapter.notifyDataSetChanged();
                //mBookListRecyclerView.setLayoutManager(new GridLayoutManager(Level_C.this,1));
                mCartRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                mCartRecyclerView.setAdapter(pb_cart_adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void GetUserData(String dsUserUID){
        user_data_ref = db.collection("HatherKacheApp").document("REGISTER");
        user_data_ref.collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String dUserPhoneNo = documentSnapshot.getString("phone_no");
                String dHomeAddress = documentSnapshot.getString("homeAddress");
                mPhoneNoText.setText(dUserPhoneNo);
                mAddressText.setText(dHomeAddress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private DocumentReference mUserRef;

    @Override
    public void onPlusBtnClick(int position, int quantity, int price) {
        //Toast.makeText(getApplicationContext(), "price "+price, Toast.LENGTH_LONG).show();
        diTotalMoney = diTotalMoney + price;
        mTotalTextCount.setText(String.valueOf(diTotalMoney));
        String dsProductUid = listCartItem.get(position).getProductUID();
        mUserRef.collection("MyCart").document(dsProductUid).update("ProductQunatity",quantity);
    }
    @Override
    public void onMinusBtnClick(int position, int quantity, int price, boolean fridge) {



        //Toast.makeText(getApplicationContext(), "price "+price, Toast.LENGTH_LONG).show();
        if(!fridge){
            diTotalMoney = diTotalMoney - price;
            mTotalTextCount.setText(String.valueOf(diTotalMoney));
            String dsProductUid = listCartItem.get(position).getProductUID();
            mUserRef.collection("MyCart").document(dsProductUid).update("ProductQunatity",quantity);
        }

    }
    @Override
    public void onWholeItemClick(int position) {
    }
    @Override
    public void onDeleteItemClick(int position, int qunatity, int price) {
        String dsProductUid = listCartItem.get(position).getProductUID();
        mUserRef.collection("MyCart").document(dsProductUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
                listCartItem.remove(position);
                pb_cart_adapter.notifyItemRemoved(position);
                int diPresentPrice = price * qunatity;
                diTotalMoney = diTotalMoney - diPresentPrice;
                mTotalTextCount.setText(String.valueOf(diTotalMoney));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Delete Failed ", Toast.LENGTH_LONG).show();
            }
        });

    }

}