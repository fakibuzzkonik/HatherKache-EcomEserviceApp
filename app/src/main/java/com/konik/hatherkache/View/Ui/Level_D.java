package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.konik.hatherkache.Service.Model.Level_C_Model;
import com.konik.hatherkache.View.Adapter.Level_C_Adapter;
import com.konik.hatherkache.View.Adapter.Level_D_Adapter;
import com.konik.hatherkache.Service.Model.Level_D_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.konik.hatherkache.ViewModel.Level_C_VM;
import com.konik.hatherkache.ViewModel.Level_D_VM;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class Level_D extends AppCompatActivity implements RecylerviewClickInterface {

    private RecyclerView mLevelD_RecyclerView;
    List<Level_D_Model> listL4ItemList;
    Level_D_Adapter mlevel_d_adapter;

    private CollectionReference notebookRef ;    //for quires of data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ////////Toolbar
    private Toolbar mainToolBar;
    private ImageView toolbarUserImage;
    private TextView toolbarTextView;
    //Variables
    private String dUserUID = "NO";
    String dUserProfilePicURL = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level__d);

        /////////Toolbar Start
        mainToolBar= findViewById(R.id.level_d_mainToolbarId);
        toolbarUserImage= findViewById(R.id.user_image);
        toolbarTextView= findViewById(R.id.toolbarSearchTextId);
        setSupportActionBar(mainToolBar);
        toolbarTextView.setVisibility(GONE);
        //toolbar setup hoye gelo
        mLevelD_RecyclerView = (RecyclerView)findViewById(R.id.level_d_recyclerview);
        listL4ItemList = new ArrayList<>();

        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dUserUID = user.getUid();

                    if(user.getPhotoUrl() != null){
                         dUserProfilePicURL = user.getPhotoUrl().toString();
                         Picasso.get().load(dUserProfilePicURL).into(toolbarUserImage);
                    }

                }else{

                }
            }
        };
        getIntentMethod();
        /////////////
        toolbarUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Level_D.this, UserLogin.class);
                startActivity(intent);
            }
        });
    }
    //////////Toolbar CODE

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.level_d_tool_bar,menu);
        ////////////SEARCHING CODE///////////////////////////
        //MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
       // MenuItem myActionMenuItem2 = menu.findItem( R.id.action_cart);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_all_product:{
                Toast.makeText(getApplicationContext(), "ADD A CLASS", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.action_add_more:{
                if(!intentFoundError){
                    level_d_add_Activity_Start();
                }
                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }
    private void level_d_add_Activity_Start() {

        if(user == null ){
            Toast.makeText(getApplicationContext(), "Please Login First." , Toast.LENGTH_SHORT).show();
        }else  if(intentFoundError ){
            Toast.makeText(getApplicationContext(), "Intent Error" , Toast.LENGTH_SHORT).show();
        }else if(user != null){
            db.collection("HatherKacheApp").document(dsLevel1_Name)
                    .collection(dsLevel2_Name).document(dsLevel3_UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String dsL3Creator = documentSnapshot.getString("L3Creator");
                        long dlL3iPrivacy = documentSnapshot.getLong("L3iPrivacy");
                        if(dlL3iPrivacy == 0){
                            Toast.makeText(getApplicationContext(), "Private Mode" , Toast.LENGTH_SHORT).show();
                            if(dsL3Creator.equals(dUserUID)){
                                Intent intent = new Intent(getApplicationContext(),Level_D_Add.class);
                                intent.putExtra("Level1_Name", dsLevel1_Name);
                                intent.putExtra("Level2_Name", dsLevel2_Name);
                                intent.putExtra("Level3_Name", dsLevel3_Name);
                                intent.putExtra("Level3_UID", dsLevel3_UID);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "You are not the Creator" , Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Creator ID: "+dsL3Creator , Toast.LENGTH_SHORT).show();
                            }
                        }else if(dlL3iPrivacy == 1){
                            Toast.makeText(getApplicationContext(), "Public Mode" , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),Level_D_Add.class);
                            intent.putExtra("Level1_Name", dsLevel1_Name);
                            intent.putExtra("Level2_Name", dsLevel2_Name);
                            intent.putExtra("Level3_Name", dsLevel3_Name);
                            intent.putExtra("Level3_UID", dsLevel3_UID);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Privacy Mode Error!" , Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Level 4 not exists" , Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {
                    Toast.makeText(getApplicationContext(), "Level 4 Privacy Retrive Failed" , Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO",
            dsLevel3_Name = "NO", dsLevel3_UID = "NO", dsLevel3_CreatorUID = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsLevel1_Name = intent.getExtras().getString("Level1_Name");    //Sylhet
            dsLevel2_Name = intent.getExtras().getString("Level2_Name");    //Grocery or Food or Home Services
            dsLevel3_Name = intent.getExtras().getString("Level3_Name");    //Doctor,Engineer,Chef / Bakeries, Cake, Vegetables / Pachbhai, Panshi, RoyalChef
            dsLevel3_UID = intent.getExtras().getString("Level3_UID");    //Level 3 UID
            dsLevel3_CreatorUID = intent.getExtras().getString("Level3_CreatorUID");    //Level 3 UID

            intentFoundError = CheckIntentMethod(dsLevel1_Name);
            intentFoundError = CheckIntentMethod(dsLevel2_Name);
            intentFoundError = CheckIntentMethod(dsLevel3_Name);
            intentFoundError = CheckIntentMethod(dsLevel3_UID);
            intentFoundError = CheckIntentMethod(dsLevel3_CreatorUID);

            if(!intentFoundError){
                 callViewModel();
            }
        }else{
            dsLevel1_Name = "NO";
            dsLevel2_Name = "NO";
            dsLevel3_Name = "NO";
            dsLevel3_UID = "NO";
        }

    }
    private boolean CheckIntentMethod(String dsTestIntent){
        if (TextUtils.isEmpty(dsTestIntent)) {
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
        } else if (dsLevel1_Name.equals("")){
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
        }else{
            intentFoundError = false;
        }
        return intentFoundError;
    }

    //View Model
    private Level_D_VM level_d_vm;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:4 level_d_vm start");
        level_d_vm = new ViewModelProvider(this).get(Level_D_VM.class);
        level_d_vm.LoadLevel4List(dsLevel1_Name, dsLevel2_Name, dsLevel3_UID).observe(this, new Observer<List<Level_D_Model>>() {
            @Override
            public void onChanged(List<Level_D_Model> level_d_models) {
                Log.d("ViewModel", "allViewModel:4 onChanged listview4 size = "+level_d_models.size());
                if (level_d_models.get(0).getL4Name().equals("NULL")){
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
                    //Collections.reverse(listBook);
                    mlevel_d_adapter = new Level_D_Adapter(Level_D.this,level_d_models,Level_D.this);
                    mlevel_d_adapter.notifyDataSetChanged();
                    //
                    listL4ItemList = level_d_models;
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mLevelD_RecyclerView.setLayoutManager(new GridLayoutManager(Level_D.this,2));
                        mLevelD_RecyclerView.setAdapter(mlevel_d_adapter);
                    } else {
                        mLevelD_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                        mLevelD_RecyclerView.setAdapter(mlevel_d_adapter);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        String dsLevel4_Name = listL4ItemList.get(position).getL4Name();
        String dsLevel4_UID = listL4ItemList.get(position).getL4Uid();
        //Toast.makeText(getApplicationContext(), BookUID+ " ", Toast.LENGTH_SHORT).show();
        if(!intentFoundError){
            Intent intent = new Intent(getApplicationContext(), Level_E.class);
            intent.putExtra("Level1_Name", dsLevel1_Name);
            intent.putExtra("Level2_Name", dsLevel2_Name);
            intent.putExtra("Level3_Name", dsLevel3_Name);
            intent.putExtra("Level3_UID", dsLevel3_UID);
            intent.putExtra("Level4_Name", dsLevel4_Name);
            intent.putExtra("Level4_UID", dsLevel4_UID);
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongCLick(int postion) {
    }

    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }
    //Login
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
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
}