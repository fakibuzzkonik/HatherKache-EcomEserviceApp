package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.konik.hatherkache.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class UserProfile extends AppCompatActivity {
    //Layout
    private boolean isOpen = false ;
    private ConstraintSet layout1,layout2;
    private ConstraintLayout constraintLayout ;
    /////////////////////////////////// INITIALIZING
    private TextView mLoginHeadText;
    private Button mLoginBtn, mLogoutBtn, mRideOrdersBtn;
    private ImageView mUserProfileCoverImg;
    private ImageView mUserProfileImageView;
    private TextView mCheckUserName, mCheckUserActivity, mCheckUTotalFollowers, mCheckUserTotalBooks, mCheckUserBio;
    private TextView mCheckUserBirthdate, mCheckUserPhoneNumber, mCheckUserRegistrationDate;
    private Button mCheckUserEditProfileBtn, mOrderBtn, mMyWorksBtn, mMyShopsOrderListBtn;
    private TextView mUserProfileFollwerText;
    private TextView mUserProfileBookText;
    private TextView mUserProfileAboutText;
    private LinearLayout mLinerLayoutBarOne;
    private LinearLayout mLinerLayoutBarTwo;
    private ProgressBar mUserProfileProgressBar;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    //Firebase Checking User Data Saved or Not
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref = db.collection("HatherKacheApp").document("REGISTER");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Initialize
        mLoginHeadText = (TextView) findViewById(R.id.login_head_text);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mLogoutBtn = (Button) findViewById(R.id.userprofile_logout_btn);
        mRideOrdersBtn = (Button) findViewById(R.id.userprofile_ride_orders_btn);
        mCheckUserName = (TextView)  findViewById(R.id.user_profile_name_text);
        mCheckUserActivity = (TextView) findViewById(R.id.user_profile_acitivity_time);
        mCheckUTotalFollowers = (TextView) findViewById(R.id.user_profile_followers_count);
        mCheckUserTotalBooks = (TextView) findViewById(R.id.user_profile_books_count);
        mCheckUserBio = (TextView) findViewById(R.id.user_profile_bio);
        mCheckUserBirthdate = (TextView) findViewById(R.id.user_profile_birthdate);
        mCheckUserPhoneNumber = (TextView) findViewById(R.id.user_profile_contact_no);
        mCheckUserRegistrationDate = (TextView) findViewById(R.id.user_profile_registration);
        mCheckUserEditProfileBtn = (Button) findViewById(R.id.user_profile_edit_btn);
        mOrderBtn = (Button) findViewById(R.id.user_profile_order);
        mMyWorksBtn = (Button) findViewById(R.id.user_profile_my_works_btn);
        mMyShopsOrderListBtn = (Button) findViewById(R.id.userprofile_my_shop_btn);

        mUserProfileCoverImg = (ImageView) findViewById(R.id.user_profile_cover_img);
        mUserProfileImageView = (ImageView) findViewById(R.id.user_profile_propic_img);
        mUserProfileFollwerText = (TextView) findViewById(R.id.textView2);
        mUserProfileBookText = (TextView) findViewById(R.id.textView4);
        mUserProfileAboutText = (TextView) findViewById(R.id.user_profile_about);
        mLinerLayoutBarOne = (LinearLayout) findViewById(R.id.linearlayout_bar_one);
        mLinerLayoutBarTwo = (LinearLayout)  findViewById(R.id.linearlayout_bar_two);
        mUserProfileProgressBar = (ProgressBar)  findViewById(R.id.user_profile_progressBar);

        ///////Layout Changing Code
        //Layout Change Code Start
        //changing the status bar color to transparent
        //Window w = getWindow();
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        layout1 = new ConstraintSet();
        layout2 = new ConstraintSet();

        constraintLayout =  findViewById(R.id.constraint_layout);
        layout2.clone(UserProfile.this, R.layout.zoom_pro_pic_layout);
        layout1.clone(constraintLayout);
        //LAYOUT CHANGE CODE FINISH
        //CircleImageView imageView = (CircleImageView) findViewById(R.id.image);
        mUserProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_pic_zoom_function();
            }
        });

        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String dsUserName = user.getDisplayName();
                    Toast.makeText(getApplicationContext(),"Welcome "+dsUserName, Toast.LENGTH_SHORT).show();;
                    getUserData();
                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                /*Intent intent = new Intent(UserProfile.this,MainActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);*/
            }
        });
        mOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, PB_OrderList.class);
                intent.putExtra("dsViewerType","uid_buyer");
                startActivity(intent);
            }
        });
        mMyWorksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this,PB_OrderList.class);
                intent.putExtra("dsViewerType","uid_master");
                startActivity(intent);
            }
        });
        mRideOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this,PB_OrderList.class);
                intent.putExtra("dsViewerType","uid_rider");
                startActivity(intent);
            }
        });
        mMyShopsOrderListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this,PB_OrderList.class);
                intent.putExtra("dsViewerType","dsExtra");  //shop owner UID
                startActivity(intent);
            }
        });
        mCheckUserEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null){
                    Intent intent = new Intent(UserProfile.this,UserProfileEdit.class);
                    intent.putExtra("dsEditMode","True");  //shop owner UID
                    startActivity(intent);
                }
            }
        });
        ///
        Random myRandom = new Random();
        int diTotalRides = myRandom.nextInt(130)+100;
        int diTotalViews = myRandom.nextInt(7500)+1000;
        mUserProfileFollwerText.setText("ORDERED");
        mUserProfileBookText.setText("VIEWED");
        mCheckUTotalFollowers.setText(String.valueOf(diTotalRides));
        mCheckUserTotalBooks.setText(String.valueOf(diTotalViews));
    }
    private void profile_pic_zoom_function() {
        if (!isOpen) {
            TransitionManager.beginDelayedTransition(constraintLayout);
            layout2.applyTo(constraintLayout);
            isOpen = !isOpen;
        } else {
            TransitionManager.beginDelayedTransition(constraintLayout);
            layout1.applyTo(constraintLayout);
            isOpen = !isOpen;
            mUserProfileProgressBar.setVisibility(GONE);
            logout_mode();
        }
    }

    private String dUserUID = "NO";
    private void getUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();
        if(dUserUID.equals("")){
            Toast.makeText(getApplicationContext(),"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
            //Please Modify Database Auth READ WRITE Condition if its not connect to database
            Toast.makeText(getApplicationContext(), "Checking Database", Toast.LENGTH_SHORT).show();;
            user_data_ref.collection("NORMAL_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                //Toast.makeText(getApplicationContext(),"User Information Found", Toast.LENGTH_SHORT).show();;
                                String UserUID = documentSnapshot.getString("uid");
                                String dUserName = documentSnapshot.getString("name");
                                String dUserBio = documentSnapshot.getString("bio");
                                String dUserPhoneNo = documentSnapshot.getString("phone_no");
                                String dTotal = documentSnapshot.getString("total");
                                String dUserPhotoURL = documentSnapshot.getString("photoURL");
                                String dBirthRegTime = documentSnapshot.getString("birth_reg");

                                String dHomeAddress = documentSnapshot.getString("homeAddress");
                                String dsLocationCity = documentSnapshot.getString("city");
                                String dUserType = documentSnapshot.getString("userType");

                                long diLastActivity2 = documentSnapshot.getLong("lastActivity");

                                if(UserUID.equals(dUserUID)){
                                    mUserProfileProgressBar.setVisibility(View.GONE);
                                    logout_mode();
                                    mCheckUserName.setText(dUserName);
                                    //mCheckUserBio.setText(dUserBio);  //comment
                                    mCheckUserBio.setText(dHomeAddress);
                                    mCheckUserPhoneNumber.setText("Phone no : "+dUserPhoneNo);
                                    Picasso.get().load(dUserPhotoURL).into(mUserProfileImageView);
                                    long diDate2 = 1623163000000L;
                                    diLastActivity2 = diLastActivity2 + diDate2;
                                    mCheckUserActivity.setText(TimeAgo(diLastActivity2));   //2 minutes ago setup

                                    if(dUserType.equals("User")){
                                        mRideOrdersBtn.setVisibility(GONE);
                                    }else if(dUserType.equals("Rider")){
                                        mMyShopsOrderListBtn.setVisibility(GONE);
                                    }

                                    methodGetUserTotalData(dTotal);
                                    methodGetBirthRegDate(dBirthRegTime);
                                    //savedUserDataPhoneMemory(dUserName, UserUID, dTotal, dUserPhotoURL);  //comment
                                }else{
                                    Toast.makeText(getApplicationContext(),"User Inforamtion Not Matched", Toast.LENGTH_SHORT).show();;
                                }

                            }else{
                                //User has no data saved
                                Toast.makeText(getApplicationContext(),"User Inforamtion 404", Toast.LENGTH_SHORT).show();;
                                Intent intent = new Intent(getApplicationContext(), UserProfileEdit.class);
                                //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                //finish();
                            }
                        }
                    });
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
    private void methodGetBirthRegDate(String dBrithRegDate){
        int len = dBrithRegDate.length();
        Vector<String> vec = new Vector<>();
        String target = "BR";
        String new_word = "";
        int i = 0, j = 0;
        for(i = 0; i<len; i++){
            if(dBrithRegDate.charAt(i) == target.charAt(j)){
                vec.add(new_word);
                new_word = "";
                j++;
            }else{
                new_word += dBrithRegDate.charAt(i);
            }
        }
        String dBirthDateMiniSeconds = vec.elementAt(0);
        String dRegistrationMiniSeconds = vec.elementAt(1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM");
        SimpleDateFormat dateFormatWithYear = new SimpleDateFormat("d/MMM/yyyy");
        Long birthTime = Long.parseLong(dBirthDateMiniSeconds);
        Long regTime = Long.parseLong(dRegistrationMiniSeconds);
        Date date1 = new Date(birthTime);
        Date date2 = new Date(regTime);
        String dBirthTime = dateFormat.format(date1);
        String dRegTime = dateFormatWithYear.format(date2);

        mCheckUserRegistrationDate.setText("Registerted on : "+ dRegTime);
        if(iGenderType == 1){
            mCheckUserBirthdate.setText("Wish Him : "+dBirthTime);  //Gender MALE
        }else if(iGenderType == 2){
            mCheckUserBirthdate.setText("Wish Her : "+dBirthTime);
        }else{
            mCheckUserBirthdate.setText("Wish on : "+dBirthTime);
        }

    }
    private static final String sZero = "0";
    final private int iZero = 0;
    String     sAdminLevel = sZero,   sGenderType =  sZero,      sFollower =   sZero,          sTotalPost =   sZero,        sTotalDiscussion=   sZero, sTotalTaka =   sZero;
    String sTotalMegaByte  = sZero,   sTotalFileView =   sZero,  sTotalFileDownload =   sZero, sTotalFileUpload =   sZero,  sTotalReview = sZero;
    String sTotalLike = sZero,        sTotalComment = sZero,     sExtraE =   sZero,            sExtraF =   sZero;
    int iAdminLevel = iZero, iGenderType =  iZero, iFollower =   iZero, iTotalPost =   iZero, iTotalDiscussion=   iZero, iTotalTaka =   iZero, iTotalMegaByte  = iZero;
    int iTotalFileView =   iZero,  iTotalFileDownload =   iZero, iTotalFileUpload =   iZero, iTotalReview = iZero, iTotalLike = iZero, iTotalComment = iZero, iExtraE =   iZero, iExtraF =  iZero;

    private void methodGetUserTotalData(String dTotal) {
        int len = dTotal.length();
        Vector<String> vec = new Vector<String>();

        String target =  "AGFPDTMVDURLCEF";
        String new_word = "";
        int i = 0; int j = 0;
        for(i = 0; i<len; i++){

            if(dTotal.charAt(i) == target.charAt(j)){
                vec.add(new_word);
                new_word = "";
                j++;
            }else{
                new_word += dTotal.charAt(i);
            }

        }
        if(vec.size() == 15){
            sAdminLevel = vec.elementAt(0);
            sGenderType = vec.elementAt(1);
            sFollower = vec.elementAt(2);
            sTotalPost = vec.elementAt(3);
            sTotalDiscussion= vec.elementAt(4);
            sTotalTaka = vec.elementAt(5);
            sTotalMegaByte = vec.elementAt(6);
            sTotalFileView = vec.elementAt(7);
            sTotalFileDownload = vec.elementAt(8);
            sTotalFileUpload = vec.elementAt(9);
            sTotalReview = vec.elementAt(10);
            sTotalLike = vec.elementAt(11);
            sTotalComment = vec.elementAt(12);
            sExtraE = vec.elementAt(13);
            sExtraF = vec.elementAt(14);
            ///String Finish, Intger ConvertStart
            iAdminLevel = Integer.parseInt(sAdminLevel);
            iGenderType = Integer.parseInt(sGenderType);
            iFollower = Integer.parseInt(sFollower);
            iTotalPost = Integer.parseInt(sTotalPost);
            iTotalDiscussion= Integer.parseInt(sTotalDiscussion);
            iTotalTaka = Integer.parseInt(sTotalTaka);
            iTotalMegaByte = Integer.parseInt(sTotalMegaByte);
            iTotalFileView = Integer.parseInt(sTotalFileView);
            iTotalFileDownload = Integer.parseInt(sTotalFileDownload);
            iTotalFileUpload = Integer.parseInt(sTotalFileUpload);
            iTotalReview =Integer.parseInt(sTotalReview);
            iTotalLike = Integer.parseInt(sTotalLike);
            iTotalComment = Integer.parseInt(sTotalComment);
            iExtraE = Integer.parseInt(sExtraE);
            iExtraF = Integer.parseInt(sExtraF);
            ///////////////////////FETCH FINISH
            //NOW INISITALIZE
            //mCheckUserTotalBooks.setText(sTotalPost);
            //mCheckUTotalFollowers.setText(sFollower);
        }else{
            Toast.makeText(getApplicationContext(),"Failed to fetch TotalData", Toast.LENGTH_SHORT).show();;
        }

/*        if(iAdminLevel >= 5){
            mOrderBtn.setVisibility(View.VISIBLE);
        }else{
            mOrderBtn.setVisibility(GONE);
        }*/
    }
    private void logout_mode() {
        mLoginHeadText.setVisibility(View.GONE);
        mLoginBtn.setVisibility(View.GONE);


        mLogoutBtn.setVisibility(View.VISIBLE);
        mRideOrdersBtn.setVisibility(View.VISIBLE);
        mMyShopsOrderListBtn.setVisibility(View.VISIBLE);

        mCheckUserName.setVisibility(View.VISIBLE);
        mCheckUserActivity.setVisibility(View.VISIBLE);
        mCheckUTotalFollowers.setVisibility(View.VISIBLE);
        mCheckUserTotalBooks.setVisibility(View.VISIBLE);
        mCheckUserBio.setVisibility(View.VISIBLE);
        mCheckUserBirthdate.setVisibility(View.VISIBLE);
        mCheckUserPhoneNumber.setVisibility(View.VISIBLE);
        mCheckUserRegistrationDate.setVisibility(View.VISIBLE);
        mCheckUserEditProfileBtn.setVisibility(View.VISIBLE);
        mOrderBtn.setVisibility(View.VISIBLE);
        mUserProfileImageView.setVisibility(View.VISIBLE);
        mUserProfileCoverImg.setVisibility(View.VISIBLE);
        mUserProfileFollwerText.setVisibility(View.VISIBLE);
        mUserProfileBookText.setVisibility(View.VISIBLE);
        mUserProfileAboutText.setVisibility(View.VISIBLE);
        mLinerLayoutBarOne.setVisibility(View.VISIBLE);
        mLinerLayoutBarTwo.setVisibility(View.VISIBLE);
    }

    private void login_mode() {
        mLoginHeadText.setVisibility(View.VISIBLE);
        mLoginBtn.setVisibility(View.VISIBLE);


        mLogoutBtn.setVisibility(View.GONE);
        mRideOrdersBtn.setVisibility(View.GONE);
        mMyShopsOrderListBtn.setVisibility(View.GONE);
        mCheckUserName.setVisibility(View.GONE);
        mCheckUserActivity.setVisibility(View.GONE);
        mCheckUTotalFollowers.setVisibility(View.GONE);
        mCheckUserTotalBooks.setVisibility(View.GONE);
        mCheckUserBio.setVisibility(View.GONE);
        mCheckUserBirthdate.setVisibility(View.GONE);
        mCheckUserPhoneNumber.setVisibility(View.GONE);
        mCheckUserRegistrationDate.setVisibility(View.GONE);
        mCheckUserEditProfileBtn.setVisibility(View.GONE);
        mOrderBtn.setVisibility(View.GONE);
        mUserProfileImageView.setVisibility(View.GONE);
        mUserProfileCoverImg.setVisibility(View.GONE);
        mUserProfileFollwerText.setVisibility(View.GONE);
        mUserProfileBookText.setVisibility(View.GONE);
        mUserProfileAboutText.setVisibility(View.GONE);
        mLinerLayoutBarOne.setVisibility(View.GONE);
        mLinerLayoutBarTwo.setVisibility(View.GONE);
    }
    private String TimeAgo(long dltime){
        String dstime = " ";
        try
        {
            Long currentTime = dltime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date date = new Date(currentTime);
            String time = simpleDateFormat.format(date);

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date past = format.parse(time);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
                dstime = seconds+" seconds ago";
            else if(minutes<60)
                dstime = minutes+" minutes ago";
            else if(hours<24)
                dstime = hours+" hours ago";
            else
                dstime = days+" days ago";


        }
        catch (Exception j){
            j.printStackTrace();
        }

        return dstime;
    }

}