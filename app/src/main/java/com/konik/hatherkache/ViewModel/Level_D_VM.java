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
import com.konik.hatherkache.Service.Model.Level_D_Model;
import com.konik.hatherkache.View.Adapter.Level_D_Adapter;
import com.konik.hatherkache.View.Ui.Level_D;

import java.util.ArrayList;
import java.util.List;

public class Level_D_VM extends AndroidViewModel {
    public Level_D_VM(@NonNull  Application application) {
        super(application);
        Log.d("ViewModel", "allViewModel:4 Level_D_VM start");
    }
    public MutableLiveData mLiveData;
    public MutableLiveData<List<Level_D_Model>> LoadLevel4List(String dsLevel1_name, String dsLevel2_name, String dsLevel2_uid) {
        List<Level_D_Model> listL4ItemList ; listL4ItemList =new ArrayList<>();
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("HatherKacheApp").document(dsLevel1_name)
                .collection(dsLevel2_name).document(dsLevel2_uid)
                .collection("Level4List");
        if(mLiveData == null) {
            mLiveData = new MutableLiveData();
            notebookRef.orderBy("L4iPriority", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {

                                listL4ItemList.add(new Level_D_Model("dsLevelD_ItemUID","NULL", "dsLevel_D_PhotoUrl", "dsLevel_D_Bio", "dsLevel_D_SearchKey", "dsLevel_D_Extra", "dsLevel_D_ItemCreatorUID", 0, 0, 0, 0));
                                mLiveData.postValue(listL4ItemList);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            }else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Level_D_Model level_d_model = documentSnapshot.toObject(Level_D_Model.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsLevelD_ItemUID = documentSnapshot.getId();
                                    String dsLevel_D_Name = level_d_model.getL4Name();
                                    String dsLevel_D_PhotoUrl = level_d_model.getL4PhotoUrl();
                                    String dsLevel_D_Bio= level_d_model.getL4Bio();
                                    String dsLevel_D_SearchKey= level_d_model.getL4Search();
                                    String dsLevel_D_Extra= level_d_model.getL4Bio();
                                    String dsLevel_D_ItemCreatorUID = level_d_model.getL4Creator();
                                    int diPrivacy = level_d_model.getL4iPrivacy();
                                    int diPriority = level_d_model.getL4iPriority();
                                    int diViewCount = level_d_model.getL4iViewCount();
                                    int diTotalProducts = level_d_model.getL4iTotalProducts();
                                    //Level_D_Model(String l4Uid, String l4Name, String l4PhotoUrl, String l4Bio, String l4Search, String l4Creator, String l4Extra, int l4iPrivacy, int l4iPriority, int l4iViewCount, int l4iTotalProducts
                                    listL4ItemList.add(new Level_D_Model(dsLevelD_ItemUID,dsLevel_D_Name, dsLevel_D_PhotoUrl, dsLevel_D_Bio, dsLevel_D_SearchKey, dsLevel_D_Extra, dsLevel_D_ItemCreatorUID, diPrivacy, diPriority, diViewCount, diTotalProducts));
                                    mLiveData.postValue(listL4ItemList);
                                }
                                listL4ItemList.add(new Level_D_Model("AllInOne","All In One", "https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FADMIN%2FLevel4%2Fall%20in%20one.jpg?alt=media&token=f02b217d-6102-4f35-a48f-65506571d17e",
                                        "Found all of them here.", "dsLevel_D_SearchKey", "dsLevel_D_Extra", "dsLevel_D_ItemCreatorUID", 1, 100, 423, 200));
                                mLiveData.postValue(listL4ItemList);    //All Items level 4 , it is a one type category

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        return mLiveData;
    }
}
