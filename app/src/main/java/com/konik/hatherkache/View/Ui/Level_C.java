package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.konik.hatherkache.View.Adapter.Level_C_Adapter;
import com.konik.hatherkache.Service.Model.Level_C_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.konik.hatherkache.ViewModel.Level_C_VM;

import java.util.ArrayList;
import java.util.List;

public class Level_C extends AppCompatActivity  implements RecylerviewClickInterface {


    //Login
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    String dsUserEmail = "NO";
    private String dsCoreAdminEmail = "a6@gmail.com";

    //View Model
    private Level_C_VM level_c_vm;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level__c);
        mBookListRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_book_list);

        listL3ItemList = new ArrayList<>();
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dsUserEmail = user.getEmail();

                }else{

                }
            }
        };


       /* mLevelC_AddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!intentFoundError1 && !intentFoundError2){
                    Intent intent = new Intent(getApplicationContext(), Level_C_Add.class);
                    intent.putExtra("Level1_Name", dsLevel1_Name);
                    intent.putExtra("Level2_Name", dsLevel2_Name);
                    startActivity(intent);
                }

            }
        });*/
        getIntentMethod();

    }

    private void callViewModel() {
        Log.d("ViewModel", "allViewModel: start");
        level_c_vm = new ViewModelProvider(this).get(Level_C_VM.class);
        level_c_vm.LoadLevel3List(dsLevel1_Name,dsLevel2_Name).observe(this, new Observer<List<Level_C_Model>>() {
            @Override
            public void onChanged(List<Level_C_Model> level_c_models) {

                //Toast.makeText(getApplicationContext(), "level_c_models Size "+level_c_models.size() , Toast.LENGTH_SHORT).show();
                Log.d("ViewModel", "allViewModel: onChanged listview size = "+level_c_models.size());
                if (level_c_models.get(0).getL3Name().equals("NULL")){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Items Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }else{
                    listL3ItemList = level_c_models;
                    if(user != null){
                        if(dsLevel2_Name.equals("HomeServices") ){
                            if(dsUserEmail.equals(dsCoreAdminEmail)){
                                String dsPhoto = "https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FSylhet%2FFoodDelivery%2F1624336851876.JPEG?alt=media&token=132e8e3a-8a81-4607-8a2a-37506b37dd37";
                                listL3ItemList.add(new Level_C_Model("NA","ADD MORE", dsPhoto, "You can easily add more shops", "fakibuzzkonik@gmail.com", 1, 0, 0, 0));
                            }
                        }else{
                            String dsPhoto = "https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FSylhet%2FFoodDelivery%2F1624336851876.JPEG?alt=media&token=132e8e3a-8a81-4607-8a2a-37506b37dd37";
                            listL3ItemList.add(new Level_C_Model("NA","ADD MORE", dsPhoto, "You can easily add more shops", "fakibuzzkonik@gmail.com", 1, 0, 0, 0));
                        }
                    }

                    mlevel_c_adapter = new Level_C_Adapter(Level_C.this,level_c_models,Level_C.this);
                    mlevel_c_adapter.notifyDataSetChanged();
                    //mBookListRecyclerView.setLayoutManager(new GridLayoutManager(Level_C.this,1));
                    mBookListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL,false));
                    mBookListRecyclerView.setAdapter(mlevel_c_adapter);

                    //Moving The Recycler View.... Not For Others
                    mBookListRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            // Call smooth scroll
                            mBookListRecyclerView.smoothScrollBy(800,0);
                        }
                    });
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    } else {
                        mBookListRecyclerView.setPadding(0,220,0,220);
                    }
                }
            }
        });
    }

    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO";
    private boolean intentFoundError1 = true;
    private boolean intentFoundError2 = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsLevel1_Name = intent.getExtras().getString("Level1_Name");    //Sylhet
            dsLevel2_Name = intent.getExtras().getString("Level2_Name");    //Grocery or Food or Home Services
            if (TextUtils.isEmpty(dsLevel1_Name)) {
                dsLevel1_Name= "NO";
                Toast.makeText(getApplicationContext(), "dsLevel1_Name NULL  " , Toast.LENGTH_SHORT).show();
            } else if (dsLevel1_Name.equals("")){
                dsLevel1_Name= "NO";
                Toast.makeText(getApplicationContext(), "dsLevel1_Name 404" , Toast.LENGTH_SHORT).show();
            }else{
                intentFoundError1 = false;
            }
            if (TextUtils.isEmpty(dsLevel2_Name)) {
                dsLevel2_Name= "NO";
                Toast.makeText(getApplicationContext(), "dsLevel2_Name NULL  " , Toast.LENGTH_SHORT).show();
            } else if (dsLevel2_Name.equals("")){
                dsLevel2_Name= "NO";
                Toast.makeText(getApplicationContext(), "dsLevel2_Name 404" , Toast.LENGTH_SHORT).show();
            }else{
                intentFoundError2 =false;
                //Toast.makeText(All_Books.this, "Category_Name Found  " , Toast.LENGTH_SHORT).show()
                callViewModel();//View Model Connection
            }
        }else{
            dsLevel1_Name = "NO";
            dsLevel2_Name = "NO";
        }
    }

    private RecyclerView mBookListRecyclerView;
    List<Level_C_Model> listL3ItemList;
    Level_C_Adapter mlevel_c_adapter;

    @Override
    public void onItemClick(int position) {

        String dsLevel3Name = listL3ItemList.get(position).getL3Name();
        String dsLevel3UID = listL3ItemList.get(position).getL3UID();
        String dsLevel3CreatorUID = listL3ItemList.get(position).getL3Creator();
        int diLevel3PrivacyMode = listL3ItemList.get(position).getL3iPrivacy();
        if(diLevel3PrivacyMode == 1)    // public
            dsLevel3CreatorUID = "Public";

        if(dsLevel3Name.equals("ADD MORE")){
            //if(!intentFoundError1 && !intentFoundError2  && dsUserEmail.equals(dsCoreAdminEmail)){
            if(user == null) {
                Toast.makeText(getApplicationContext(), "Please Login",Toast.LENGTH_LONG).show();
            }else if(!intentFoundError1 && !intentFoundError2){
                Intent intent = new Intent(getApplicationContext(),Level_C_Add.class);
                intent.putExtra("Level1_Name", dsLevel1_Name);
                intent.putExtra("Level2_Name", dsLevel2_Name);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Core Admin 404",Toast.LENGTH_LONG).show();
            }
        }else{
            //Toast.makeText(getApplicationContext(), BookUID+ " ", Toast.LENGTH_SHORT).show();
            if(!intentFoundError1 && !intentFoundError2){
                Intent intent = new Intent(getApplicationContext(), Level_D.class);
                intent.putExtra("Level1_Name", dsLevel1_Name);
                intent.putExtra("Level2_Name", dsLevel2_Name);
                intent.putExtra("Level3_Name", dsLevel3Name);
                intent.putExtra("Level3_UID", dsLevel3UID);
                intent.putExtra("Level3_CreatorUID", dsLevel3CreatorUID);

                startActivity(intent);
            }
        }

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
    public void onItemLongCLick(int postion) {

    }

    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }
}