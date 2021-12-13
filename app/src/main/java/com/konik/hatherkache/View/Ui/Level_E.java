package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import com.konik.hatherkache.View.Adapter.Level_E_Adapter;
import com.konik.hatherkache.View.Adapter.Level_E_Bar_Adapter;
import com.konik.hatherkache.Service.Model.Level_E_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewBarBtnClickInterface;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Level_E extends AppCompatActivity implements RecylerviewClickInterface, RecylerviewBarBtnClickInterface {
    RecylerviewBarBtnClickInterface recylclerBarBtnInterface;
    private RecyclerView mLevelE_RecyclerView;
    private RecyclerView mLevelE_BAR_RecyclerView;
    List<Level_E_Model> listL5ItemList;
    List<String> listL5BarKeyword;
    Level_E_Adapter mlevel_e_adapter;
    Level_E_Bar_Adapter mlevel_e_bar_adapter;

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
        setContentView(R.layout.activity_level__e);
        /////////Toolbar Start
        mainToolBar= findViewById(R.id.mainToolbarId);
        toolbarUserImage= findViewById(R.id.user_image);
        toolbarTextView= findViewById(R.id.toolbarSearchTextId);
        //toolbarTextView.setText("Player List");
        setSupportActionBar(mainToolBar);
        //toolbar setup hoye gelo

        mLevelE_RecyclerView = (RecyclerView)findViewById(R.id.level_e_recyclerview);
        mLevelE_BAR_RecyclerView = (RecyclerView)findViewById(R.id.level_e_bar_item_recyclerview);
        listL5ItemList = new ArrayList<>();
        listL5BarKeyword = new ArrayList<>();
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
                Intent intent = new Intent(Level_E.this, UserLogin.class);
                startActivity(intent);
            }
        });
    }


    //////////Toolbar CODE
    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.normal_menu,menu);
        ////////////SEARCHING CODE///////////////////////////
        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        MenuItem myActionMenuItem2 = menu.findItem( R.id.action_cart);
        searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                Toast.makeText(getApplicationContext(),"S : "+query,Toast.LENGTH_SHORT).show();
                //serachType(query);
                //LoadLevel5List(dsLevel1_Name, dsLevel2_Name, dsLevel3_UID, query);

                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(getApplicationContext(),"X: "+s,Toast.LENGTH_SHORT).show();
                filter(s);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_cart:
                if(user != null){
                    Intent intent = new Intent(getApplicationContext(), PB_Cart.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_LONG).show();
                }

                return true;
             case R.id.level_e_add_item:{
                 level_e_add_Activity_Start();

                 return true;
             }


        }
        return super.onOptionsItemSelected(item);
    }

    private void level_e_add_Activity_Start() {

        if(user == null ){
            Toast.makeText(getApplicationContext(), "Please Login First." , Toast.LENGTH_SHORT).show();
        }else  if(intentFoundError ){
            Toast.makeText(getApplicationContext(), "Intent Error" , Toast.LENGTH_SHORT).show();
        }else if(user != null){
            //Toast.makeText(getApplicationContext(), "dsLevel4_UID "+dsLevel4_UID , Toast.LENGTH_SHORT).show();
            db.collection("HatherKacheApp").document(dsLevel1_Name)
                    .collection(dsLevel2_Name).document(dsLevel3_UID)
                    .collection("Level4List").document(dsLevel4_UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String dsL4Creator = documentSnapshot.getString("L4Creator");
                        long dlL4iPrivacy = documentSnapshot.getLong("L4iPrivacy");
                        if(dlL4iPrivacy == 0){
                            Toast.makeText(getApplicationContext(), "Private Mode" , Toast.LENGTH_SHORT).show();
                            if(dsL4Creator.equals(dUserUID)){
                                Intent intentx = new Intent(getApplicationContext(), Level_E_Add.class);
                                intentx.putExtra("Level1_Name", dsLevel1_Name);
                                intentx.putExtra("Level2_Name", dsLevel2_Name);
                                intentx.putExtra("Level3_Name", dsLevel3_Name);
                                intentx.putExtra("Level3_UID", dsLevel3_UID);
                                intentx.putExtra("Level4_Name", dsLevel4_Name);
                                intentx.putExtra("Level4_UID", dsLevel4_UID);
                                startActivity(intentx);
                            }else{
                                Toast.makeText(getApplicationContext(), "You are not the Creator" , Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Creator ID: "+dsL4Creator , Toast.LENGTH_SHORT).show();
                            }
                        }else if(dlL4iPrivacy == 1){
                            Toast.makeText(getApplicationContext(), "Public Mode" , Toast.LENGTH_SHORT).show();
                            Intent intentx = new Intent(getApplicationContext(), Level_E_Add.class);
                            intentx.putExtra("Level1_Name", dsLevel1_Name);
                            intentx.putExtra("Level2_Name", dsLevel2_Name);
                            intentx.putExtra("Level3_Name", dsLevel3_Name);
                            intentx.putExtra("Level3_UID", dsLevel3_UID);
                            intentx.putExtra("Level4_Name", dsLevel4_Name);
                            intentx.putExtra("Level4_UID", dsLevel4_UID);
                            startActivity(intentx);
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

    public void  filter(String dsGetSearchKey){
        listL5Filtered = new ArrayList<>();;

        int list_size = listL5ItemList.size();
        for(int i = 0; i<list_size; i++){
            Level_E_Model mkey = listL5ItemList.get(i);
            String item = mkey.getL5Search().toLowerCase();
            if(item.contains(dsGetSearchKey.toLowerCase())){
                listL5Filtered.add(mkey);
            }
        }
        mlevel_e_adapter = new Level_E_Adapter(Level_E.this,listL5Filtered,Level_E.this,dsLevel2_Name);
        mlevel_e_adapter.notifyDataSetChanged();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLevelE_RecyclerView.setLayoutManager(new GridLayoutManager(Level_E.this,3));
            mLevelE_RecyclerView.setAdapter(mlevel_e_adapter);
        } else {
            mLevelE_RecyclerView.setLayoutManager(new GridLayoutManager(Level_E.this,2));
            mLevelE_RecyclerView.setAdapter(mlevel_e_adapter);
        }
    }
    private void LoadLevel5List(String dsLevel1_name, String dsLevel2_name, String dsLevel3_uid, String dsGetSearhKey) {

        notebookRef = db.collection("HatherKacheApp").document(dsLevel1_name)
                .collection(dsLevel2_name).document(dsLevel3_uid)
                .collection("AllProducts");//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3
        if(dsGetSearhKey.equals("NO")){
            //.orderBy("L5iPriority", Query.Direction.ASCENDING).startAt(dsGetSearhKey).endAt(dsGetSearhKey+"\uf8ff");
        }else{
            notebookRef.orderBy("L5Search", Query.Direction.ASCENDING).startAt(dsGetSearhKey).endAt(dsGetSearhKey+"\uf8ff");
        }
        Query level5ListCollectionRef = notebookRef.whereEqualTo("L5UIDofL3",dsLevel3_uid).whereEqualTo("L5UIDofL4",dsLevel4_UID);
        if(dsLevel4_UID.equals("AllInOne")){
             level5ListCollectionRef = notebookRef.whereEqualTo("L5UIDofL3",dsLevel3_uid);
        }else{
             level5ListCollectionRef = notebookRef.whereEqualTo("L5UIDofL3",dsLevel3_uid).whereEqualTo("L5UIDofL4",dsLevel4_UID);
        }

        /*notebookRef
                .whereEqualTo("L5UIDofL3",dsLevel3_uid)
                .whereEqualTo("L5UIDofL4",dsLevel4_UID)*/
                //.orderBy("L5iPriority", Query.Direction.ASCENDING)
        level5ListCollectionRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
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
                                Level_E_Model Level_e_Model = documentSnapshot.toObject(Level_E_Model.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                 String dsLevelD_ItemUID = documentSnapshot.getId();

                                 String dsL5Name = Level_e_Model.getL5Name();
                                 String dsL5From = Level_e_Model.getL5From();
                                 List<String> dsaL5PhotoUrlStringList = new ArrayList<String>() ;
                                dsaL5PhotoUrlStringList = Level_e_Model.getL5PhotoUrl();
                                 String dsL5Bio = Level_e_Model.getL5Bio();
                                 String dsL5Address = Level_e_Model.getL5Address();
                                 String dsL5Search = Level_e_Model.getL5Search();

                                 String dsL5Privacy = Level_e_Model.getL5Privacy();
                                 String dsL5PaymentMode = Level_e_Model.getL5PaymentMode();
                                 String dsL5UIDoCreator = Level_e_Model.getL5UIDoCreator();
                                 String dsL5UIDofL3 = Level_e_Model.getL5UIDofL3();
                                 String dsL5UIDofL4 = Level_e_Model.getL5UIDofL4();
                                 String dsL5UIDofL2 = Level_e_Model.getL5UIDofL2();
                                 String dsL5BarKeyword = Level_e_Model.getL5BarKeyword();
                                 String dsL5Extra = Level_e_Model.getL5Extra();
                                 String dsL5Phone = Level_e_Model.getL5Phone();

                                 int  diL5iRating = Level_e_Model.getL5iRating();
                                 int  diL5iViews = Level_e_Model.getL5iViews();
                                 int  diL5iOrders = Level_e_Model.getL5iOrders();
                                 int  diL5iQuantity = Level_e_Model.getL5iQuantity();
                                 int  diL5iQuantityLimit = Level_e_Model.getL5iQuantityLimit();
                                 int  diL5iPrice = Level_e_Model.getL5iPrice();
                                 int  diL5iPriceDiscount = Level_e_Model.getL5iPriceDiscount();
                                 int  diL5iLoved = Level_e_Model.getL5iLoved();
                                //Bar Key, ExtraKey, Creator id, Department Uid, SubCategory UID Not added, PhotoURL Array
                                 int  diL5iPriority = Level_e_Model.getL5iPriority();
                                 long  diL5iRegDateLong = Level_e_Model.getL5iRegDate();
                                 Log.e("PB_OrderDetails", "onResult:LongData: diL5iRegDateLong = "+diL5iRegDateLong );
                                 int diMaxValue = 214748364;
                                 int diMinValue = -2147483648;
                                 int diL5iRegDate = 0;
                                 if(diMaxValue <= diL5iRegDateLong   || diMinValue >= diL5iRegDateLong  )
                                     diL5iRegDate = 0;
                                 else
                                     diL5iRegDate = (int)(diL5iRegDateLong);


                                listL5ItemList.add(new Level_E_Model(dsLevelD_ItemUID, dsL5Name, dsL5From, dsaL5PhotoUrlStringList,
                                        dsL5Bio, dsL5Address, dsL5Search, dsL5Privacy,
                                        dsL5PaymentMode, dsL5UIDoCreator, dsL5UIDofL3,
                                        dsL5UIDofL4, dsL5UIDofL2, dsL5BarKeyword,
                                        dsL5Extra, dsL5Phone, diL5iRating, diL5iViews,
                                        diL5iOrders, diL5iQuantity, diL5iQuantityLimit, diL5iPrice,
                                        diL5iPriceDiscount, diL5iLoved, diL5iPriority, diL5iRegDate));



                                ;
                                addBarKeyOnList(dsL5BarKeyword);
                            }
                        }

                        //Collections.reverse(listBook);
                        mlevel_e_adapter = new Level_E_Adapter(Level_E.this,listL5ItemList,Level_E.this, dsLevel2_Name);
                        mlevel_e_adapter.notifyDataSetChanged();
                        int orientation = getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            mLevelE_RecyclerView.setLayoutManager(new GridLayoutManager(Level_E.this,3));
                            mLevelE_RecyclerView.setAdapter(mlevel_e_adapter);
                        } else {
                            mLevelE_RecyclerView.setLayoutManager(new GridLayoutManager(Level_E.this,2));
                            mLevelE_RecyclerView.setAdapter(mlevel_e_adapter);
                        }
                        ///////LEVEL L5 BAR KEY RECYCLER VIEW SETUP//////////

                        /////LEVEL E BAR RECYCLER VIEW
                        mlevel_e_bar_adapter = new Level_E_Bar_Adapter(Level_E.this,listL5BarKeyword, Level_E.this, dsLevel2_Name);
                        mlevel_e_bar_adapter.notifyDataSetChanged();
                        //mLevelE_RecyclerView.setLayoutManager(new GridLayoutManager(Level_E.this,2));
                        mLevelE_BAR_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL,false));
                        mLevelE_BAR_RecyclerView.setAdapter(mlevel_e_bar_adapter);


                        /*Map<Integer,String> map=new HashMap();
                        map.put(100,"Amit");
                        map.put(100,"Sumit");
                        map.put(100,"Konik");
                        map.put(101,"Vijay");
                        map.put(102,"Vijay");
                        ArrayList<Integer> sortedKeys = new ArrayList<Integer>(map.keySet());
                        Collections.sort(sortedKeys);

                        // Display the TreeMap which is naturally sorted
                        for (int x : sortedKeys)
                            System.out.println("Key = " + x
                                    + ", Value = " + map.get(x));*/
                        ////////////////

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }



    private void checkMulitpleWord(String dsL5BarKeyword) {
        int diListL5BarSize = listL5BarKeyword.size();
        boolean matched = false;
        for(int i = 0; i<diListL5BarSize; i++){
            String dsOldL5BarKeyword = listL5BarKeyword.get(i);
            if(dsOldL5BarKeyword.equals(dsL5BarKeyword)){
                matched = true; break;
            }
        }
        if(matched == false){
            listL5BarKeyword.add(dsL5BarKeyword);
        }
    }

    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO", dsLevel3_Name = "NO",
            dsLevel3_UID = "NO", dsLevel4_Name = "NO", dsLevel4_UID = "NO";
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
            dsLevel4_Name = intent.getExtras().getString("Level4_Name");    //Doctor,Engineer,Chef / Bakeries, Cake, Vegetables / Pachbhai, Panshi, RoyalChef
            dsLevel4_UID = intent.getExtras().getString("Level4_UID");    //Level 3 UID

            intentFoundError = CheckIntentMethod(dsLevel1_Name);
            intentFoundError = CheckIntentMethod(dsLevel2_Name);
            intentFoundError = CheckIntentMethod(dsLevel3_Name);
            intentFoundError = CheckIntentMethod(dsLevel3_UID);
            intentFoundError = CheckIntentMethod(dsLevel4_Name);
            intentFoundError = CheckIntentMethod(dsLevel4_UID);

            if(!intentFoundError ){
                LoadLevel5List(dsLevel1_Name, dsLevel2_Name, dsLevel3_UID, "NO");
            }
        }else{
            dsLevel1_Name = "NO";
            dsLevel2_Name = "NO";
            dsLevel3_Name = "NO";
            dsLevel3_UID = "NO";
            dsLevel4_Name = "NO";
            dsLevel4_UID = "NO";

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

    @Override
    public void onItemClick(int position) {
        String dsLevel5_Name = "NO";
        String dsLevel5_UID = "NO";
        String dsLevel5_CreatorUID = "NO";
        if(listL5Filtered.size() > 0){
             dsLevel5_Name = listL5Filtered.get(position).getL5Name();
             dsLevel5_UID = listL5Filtered.get(position).getL5UID();
             dsLevel5_CreatorUID = listL5Filtered.get(position).getL5UIDoCreator();
        }else{
             dsLevel5_Name = listL5ItemList.get(position).getL5Name();
             dsLevel5_UID = listL5ItemList.get(position).getL5UID();
             dsLevel5_CreatorUID = listL5ItemList.get(position).getL5UIDoCreator();
        }

        if(!intentFoundError  && !dsLevel5_Name.equals("NO") && !dsLevel5_UID.equals("NO")  ){
            Intent intent = new Intent(getApplicationContext(), Product.class);
            intent.putExtra("Level1_Name", dsLevel1_Name);
            intent.putExtra("Level2_Name", dsLevel2_Name);
            intent.putExtra("Level3_Name", dsLevel3_Name);
            intent.putExtra("Level3_UID", dsLevel3_UID);
            intent.putExtra("Level4_Name", dsLevel4_Name);  //Sub Category Name
            intent.putExtra("Level4_UID", dsLevel4_UID);    //Sub Category UID
            intent.putExtra("Level5_Name", dsLevel5_Name);  //Product Name
            intent.putExtra("Level5_UID", dsLevel5_UID);    //Product UID
            intent.putExtra("Level5_CreatorUID", dsLevel5_CreatorUID);    //Product Creator UID
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Conditions Failed" , Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onItemLongCLick(int position) {
        Toast.makeText(getApplicationContext(), "Long Pressed" , Toast.LENGTH_SHORT).show();
        String dsLevel5_Name = "NO";
        String dsLevel5_UID = "NO";
        String dsProductCreatorUID = "NO";
        String dsProductPrivacy= "NO";
        if(user != null){
            if(listL5Filtered.size() > 0){
                dsLevel5_Name = listL5Filtered.get(position).getL5Name();
                dsLevel5_UID = listL5Filtered.get(position).getL5UID();
                dsProductCreatorUID = listL5Filtered.get(position).getL5UIDoCreator();
                dsProductPrivacy = listL5Filtered.get(position).getL5Privacy();
            }else{
                dsLevel5_Name = listL5ItemList.get(position).getL5Name();
                dsLevel5_UID = listL5ItemList.get(position).getL5UID();
                dsProductCreatorUID = listL5ItemList.get(position).getL5UIDoCreator();
                dsProductPrivacy = listL5ItemList.get(position).getL5Privacy();
            }

            if(dsProductPrivacy.equals("Private")   && dsProductCreatorUID.equals(dUserUID)) {
                if(!intentFoundError ){
                    Intent intentx = new Intent(getApplicationContext(),Level_E_Add.class);
                    intentx.putExtra("Level1_Name", dsLevel1_Name);
                    intentx.putExtra("Level2_Name", dsLevel2_Name);
                    intentx.putExtra("Level3_Name", dsLevel3_Name);
                    intentx.putExtra("Level3_UID", dsLevel3_UID);
                    intentx.putExtra("Level4_Name", dsLevel4_Name);
                    intentx.putExtra("Level4_UID", dsLevel4_UID);
                    intentx.putExtra("Level5_Product_Edit_ID", dsLevel5_UID);  //EDIT or Add
                    startActivity(intentx);
                }
            }else if(dsProductPrivacy.equals("Private")  ){
                    Toast.makeText(getApplicationContext(), "Permission not allowed" , Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Creator UID: "+dsProductCreatorUID , Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Product Creator UID: "+dsProductCreatorUID , Toast.LENGTH_SHORT).show();
            }else if(dsProductPrivacy.equals("Public")  ){
                Toast.makeText(getApplicationContext(), "Product privacy is public" , Toast.LENGTH_SHORT).show();
                if(!intentFoundError ){
                    Intent intentx = new Intent(getApplicationContext(),Level_E_Add.class);
                    intentx.putExtra("Level1_Name", dsLevel1_Name);
                    intentx.putExtra("Level2_Name", dsLevel2_Name);
                    intentx.putExtra("Level3_Name", dsLevel3_Name);
                    intentx.putExtra("Level3_UID", dsLevel3_UID);
                    intentx.putExtra("Level4_Name", dsLevel4_Name);
                    intentx.putExtra("Level4_UID", dsLevel4_UID);
                    intentx.putExtra("Level5_Product_Edit_ID", dsLevel5_UID);  //EDIT or Add
                    startActivity(intentx);
                }
            }else{
                Toast.makeText(getApplicationContext(), "Condition not matched." , Toast.LENGTH_SHORT).show();
            }


        }else{
            Toast.makeText(getApplicationContext(), "Please Login" , Toast.LENGTH_SHORT).show();
        }

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


    @Override
    public void  onBarItemClick(int position) {
        //Click on bar item
        String dsBarKey = listL5BarKeyword.get(position);
        filter_L5_BarKeyword(dsBarKey);
        Toast.makeText(getApplicationContext(),"CLICKEWD ",Toast.LENGTH_SHORT).show();
    }
    List<Level_E_Model> listL5Filtered = new ArrayList<>();;
    public void  filter_L5_BarKeyword(String dsGetSearchKey){
        listL5Filtered = new ArrayList<>();;
        int diLen = dsGetSearchKey.length();
        String dsFinalWord = "";
        for(int j = 0; j<diLen; j++){
            char ch2 = dsGetSearchKey.charAt(j);
            if(ch2 == ' '){
                ch2 = '_';
                dsFinalWord = dsFinalWord+ch2;
            }else {
                dsFinalWord = dsFinalWord+ch2;
            }

        } dsGetSearchKey = dsFinalWord;



        int list_size = listL5ItemList.size();
        for(int i = 0; i<list_size; i++){
            Level_E_Model mkey = listL5ItemList.get(i);
            String item = mkey.getL5BarKeyword().toLowerCase();
            if(item.contains(dsGetSearchKey.toLowerCase())){
                listL5Filtered.add(mkey);
            }
        }
        mlevel_e_adapter = new Level_E_Adapter(Level_E.this,listL5Filtered,Level_E.this,dsLevel2_Name);
        mlevel_e_adapter.notifyDataSetChanged();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLevelE_RecyclerView.setLayoutManager(new GridLayoutManager(Level_E.this,3));
            mLevelE_RecyclerView.setAdapter(mlevel_e_adapter);
        } else {
            mLevelE_RecyclerView.setLayoutManager(new GridLayoutManager(Level_E.this,2));
            mLevelE_RecyclerView.setAdapter(mlevel_e_adapter);
        }
    }
    private void addBarKeyOnList(String word) {
        int len = word.length();
        String dsNewWord = "";
        for(int i =0 ; i<len; i++){
            char ch = word.charAt(i);
            if(ch == ' '){
                int diLen = dsNewWord.length();
                String dsFinalWord = "";
                for(int j = 0; j<diLen; j++){
                    char ch2 = dsNewWord.charAt(j);
                    if(ch2 == ' '){

                    }else if(ch2 == '_'){
                        ch2 = ' ';
                        dsFinalWord = dsFinalWord+ch2;
                    }else {
                        dsFinalWord = dsFinalWord+ch2;
                    }

                } dsNewWord = dsFinalWord;
                if(!dsNewWord.equals(""))
                    checkMulitpleWord(dsNewWord);
                dsNewWord = "";
                //break;
            }else{
                dsNewWord = dsNewWord+ch;
            }
        }
        int diLen = dsNewWord.length();
        String dsFinalWord = "";
        for(int j = 0; j<diLen; j++){
            char ch2 = dsNewWord.charAt(j);
            if(ch2 == '_'){
                ch2 = ' ';
                dsFinalWord = dsFinalWord+ch2;
            }else{
                dsFinalWord = dsFinalWord+ch2;
            }
        }
        dsNewWord = dsFinalWord;
        if(!dsNewWord.equals(""))
            checkMulitpleWord(dsNewWord);
    }
}
/////////////////Level_E_Bar_Recycler_Adapter////////////////////////////////////

