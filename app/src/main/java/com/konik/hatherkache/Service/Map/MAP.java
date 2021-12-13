package com.konik.hatherkache.Service.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.konik.hatherkache.R;
import com.konik.hatherkache.Service.Model.PC_DeliveryStatusModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MAP extends AppCompatActivity {

    private ArrayList<MAP_UserLocationModel> mUserArrayList = new ArrayList<>();
    private ArrayList<MAP_UserLocationModel> mUserLocations = new ArrayList<>();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private Button mBtn ,mShowRidersBtn;

    private String dsLevel2_Name = "NX";    //ERROR
    boolean showAllRiders = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_a_p);
        mBtn = (Button)findViewById(R.id.refresh);
        mShowRidersBtn = (Button)findViewById(R.id.map_show_riders_btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateUserListFragment();
            }
        });
        mShowRidersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllRiders = true;
                mapStartMethod();
            }
        });
        Log.d("MAP", "onResult: Class Start ");
        getIntentMethod();
        mapStartMethod();




    }
    private void addAllRidersLocation(){
        mDb.collection("HatherKacheApp").document("Location")
                .collection("Riders").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){

                }else{
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        MAP_UserLocationModel map_userLocationModel = documentSnapshot.toObject(MAP_UserLocationModel.class);
                        mUserLocations.add(map_userLocationModel);
                    }
                }
            }
        });
    }
    private void mapStartMethod() {
        if(intentErrorFound){
            Toast.makeText(MAP.this, "Intent Not Found ", Toast.LENGTH_SHORT).show();
            Toast.makeText(MAP.this, "FAKE LOCATION SETUP ", Toast.LENGTH_SHORT).show();
            Log.d("MAP", "onResult: Intent not Found ");

            Log.d("MAP", "onResult: executorService ");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // do background staff
                    Log.d("MAP", "onResult: Excuite Start; INTENT ERROR OrderType "+dsOrderType);
                    getUserLocation("wAgeyp6QXTutNTgYPBSz","FoodDelivery");
                    getUserLocation("13wjmGjiUBY1ydpqq3tSmblNlIh2","Users");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("MAP", "onResult: Excuite ON UI ");
                            //inflateUserListFragment();
                            delay();
                        }
                    });
                }
            });
        }else{
            Log.d("MAP", "onResult: executorService; OrderType "+dsOrderType);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // do background staff
                    Log.d("MAP", "onResult: Excuite Start, Intetn Found all Ok");
                    Log.d("MAP", "onResult: dsOrderType = "+dsOrderType);
                    Log.d("MAP", "onResult: dsRiderUserUID  = "+dsRiderUserUID);
                    getUserLocation(dsProductPlaceUID,dsOrderType);
                    getUserLocation(dsPresentUserUID,"Users");
                    if(showAllRiders == true){
                        addAllRidersLocation();
                    }
                    if(dsOrderType.equals("FoodDelivery")    || dsOrderType.equals("GroceryShopping")){
                        if(dsRiderUserUID.equals("NIL")){
                            dsRiderUID = "NIL"; //it will sent to MAP FRAGMENT , that will find shortest distance rider.
                        }else{
                            if(showAllRiders == false){
                                getUserLocation(dsRiderUserUID,"Riders");
                            }

                        }

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("MAP", "onResult: Excuite ON UI ");
                            //inflateUserListFragment();
                            delay();
                        }
                    });
                }
            });
        }
    }

    String dsOrderType = "NO";
    String dsProductPlaceUID = "NO";
    String dsPresentUserUID = "NO";
    String dsRiderUserUID = "NO";
    String dsOrderUID = "NO";
    String dsRiderUID = "NO";
    private boolean intentErrorFound = false;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {   Log.d("MAP", "onResult: Intent Found ");
            dsOrderType = intent.getExtras().getString("dsOrderType");    //Food Delivery or HomeService
            dsProductPlaceUID = intent.getExtras().getString("dsProductPlaceUID");    //ShopUID or HomerServiceCreatorUID
            dsPresentUserUID = intent.getExtras().getString("dsPresentUserUID");
            dsRiderUserUID = intent.getExtras().getString("dsRiderUserUID");
            dsOrderUID = intent.getExtras().getString("dsOrderUID");
            intentErrorFound = CheckIntentMethod(dsOrderType,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsProductPlaceUID,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsPresentUserUID,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsRiderUserUID,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsOrderUID,intentErrorFound);
            /*if(!intentErrorFound){  //if intent error found false then do the next work

            }*/
            Log.d("MAP", "onResult: Intent END ");
        }else{  Log.d("MAP", "onResult: Intent not Found ");
            Toast.makeText(this, "Intent Not Found ", Toast.LENGTH_SHORT).show();
            dsOrderType = "NO";
            dsProductPlaceUID = "NO";
            dsPresentUserUID = "NO";
            dsRiderUserUID = "NO";
            dsOrderUID = "NO";
        }
        if(dsOrderType.equals("FoodDelivery")    || dsOrderType.equals("GroceryShopping")){
            mShowRidersBtn.setVisibility(View.VISIBLE);
        }
    }
    private boolean CheckIntentMethod(String dsTestIntent, boolean dbIntentErrorFound ){

        if(dsTestIntent == null){   Log.d("MAP", "onResult: dsTestIntent null "+dsTestIntent);
            Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
            dbIntentErrorFound = true;
            return dbIntentErrorFound;
        }
        if(!dbIntentErrorFound){
            if (TextUtils.isEmpty(dsTestIntent)) {   Log.d("MAP", "onResult: TextUtils true "+dsTestIntent);
                dbIntentErrorFound = true;
                dsTestIntent= "NO";
                Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
                return dbIntentErrorFound;
            }

            if (dsTestIntent.equals("")){   Log.d("MAP", "onResult: dsTestIntent empty "+dsTestIntent);
                dbIntentErrorFound = true;
                dsTestIntent= "NO";
                Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
            }
        }

        return dbIntentErrorFound;
    }

    private android.os.Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 2000;
    public void delay(){
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                inflateUserListFragment();
                //mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }


    private void inflateUserListFragment(){ //For sharing the data from activity to fragment
        Log.d("MAP", "onResult: inflateUserListFragment");

        MAP_Fragment fragment = MAP_Fragment.newInstance();
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList(getString(R.string.intent_user_list), mUserList);
        bundle.putParcelableArrayList(getString(R.string.intent_user_locations), mUserLocations);
        bundle.putString("dsOrderUID",dsOrderUID);
        bundle.putString("dsRiderUID",dsRiderUID);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.user_list_container, fragment, getString(R.string.fragment_user_list));
        transaction.addToBackStack(getString(R.string.fragment_user_list));
        transaction.commit();
    }
    private void getUserLocation(String dsUserUID, String dsLevel2_Name){
        DocumentReference locationsRef = mDb
                .collection("HatherKacheApp").document("Location")
                .collection(dsLevel2_Name).document(dsUserUID);

        locationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    if(task.getResult().toObject(MAP_UserLocationModel.class) != null){
                       //Toast.makeText(getApplicationContext(),"User Found ",Toast.LENGTH_SHORT).show();
                        mUserLocations.add(task.getResult().toObject(MAP_UserLocationModel.class));
                    }else{
                        Toast.makeText(getApplicationContext(),dsUserUID+" User Location Not Found "+dsLevel2_Name,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"User Location Not Found ",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void GetUserList() {
        CollectionReference notebookRef;
        notebookRef = mDb.collection("HatherKacheApp")
                .document("REGISTER").collection("NORMAL_USER");//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3

        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if(queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getApplicationContext(),"No User Found ",Toast.LENGTH_SHORT).show();
                        }else {
                            int diSize = queryDocumentSnapshots.size();
                            int i = 0;
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                i++;
                                String dsUserUID = documentSnapshot.getId();
                                getUserLocation(dsUserUID, dsLevel2_Name);
                                /*if(i == diSize)
                                    delay();*/
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private String result = "res";
    public void setKonik(String result){
        this.result = result;
    }

}