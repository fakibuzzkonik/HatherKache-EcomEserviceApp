package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.konik.hatherkache.R;
import com.konik.hatherkache.extra.DatePickerFragment;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserProfileEdit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private ImageView mUserProfilePic;
    private EditText mUserInfoName, mUserInfoPhoneNo, mUserInfoHomeAddress;
    private RadioGroup mRadioGenderGroup;
    private RadioButton mRadioGenderMale, mRadioGenderFemale;
    private RadioGroup mRadioUserTypeGroup;
    private RadioButton mRadioTypeRider, mRadioTypeUser, mRadioTypeAdmin;
    private TextView mBirthdate;
    private Spinner mLocationListSpinner;
    private Button mUserInfoUpdateBtn;
    //private static final String NO = "NO";
    List< String > daLocation_list = new ArrayList<>();
    private long dBirthDate = 0;
    private String dUserName = "NO",dHomeAddress = "NO", dUserBio = "NO", dUserPhone = "NO", dUserType = "NO", dGender = "NO";
    private String dUserUID = "NO",dUserEmail = "NO", dUserRegistrationDate = "NO", dUserLastActivity = "NO"; private long diUserLastActivity = 0;
    private String dExtra = "NO"; int diSize = 0, diGender = 0;
    //Photo Selecting and Croping
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference category_ref;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
        mUserProfilePic = (ImageView)findViewById(R.id.image_add_user_name);
        mUserInfoName = (EditText)findViewById(R.id.edit_add_user_name);
        mUserInfoHomeAddress = (EditText)findViewById(R.id.edit_add_user_address_text);
        mUserInfoPhoneNo = (EditText)findViewById(R.id.edit_add_user_phone);
        mRadioGenderGroup =(RadioGroup)findViewById(R.id.user_add_info_radio_group);
        mRadioGenderMale =(RadioButton)findViewById(R.id.radio_male);
        mRadioGenderFemale =(RadioButton) findViewById(R.id.radio_female);
        mRadioUserTypeGroup =(RadioGroup)findViewById(R.id.user_add_info_radio_group_user_type);
        mRadioTypeRider =(RadioButton)findViewById(R.id.radio_rider);
        mRadioTypeUser =(RadioButton)findViewById(R.id.radio_user);
        mRadioTypeAdmin =(RadioButton) findViewById(R.id.radio_admin);

        mBirthdate = (TextView)findViewById(R.id.edit_add_user_birthdate);
        mLocationListSpinner =  (Spinner)findViewById(R.id.user_infor_location_spinner);


        mUserInfoUpdateBtn = (Button)findViewById(R.id.user_infor_update_btn);
        SpinnerDataSetup();
        mBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                String dsUserName = user.getDisplayName();
                mUserInfoName.setText(dsUserName);
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Update Profile of "+dsUserName, Toast.LENGTH_SHORT).show();;
                    getIntentMethod();
                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };

        mUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });
        mUserInfoUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mRadioGenderMale.isChecked() && !mRadioGenderFemale.isChecked()){
                    dGender = "NO";
                    Toast.makeText(getApplicationContext(),"Please Select Gender", Toast.LENGTH_SHORT).show();;
                }else if(mRadioGenderMale.isChecked()){
                    dGender = mRadioGenderMale.getText().toString();
                }else if(mRadioGenderFemale.isChecked()){
                    dGender = mRadioGenderFemale.getText().toString();
                }

                if(!mRadioTypeRider.isChecked() && !mRadioTypeUser.isChecked() && !mRadioTypeAdmin.isChecked()){
                    dUserType = "NO";
                    Toast.makeText(getApplicationContext(),"Please Select User Type", Toast.LENGTH_SHORT).show();;
                }else if(mRadioTypeRider.isChecked()){
                    dUserType = mRadioTypeRider.getText().toString();
                }else if(mRadioTypeUser.isChecked()){
                    dUserType = mRadioTypeUser.getText().toString();
                }else if(mRadioTypeAdmin.isChecked()){
                    dUserType = mRadioTypeAdmin.getText().toString();
                }
                dUserName = mUserInfoName.getText().toString();
                dUserBio = "I am from sylhet. I am good human being. My target is to do my best for others";
                dHomeAddress = mUserInfoHomeAddress.getText().toString();
                dUserPhone = mUserInfoPhoneNo.getText().toString();
                if(imageUriResultCrop == null   && dRetrivePhotoURL.equals("NO")){
                    if(imageUri_storage == null){
                        Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();;
                    }else{
                        Toast.makeText(getApplicationContext(),"Please Crop Image", Toast.LENGTH_SHORT).show();;
                    }

                }else if(dUserName.equals("") || dUserBio.equals("") || dGender.equals("") || dUserType.equals("") || dBirthDate == 0){
                    Toast.makeText(getApplicationContext(),"Please Fill Up All", Toast.LENGTH_SHORT).show();;
                }else if(dUserName.equals("NO") || dUserBio.equals("NO") || dGender.equals("NO") || dUserType.equals("NO") || dsLocationCity.equals("NO") || dBirthDate == 0){
                    Toast.makeText(getApplicationContext(),"Please Fill Up All", Toast.LENGTH_SHORT).show();;
                }else{
                    String date = String.valueOf(System.currentTimeMillis());
                    dUserRegistrationDate = date;
                    dUserLastActivity = date;
                    long diDate = System.currentTimeMillis();
                    long diDate2 = 1623163000000L;
                    diDate = diDate - diDate2 ;
                    diUserLastActivity = diDate;
                    if(dUserPhone.equals("")){
                        dUserPhone = "123";
                    }
                    if(dGender.equals("Female")){
                        diGender = 2;   //FEMALE
                    }else{
                        diGender = 1;   //MALE
                    }
                    if(dUserType.equals("User")){
                        diUserType = 1;
                    }else if(dUserType.equals("Rider")){
                        diUserType = 2;
                    }else if(dUserType.equals("Admin")){
                        diUserType = 3;
                    }else {
                        diUserType = 1;
                    }
                    UploadCropedImageFunction(imageUriResultCrop);
                }
            }
        });
    }

    String dsEditMode = "NO";
    private void getIntentMethod() {
        final Intent intent = getIntent();
        if (intent.getExtras() != null) {
            dsEditMode = intent.getExtras().getString("dsEditMode");
            if (TextUtils.isEmpty(dsEditMode)) {
                dsEditMode = "NO";
                Toast.makeText(getApplicationContext(), "intent NULL  ", Toast.LENGTH_SHORT).show();
            } else if (dsEditMode.equals("")) {
                dsEditMode = "NO";
                Toast.makeText(getApplicationContext(), "intent 404", Toast.LENGTH_SHORT).show();
            } else {
                if(dsEditMode.equals("True")){
                    dHomeAddress = mUserInfoHomeAddress.getText().toString();
                    if(dHomeAddress.length()==0)
                        RetriveUserOldInformation();
                    else
                        dRetrivePhotoURL = "NO";
                }

            }
        }else{
            Toast.makeText(getApplicationContext(), "intent not Exists", Toast.LENGTH_SHORT).show();
        }
    }
    String dRetrivePhotoURL = "NO";
    private void RetriveUserOldInformation() {
        if(user != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER")
                    .document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                            dRetrivePhotoURL  = documentSnapshot.getString("photoURL");
                            dUserName = documentSnapshot.getString("name");
                            dHomeAddress = documentSnapshot.getString("homeAddress");
                            dUserPhone = documentSnapshot.getString("phone_no");
                            dUserType = documentSnapshot.getString("userType");

                            Picasso.get().load(dRetrivePhotoURL).into(mUserProfilePic);
                            mUserInfoHomeAddress.setText(dHomeAddress);
                            mUserInfoPhoneNo.setText(dUserPhone);
                            if(dUserType.equals("User")){
                                mRadioTypeUser.setChecked(true);
                            }else if(dUserType.equals("Rider")){
                                mRadioTypeRider.setChecked(true);
                            }else {
                                mRadioTypeAdmin.setChecked(true);
                            }
                    }else{
                        Toast.makeText(getApplicationContext(), "No User Data Exists", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Retrive Failed ", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "User Not Logged In", Toast.LENGTH_SHORT).show();
        }
    }

    private int diUserType = 1;
    private String dsLocationCity = "NO";
    private void SpinnerDataSetup() {
        ArrayAdapter<String> SpinnerdataAdapter;
        daLocation_list.add(0,"Choose City");
        daLocation_list.add(1,"Sylhet");
        daLocation_list.add(2,"Dhaka");
        SpinnerdataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,daLocation_list);
        mLocationListSpinner.setAdapter(SpinnerdataAdapter);
        mLocationListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int postion, long l) {
                if(parent.getItemAtPosition(postion).equals("Choose City")){
                    Toast.makeText(getApplicationContext(),"Please Select City",Toast.LENGTH_SHORT).show();
                }else{
                    String spinner_item = parent.getItemAtPosition(postion).toString();
                    /*String dGetValue= ""; ///comment
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
                    Toast.makeText(getApplicationContext(),dSelectedCategoryUID,Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(getApplicationContext(),"S "+spinner_item,Toast.LENGTH_SHORT).show();
                    dsLocationCity = spinner_item;
                    //dCategory = spinner_item;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    //Uplaoding Photo to FireStorage
    private void UploadCropedImageFunction(Uri filePath) {
        if(!dRetrivePhotoURL.equals("NO")){
            //Setting PhotoURL for
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(dUserName)
                    .build();
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Photo URL Attached",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Photo URL Attach Failed!",Toast.LENGTH_SHORT).show();
                }
            });


            Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
            String dTotal = methodSetUserTotalData(  diUserType,diGender,150,121,0,0,
                    diSize,0,0,0,0,
                    0,0,0,0);

            Map<String, Object> note = new HashMap<>();
            note.put("name", dUserName);
            note.put("email", dUserEmail); //map is done
            note.put("birth_reg", String.valueOf(dBirthDate)+"B"+dUserRegistrationDate+"R");
            note.put("uid",dUserUID);
            note.put("bio",dUserBio);
            note.put("photoURL",dRetrivePhotoURL);
            note.put("phone_no",dUserPhone);;
            note.put("total",dTotal);       //String
            note.put("homeAddress",dHomeAddress);   //address   new
            note.put("city","BD"+dsLocationCity);   //city      new
            note.put("userType",dUserType);   //city            new
            note.put("lastActivity",diUserLastActivity); //modify

            //note.put("gender", dGender);
            //note.put("type","NORMAL");
            //note.put("follower","0");
            //note.put("post","0");
            //note.put("reg_date",dUserRegistrationDate);
            db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER").document(dUserUID).set(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            mUserInfoUpdateBtn.setText("UPDATED");
                            mUserInfoName.setText("");
                            mUserInfoHomeAddress.setText("");
                            mUserInfoPhoneNo.setText("");
                            finish();
                            Intent intent = new Intent(UserProfileEdit.this, UserProfile.class);
                            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                    mUserInfoUpdateBtn.setText("FAILED Information Sent");
                }
            });
        }else if(filePath != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //System.currentTimeMills();
            ref = storageReference.child("HatherKacheApp/Users_Profile_Pic/"+dUserUID+"/"+ dUserUID +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            float dProPicServerSize = taskSnapshot.getTotalByteCount() /1024 ;
                            diSize = (int)dProPicServerSize;
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String dPhotoURL = uri.toString();

                                    //Setting PhotoURL for
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(dUserName)
                                            .setPhotoUri(uri)
                                            .build();
                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Photo URL Attached",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Photo URL Attach Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();
                                    String dTotal = methodSetUserTotalData(  diUserType,diGender,150,121,0,0,
                                            diSize,0,0,0,0,
                                            0,0,0,0);

                                    Map<String, Object> note = new HashMap<>();
                                    note.put("name", dUserName);
                                    note.put("email", dUserEmail); //map is done
                                    note.put("birth_reg", String.valueOf(dBirthDate)+"B"+dUserRegistrationDate+"R");
                                    note.put("uid",dUserUID);
                                    note.put("bio",dUserBio);
                                    note.put("photoURL",dPhotoURL);
                                    note.put("phone_no",dUserPhone);;
                                    note.put("total",dTotal);       //String
                                    note.put("homeAddress",dHomeAddress);   //address   new
                                    note.put("city","BD"+dsLocationCity);   //city      new
                                    note.put("userType",dUserType);   //city            new
                                    note.put("lastActivity",diUserLastActivity); //modify

                                    //note.put("gender", dGender);
                                    //note.put("type","NORMAL");
                                    //note.put("follower","0");
                                    //note.put("post","0");
                                    //note.put("reg_date",dUserRegistrationDate);
                                    db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER").document(dUserUID).set(note)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mUserInfoUpdateBtn.setText("UPDATED");
                                                    mUserInfoName.setText("");
                                                    mUserInfoHomeAddress.setText("");
                                                    mUserInfoPhoneNo.setText("");
                                                    finish();
                                                    Intent intent = new Intent(UserProfileEdit.this, UserProfile.class);
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();
                                            mUserInfoUpdateBtn.setText("FAILED Information Sent");
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
                            mUserInfoUpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    private String methodSetUserTotalData(int AdminLevel,      int GenderType,     int Follower,       int TotalPost,      int TotalDiscussion,
                                          int TotalTaka,       int TotalMegaByte,  int TotalFileView,  int TotalFileDownload,
                                          int TotalFileUplaod, int TotalReview,    int TotalLike,      int TotalComment,   int ExtraE,   int ExtraF){
        String sAdminLevel = String.valueOf(AdminLevel) + "A";
        String sGenderType = String.valueOf(GenderType) + "G";
        String sFollower = String.valueOf(Follower) + "F";
        String sTotalPost = String.valueOf(TotalPost) + "P";
        String sTotalDiscussion= String.valueOf(TotalDiscussion) + "D";
        String sTotalTaka = String.valueOf(TotalTaka) + "T";
        String sTotalMegaByte = String.valueOf(TotalMegaByte) + "M";
        String sTotalFileView = String.valueOf(TotalFileView) + "V";
        String sTotalFileDownload = String.valueOf(TotalFileDownload) + "D";
        String sTotalFileUpload = String.valueOf(TotalFileUplaod) + "U";

        String sTotalReview = String.valueOf(TotalReview) + "R";
        String sTotalLike = String.valueOf(TotalLike) + "L";
        String sTotalComment = String.valueOf(TotalComment) + "C";
        String sExtraE = String.valueOf(ExtraE) + "E";
        String sExtraF = String.valueOf(ExtraF) + "F";

        //"AGF PDT MVD URL CEF";
        String dTotalString = sAdminLevel+sGenderType+sFollower+sTotalPost+sTotalDiscussion+sTotalTaka+sTotalMegaByte+sTotalFileView+sTotalFileDownload+sTotalFileUpload+sTotalReview+sTotalLike+sTotalComment+sExtraE+sExtraF;
        int len = dTotalString.length();
        String target =  "AGFPDTMVDURLCEF";

        int i = 0; int j = 0;
        for(i = 0; i<len; i++){
            if(dTotalString.charAt(i) == target.charAt(j)){
                j++;
            }
        }
        if(j != 15){
            dTotalString = "0A0G0F0P0D0T0M0V0D0U0R0L0C0E0F";
        }
        return dTotalString;
    }
    //Dont forget to add class code on MainfestXml
    @Override   //Selecting Image
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected
            dRetrivePhotoURL = "NO";
            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mUserProfilePic);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                startCrop(imageUri_storage);
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            //Photo Cropped
            dRetrivePhotoURL = "NO";
            imageUriResultCrop = UCrop.getOutput(data);
            if(imageUriResultCrop != null){
                Picasso.get().load(imageUriResultCrop).into(mUserProfilePic);
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
    Random random = new Random();

    private void startCrop(@NonNull Uri uri){


        if(imageUri_storage != null){
            final int diRandomFinal = random.nextInt(5000);
            //String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
            String destinationFileName = String.valueOf(diRandomFinal);
            destinationFileName += "jpg";

            UCrop ucrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
            ucrop.withAspectRatio(1,1);
            ucrop.withOptions(getCropOptions());
            ucrop.start(UserProfileEdit.this);

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

    @Override   //Date Picker, add implements also
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        dBirthDate  = c.getTimeInMillis();

        mBirthdate.setText(currentDateString);
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