package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.konik.hatherkache.Service.Map.MAP_SetLocation_Fragment;
import com.konik.hatherkache.Service.Map.MAP_UserLocationModel;
import com.konik.hatherkache.R;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Level_C_Add extends AppCompatActivity {
    //Note Search Key not added 404
    private ImageView mLevel3_ImageView;
    private EditText mLevel3_Name, mLevel3_BioText, mLevel3_Priority, mLevel3_ViewCount, mLevel3_TotalProducts;
    private RadioGroup mLevel3_PrivacyRadioGroup;
    private RadioButton mLevel3_PublicRadioBtn, mLevel3_PrivateRadioBtn;
    private Button mLevel3_UpdateBtn;
    private Button mLevel3_SetOnMap;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level__c__add);

        mLevel3_ImageView = (ImageView)findViewById(R.id.level_c_imageview);
        mLevel3_Name = (EditText)findViewById(R.id.level_c_name);
        mLevel3_BioText = (EditText)findViewById(R.id.level_c_bio_text);
        mLevel3_Priority = (EditText)findViewById(R.id.level_c_priority);
        mLevel3_ViewCount = (EditText)findViewById(R.id.level_c_view_count);
        mLevel3_TotalProducts = (EditText)findViewById(R.id.level_c_total_products);
        mLevel3_PrivacyRadioGroup = (RadioGroup)findViewById(R.id.level_c_privacy_radio_group);
        mLevel3_PublicRadioBtn = (RadioButton)findViewById(R.id.level_c_public_radio);
        mLevel3_PrivateRadioBtn = (RadioButton)findViewById(R.id.level_c_private_radio);
        mLevel3_SetOnMap = (Button)findViewById(R.id.level_c_set_map_btn);
        mLevel3_UpdateBtn = (Button)findViewById(R.id.level_c_update_btn);
        getIntentMethod();
        if(dsLevel2_Name.equals("HomeServices"))
            mLevel3_SetOnMap.setVisibility(View.GONE);
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                String dsUserName = user.getDisplayName();
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Add Level3 Information", Toast.LENGTH_SHORT).show();;

                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        mLevel3_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });
        mLevel3_SetOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateUserListFragment();
            }
        });
        mLevel3_UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsL3Name = mLevel3_Name.getText().toString();
                dsL3Bio = mLevel3_BioText.getText().toString();
                dsL3Priority = mLevel3_Priority.getText().toString();
                dsL3ViewCount = mLevel3_ViewCount.getText().toString();
                dsL3TotalProducts = mLevel3_TotalProducts.getText().toString();
                if(!mLevel3_PublicRadioBtn.isChecked() && !mLevel3_PrivateRadioBtn.isChecked()){
                    Toast.makeText(getApplicationContext(),"Please Select Privacy", Toast.LENGTH_SHORT).show();
                }else if(mLevel3_PublicRadioBtn.isChecked()){
                    dsL3Privacy = "Public";
                }else if(mLevel3_PrivateRadioBtn.isChecked()){
                    dsL3Privacy = "Private";
                }
                if(imageUriResultCrop == null){
                    if(imageUri_storage == null){
                        Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(getApplicationContext(),"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dsL3Privacy.equals("NO") || dsL3Name.equals("NO")  || dsL3Bio.equals("NO") || dsL3Priority.equals("NO") || dsL3ViewCount.equals("NO") || dsL3TotalProducts.equals("NO") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if(dsL3Privacy.equals("") || dsL3Name.equals("")  || dsL3Bio.equals("") || dsL3Priority.equals("") || dsL3ViewCount.equals("") || dsL3TotalProducts.equals("") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if(intentFoundError1 || intentFoundError2){
                   Toast.makeText(getApplicationContext(),"Intent 404",Toast.LENGTH_SHORT).show();
                }else if(myGeo == null  && !dsLevel2_Name.equals("HomeServices")){
                   Toast.makeText(getApplicationContext(),"Select Map Location",Toast.LENGTH_SHORT).show();
                }else{
                    diL3Priority = Integer.parseInt(dsL3Priority);
                    diL3ViewCount = Integer.parseInt(dsL3ViewCount);
                    diL3TotalProducts = Integer.parseInt(dsL3TotalProducts);
                    if(dsL3Privacy.equals("Public")){
                        diPrivacy = 1;
                    }else if(dsL3Privacy.equals("Private")){
                        diPrivacy = 0;
                    }
                    UploadCropedImageFunction(imageUriResultCrop);
                }
            }
        });
    }
    ////////CALLING FRAGMENT////////////
    private void inflateUserListFragment(){ //For sharing the data from activity to fragment


        MAP_SetLocation_Fragment fragment = MAP_SetLocation_Fragment.newInstance();
        Bundle bundle = new Bundle();
        //bundle.putParcelableArrayList(getString(R.string.intent_user_list), mUserList);
        //bundle.putParcelableArrayList(getString(R.string.intent_user_locations), mUserLocations);
        //fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.level_c_set_map_frame, fragment, getString(R.string.fragment_set_map_address));
        transaction.addToBackStack(getString(R.string.fragment_set_map_address));
        transaction.commit();
    }
    ////////////////FRAGMENT CODE END

    private String dUserUID = "NO";
    private String dsPhotoUrl = "NO", dsL3Name = "NO", dsL3Bio = "NO", dsL3Priority = "NO", dsL3ViewCount = "NO", dsL3TotalProducts = "NO",dsL3Privacy = "NO";
    private int  diL3Priority = 0, diL3ViewCount = 0, diL3TotalProducts = 0, diPrivacy = 0;
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
                //Toast.makeText(All_Books.this, "Category_Name Found  " , Toast.LENGTH_SHORT).show();
                //LoadCategory(dCategoryUID);
            }
        }else{
            dsLevel1_Name = "NO";
            dsLevel2_Name = "NO";
        }
    }
    private void UploadCropedImageFunction(Uri filePath) {
        if(filePath != null)
        {
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
            ref = storageReference.child("HatherKacheApp/"+dsLevel1_Name+"/"+dsLevel2_Name+"/"+ dsTimeMiliSeconds +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String dPhotoURL = uri.toString();
                                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();

                                    Map<String, Object> note = new HashMap<>();
                                    note.put("L3Name", dsL3Name);
                                    note.put("L3PhotoUrl", dPhotoURL);
                                    note.put("L3Bio", dsL3Bio);
                                    note.put("L3Creator", dUserUID);
                                    note.put("L3iPrivacy", diPrivacy);
                                    note.put("L3iPriority", diL3Priority);
                                    note.put("L3iViewCount", diL3ViewCount);
                                    note.put("L3iTotalProducts", diL3TotalProducts);


                                    //note.put("gender", dGender);
                                    //note.put("type","NORMAL");
                                    //note.put("follower","0");
                                    //note.put("post","0");
                                    //note.put("reg_date",dUserRegistrationDate);
                                    db.collection("HatherKacheApp").document(dsLevel1_Name).collection(dsLevel2_Name).add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    String dsLevel3_UID = documentReference.getId();
                                                        if(dsLevel2_Name.equals("HomeServices")){
                                                            Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                            mLevel3_UpdateBtn.setText("UPDATED");
                                                            mLevel3_Name.setText("");
                                                            mLevel3_BioText.setText("");
                                                            mLevel3_Priority.setText("");
                                                            finish();
                                                            Intent intent = new Intent(Level_C_Add.this, Level_C.class);
                                                            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                            startActivity(intent);
                                                        }else{
                                                            MAP_UserLocationModel map_userLocationModel = new MAP_UserLocationModel(myGeo, null, dsLevel3_UID,dsLevel2_Name, 0, dsL3Name, dsL3Address);
                                                            db.collection("HatherKacheApp").document("Location")
                                                                    .collection(dsLevel2_Name).document(dsLevel3_UID).set(map_userLocationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                    mLevel3_UpdateBtn.setText("UPDATED");
                                                                    mLevel3_Name.setText("");
                                                                    mLevel3_BioText.setText("");
                                                                    mLevel3_Priority.setText("");
                                                                    finish();
                                                                    Intent intent = new Intent(Level_C_Add.this, Level_C.class);
                                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                                    startActivity(intent);
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                    mLevel3_UpdateBtn.setText("Try Again");
                                                                    mLevel3_Name.setText("Failed");
                                                                    mLevel3_BioText.setText("");
                                                                    mLevel3_Priority.setText("");
                                                                    Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }



                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            mLevel3_UpdateBtn.setText("Try Again");
                                            mLevel3_Name.setText("Failed");
                                            mLevel3_BioText.setText("");
                                            mLevel3_Priority.setText("");
                                            Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();

                                        }
                                    });



                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            mLevel3_UpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(getApplicationContext(), "Failed Photo"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Upload Failed Photo Not Found ", Toast.LENGTH_SHORT).show();
        }
    }
    //Dont forget to add class code on MainfestXml
    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected
            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mLevel3_ImageView);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                startCrop(imageUri_storage);
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            //Photo Cropped
            imageUriResultCrop = UCrop.getOutput(data);
            if(imageUriResultCrop != null){
                Picasso.get().load(imageUriResultCrop).into(mLevel3_ImageView);
                /*String dFileSizeAfterCrop = getSize(imageUriResultCrop);
                double  dFileSizeDouble = Double.parseDouble(dFileSizeAfterCrop);
                int dMB = 1000;
                dFileSizeDouble =  dFileSizeDouble/dMB; Toast.makeText(this, "Size = "+dFileSizeDouble,Toast.LENGTH_SHORT).show();
                */

                /*File file = FileUtils.getFile(Photo.this, imageUri);
                InputStream inputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);*/

                Toast.makeText(this, "Croped "+imageUriResultCrop.getPath(),Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Not Croped",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Croping Function
    private void startCrop(@NonNull Uri uri){


        if(imageUri_storage != null){
            String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
            destinationFileName += "jpg";

            UCrop ucrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
            ucrop.withAspectRatio(9,16);
            ucrop.withOptions(getCropOptions());
            ucrop.start(Level_C_Add.this);

        }else{
            Toast.makeText(getApplicationContext(), "image URI NULL ", Toast.LENGTH_SHORT).show();
        }
    }
    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);

        //Compress Type
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Crop Image");

        return options;
    }
    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }

    public String getSize(Uri uri) {
        String fileSize = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex);
                }
            }
        } finally {
            cursor.close();
        }
        return fileSize;
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
    private GeoPoint myGeo;
    private String dsL3Address = "NO";
    public void setGeoPoint(GeoPoint myGeo, String dsL3Address){
        if(myGeo == null){
            Toast.makeText(getApplicationContext(), "Location null", Toast.LENGTH_SHORT).show();
        }else{
            this.myGeo = myGeo;
            this.dsL3Address = dsL3Address;
            mLevel3_SetOnMap.setText("MAP SETTED");
        }
    }
}