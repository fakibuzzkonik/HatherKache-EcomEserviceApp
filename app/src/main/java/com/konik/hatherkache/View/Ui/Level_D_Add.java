package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.konik.hatherkache.R;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Level_D_Add extends AppCompatActivity {

    private ImageView mLevel4_ImageView;
    private EditText mLevel4_Name, mLevel4_BioText, mLevel4_Priority, mLevel4_ViewCount, mLevel4_TotalProducts;
    private EditText mLevel4_SearchKey;
    private RadioGroup mLevel4_PrivacyRadioGroup;
    private RadioButton mLevel4_PublicRadioBtn, mLevel4_PrivateRadioBtn;
    private Button mLevel4_UpdateBtn;

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
        setContentView(R.layout.activity_level__d__add);
        mLevel4_ImageView = (ImageView)findViewById(R.id.level_d_imageview);
        mLevel4_Name = (EditText)findViewById(R.id.level_d_name);
        mLevel4_BioText = (EditText)findViewById(R.id.level_d_bio_text);
        mLevel4_Priority = (EditText)findViewById(R.id.level_d_priority);
        mLevel4_ViewCount = (EditText)findViewById(R.id.level_d_view_count);
        mLevel4_TotalProducts = (EditText)findViewById(R.id.level_d_total_products);
        mLevel4_SearchKey = (EditText)findViewById(R.id.level_d_search_edit);
        mLevel4_PrivacyRadioGroup = (RadioGroup)findViewById(R.id.level_d_privacy_radio_group);
        mLevel4_PublicRadioBtn = (RadioButton)findViewById(R.id.level_d_public_radio);
        mLevel4_PrivateRadioBtn = (RadioButton)findViewById(R.id.level_d_private_radio);
        mLevel4_UpdateBtn = (Button)findViewById(R.id.level_d_update_btn);
        getIntentMethod();
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Add Level4 Information", Toast.LENGTH_SHORT).show();;

                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        mLevel4_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });
        mLevel4_UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsL3Name = mLevel4_Name.getText().toString();
                dsL3Bio = mLevel4_BioText.getText().toString();
                dsL3Priority = mLevel4_Priority.getText().toString();
                dsL3ViewCount = mLevel4_ViewCount.getText().toString();
                dsL3TotalProducts = mLevel4_TotalProducts.getText().toString();
                dsL3Searchkey  = mLevel4_SearchKey.getText().toString();
                if(!mLevel4_PublicRadioBtn.isChecked() && !mLevel4_PrivateRadioBtn.isChecked()){
                    Toast.makeText(getApplicationContext(),"Please Select Privacy", Toast.LENGTH_SHORT).show();
                }else if(mLevel4_PublicRadioBtn.isChecked()){
                    dsL3Privacy = "Public";
                }else if(mLevel4_PrivateRadioBtn.isChecked()){
                    dsL3Privacy = "Private";
                }
                if(imageUriResultCrop == null){
                    if(imageUri_storage == null){
                        Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(getApplicationContext(),"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dsL3Privacy.equals("NO") || dsL3Name.equals("NO")  || dsL3Bio.equals("NO") || dsL3Searchkey.equals("NO") || dsL3Priority.equals("NO") || dsL3ViewCount.equals("NO") || dsL3TotalProducts.equals("NO") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if(dsL3Privacy.equals("") || dsL3Name.equals("")  || dsL3Bio.equals("") || dsL3Searchkey.equals("") || dsL3Priority.equals("") || dsL3ViewCount.equals("") || dsL3TotalProducts.equals("") ){
                    Toast.makeText(getApplicationContext(), "Please Fillup all", Toast.LENGTH_SHORT).show();
                }else if(intentFoundError1 || intentFoundError2 || intentFoundError3 || intentFoundError4){
                    Toast.makeText(getApplicationContext(),"Intent 404",Toast.LENGTH_SHORT).show();
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
    private String dsLevel1_Name = "NO", dsLevel2_Name = "NO", dsLevel3_Name = "NO", dsLevel3_UID = "NO";
    private boolean intentFoundError1 = true;
    private boolean intentFoundError2 = true;
    private boolean intentFoundError3 = true;
    private boolean intentFoundError4 = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsLevel1_Name = intent.getExtras().getString("Level1_Name");    //Sylhet
            dsLevel2_Name = intent.getExtras().getString("Level2_Name");    //Grocery or Food or Home Services
            dsLevel3_Name = intent.getExtras().getString("Level3_Name");    //Doctor,Engineer,Chef / Bakeries, Cake, Vegetables / Pachbhai, Panshi, RoyalChef
            dsLevel3_UID = intent.getExtras().getString("Level3_UID");    //Level 3 UID

            intentFoundError1 = CheckIntentMethod(dsLevel1_Name);
            intentFoundError2 = CheckIntentMethod(dsLevel2_Name);
            intentFoundError3 = CheckIntentMethod(dsLevel3_Name);
            intentFoundError4 = CheckIntentMethod(dsLevel3_UID);

            if(!intentFoundError1 && !intentFoundError2 && !intentFoundError3 && !intentFoundError4 ){

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
            intentFoundError1 = false;
        }
        return intentFoundError1;
    }

    //Firstore Uplaoding CODE
    private String dUserUID = "NO";
    private String dsPhotoUrl = "NO", dsL3Name = "NO", dsL3Bio = "NO", dsL3Searchkey = "NO", dsL3Priority = "NO", dsL3ViewCount = "NO", dsL3TotalProducts = "NO",dsL3Privacy = "NO";
    private int  diL3Priority = 0, diL3ViewCount = 0, diL3TotalProducts = 0, diPrivacy = 0;
    private void UploadCropedImageFunction(Uri filePath) {
        if(filePath != null)
        {
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
            ref = storageReference.child("HatherKacheApp/"+dsLevel1_Name+"/"+dsLevel2_Name+"/"+dsLevel3_UID+"/"+ dsTimeMiliSeconds +"."+getFileExtention(filePath));
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
                                    dsL3Searchkey = dsL3Searchkey + " "+ dsL3Name +" "+dsL3Bio+" ";
                                    Map<String, Object> note = new HashMap<>();
                                    note.put("L4Name", dsL3Name);
                                    note.put("L4PhotoUrl", dPhotoURL);
                                    note.put("L4Bio", dsL3Bio);
                                    note.put("L4Search", dsL3Searchkey);
                                    note.put("L4Creator", dUserUID);
                                    note.put("L4Extra", "0");
                                    note.put("L4iPrivacy", diPrivacy);
                                    note.put("L4iPriority", diL3Priority);
                                    note.put("L4iViewCount", diL3ViewCount);
                                    note.put("L4iTotalProducts", diL3TotalProducts);

                                    /*List<String> mStrings = new ArrayList<String>();
                                    mStrings.add("PhotoURL 1");
                                    mStrings.add("PhotoURL 2");
                                    mStrings.add("PhotoURL 2");
                                    note.put("PhotoURL", mStrings);*/


                                    db.collection("HatherKacheApp").document(dsLevel1_Name).collection(dsLevel2_Name).document(dsLevel3_UID).collection("Level4List").add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mLevel4_UpdateBtn.setText("UPDATED");
                                                    mLevel4_Name.setText("");
                                                    mLevel4_BioText.setText("");
                                                    mLevel4_Priority.setText("");
                                                    finish();
                                                    Intent intent = new Intent(Level_D_Add.this, Level_D.class);
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            mLevel4_UpdateBtn.setText("Try Again");
                                            mLevel4_Name.setText("Failed");
                                            mLevel4_BioText.setText("");
                                            mLevel4_Priority.setText("");
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
                            mLevel4_UpdateBtn.setText("Failed Photo Upload");
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
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mLevel4_ImageView);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                startCrop(imageUri_storage);
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            //Photo Cropped
            imageUriResultCrop = UCrop.getOutput(data);
            if(imageUriResultCrop != null){
                Picasso.get().load(imageUriResultCrop).into(mLevel4_ImageView);
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
            ucrop.withAspectRatio(130  ,170);
            ucrop.withOptions(getCropOptions());
            ucrop.start(Level_D_Add.this);

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
}