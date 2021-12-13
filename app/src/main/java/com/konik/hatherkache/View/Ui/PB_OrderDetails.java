package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.konik.hatherkache.Service.Map.MAP;
import com.konik.hatherkache.Service.Map.MAP_UserLocationModel;
import com.konik.hatherkache.View.Adapter.PB_OrderDetails_ItemAdapter;
import com.konik.hatherkache.Service.Model.PB_OrderList_Model;
import com.konik.hatherkache.View.Adapter.PC_DeliveryStatusAdapter;
import com.konik.hatherkache.Service.Model.PC_DeliveryStatusModel;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.konik.hatherkache.Service.Model.PB_Cart_Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PB_OrderDetails extends AppCompatActivity implements RecylerviewClickInterface {
    private Button mAddDeliveryStatusBtn;
    private Button mRiderSetBtn, mViewOnMapBtn, mEditUserAddressBtn;

    //Get Delivery Status RecyclerView
    RecyclerView mDeliveryStatusmRecyclerView;
    List<PC_DeliveryStatusModel> listDeliveryStatus;
    PC_DeliveryStatusAdapter pc_deliveryStatusAdapter;
    //Get Product ITEM
    RecyclerView mItemRecyclerView;
    List<PB_Cart_Model> listCartItem;
    PB_OrderDetails_ItemAdapter pb_items_adapter;
    //FirebaseAUTH
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    //FireStore
    private CollectionReference notebookRef ;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Initialize
    private TextView mInvoiceText, mInvoiceOrderDateText, mDeliveryStatusHeadText;
    private TextView mTotalAmountCountTxt, mPaidAmountCountTxt, mDueAmountCountTxt, mPaymentStatusText;
    private TextView mMasterNameText, mMasterAddressText, mMasterPhoneNoText;
    private TextView mBuyerNameText, mBuyerAddressText, mBuyerPhoneNoText;
    private ImageView mMasterPhoneImage, mMasterNameImage;
    //Variables
    private String dUserUID = "NO";
    private String  dsRiderUID  = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_b__order_details);
        mDeliveryStatusmRecyclerView = (RecyclerView)findViewById(R.id.order_details_delivery_status_recyclerview) ;
        listDeliveryStatus = new ArrayList<>();
        mItemRecyclerView = (RecyclerView)findViewById(R.id.order_details_item_recyclerview) ;
        //mItemRecyclerView.setHasFixedSize(true);
        mItemRecyclerView.setNestedScrollingEnabled(false);
        listCartItem = new ArrayList<>();

        //Initialize
        mInvoiceText = (TextView)findViewById(R.id.order_details_invoice_text);
        mInvoiceOrderDateText = (TextView)findViewById(R.id.order_details_invoice_date_text);
        mDeliveryStatusHeadText = (TextView)findViewById(R.id.order_details_delivery_status_head_text);
        mTotalAmountCountTxt = (TextView)findViewById(R.id.order_details_total_amount);
        mPaidAmountCountTxt = (TextView)findViewById(R.id.order_details_paid_amount);
        mDueAmountCountTxt = (TextView)findViewById(R.id.order_details_due_amount) ;
        mPaymentStatusText = (TextView)findViewById(R.id.order_details_payment_status) ;
        mMasterNameText = (TextView)findViewById(R.id.order_details_master_name);
        mMasterAddressText = (TextView)findViewById(R.id.order_details_master_address);
        mMasterPhoneNoText =(TextView)findViewById(R.id.order_details_master_phone_no);
        mMasterNameImage = (ImageView)findViewById(R.id.order_details_master_name_img);
        mMasterPhoneImage = (ImageView)findViewById(R.id.order_details_master_phone_no_img);
        mBuyerNameText = (TextView)findViewById(R.id.order_details_buyer_name);
        mBuyerAddressText = (TextView)findViewById(R.id.order_details_buyer_address);
        mBuyerPhoneNoText =(TextView)findViewById(R.id.order_details_buyer_phone_no);

        mAddDeliveryStatusBtn = (Button)findViewById(R.id.order_details_add_delivery_sttus_btn);
        mViewOnMapBtn = (Button)findViewById(R.id.order_details_view_on_map_btn);
        mRiderSetBtn = (Button)findViewById(R.id.order_details_view_on_map_rider_set);
        mEditUserAddressBtn = (Button)findViewById(R.id.order_details_edit_user_address_btn);
        getIntentMethod();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                String dsUserName = user.getDisplayName();
                if(user != null){
                    dUserUID = FirebaseAuth.getInstance().getUid();
                    Toast.makeText(getApplicationContext(),dsUserName+"'s Order Class", Toast.LENGTH_SHORT).show();;


                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        mAddDeliveryStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PC_DeliveryStatusAdd.class);
                intent.putExtra("dsOrderUID",dsOrderUID);
                intent.putExtra("dsCart_UID",dsCart_UID);
                startActivity(intent);
            }
        });
        mRiderSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dsRiderBtnName = mRiderSetBtn.getText().toString();
                if(dsRiderBtnName.equals("Find Rider")){    //dont modify String Find Rider
                    gotoMapActivity();
                }else if(dsRiderBtnName.equals("Rider Info")){  //dont modify String    Rider Info
                    DocumentReference locationRef = db
                            .collection("HatherKacheApp").document("Location")
                            .collection("Riders").document(dsRiderUID);
                    locationRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                 MAP_UserLocationModel riderLocationInfo = documentSnapshot.toObject(MAP_UserLocationModel.class);
                                 viewRiderInfo(riderLocationInfo);
                            }else{
                                Toast.makeText(getApplicationContext(),"Rider Location Info 404", Toast.LENGTH_SHORT).show();;
                            }
                        }
                    });
                }

            }
        });
        mViewOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMapActivity();
            }
        });
        viewSetMethod();
    }

    private void viewRiderInfo(MAP_UserLocationModel riderLocationInfo) {
        LayoutInflater li = LayoutInflater.from(PB_OrderDetails.this);
        View promptsView = li.inflate(R.layout.popup_rider_info, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PB_OrderDetails.this);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final ImageView riderImageView = (ImageView) promptsView.findViewById(R.id.rider_info_image);
        final TextView riderName = (TextView) promptsView.findViewById(R.id.rider_info_name);
        final TextView riderTime = (TextView) promptsView.findViewById(R.id.rider_info_last_time);
        final TextView riderID = (TextView) promptsView.findViewById(R.id.rider_info_id);
        final TextView riderAddress = (TextView) promptsView.findViewById(R.id.rider_info_address);

        // set dialog message
        String dsRiderUID = riderLocationInfo.getUid();
        String dsRiderName = riderLocationInfo.getName();
        String dsRiderAddress = riderLocationInfo.getAddress();
        Date date = riderLocationInfo.getTimestamp();
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mm:ss a  dd/MMM/yy");
        String dateText = df2.format(date);

        riderName.setText("Name: "+dsRiderName);
        riderTime.setText("Last Activity: "+dateText);
        riderID.setText("ID: "+dsRiderUID);
        riderAddress.setText("Address: "+dsRiderAddress);

        alertDialogBuilder
                .setCancelable(true);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show(); //show it
    }

    private void gotoMapActivity(){
        db.collection("HatherKacheApp").document("Sylhet")
                .collection("Orders").document(dsOrderUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String Level2UID = documentSnapshot.getString("Level2UID");
                    String TargetPlace = documentSnapshot.getString("uid_master");
                    String BuyerUID = documentSnapshot.getString("uid_buyer");
                    String RiderUID = documentSnapshot.getString("uid_rider");  // if vaule return NIL that means rider not set
                    if(Level2UID.equals("HomeServices")){
                        Level2UID = "Users";
                        RiderUID = "NO";
                        if(dsViewerType.equals("uid_buyer")){
                            if(dUserUID.equals(BuyerUID)){
                                BuyerUID = dUserUID;
                            }else if(dUserUID.equals(TargetPlace)){
                                String temp = BuyerUID;
                                BuyerUID = TargetPlace;
                                TargetPlace = temp;
                                Toast.makeText(getApplicationContext(),"Error! Target User and Present User Same",Toast.LENGTH_LONG).show();
                            }
                        }else  if(dsViewerType.equals("uid_master")){
                            if(dUserUID.equals(TargetPlace)){
                                TargetPlace = dUserUID;
                            }else if(dUserUID.equals(BuyerUID)){
                                String temp = TargetPlace;
                                TargetPlace = BuyerUID;
                                BuyerUID = temp;
                                Toast.makeText(getApplicationContext(),"Error! Target User and Present User Same",Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    Intent intent = new Intent(getApplicationContext(), MAP.class);
                    intent.putExtra("dsOrderType",Level2UID);           //that means HomeService or FoodDelivery
                    intent.putExtra("dsProductPlaceUID",TargetPlace);    //DoctorPersonCreatorUID, ShopsUID
                    intent.putExtra("dsPresentUserUID",BuyerUID);
                    intent.putExtra("dsRiderUserUID",RiderUID);
                    intent.putExtra("dsOrderUID",dsOrderUID);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void viewSetMethod(){
        db.collection("HatherKacheApp").document("Sylhet")
                .collection("Orders").document(dsOrderUID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    PB_OrderList_Model pb_orderList_model = documentSnapshot.toObject(PB_OrderList_Model.class);
                    Date today = pb_orderList_model.getDiTime();
                    long dlTime = today.getTime();
                    Log.e("PB_OrderDetails", "onResult: long Time: "+dlTime);
                    dlTime = dlTime - -1625464583260L;
                    String dsInvoiceID =  String.valueOf(dlTime);
                    int dsInvoiceIDSize = dsInvoiceID.length();
                    mInvoiceText.setText("INVOICE ID: "+ dsInvoiceID.substring(4,7)+" "+dsInvoiceID.substring(8,dsInvoiceIDSize-1));

                    DateFormat formatter= new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Dubai"));
                    mInvoiceOrderDateText.setText(formatter.format(today.getTime()));
                    //////INVOICE & DATE SET
                    int diTotalAmount = pb_orderList_model.getDiTotal_Money();
                    String dsPaymentStatus = pb_orderList_model.getDsPaymentStatus();
                    String dsBuyerUID = pb_orderList_model.getUid_buyer();
                    dsRiderUID = pb_orderList_model.getUid_rider();
                    String dsLevel2UID = pb_orderList_model.getLevel2UID(); //HomeService, FoodDelivery
                    if(!dsViewerType.equals("uid_buyer")){
                        if(dsRiderUID.equals("NIL")){
                            mRiderSetBtn.setText("Find Rider");
                        }else{
                            mRiderSetBtn.setText("Rider Info");
                        }
                    }else{
                        mRiderSetBtn.setVisibility(View.GONE);
                        mRiderSetBtn.setVisibility(View.GONE);
                    }

                    if(dsLevel2UID.equals("FoodDelivery") || dsLevel2UID.equals("GroceryShopping")){
                        String dsShopUID = pb_orderList_model.getUid_master();
                        db.collection("HatherKacheApp").document("Location")
                                .collection(dsLevel2UID).document(dsShopUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    String dsAddress = documentSnapshot.getString("address");
                                    String dsName = documentSnapshot.getString("name");
                                    mMasterNameText.setText(dsName);
                                    mMasterAddressText.setText(dsAddress);
                                    mMasterPhoneNoText.setVisibility(View.GONE);
                                    mMasterPhoneImage.setVisibility(View.GONE);
                                    mMasterNameImage.setImageResource(R.drawable.ic_baseline_local_dining_24);
                                }

                            }
                        });
                        if(dsViewerType.equals("uid_buyer")){
                            mAddDeliveryStatusBtn.setEnabled(false);
                            mEditUserAddressBtn.setEnabled(true );
                        }else {
                            mAddDeliveryStatusBtn.setEnabled(true);
                            mEditUserAddressBtn.setEnabled(false);
                        }

                    }else if(dsLevel2UID.equals("HomeServices")){
                        String dsHomeServicePersonUID = pb_orderList_model.getUid_master();

                        db.collection("HatherKacheApp").document("Location")
                                .collection("Users").document(dsHomeServicePersonUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    String dsAddress = documentSnapshot.getString("address");
                                    String dsName = documentSnapshot.getString("name");
                                    mMasterNameText.setText(dsName);
                                    mMasterAddressText.setText(dsAddress);
                                    mMasterPhoneNoText.setVisibility(View.GONE);
                                    mMasterPhoneImage.setVisibility(View.INVISIBLE);
                                }

                            }
                        });
                        mRiderSetBtn.setVisibility(View.GONE);
                        if(dsViewerType.equals("uid_buyer")){   //buyer view korle add status btn disable
                            mAddDeliveryStatusBtn.setEnabled(false);
                            mEditUserAddressBtn.setEnabled(true);
                        }else  if(dsViewerType.equals("uid_master")){   //HomeServicePerson view korle enable
                            mAddDeliveryStatusBtn.setEnabled(true);
                            mEditUserAddressBtn.setEnabled(false);
                        }
                    }
                    db.collection("HatherKacheApp").document("REGISTER")
                            .collection("NORMAL_USER").document(dsBuyerUID)
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                String dssAddress = documentSnapshot.getString("homeAddress");
                                String dssName = documentSnapshot.getString("name");
                                String dsPhoneNo = documentSnapshot.getString("phone_no");
                                mBuyerNameText.setText(dssName);
                                mBuyerAddressText.setText(dssAddress);
                                mBuyerPhoneNoText.setText(dsPhoneNo);

                            }
                        }
                    });
                    mTotalAmountCountTxt.setText(String.valueOf(diTotalAmount)+" TK");
                    mPaymentStatusText.setText(dsPaymentStatus);
                    mDueAmountCountTxt.setText("0 TK");
                    if(dsPaymentStatus.equals("Paid")){
                        mPaidAmountCountTxt.setText(String.valueOf(diTotalAmount)+" TK");
                    }else{
                        mPaidAmountCountTxt.setText("0 TK");
                    }
                    //Retrive RecyclerView Data
                    LoadCartItemList(dsBuyerUID,dsCart_UID);
                    LoadDeliveryStatusItemList(dsOrderUID);


                }
            }
        });
    }
    private void LoadDeliveryStatusItemList(String dOrderUID) {
        notebookRef = db.collection("HatherKacheApp").document("Sylhet")
                .collection("Orders").document(dsOrderUID).collection("DeliveryStatus");//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3
        notebookRef.orderBy("ddDate", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"No Status Found ",Toast.LENGTH_SHORT).show();
                    /*View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Items Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();*/
                }else {
                    int size = queryDocumentSnapshots.size(), i = 0;
                    if(size>0){
                        mDeliveryStatusHeadText.setVisibility(View.VISIBLE);
                    }
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        i++;
                        PC_DeliveryStatusModel pc_deliveryStatusModel = documentSnapshot.toObject(PC_DeliveryStatusModel.class);

                        String dsStatusUID = documentSnapshot.getId();
                        Date ddDate = pc_deliveryStatusModel.getDdDate();
                        String dsNote = pc_deliveryStatusModel.getDsNote();
                        String dsStatus = pc_deliveryStatusModel.getDsStatus();



                        if(i == 1){
                            listDeliveryStatus.add(new PC_DeliveryStatusModel(ddDate,"FIRST",dsNote,dsStatus));
                        }else if(size != i) {
                            PC_DeliveryStatusModel test = new PC_DeliveryStatusModel();
                          //  Log.e("PB_OrderDetails", "onResult:DeliveryStatus(): i = "+i +  "size: "+listDeliveryStatus.size()+" status "+listDeliveryStatus.get(i).getDsStatus());
                            //if(listDeliveryStatus.get(i-2). != null){
                                test = listDeliveryStatus.get(i-2);
                                test.setDsStatusUID(dsStatusUID);
                                listDeliveryStatus.remove(i-2);
                                listDeliveryStatus.add(test);
                                listDeliveryStatus.add(new PC_DeliveryStatusModel(ddDate,dsStatusUID,dsNote,dsStatus));
                            //}

                        }else if(i == 2 && size == i) {
                            PC_DeliveryStatusModel test = new PC_DeliveryStatusModel();
                            test = listDeliveryStatus.get(i-2);
                            test.setDsStatusUID("2nd");
                            listDeliveryStatus.remove(i-2);
                            listDeliveryStatus.add(test);
                            listDeliveryStatus.add(new PC_DeliveryStatusModel(ddDate,"LAST",dsNote,dsStatus));
                            db.collection("HatherKacheApp").document("Sylhet")
                                    .collection("Orders").document(dsOrderUID).update("dsCompleteLevel", dsStatus);
                        }else if(size == i ){
                            listDeliveryStatus.add(new PC_DeliveryStatusModel(ddDate,"LAST",dsNote,dsStatus));
                            db.collection("HatherKacheApp").document("Sylhet")
                                    .collection("Orders").document(dsOrderUID).update("dsCompleteLevel", dsStatus);
                        }else{
                            listDeliveryStatus.add(new PC_DeliveryStatusModel(ddDate,dsStatusUID,dsNote,dsStatus));
                        }

                        //Log.e("PB_OrderDetails", "onResult:DeliveryStatus(): i = "+i + " size = "+size+ " status "+listDeliveryStatus.get(i-1).getDsStatus());
                        for(int j =0; j<listDeliveryStatus.size(); j++){
                            Log.e("PB_OrderDetails", "onResult:DeliveryStatus(): i = "+j +  " status "+listDeliveryStatus.get(j).getDsStatus());
                        }

                    }

                    //mTotalTextCount.setText(String.valueOf(diTotalMoney));
                }
                for(int i =0; i<listDeliveryStatus.size(); i++){
                    Log.e("PB_OrderDetails", "onResult:DeliveryStatus(): i = "+i +  " status "+listDeliveryStatus.get(i).getDsStatus());
                }
                pc_deliveryStatusAdapter = new PC_DeliveryStatusAdapter(0,PB_OrderDetails.this,listDeliveryStatus,PB_OrderDetails.this);
                pc_deliveryStatusAdapter.notifyDataSetChanged();
                //mBookListRecyclerView.setLayoutManager(new GridLayoutManager(Level_C.this,1));
                mDeliveryStatusmRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                mDeliveryStatusmRecyclerView.setAdapter(pc_deliveryStatusAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void LoadCartItemList(String dUserUID, String dCart_UID) {
        Toast.makeText(getApplicationContext(),"CART UID "+dCart_UID,Toast.LENGTH_LONG).show();
        notebookRef = db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER")
                .document(dUserUID).collection("Ordered").document("MyOrder").collection(dCart_UID);
        notebookRef.orderBy("ProductTime", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"No Items Found ",Toast.LENGTH_SHORT).show();
                    /*View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Items Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();*/
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
                        Date ProductTime = pb_cart_model.getProductTime();
                        //Level_D_Model(String l4Uid, String l4Name, String l4PhotoUrl, String l4Bio, String l4Search, String l4Creator, String l4Extra, int l4iPrivacy, int l4iPriority, int l4iViewCount, int l4iTotalProducts
                        listCartItem.add(new PB_Cart_Model(ProductUID,ProductName,ProductPHOTO,ProductCreatorUID,Level2UID,Level3UID,ProductPrice,ProductQunatity,ProductTime));
                        //diTotalMoney = diTotalMoney + ProductPrice;

                    }
                    //mTotalTextCount.setText(String.valueOf(diTotalMoney));
                }
                //Collections.reverse(listBook);
                pb_items_adapter = new PB_OrderDetails_ItemAdapter(PB_OrderDetails.this,listCartItem,PB_OrderDetails.this);
                pb_items_adapter.notifyDataSetChanged();
                //mBookListRecyclerView.setLayoutManager(new GridLayoutManager(Level_C.this,1));
                mItemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                mItemRecyclerView.setAdapter(pb_items_adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //Intents
    private String dsOrderUID = "NO",dsCart_UID = "NO",dsViewerType = "NO";
    private boolean intentErrorFound = false;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsOrderUID = intent.getExtras().getString("dsOrderUID");
            dsCart_UID = intent.getExtras().getString("dsCart_UID");
            dsViewerType = intent.getExtras().getString("dsViewerType");
            intentErrorFound = CheckIntentMethod(dsOrderUID,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsCart_UID,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsViewerType,intentErrorFound);
            if(!intentErrorFound){  //if intent error found false then do the next work

            }
        }else{
            Toast.makeText(this, "Intent Not Found ", Toast.LENGTH_SHORT).show();
            dsOrderUID = "NO";
            dsCart_UID = "NO";
            dsViewerType = "NO";
        }

    }
    private boolean CheckIntentMethod(String dsTestIntent, boolean dbIntentErrorFound ){
        if(!dbIntentErrorFound){
            if (TextUtils.isEmpty(dsTestIntent)) {
                dbIntentErrorFound = true;
                dsTestIntent= "NO";
                Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (dsTestIntent.equals("")){
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
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemLongCLick(int postion) {

    }

    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }
    @Override
    public void onResume() {
        super.onResume();
        listCartItem.clear();
        listDeliveryStatus.clear();
    }
}