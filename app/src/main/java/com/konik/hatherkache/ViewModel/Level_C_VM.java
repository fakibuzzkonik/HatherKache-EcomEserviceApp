package com.konik.hatherkache.ViewModel;

import android.app.Application;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.konik.hatherkache.Service.Model.Level_C_Model;
import com.konik.hatherkache.View.Adapter.Level_C_Adapter;
import com.konik.hatherkache.View.Ui.Level_C;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Level_C_VM extends AndroidViewModel {

    private MutableLiveData mLiveData;

    public Level_C_VM(@NonNull Application application) {
        super(application);
        Log.d("ViewModel", "allViewModel: Level_C_VM start");
    }

    public MutableLiveData<List<Level_C_Model>> LoadLevel3List(String dsLevel1_name, String dsLevel2_name){
        List<Level_C_Model> listL3ItemList ; listL3ItemList =new ArrayList<>();
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel: LoadLevel3List start");
        if(mLiveData == null){
            mLiveData = new MutableLiveData();
            Log.d("ViewModel", "allViewModel: mLiveData null ");
        notebookRef = db.collection("HatherKacheApp").document(dsLevel1_name).collection(dsLevel2_name);//.orderBy("time", Query.Direction.ASCENDING).limitToLast(3
        notebookRef.orderBy("L3iPriority", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                        String data = "";
                        //Collections.reverse(listBook);
                        if(queryDocumentSnapshots.isEmpty()) {
                            //Toast.makeText(getApplicationContext(),"No Items Found ",Toast.LENGTH_SHORT).show();
                            listL3ItemList.add(new Level_C_Model("dsLevelC_ItemUID","NULL", "dsLevelC_PhotoUrl", "dsLevelC_Bio", "dsLevelC_ItemCreatorUID", 0, 0, 0, 0));
                            mLiveData.postValue(listL3ItemList);
                            Log.d("ViewModel", "allViewModel: queryDocumentSnapshots empty");
                        }else {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Level_C_Model level_c_model = documentSnapshot.toObject(Level_C_Model.class);
                                //messageModel.setDocumentID(documentSnapshot.getId());
                                String dsLevelC_ItemUID = documentSnapshot.getId();
                                String dsLevelC_Name = level_c_model.getL3Name();
                                String dsLevelC_PhotoUrl = level_c_model.getL3PhotoUrl();
                                String dsLevelC_Bio= level_c_model.getL3Bio();
                                String dsLevelC_ItemCreatorUID = level_c_model.getL3Creator();
                                int diPrivacy = level_c_model.getL3iPrivacy();
                                int diPriority = level_c_model.getL3iPriority();
                                int diViewCount = level_c_model.getL3iViewCount();
                                int diTotalProducts = level_c_model.getL3iTotalProducts();

                                //Random random = new Random();   int diRandom = random.nextInt(100);
                                //String l3Name, String l3PhotoUrl, String l3Bio, String l3Creator, int l3iPrivacy, int l3iPriority, int l3iViewCount, int l3iTotalProducts
                                listL3ItemList.add(new Level_C_Model(dsLevelC_ItemUID,dsLevelC_Name, dsLevelC_PhotoUrl, dsLevelC_Bio, dsLevelC_ItemCreatorUID, diPrivacy, diPriority, diViewCount, diTotalProducts));
                                mLiveData.postValue(listL3ItemList);
                                Log.d("ViewModel", "allViewModel: mLiveData posting value");
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
        Log.d("ViewModel", "allViewModel: mLiveData total list size"+listL3ItemList.size());
        return mLiveData;
    }


}
