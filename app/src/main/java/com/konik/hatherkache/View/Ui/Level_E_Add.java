package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.konik.hatherkache.View.Adapter.Level_E_Add_PhotosAdapter;
import com.konik.hatherkache.Service.Model.Level_E_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Level_E_Add extends AppCompatActivity implements RecylerviewClickInterface {


    private Button mL5A_PhotoAddBtn,mL5A_PublishInfoBtn;
    private RecyclerView mL5A_FilesRecyclerview;
    private EditText mL5A_Name, mL5A_From, mL5A_Bio, mL5A_Address, mL5A_RatingEdit, mL5A_TotalViewsEdit, mL5A_TotalOrderEdit;
    private EditText mL5A_Quantity, mL5A_QuantityLimit, mL5A_SearchKey, mL5A_Price, mL5A_PriceDiscount,  mL5A_Loved,  mL5A_Priority;
    private TextView mL5A_DatePickText;
    private RadioButton mL5A_PublicRadioBtn, mL5A_PrivateRadioBtn;
    private RadioButton mL5A_PHourRadioBtn, mL5A_PDayRadioBtn,mL5A_PWeeklyRadioBtn, mL5A_PDMonthluRadioBtn;
    private RadioGroup mL5A_PaymentModeRadioGroup;
    private Spinner mL5A_BarkeywordSpinner;
    private EditText mL5A_BarKeyAddEdit;
    //Image Selecting Code
    private static final int RESULT_LOAD_IMAGE1 = 1;
    private List<String> dFileNameList;
    private List<String> dFileDoneList;
    private List<String> dsaPhotoUrlStringList ;
    List<String> dsaSpinnerList = new ArrayList<String>();
    private Level_E_Add_PhotosAdapter file_add_list_adapter;

    //Firebase Storage
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private StorageReference FileToUpload;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Variables
    private String dsBarKey = "NO";
    private String dsName = "NO", dsFrom = "NO", dsBio = "NO", dsAddress = "NO", dsRatingEdit = "NO", dsTotalViewsEdit = "NO", dsTotalOrderEdit = "NO";
    private String dsQuantity = "NO", dsQuantityLimit = "NO", dsSearchKey = "NO", dsPrice = "NO",   dsPriceDiscount = "NO",  dsLoved = "NO",  dsPrivacy = "NO";
    private String dUserUID = "NO",dsPriority = "NO", dsPaymentMode = "NO";
    private String dsBarKeyword = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level__e__add);
        //Address is not setted, Discount is not setted
        //Initialize
        mL5A_PhotoAddBtn = (Button)findViewById(R.id.level_e_add_image_btn);
        mL5A_Name = (EditText)findViewById(R.id.level_e_name);
        mL5A_From = (EditText)findViewById(R.id.level_e_from);
        mL5A_Bio = (EditText)findViewById(R.id.level_e_bio_text);
        mL5A_Address = (EditText)findViewById(R.id.level_e_address);
        mL5A_Priority = (EditText)findViewById(R.id.level_e_priority_edit);
        mL5A_RatingEdit = (EditText)findViewById(R.id.level_e_rating_edit);
        mL5A_TotalViewsEdit = (EditText)findViewById(R.id.level_e_total_views_edit);
        mL5A_TotalOrderEdit = (EditText)findViewById(R.id.level_e_total_order_edit);
        mL5A_Quantity = (EditText)findViewById(R.id.level_e_total_quantity_edit);
        mL5A_QuantityLimit = (EditText)findViewById(R.id.level_e_total_quantity_limit_edit);
        mL5A_SearchKey = (EditText)findViewById(R.id.level_e_search_edit);
        mL5A_Price = (EditText)findViewById(R.id.level_e_price);
        mL5A_PriceDiscount = (EditText)findViewById(R.id.level_e_price_discount);
        mL5A_Loved = (EditText)findViewById(R.id.level_e_total_loved);
        mL5A_DatePickText = (TextView)findViewById(R.id.level_e_publish_date_txt);
        mL5A_PublicRadioBtn = (RadioButton)findViewById(R.id.level_e_public_radio) ;
        mL5A_PrivateRadioBtn = (RadioButton)findViewById(R.id.level_e_private_radio) ;
        mL5A_PHourRadioBtn = (RadioButton)findViewById(R.id.level_e_perhour_radio);
        mL5A_PDayRadioBtn = (RadioButton)findViewById(R.id.level_e_perday_radio);
        mL5A_PWeeklyRadioBtn = (RadioButton)findViewById(R.id.level_e_perweek_radio);
        mL5A_PDMonthluRadioBtn = (RadioButton)findViewById(R.id.level_e_permonthly_radio);
        mL5A_PaymentModeRadioGroup = (RadioGroup)findViewById(R.id.level_e_payment_mode_radio_group);
        mL5A_PublishInfoBtn = (Button)findViewById(R.id.level_e_update_btn);
        mL5A_BarkeywordSpinner = (Spinner)findViewById(R.id.level_e_spiner_bar_key);
        mL5A_BarKeyAddEdit = (EditText) findViewById(R.id.level_e_bar_key_add);

        //Recycler View
        mL5A_FilesRecyclerview = (RecyclerView)findViewById(R.id.level_e_photo_recyclerview);
        dsaPhotoUrlStringList = new ArrayList<>();
        dFileNameList = new ArrayList<>();
        dFileDoneList = new ArrayList<>();
        file_add_list_adapter = new Level_E_Add_PhotosAdapter(dsaPhotoUrlStringList, dFileNameList,dFileDoneList,Level_E_Add.this);
        mL5A_FilesRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mL5A_FilesRecyclerview.setHasFixedSize(true);
        mL5A_FilesRecyclerview.setAdapter(file_add_list_adapter);


        getIntentMethod();
        SpinnerDataSetup();
        VisibilityMode(dsLevel2_Name);

        mL5A_PhotoAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!intentFoundError){
                    mL5A_PhotoAddBtn.setVisibility(View.GONE);
                    mL5A_PublishInfoBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Select Image Files",Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE1);
                }
            }
        });
        //mL5A_PublishInfoBtn.setVisibility(View.VISIBLE);
        mL5A_PublishInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataErrorFound = false;
                getDataFromViews();
                int len = dsaPhotoUrlStringList.size();
                if(!DataErrorFound && len > 0){
                    //showToast("DONE");
                    UplaodInfoToFirbase();
                }else{
                    showToast("Fillup All Correctly");
                }
            }
        });
    }
    boolean dbRertiveSuccessful = false;
    private void RetriveProductData() {
        db.collection("HatherKacheApp").document(dsLevel1_Name).collection(dsLevel2_Name)
                .document(dsLevel3_UID).collection("AllProducts").document(dsLevel5_Product_Edit_ID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dbRertiveSuccessful = true;
                Level_E_Model level_e_model = documentSnapshot.toObject(Level_E_Model.class);
                List<String> PhotoUrlStringList = level_e_model.getL5PhotoUrl();
                //mL5A_PhotoAddBtn.setEnabled(false);
                mL5A_PublishInfoBtn.setVisibility(View.VISIBLE);
                int diPhotoUrlListLen = PhotoUrlStringList.size();
                for(int i = 1; i<=diPhotoUrlListLen; i++){
                    dFileNameList.add("File No "+ i);
                    dFileDoneList.add("retrive");
                    dsaPhotoUrlStringList.add(PhotoUrlStringList.get(i-1));
                }
                file_add_list_adapter.notifyDataSetChanged();;
                mL5A_Name.setText(level_e_model.getL5Name());
                mL5A_From.setText(level_e_model.getL5From());
                mL5A_Bio.setText(level_e_model.getL5Bio());
                mL5A_Address.setText(level_e_model.getL5Address());
                mL5A_Priority.setText(String.valueOf(level_e_model.getL5iPriority()));
                mL5A_RatingEdit.setText(String.valueOf(level_e_model.getL5iRating()));
                mL5A_TotalViewsEdit.setText(String.valueOf(level_e_model.getL5iViews()));
                mL5A_TotalOrderEdit.setText(String.valueOf(level_e_model.getL5iOrders()));
                mL5A_Quantity.setText(String.valueOf(level_e_model.getL5iQuantity()));
                mL5A_QuantityLimit.setText(String.valueOf(level_e_model.getL5iQuantityLimit()));
                mL5A_SearchKey.setText(String.valueOf(level_e_model.getL5Search()));
                mL5A_Price.setText(String.valueOf(level_e_model.getL5iPrice()));
                mL5A_PriceDiscount.setText(String.valueOf(level_e_model.getL5iPriceDiscount()));
                mL5A_Loved.setText(String.valueOf(level_e_model.getL5iLoved()));
                dsPrivacy = level_e_model.getL5Privacy();
                dsPaymentMode = level_e_model.getL5PaymentMode();
                dsBarKeyword = level_e_model.getL5BarKeyword();
                dsaSpinnerList.clear();
                dsaSpinnerList.add(dsBarKeyword);
                dsaSpinnerList.add("Others");
                if(dsPrivacy.equals("Private")){
                    mL5A_PrivateRadioBtn.setChecked(true);
                }else{
                    mL5A_PublicRadioBtn.setChecked(true);
                }
                if(dsPaymentMode.equals("Hourly")){
                    mL5A_PHourRadioBtn.setChecked(true);
                }else if(dsPaymentMode.equals("Daily")){
                    mL5A_PDayRadioBtn.setChecked(true);
                }else if(dsPaymentMode.equals("Weekly")){
                    mL5A_PWeeklyRadioBtn.setChecked(true);
                }else if(dsPaymentMode.equals("Monthly")){
                    mL5A_PDMonthluRadioBtn.setChecked(true);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Retrive Data Failed",Toast.LENGTH_SHORT).show();;
            }
        });
    }
    private void VisibilityMode(String dsLevel2_name) {
        if(dsLevel2_name.equals("FoodDelivery") ){
            mL5A_From.setText(dsLevel3_Name);
            mL5A_Address.setText(dsLevel3_UID);
            mL5A_PHourRadioBtn.setChecked(true);

            mL5A_From.setVisibility(View.GONE);
            mL5A_Address.setVisibility(View.GONE);
            mL5A_PaymentModeRadioGroup.setVisibility(View.GONE);
            mL5A_Quantity.setHint("Qunatity 1:x");
        }else if(dsLevel2_name.equals("GroceryShopping")){
            mL5A_From.setText(dsLevel3_Name);
            mL5A_Address.setText(dsLevel3_UID);
            mL5A_PHourRadioBtn.setChecked(true);

            mL5A_From.setVisibility(View.GONE);
            mL5A_Address.setVisibility(View.GONE);
            mL5A_PaymentModeRadioGroup.setVisibility(View.GONE);
            mL5A_Quantity.setHint("Weight (gram)");
        }else if(dsLevel2_name.equals("HomeServices")){
            mL5A_Quantity.setHint("Experienced Years");
            mL5A_QuantityLimit.setText("5");;
            mL5A_QuantityLimit.setVisibility(View.GONE);
        }
    }

    private void UplaodInfoToFirbase() {
        Toast.makeText(getApplicationContext(), "Uploading Information", Toast.LENGTH_SHORT).show();
        dUserUID = FirebaseAuth.getInstance().getUid();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());

        int diRatingEdit = Integer.parseInt(dsRatingEdit);
        int diTotalViewsEdit = Integer.parseInt(dsTotalViewsEdit);
        int diTotalOrderEdit = Integer.parseInt(dsTotalOrderEdit);
        int diQuantity = Integer.parseInt(dsQuantity);
        int diQuantityLimit = Integer.parseInt(dsQuantityLimit);
        int diPrice = Integer.parseInt(dsPrice);
        int diPriceDiscount = Integer.parseInt(dsPriceDiscount);
        int diLoved = Integer.parseInt(dsLoved);
        int diPriority = Integer.parseInt(dsPriority);
        long diDate = System.currentTimeMillis();
        long diDate2 = 1623163000000L;
        diDate = diDate - diDate2;
        int diMaxValue = 214748364;
        if(diMaxValue < diDate)
            diDate = 0;
        Map<String, Object> note = new HashMap<>();
        note.put("L5Name", dsName);
        note.put("L5From", dsFrom);
        note.put("L5PhotoUrl", dsaPhotoUrlStringList);
        note.put("L5Bio", dsBio);
        note.put("L5Address", dsAddress);
        note.put("L5Search", dsName+" "+ dsFrom+ " "+dsSearchKey);

        note.put("L5Privacy", dsPrivacy);
        note.put("L5PaymentMode", dsPaymentMode);
        note.put("L5UIDoCreator", dUserUID);
        note.put("L5UIDofL3", dsLevel3_UID);
        note.put("L5UIDofL4", dsLevel4_UID);
        note.put("L5UIDofL2", dsLevel2_Name);
        note.put("L5BarKeyword", dsBarKeyword);
        note.put("L5Extra", "1");
        note.put("L5Phone", "01726638108");

        note.put("L5iRating", diRatingEdit);
        note.put("L5iViews", diTotalViewsEdit);
        note.put("L5iOrders", diTotalOrderEdit);
        note.put("L5iQuantity", diQuantity);
        note.put("L5iQuantityLimit", diQuantityLimit);
        note.put("L5iPrice", diPrice);
        note.put("L5iPriceDiscount", diPriceDiscount);
        note.put("L5iLoved", diLoved);
        //Bar Key, ExtraKey, Creator id, Department Uid, SubCategory UID Not added, PhotoURL Array
        note.put("L5iPriority", diPriority);
        note.put("L5iRegDate", diDate); //long long
        DocumentReference product_ref = db.collection("HatherKacheApp").document(dsLevel1_Name).collection(dsLevel2_Name).document(dsLevel3_UID);
        if(dsLevel5_Product_Edit_ID.equals("NO")){
            Toast.makeText(getApplicationContext(),"Uploading New Item", Toast.LENGTH_SHORT).show();
            product_ref.collection("AllProducts").add(note)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            mL5A_PublishInfoBtn.setText("UPDATED");
                            mL5A_Name.setText("");
                            mL5A_Bio.setText("");

                            finish();
                            Intent intent = new Intent(getApplicationContext(), Level_E.class);
                            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.putExtra("Level1_Name", dsLevel1_Name);
                            intent.putExtra("Level2_Name", dsLevel2_Name);
                            intent.putExtra("Level3_Name", dsLevel3_Name);
                            intent.putExtra("Level3_UID", dsLevel3_UID);
                            intent.putExtra("Level4_Name", dsLevel4_Name);
                            intent.putExtra("Level4_UID", dsLevel4_UID);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    mL5A_PublishInfoBtn.setText("FAILED");
                    mL5A_Name.setText("");
                    mL5A_Bio.setText("");
                    Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Updating New Item", Toast.LENGTH_SHORT).show();
            product_ref.collection("AllProducts").document(dsLevel5_Product_Edit_ID).update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Successfully Updated", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    mL5A_PublishInfoBtn.setText("UPDATED");
                    mL5A_Name.setText("");
                    mL5A_Bio.setText("");

                    finish();
                    Intent intent = new Intent(getApplicationContext(),Level_E.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("Level1_Name", dsLevel1_Name);
                    intent.putExtra("Level2_Name", dsLevel2_Name);
                    intent.putExtra("Level3_Name", dsLevel3_Name);
                    intent.putExtra("Level3_UID", dsLevel3_UID);
                    intent.putExtra("Level4_Name", dsLevel4_Name);
                    intent.putExtra("Level4_UID", dsLevel4_UID);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    mL5A_PublishInfoBtn.setText("FAILED");
                    mL5A_Name.setText("");
                    mL5A_Bio.setText("");
                    Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK){
            if(data.getClipData() != null){
                ///Multiple file selected
                int diRetriveFileListSize = dFileNameList.size();
                int dTotalItemSelected = data.getClipData().getItemCount();
                for(int i = 0; i<dTotalItemSelected; i++){      //LoopStart
                    Uri dFileUri = data.getClipData().getItemAt(i).getUri();
                    final int dFinali = i+diRetriveFileListSize;
                    final String dDate = String.valueOf(System.currentTimeMillis());
                    ///RecyclerView Ready
                    final String dFileNmae = getFileName(dFileUri);
                    //dFileNameList.add(dFileNmae);
                    //dFileDoneList.add("uploading");
                    //dsaPhotoUrlStringList.add("NO");
                    //file_add_list_adapter.notifyDataSetChanged();

                    //Firebase Code Start
                    String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
                    FileToUpload = mStorage.child("HatherKacheApp/"+dsLevel1_Name+"/"+dsLevel2_Name+"/"+dsLevel3_UID+"/"+"AllProducts"+"/"+ dsTimeMiliSeconds +"."+getFileExtention(dFileUri));
                    FileToUpload.putFile(dFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getApplicationContext(), String.valueOf(dFinali+1)+"File Uploaded" , Toast.LENGTH_SHORT).show();
                            //RecyclerView Update


                            //Get Photo Download URl
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    if(!imageUrl.equals("NO")){
                                        int diFileSize = dFileDoneList.size();
                                        //Log.e("Level_E_Add", "onResult:FileToUpload: diFileSize = "+diFileSize +  " dFinali "+dFinali);
                                        if(dFinali <= diFileSize){
                                            Log.e("Level_E_Add", "onResult:FileToUpload: Done diFileSize = "+diFileSize +  " dFinali "+dFinali);
                                            dFileNameList.add(dFileNmae);
                                            dsaPhotoUrlStringList.add(imageUrl);
                                            dFileDoneList.add(dFinali, "done"); //insted of uploading word we set don
                                            file_add_list_adapter.notifyDataSetChanged();;
                                            showToast("File Added "+dsaPhotoUrlStringList.size());
                                        }else{
                                            Log.e("Level_E_Add", "onResult:FileToUpload: xDONE diFileSize = "+diFileSize +  " dFinali "+dFinali);
                                            dFileNameList.add(dFileNmae);
                                            dsaPhotoUrlStringList.add(imageUrl);
                                            dFileDoneList.add(diFileSize, "done"); //insted of uploading word we set don

                                            showToast(dFinali+" i no File Done ");
                                        }


                                    }else{
                                        int diFileSize = dFileDoneList.size();
                                        Log.e("Level_E_Add", "onResult:FileToUpload: Failed diFileSize = "+diFileSize +  " dFinali "+dFinali);
                                        if(dFinali <= diFileSize){
                                            dFileDoneList.add(dFinali, "Failed. (no url)"); //insted of uploading word we set don
                                            file_add_list_adapter.notifyDataSetChanged();;
                                            showToast(" Failed to UPLAOD ");
                                            mL5A_PublishInfoBtn.setText("FAILED");
                                            mL5A_PublishInfoBtn.setEnabled(false);
                                        }else{
                                            showToast(" Failed to UPLAOD ");
                                            mL5A_PublishInfoBtn.setText("FAILED");
                                            mL5A_PublishInfoBtn.setEnabled(false);
                                        }

                                    }

                                }
                            });
                            /*FileToUpload.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast("URL GET FAILED "+dsaPhotoUrlStringList.size());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String dFileUrl = uri.toString();
                                    //dsaPhotoUrlStringList.add(FileToUpload.getDownloadUrl().toString(););
                                    showToast("File Added "+dsaPhotoUrlStringList.size());
                                }
                            });*/

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed File Uplaod !" , Toast.LENGTH_SHORT).show();
                            //Recycler View Update
                            int diFileSize = dFileDoneList.size();
                            if(dFinali <= diFileSize){{
                                dFileNameList.add(dFileNmae);
                                dsaPhotoUrlStringList.add("NX");
                                dFileDoneList.add(dFinali, "failed");
                                file_add_list_adapter.notifyDataSetChanged();;
                            }}else{
                                dFileNameList.add(dFileNmae+"Failed");
                                dsaPhotoUrlStringList.add("NX");
                                file_add_list_adapter.notifyDataSetChanged();;
                            }


                        }
                    });
                }   //loop End
            }else if(data.getData() != null){
                //selected single file
                Toast.makeText(getApplicationContext(), "Single File Selected" , Toast.LENGTH_SHORT).show();
                // share_file_to_another_app(FileUri);
                Uri dFileUri = data.getData();
                final String dDate = String.valueOf(System.currentTimeMillis());
                ///RecyclerView Ready
                final String dFileNmae = getFileName(dFileUri);
                dFileNameList.add(dFileNmae);
                dFileDoneList.add("uploading");
                dsaPhotoUrlStringList.add("NO");
                file_add_list_adapter.notifyDataSetChanged();
                //Firebase Start
                String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
                FileToUpload = mStorage.child("HatherKacheApp/"+dsLevel1_Name+"/"+dsLevel2_Name+"/"+dsLevel3_UID+"/"+"AllProducts"+"/"+ dsTimeMiliSeconds +"."+getFileExtention(dFileUri));
                FileToUpload.putFile(dFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Single File Uploaded" , Toast.LENGTH_SHORT).show();

                        //Get Photo Download URl
                        FileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String dFileUrl = uri.toString();
                                dsaPhotoUrlStringList.remove(dFileNameList.size()-1);
                                dsaPhotoUrlStringList.add(dFileUrl);
                                //RecyclerView Update
                                dFileDoneList.remove(dFileNameList.size()-1);
                                dFileDoneList.add(dFileNameList.size()-1, "done"); //insted of uploading word we set done
                                file_add_list_adapter.notifyDataSetChanged();;
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed File Uplaod !" , Toast.LENGTH_SHORT).show();
                        //Recycler View Update
                        dFileDoneList.remove(dFileNameList.size()-1);
                        dFileDoneList.add(dFileNameList.size()-1, "failed");

                        file_add_list_adapter.notifyDataSetChanged();;
                    }
                });


            }else{
                Toast.makeText(getApplicationContext(), "File Not Selected" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean DataErrorFound = false;
    private boolean StopChecking = false;
    private void getDataFromViews() {

        dsName = mL5A_Name.getText().toString();
        dsFrom = mL5A_From.getText().toString();
        dsBio = mL5A_Bio.getText().toString();
        dsAddress = mL5A_Address.getText().toString();
        dsPriority = mL5A_Priority.getText().toString();
        dsRatingEdit = mL5A_RatingEdit.getText().toString();
        dsTotalViewsEdit = mL5A_TotalViewsEdit.getText().toString();
        dsTotalOrderEdit = mL5A_TotalOrderEdit.getText().toString();
        dsQuantity = mL5A_Quantity.getText().toString();
        dsQuantityLimit = mL5A_QuantityLimit.getText().toString();
        dsSearchKey = mL5A_SearchKey.getText().toString();
        dsPrice = mL5A_Price.getText().toString();
        dsPriceDiscount = mL5A_PriceDiscount.getText().toString();
        dsLoved = mL5A_Loved.getText().toString();
        String dsBarKey = mL5A_BarkeywordSpinner.getSelectedItem().toString();
        dsBarKeyword = mL5A_BarkeywordSpinner.getSelectedItem().toString();
        if(dsBarKey.equals("Others")){
            dsBarKeyword = mL5A_BarKeyAddEdit.getText().toString();
            if(dsBarKeyword.equals(""))
                dsBarKeyword = "NO";
        }

        DataErrorFound = CheckDataError(dsName, "dsName");
        DataErrorFound = CheckDataError(dsFrom, "dsFrom");
        DataErrorFound = CheckDataError(dsBio, "dsBio");
        DataErrorFound = CheckDataError(dsAddress, "dsAddress");
        DataErrorFound = CheckDataError(dsPriority, "dsPriority");
        DataErrorFound = CheckDataError(dsRatingEdit, "dsRatingEdit");
        DataErrorFound = CheckDataError(dsTotalViewsEdit, "dsTotalViewsEdit");
        DataErrorFound = CheckDataError(dsTotalOrderEdit, "dsTotalOrderEdit");
        DataErrorFound = CheckDataError(dsQuantity, "dsQuantity");
        DataErrorFound = CheckDataError(dsQuantityLimit, "dsQuantityLimit");
        DataErrorFound = CheckDataError(dsSearchKey, "dsSearchKey");
        DataErrorFound = CheckDataError(dsPrice, "dsPrice");
        DataErrorFound = CheckDataError(dsPriceDiscount, "dsPriceDiscount");
        DataErrorFound = CheckDataError(dsLoved, "dsLoved");
        DataErrorFound = CheckDataError(dsBarKeyword, "dsBarKeyword");

        DataErrorFound = isInteger(dsPriority);
        DataErrorFound = isInteger(dsRatingEdit);
        DataErrorFound = isInteger(dsTotalViewsEdit);
        DataErrorFound = isInteger(dsTotalOrderEdit);
        DataErrorFound = isInteger(dsQuantity);
        DataErrorFound = isInteger(dsQuantityLimit);
        DataErrorFound = isInteger(dsPrice);
        DataErrorFound = isInteger(dsPriceDiscount);
        DataErrorFound = isInteger(dsLoved);

        if(mL5A_PrivateRadioBtn.isChecked()){
            dsPrivacy = mL5A_PrivateRadioBtn.getText().toString();
        }else if(mL5A_PublicRadioBtn.isChecked()){
            dsPrivacy = mL5A_PublicRadioBtn.getText().toString();
        }else{
            if(!DataErrorFound) //DataErrorFound == false
                showToast("Please Select Privacy");
            DataErrorFound = true;
        }
        if(mL5A_PHourRadioBtn.isChecked()){
            dsPaymentMode = mL5A_PHourRadioBtn.getText().toString();
        }else if(mL5A_PDayRadioBtn.isChecked()){
            dsPaymentMode = mL5A_PDayRadioBtn.getText().toString();
        }else if(mL5A_PWeeklyRadioBtn.isChecked()){
            dsPaymentMode = mL5A_PWeeklyRadioBtn.getText().toString();
        }else if(mL5A_PDMonthluRadioBtn.isChecked()){
            dsPaymentMode = mL5A_PDMonthluRadioBtn.getText().toString();
        }else{
            if(!DataErrorFound) //DataErrorFound == false
                showToast("Please Select Payment Mode");
            DataErrorFound = true;
        }


    }
    private String dsDataErrorFoundWord;
    private boolean CheckDataError(String dsWordCheck, String Type) {
        if(!DataErrorFound){
            if(dsWordCheck.equals("NO")){
                DataErrorFound = true;
            }else if(dsWordCheck.equals("")){
                DataErrorFound = true;
            }else{
                DataErrorFound = false;
            }
            if (TextUtils.isEmpty(dsWordCheck)) {
                DataErrorFound = true;
            }
            if(DataErrorFound)
                Toast.makeText(getApplicationContext(), "Error on "+Type+" = "+dsWordCheck , Toast.LENGTH_LONG).show();
        }
        return DataErrorFound;
    }
    private boolean isInteger(String str) {
        if(!DataErrorFound) {
            if (str == null) {
                showToast("add intger" + str);
                DataErrorFound = true;
            }
            int length = str.length();
            if (length == 0) {
                showToast("int len zero " + str);
                DataErrorFound = true;
            }
            int i = 0;
            if (str.charAt(0) == '-') {
                if (length == 1) {
                    showToast("int invalid " + str);
                    DataErrorFound = true;
                }
                i = 1;
            }
            for (; i < length; i++) {
                char c = str.charAt(i);
                if (c < '0' || c > '9') {
                    showToast("int wrong " + str);
                    DataErrorFound = true;
                }
            }
        }
        return DataErrorFound;
    }

    //Get Intent Start
    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO", dsLevel3_Name = "NO", dsLevel3_UID = "NO", dsLevel4_Name = "NO", dsLevel4_UID = "NO", dsLevel5_Product_Edit_ID = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsLevel1_Name = intent.getExtras().getString("Level1_Name");    //Sylhet
            dsLevel2_Name = intent.getExtras().getString("Level2_Name");    //Grocery or Food or Home Services
            dsLevel3_Name = intent.getExtras().getString("Level3_Name");    //Doctor,Engineer,Chef / Bakeries, Cake, Vegetables / Pachbhai, Panshi, RoyalChef
            dsLevel3_UID = intent.getExtras().getString("Level3_UID");      //Level 3 UID
            dsLevel4_Name = intent.getExtras().getString("Level4_Name");    //Sub Category
            dsLevel4_UID = intent.getExtras().getString("Level4_UID");      //Sub Category UID
            dsLevel4_UID = intent.getExtras().getString("Level4_UID");      //Sub Category UID
            dsLevel5_Product_Edit_ID = intent.getExtras().getString("Level5_Product_Edit_ID","NO");      //Sub Category UID

            intentFoundError = CheckIntentMethod(dsLevel1_Name);
            intentFoundError = CheckIntentMethod(dsLevel2_Name);
            intentFoundError = CheckIntentMethod(dsLevel3_Name);
            intentFoundError = CheckIntentMethod(dsLevel3_UID);
            intentFoundError = CheckIntentMethod(dsLevel4_Name);
            intentFoundError = CheckIntentMethod(dsLevel4_UID);
            //intentFoundError = CheckIntentMethod(dsLevel5_Product_Edit_ID);

            if(!intentFoundError){
                if(dsLevel5_Product_Edit_ID.equals("NO")){
                    //Add Product
                    Toast.makeText(getApplicationContext(),"Add Product Info",Toast.LENGTH_SHORT).show();;
                    Log.d("Level_E_Add", "onResult: Add Product Info");
                }else{
                    Toast.makeText(getApplicationContext(),"Edit Product Info",Toast.LENGTH_SHORT).show();;
                    Log.d("Level_E_Add", "onResult: Edit Product Info");
                    RetriveProductData();
                }
            }
        }else{
            dsLevel1_Name = "NO";
            dsLevel2_Name = "NO";
            dsLevel3_Name = "NO";
            dsLevel3_UID = "NO";
            dsLevel4_Name = "NO";
            dsLevel4_UID = "NO";
            dsLevel5_Product_Edit_ID = "NO";

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
    private void SpinnerDataSetup() {
        ArrayAdapter<String> SpinnerdataAdapter;
        if(dsLevel2_Name.equals("GroceryShopping")){
            dsaSpinnerList.clear();
            dsaSpinnerList.add("Popular");
            dsaSpinnerList.add("Others");
            dsaSpinnerList.add("Latest");
            dsaSpinnerList.add("Branded");
            dsaSpinnerList.add("Chipper");
        }else if(dsLevel2_Name.equals("FoodDelivery")){
            dsaSpinnerList.clear();
            dsaSpinnerList.add("Latest");
            dsaSpinnerList.add("Popular");
            dsaSpinnerList.add("Chipper");
            dsaSpinnerList.add("Royal");
            dsaSpinnerList.add("Best One");
        }else if(dsLevel2_Name.equals("HomeServices")){
            dsaSpinnerList.clear();
            dsaSpinnerList.add("Senior");
            dsaSpinnerList.add("Junior");
            dsaSpinnerList.add("Intern");
            dsaSpinnerList.add("Newbie");
        }else{
            dsaSpinnerList.add("Popular");
        }
        SpinnerdataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dsaSpinnerList);
        mL5A_BarkeywordSpinner.setAdapter(SpinnerdataAdapter);
        mL5A_BarkeywordSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Others")){
                    mL5A_BarKeyAddEdit.setVisibility(View.VISIBLE);

                    if(dsLevel2_Name.equals("GroceryShopping")  && dbRertiveSuccessful == false){
                        //mL5A_Bio.setText("Most important food item of all time. In Bangladesh its hard to skip this item. We really appreciate your choice. Chose this hope you will not disapointed.");
                        //mL5A_Address.setText(dsLevel3_UID);
                        //mL5A_RatingEdit.setText("4");
                        //Random myRadndom = new Random();
                        //int diViews = myRadndom.nextInt(3000)+2000;
                        //int diTotalOrders = myRadndom.nextInt(100)+200;
                        //int diQuantity = myRadndom.nextInt(25)+5;
                        //int diQunatityLimit = myRadndom.nextInt(5)+3;
                        //int diTotalLove = myRadndom.nextInt(50)+50;
                        //mL5A_TotalViewsEdit.setText(String.valueOf(diViews));
                        //mL5A_TotalOrderEdit.setText(String.valueOf(diTotalOrders));;
                        //mL5A_Quantity.setText(String.valueOf(diQuantity));;
                        //mL5A_QuantityLimit.setText(String.valueOf(250));;
                        //mL5A_Loved.setText(String.valueOf(diTotalLove));
                        Toast.makeText(getApplicationContext(),"Bar key = "+dsBarKeyword,Toast.LENGTH_SHORT).show();
                    }

                }else{
                    mL5A_BarKeyAddEdit.setVisibility(View.GONE);
                    mL5A_BarKeyAddEdit.setText("NIX");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(),"Please Select",Toast.LENGTH_SHORT).show();
            }
        });
        /*mL5A_BarkeywordSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int postion, long l) {
                if(parent.getItemAtPosition(postion).equals("Choose City")){
                    Toast.makeText(getApplicationContext(),"Please Select City",Toast.LENGTH_SHORT).show();
                }else{
                    String spinner_item = parent.getItemAtPosition(postion).toString();
                    String dGetValue= ""; ///comment
                    String dSelectedCategoryUID = "";
                    for(Map.Entry m:map.entrySet()){
                        dGetValue = m.getValue().toString();
                        if(dGetValue.equals(spinner_item)) {
                            dSelectedCategoryUID = m.getKey().toString();
                            break;
                        }
                    }
                    dCategoryType = spinner_item;
                    dCategoryUID = dSelectedCategoryUID;
                    Toast.makeText(getApplicationContext(),dSelectedCategoryUID,Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),"S "+spinner_item,Toast.LENGTH_SHORT).show();
                    dsBarKeyword = spinner_item;
                    //dCategory = spinner_item;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
    }
    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }
    //Date, File Name , File Size, File Type -- > Database
    public String getFileName(Uri uri) {    //File Name from URI METHOD
        String result = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    public void showToast(String ToastWord){
        Toast.makeText(getApplicationContext(), ToastWord , Toast.LENGTH_SHORT).show();
    }
    //Recycler View Method Implemetns
    @Override
    public void onItemClick(int position) {
            showToast("Deleted");
            dsaPhotoUrlStringList.remove(position);
            dFileNameList.remove(position);
            dFileDoneList.remove(position);
            file_add_list_adapter.notifyDataSetChanged();;
    }

    @Override
    public void onItemLongCLick(int postion) {

    }

    @Override
    public void onItemClickAuthorID(int position, String AuthorUID) {

    }
}