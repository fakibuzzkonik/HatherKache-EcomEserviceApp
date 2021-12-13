package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.konik.hatherkache.View.Adapter.PB_OrderList_Adapter;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.konik.hatherkache.Service.Model.PB_OrderList_Model;
import com.konik.hatherkache.Service.Model.SortbyServerTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PB_OrderList extends AppCompatActivity implements RecylerviewClickInterface {
    private RecyclerView mLevelD_RecyclerView;
    List<PB_OrderList_Model> listL4ItemList;
    PB_OrderList_Adapter pb_orderList_adapter;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    private CollectionReference notebookRef;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Variables
    private String dUserUID = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_b__order_list);
        mLevelD_RecyclerView = (RecyclerView) findViewById(R.id.pb_order_list_recyclerview);
        listL4ItemList = new ArrayList<>();
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    dUserUID = user.getUid();
                    getIntentMethod();
                    if (dsViewerType.equals("NO") || dUserUID.equals("NO")) {
                        Toast.makeText(getApplicationContext(), "Error Viewer or UserUID 404", Toast.LENGTH_SHORT).show();
                    } else {
                        LoadOrderList();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
    }

    private void LoadOrderList() {  //(dsViewerType,dUserUID)

        notebookRef = db.collection("HatherKacheApp").document("Sylhet")
                .collection("Orders");//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3

        notebookRef.orderBy("diTime", Query.Direction.ASCENDING)//.whereEqualTo(dsViewerType, dUserUID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No Orders Found ", Toast.LENGTH_SHORT).show();
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No Items Found", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                PB_OrderList_Model pb_orderList_model = documentSnapshot.toObject(PB_OrderList_Model.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String dsOrder_UID = documentSnapshot.getId();
                                String dsOrder_CartUID = pb_orderList_model.getUid_cart();
                                String dsOrder_BuyerUID = pb_orderList_model.getUid_buyer();
                                String dsOrder_RiderUID = pb_orderList_model.getUid_rider();
                                String dsOrder_MasterUID = pb_orderList_model.getUid_master();    //shop uid or product creator uid
                                String dsOrder_ReportUID = pb_orderList_model.getUid_cart();
                                String dsLevel2UID = pb_orderList_model.getUid_cart();
                                String dsLevel3UID = pb_orderList_model.getUid_cart();

                                String dsOrderNote = pb_orderList_model.getDsNote();
                                String dsOrderDeliveryHour = pb_orderList_model.getDsDeliveryHour();
                                String dsOrderPaymentStatus = pb_orderList_model.getDsPaymentStatus();
                                String dsOrderCompleteLevel = pb_orderList_model.getDsCompleteLevel();
                                String dsOrderExtra = pb_orderList_model.getDsExtra();
                                Date diTime = pb_orderList_model.getDiTime();
                                int diTotalMoney = pb_orderList_model.getDiTotal_Money();
                                //Level_D_Model(String l4Uid, String l4Name, String l4PhotoUrl, String l4Bio, String l4Search, String l4Creator, String l4Extra, int l4iPrivacy, int l4iPriority, int l4iViewCount, int l4iTotalProducts
                                if(dsViewerType.equals("uid_buyer") && dUserUID.equals(dsOrder_BuyerUID)){
                                    listL4ItemList.add(new PB_OrderList_Model(diTime, diTotalMoney, dsOrder_UID, dsOrder_CartUID,
                                            dsOrder_BuyerUID, dsOrder_RiderUID, dsOrder_MasterUID, dsOrder_ReportUID, dsLevel2UID, dsLevel3UID,
                                            dsOrderNote, dsOrderDeliveryHour, dsOrderPaymentStatus, dsOrderCompleteLevel, dsOrderExtra));
                                }else if(dsViewerType.equals("dsExtra") && dUserUID.equals(dsOrderExtra)){
                                    listL4ItemList.add(new PB_OrderList_Model(diTime, diTotalMoney, dsOrder_UID, dsOrder_CartUID,
                                            dsOrder_BuyerUID, dsOrder_RiderUID, dsOrder_MasterUID, dsOrder_ReportUID, dsLevel2UID, dsLevel3UID,
                                            dsOrderNote, dsOrderDeliveryHour, dsOrderPaymentStatus, dsOrderCompleteLevel, dsOrderExtra));
                                }else if(dsViewerType.equals("uid_rider") && dUserUID.equals(dsOrder_RiderUID)){
                                    listL4ItemList.add(new PB_OrderList_Model(diTime, diTotalMoney, dsOrder_UID, dsOrder_CartUID,
                                            dsOrder_BuyerUID, dsOrder_RiderUID, dsOrder_MasterUID, dsOrder_ReportUID, dsLevel2UID, dsLevel3UID,
                                            dsOrderNote, dsOrderDeliveryHour, dsOrderPaymentStatus, dsOrderCompleteLevel, dsOrderExtra));
                                }else if(dsViewerType.equals("uid_master") && dUserUID.equals(dsOrder_MasterUID)){
                                    listL4ItemList.add(new PB_OrderList_Model(diTime, diTotalMoney, dsOrder_UID, dsOrder_CartUID,
                                            dsOrder_BuyerUID, dsOrder_RiderUID, dsOrder_MasterUID, dsOrder_ReportUID, dsLevel2UID, dsLevel3UID,
                                            dsOrderNote, dsOrderDeliveryHour, dsOrderPaymentStatus, dsOrderCompleteLevel, dsOrderExtra));
                                }



                            }
                        }

                        //Collections.sort(listL4ItemList, new SortbyServerTime());
                        Collections.reverse(listL4ItemList);
                        pb_orderList_adapter = new PB_OrderList_Adapter(PB_OrderList.this, listL4ItemList, PB_OrderList.this);
                        pb_orderList_adapter.notifyDataSetChanged();
                        //mBookListRecyclerView.setLayoutManager(new GridLayoutManager(Level_C.this,1));
                        mLevelD_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        mLevelD_RecyclerView.setAdapter(pb_orderList_adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    @Override
    public void onItemClick(int position) {
        String dsOrderUID = listL4ItemList.get(position).getUid_order();
        String dsCart_UID = listL4ItemList.get(position).getUid_cart();
        Intent intent = new Intent(getApplicationContext(), PB_OrderDetails.class);
        intent.putExtra("dsOrderUID", dsOrderUID);
        intent.putExtra("dsCart_UID", dsCart_UID);
        intent.putExtra("dsViewerType", dsViewerType);
        startActivity(intent);
    }

    @Override
    public void onItemLongCLick(int postion) {

    }

    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }

    private String dsViewerType = "NO";
    private boolean intentErrorFound = false;

    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if (intent.getExtras() != null) {
            dsViewerType = intent.getExtras().getString("dsViewerType");
            intentErrorFound = CheckIntentMethod(dsViewerType, intentErrorFound);
            if (!intentErrorFound) {  //if intent error found false then do the next work

            }
        } else {
            Toast.makeText(this, "Intent Not Found ", Toast.LENGTH_SHORT).show();
            dsViewerType = "NO";
        }

    }

    private boolean CheckIntentMethod(String dsTestIntent, boolean dbIntentErrorFound) {
        if (!dbIntentErrorFound) {
            if (TextUtils.isEmpty(dsTestIntent)) {
                dbIntentErrorFound = true;
                dsTestIntent = "NO";
                Toast.makeText(getApplicationContext(), "intent NULL  ", Toast.LENGTH_SHORT).show();
            }
            if (dsTestIntent.equals("")) {
                dbIntentErrorFound = true;
                dsTestIntent = "NO";
                Toast.makeText(getApplicationContext(), "intent 404", Toast.LENGTH_SHORT).show();
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listL4ItemList.clear();
    }
}