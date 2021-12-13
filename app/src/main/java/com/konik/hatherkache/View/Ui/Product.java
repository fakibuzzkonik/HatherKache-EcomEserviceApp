package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.konik.hatherkache.View.Adapter.Product_ReviewAdapter;
import com.konik.hatherkache.Service.Model.Produt_UserReviewModel;
import com.konik.hatherkache.R;
import com.konik.hatherkache.Service.Model.Level_E_Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product extends AppCompatActivity {
    //mp = mProduct
    private ImageSlider mpImageSlider;
    List<SlideModel> imageList = new ArrayList<>();
    private TextView mpName, mpFrom, mpRatingCount;
    private TextView mpOrderCount, mpQunatityExperienceHead, mpQunatityExperienceCount, mpPriceHead,mpPriceCount;
    private TextView mpAbout, mpBio;
    private Button mpWriteReviewBtn, mpAskQuestionBtn;
    private Button mpCartPlusBtn, mpCartMinus, mpCartBooked;
    private RatingBar mpRatingBar ;
    private TextView mpReviewHeadText;
    private RecyclerView mpReviewRecyclerView;

    ////Firebase
    private DocumentReference notebookRef ;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Level_E_Model Level_e_Model; //Model Class

    //FirebaseAUTH
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    private DocumentReference mCartRef, mUserUidRef;

    //Variables
    private String dUserUID = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        mpImageSlider = (ImageSlider) findViewById(R.id.product_image_slider);
        mpName = (TextView)findViewById(R.id.product_name); 
        mpFrom = (TextView)findViewById(R.id.product_from); 
        mpRatingCount = (TextView)findViewById(R.id.product_text_rating_int);
        mpRatingBar = (RatingBar)findViewById(R.id.product_text_ratingbar);
        mpOrderCount = (TextView)findViewById(R.id.product_text_ordered_count); 
        mpQunatityExperienceHead = (TextView)findViewById(R.id.product_text_quantity); 
        mpQunatityExperienceCount = (TextView)findViewById(R.id.product_text_quantity_count);
        mpPriceHead = (TextView)findViewById(R.id.product_text_price);
        mpPriceCount = (TextView)findViewById(R.id.product_text_price_count);
        mpAbout = (TextView)findViewById(R.id.product_about);
        mpBio = (TextView)findViewById(R.id.product_bio);

        mpWriteReviewBtn = (Button)findViewById(R.id.product_btn_write_review);
        mpAskQuestionBtn = (Button)findViewById(R.id.product_btn_ask_question);
        mpCartPlusBtn = (Button)findViewById(R.id.product_btn_cart_plus);
        mpCartMinus = (Button)findViewById(R.id.product_btn_cart_minus);
        mpCartBooked = (Button)findViewById(R.id.product_btn_cart_booked);
        mpReviewHeadText = (TextView)findViewById(R.id.product_review_head_text);
        mpReviewRecyclerView = findViewById(R.id.product_review_recylerview);
        mpReviewRecyclerView.setNestedScrollingEnabled(false);
        getIntentMethod();
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if(user != null){
                    String dsUserName = user.getDisplayName();
                    dUserUID = FirebaseAuth.getInstance().getUid();
                    check_review_writed();
                    //setup_review_recycler_view();
                   /* product_review_adapter = new Product_ReviewAdapter(options, dbProductCreatorAndViewerSame);
                    product_review_adapter.notifyDataSetChanged();
                    mpReviewRecyclerView.setAdapter(product_review_adapter);*/
                }else{
                    /*Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }
            }
        };
        //Slider Code
        mpImageSlider.setImageList(imageList,ScaleTypes.CENTER_CROP);
        mpImageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                String word = imageList.get(i).getTitle();
                Toast.makeText(getApplicationContext(), "Pic no "+word,
                        Toast.LENGTH_LONG).show();
            }
        });
        mpCartBooked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dUserUID.equals("NO")){
                    Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_LONG).show();
                }else{
                    UpdateCartServer("Add");
                }
            }
        });
        mpCartPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dUserUID.equals("NO")){
                    Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_LONG).show();
                }else{
                    UpdateCartServer("Add");
                }
            }
        });
        mpCartMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dUserUID.equals("NO")){
                    Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_LONG).show();
                }else{
                    UpdateCartServer("Minus");
                }
            }
        });
        mpWriteReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(Product.this);
                View promptsView = li.inflate(R.layout.popup_write_review, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        Product.this);

                // set alert_dialog.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final RatingBar userInputRatingBar = (RatingBar) promptsView.findViewById(R.id.popup_write_review_ratingbar);
                final EditText userInputsReview= (EditText) promptsView.findViewById(R.id.popup_write_review_edit_text);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                float dfRatingScore = userInputRatingBar.getRating();
                                String dsReviewText = userInputsReview.getText().toString();
                                if(!intentErrorFound){
                                    if(dsReviewText.equals("") ){
                                        Toast.makeText(getApplicationContext(),"Please Fillup Text.", Toast.LENGTH_SHORT).show();
                                    }else if( dfRatingScore == 0){
                                        Toast.makeText(getApplicationContext(),"Please Fillup Rating.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        sent_review_to_database(dsReviewText, dfRatingScore);
                                    }

                                }
                                else
                                    Toast.makeText(getApplicationContext(),"intent 404", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        setup_review_recycler_view();
    }
    private void check_review_writed() {
        if (!intentErrorFound) {
            db.collection("HatherKacheApp").document(dsLevel1_Name)
                    .collection(dsLevel2_Name).document(dsLevel3_UID)
                    .collection("AllProducts").document(dsLevel5_UID)
                    .collection("Reviews").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String dUserid = documentSnapshot.getString("user_uid");
                        if (dUserid.equals(dUserUID)) {
                            mpWriteReviewBtn.setVisibility(View.GONE);
                        } else {
                            mpWriteReviewBtn.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mpWriteReviewBtn.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
    private void sent_review_to_database(String dReview, float dRate) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            String dsUserName = user.getDisplayName();
            String dDate = String.valueOf(System.currentTimeMillis());
            FieldValue ddDate =  FieldValue.serverTimestamp();
            Map<String, Object> review_data = new HashMap<>();
            review_data.put("user_time", ddDate);
            review_data.put("user_uid", dUserUID);
            review_data.put("user_review",dReview );
            review_data.put("user_rating", dRate);
            review_data.put("admin_reply", "NO");
            review_data.put("admin_id", "NO");

            // db.document("Notebook/My First Note")
            db.collection("HatherKacheApp").document(dsLevel1_Name)
                    .collection(dsLevel2_Name).document(dsLevel3_UID)
                    .collection("AllProducts").document(dsLevel5_UID)
                    .collection("Reviews").document(dUserUID)//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3
                    .set(review_data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            mpWriteReviewBtn.setText("Reviewed");
                            mpWriteReviewBtn.setEnabled(false);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed to Connect Database", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        }else{
            progressDialog.dismiss();
            Toast.makeText(Product.this,"Please LOGIN",Toast.LENGTH_SHORT).show();
        }
    }
    private Product_ReviewAdapter product_review_adapter;
    FirestoreRecyclerOptions<Produt_UserReviewModel> options;
    private void setup_review_recycler_view() {

        if(intentErrorFound){
            Toast.makeText(Product.this,"Intent Error.",Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(BookInfo.this,"ALL REVIEW START",Toast.LENGTH_SHORT).show();;0
            CollectionReference product_review_ref;
            product_review_ref = db.collection("HatherKacheApp").document(dsLevel1_Name)
                    .collection(dsLevel2_Name).document(dsLevel3_UID)
                    .collection("AllProducts").document(dsLevel5_UID)
                    .collection("Reviews");
            Query query = product_review_ref.orderBy("user_time", Query.Direction.ASCENDING);
            options = new FirestoreRecyclerOptions.Builder<Produt_UserReviewModel>()
                    .setQuery(query,Produt_UserReviewModel.class)
                    .build();

            //Checking User is Admin or not
            if(user != null){
                dUserUID = user.getUid();
                Log.e("Product", "onResult: user not null");

            }
            if(dsLevel5_CreatorUID.equals(dUserUID) && !dsLevel5_CreatorUID.equals("NO"))
                dbProductCreatorAndViewerSame = true;

                Log.e("Product", "onResult: setup_review_recycler_view(): dsLevel5_CreatorUID = "+dsLevel5_CreatorUID + " dUserUID = "+dUserUID+ " boolean = "+dbProductCreatorAndViewerSame);

            product_review_adapter = new Product_ReviewAdapter(options, dbProductCreatorAndViewerSame);

            //mpReviewRecyclerView.setHasFixedSize(true);
            mpReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mpReviewRecyclerView.setAdapter(product_review_adapter);


            product_review_adapter.replyBtnClickListener(new Product_ReviewAdapter.ClickListenerPackage(){
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int postion, String dReplyText) {
                    if(documentSnapshot.exists()){
                        if(dReplyText.equals("")){
                            Toast.makeText(getApplicationContext(),"Please fillup reply box",Toast.LENGTH_SHORT).show();
                        }else if(dUserUID.equals("NO")){
                            Toast.makeText(getApplicationContext(),"USER NOT LOGIN",Toast.LENGTH_SHORT).show();
                        }else{
                            String dReviewUID = documentSnapshot.getId();
                            CollectionReference review_ref = db.collection("HatherKacheApp").document(dsLevel1_Name)
                                    .collection(dsLevel2_Name).document(dsLevel3_UID)
                                    .collection("AllProducts").document(dsLevel5_UID)
                                    .collection("Reviews");

                            review_ref.document(dReviewUID).update("admin_id", dUserUID).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Failed to sent Reply",Toast.LENGTH_SHORT).show();
                                }
                            });
                            review_ref.document(dReviewUID).update("admin_reply", dReplyText);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Review Server not found",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }
    private void UpdateCartServer(String type) {

        long diDate = System.currentTimeMillis();
        long diDate2 = 1623163000000L;
        diDate = diDate - diDate2 ;

        Map<String, Object> cart = new HashMap<>();
        cart.put("ProductName", Level_e_Model.getL5Name());
        cart.put("ProductPHOTO", Level_e_Model.getL5PhotoUrl().get(0)); //map is done

        cart.put("ProductCreatorUID",Level_e_Model.getL5UIDoCreator());
        cart.put("Level3UID",Level_e_Model.getL5UIDofL3()); //Doctor/Engineer/Chef or PachBhai,RoyelChef,Panshi
        cart.put("Level2UID",Level_e_Model.getL5UIDofL2()); //Doctor/Engineer/Chef or PachBhai,RoyelChef,Panshi
        cart.put("ProductPrice", Level_e_Model.getL5iPrice()); //Integer
        FieldValue ddDate =  FieldValue.serverTimestamp();
        cart.put("ProductTime", ddDate); //Integer

        String dsProductUID =Level_e_Model.getL5UID();
        mCartRef = db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER")
                .document(dUserUID).collection("MyCart").document(dsProductUID);
        mUserUidRef = db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER")
                .document(dUserUID);

        mUserUidRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {  //Checking if the product is from same shop(Level3) or not
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    String dsPresent_cart_L3_uid = documentSnapshot.getString("cart_L3_uid");
                    if (TextUtils.isEmpty(dsPresent_cart_L3_uid)) {
                        mUserUidRef.update("cart_L3_uid",dsLevel3_UID);
                        Toast.makeText(getApplicationContext(), "Add new shop to cart, Check Cart",Toast.LENGTH_LONG).show();
                    }
                    if (dsPresent_cart_L3_uid == null) {
                        mUserUidRef.update("cart_L3_uid",dsLevel3_UID);
                        dsPresent_cart_L3_uid = dsLevel3_UID;
                        if(dsLevel2_Name.equals("HomeServices")){
                            Toast.makeText(getApplicationContext(), "Add Home Service to cart, Check Cart",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Add new shop to cart, Check Cart",Toast.LENGTH_LONG).show();
                        }

                    }
                    if(dsPresent_cart_L3_uid.equals(dsLevel3_UID)   || dsPresent_cart_L3_uid.equals("NO")){
                        mCartRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    long diPresentQuantity = documentSnapshot.getLong("ProductQunatity");
                                    if(type.equals("Add")){
                                        diPresentQuantity++;
                                        cart.put("ProductQunatity", diPresentQuantity); //Integer
                                        mCartRef.set(cart);
                                        Toast.makeText(getApplicationContext(), "Quantity Increased",Toast.LENGTH_SHORT).show();
                                    }else if(type.equals("Minus")){
                                        if(diPresentQuantity == 1){
                                            mCartRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Removed",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{
                                            diPresentQuantity--;
                                            cart.put("ProductQunatity", diPresentQuantity); //Integer
                                            mCartRef.set(cart);
                                            Toast.makeText(getApplicationContext(), "Quantity Decreased",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }else{
                                    if(type.equals("Add")){
                                        cart.put("ProductQunatity", 1); //Integer
                                        mCartRef.set(cart);
                                        Toast.makeText(getApplicationContext(), "Added",Toast.LENGTH_SHORT).show();
                                    }else if(type.equals("Minus")){
                                        Toast.makeText(getApplicationContext(), "Nothing in this name",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
                    }else{
                        //Toast.makeText(getApplicationContext(), "Can't add from different shops",Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Product.this);
                        builder1.setMessage("You have already selected products from different shops. If you continue your cart and selection will be removed.");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deletePresentCartData();
                                        mUserUidRef.update("cart_L3_uid",dsLevel3_UID);
                                        dialog.cancel();
                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                }else{
                    mUserUidRef.update("cart_L3_uid",dsLevel3_UID);
                    if(dsLevel2_Name.equals("HomeServices")){
                        Toast.makeText(getApplicationContext(), "HomeService mode for cart",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Shop mode for cart",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });


    }
    private CollectionReference MyCartDocumentRef;
    private void deletePresentCartData() {
        MyCartDocumentRef = db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER")
                .document(dUserUID).collection("MyCart");

        MyCartDocumentRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Cart is Empty.",Toast.LENGTH_SHORT).show();
                }else {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String ProductUID  = documentSnapshot.getId();
                        MyCartDocumentRef.document(ProductUID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(),"Cart is empty now.",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed to delete my cart.",Toast.LENGTH_SHORT).show();
            }
        });
    }
    boolean dbProductCreatorAndViewerSame = false;
    private void setViewData() {
        if(dsLevel5_CreatorUID.equals(dUserUID) && user != null){
            dbProductCreatorAndViewerSame = true;
            product_review_adapter.ddUserisAuthor = dbProductCreatorAndViewerSame;
            product_review_adapter.notifyDataSetChanged();
        }
        mpName.setText(Level_e_Model.getL5Name());
        mpFrom.setText(Level_e_Model.getL5From());
        int diRating = Level_e_Model.getL5iRating();
        if(diRating > 5)    diRating = 5;
        mpRatingCount.setText(String.valueOf(diRating));
        float dfRating = (float)diRating;
        mpRatingBar.setRating(dfRating);
        mpOrderCount.setText(String.valueOf(Level_e_Model.getL5iOrders())+"+");
        if(dsLevel2_Name.equals("HomeServices")){
            mpPriceHead.setText(Level_e_Model.getL5PaymentMode());
            mpPriceCount.setText(String.valueOf(Level_e_Model.getL5iPrice())+"Tk");
            mpQunatityExperienceHead.setText("Experience");
            int diQunatityExperience =  Level_e_Model.getL5iQuantity();
            if(diQunatityExperience>10) diQunatityExperience = 10;
            mpQunatityExperienceCount.setText(String.valueOf(diQunatityExperience)+" yr");
            mpCartPlusBtn.setVisibility(View.GONE);
            mpCartMinus.setVisibility(View.GONE);
        }else if(dsLevel2_Name.equals("GroceryShopping")){
            mpPriceHead.setText("Price");
            mpPriceCount.setText(String.valueOf(Level_e_Model.getL5iPrice())+"TK");
            mpQunatityExperienceHead.setText("Quantity");
            int diQunatityExperience =  Level_e_Model.getL5iQuantity();
            if(diQunatityExperience<100) diQunatityExperience = 100;
            mpQunatityExperienceCount.setText(String.valueOf(diQunatityExperience)+" gm");

            mpCartBooked.setText("Add to Cart");
        }else if(dsLevel2_Name.equals("FoodDelivery")){
            mpPriceHead.setText("Price");
            mpPriceCount.setText(String.valueOf(Level_e_Model.getL5iPrice())+"TK");
            mpQunatityExperienceHead.setText("Quantity");
            int diQunatityExperience =  Level_e_Model.getL5iQuantity();
            if(diQunatityExperience<5)
                mpQunatityExperienceCount.setText("1:"+diQunatityExperience);
            else
                mpQunatityExperienceCount.setText("1:4");

            mpCartBooked.setText("Add to Cart");
        }

        mpBio.setText(Level_e_Model.getL5Bio());
        //Image Slider Code
        List<String> dsaL5PhotoUrlList = new ArrayList<String>();
        dsaL5PhotoUrlList = Level_e_Model.getL5PhotoUrl();
        int diPhotoListSize = dsaL5PhotoUrlList.size();
        for(int i = 0; i<diPhotoListSize; i++){
            imageList.add(new SlideModel(dsaL5PhotoUrlList.get(i), ScaleTypes.CENTER_INSIDE));
        }
        mpImageSlider.setImageList(imageList,ScaleTypes.FIT);
    }
    private void LoadProductData(String dsLevel1_name, String dsLevel2_name, String dsLevel3_uid, String dsLevel5_ProductUid) {

        notebookRef = db.collection("HatherKacheApp").document(dsLevel1_name)
                .collection(dsLevel2_name).document(dsLevel3_uid)
                .collection("AllProducts").document(dsLevel5_ProductUid);//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3


        notebookRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Level_e_Model = documentSnapshot.toObject(Level_E_Model.class);

                    String dsLevelD_ItemUID = documentSnapshot.getId();
                    Level_e_Model.setL5UID(dsLevelD_ItemUID);
                    setViewData();

                }else{
                    Toast.makeText(getApplicationContext(),"No Items Found "+dsLevel5_ProductUid,Toast.LENGTH_SHORT).show();
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Items Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
            }
        });

    }

    //Get Intent and Chekcintg
    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO", dsLevel3_Name = "NO", dsLevel3_UID = "NO", dsLevel4_Name = "NO", dsLevel4_UID = "NO", dsLevel5_Name = "NO", dsLevel5_UID = "NO", dsLevel5_CreatorUID = "NO";
    private boolean intentErrorFound = false;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {

            dsLevel1_Name = intent.getExtras().getString("Level1_Name");    //Sylhet
            dsLevel2_Name = intent.getExtras().getString("Level2_Name");    //Grocery or Food or Home Services
            dsLevel3_Name = intent.getExtras().getString("Level3_Name");    //Doctor,Engineer,Chef / Bakeries, Cake, Vegetables / Pachbhai, Panshi, RoyalChef
            dsLevel3_UID = intent.getExtras().getString("Level3_UID");      //Level 3 UID
            dsLevel4_Name = intent.getExtras().getString("Level4_Name");    //Sub Category Name
            dsLevel4_UID = intent.getExtras().getString("Level4_UID");      //Sub Category ID
            dsLevel5_Name = intent.getExtras().getString("Level5_Name");    //Product Name
            dsLevel5_UID = intent.getExtras().getString("Level5_UID");      //Product UID
            dsLevel5_CreatorUID = intent.getExtras().getString("Level5_CreatorUID");      //Product UID

            intentErrorFound = CheckIntentMethod(dsLevel1_Name,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsLevel2_Name,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsLevel3_Name,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsLevel3_UID,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsLevel5_CreatorUID,intentErrorFound);

            if(!intentErrorFound){  //if intent error found false then do the next work
                LoadProductData(dsLevel1_Name, dsLevel2_Name, dsLevel3_UID, dsLevel5_UID);
            }
        }else{
            Toast.makeText(this, "Intent Not Found ", Toast.LENGTH_SHORT).show();
            dsLevel1_Name = "NO";
            dsLevel2_Name = "NO";
            dsLevel3_Name = "NO";
            dsLevel3_UID = "NO";
            dsLevel4_Name = "NO";
            dsLevel4_UID = "NO";
            dsLevel5_Name = "NO";
            dsLevel5_UID = "NO";
        }

    }
    private boolean CheckIntentMethod(String dsTestIntent, boolean dbIntentErrorFound ){
        if(!dbIntentErrorFound){
            if (TextUtils.isEmpty(dsTestIntent)) {
                dbIntentErrorFound = true;
                dsTestIntent= "NO";
                Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (dsLevel1_Name.equals("")){
                dbIntentErrorFound = true;
                dsTestIntent= "NO";
                Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
            }
        }

        return dbIntentErrorFound;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        product_review_adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        product_review_adapter.stopListening();
    }
}