package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.konik.hatherkache.R;

import java.util.Arrays;
import java.util.List;

public class UserLogin extends AppCompatActivity {
    //Firebase AUth
    private FirebaseUser user;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    //Firebase Checking User Data Saved or Not
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref = db.collection("USER_DATA").document("REGISTER");

    ///EMAIL
    private static final String TAGO = "LoginRegisterActivity";
    int AUTHUI_REQUEST_CODE = 10001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(UserLogin.this, UserProfile.class);
                    startActivity(intent);
                }else{
                    handleLoginRegister();
                }
            }
        };
    }
    //EMAIL LOGIN
    public void handleLoginRegister(){     //EMAIL

        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        //.setAlwaysShowSignInMethodScreen(true)
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTosAndPrivacyPolicyUrls("https://google.com", "https://facebook.com")
                .build();

        startActivityForResult(intent,AUTHUI_REQUEST_CODE);
    }
    @Override   //handleLoginRegister will call this method
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   //EMAIL
        if(requestCode == AUTHUI_REQUEST_CODE){     //EMAIL
            if(resultCode == RESULT_OK){
                //We have signed in the user or new user
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAGO,"onActivityResult:" + user.getEmail());
                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(UserLogin.this,"Welcome New User", Toast.LENGTH_SHORT).show();;
                    checkVerfication();
                }else{
                    Toast.makeText(UserLogin.this,"Welcome back Again", Toast.LENGTH_SHORT).show();
                    checkVerfication();
                }
            }else{
                // Signing in Failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response == null){
                    Toast.makeText(UserLogin.this,"user cancelled", Toast.LENGTH_SHORT).show();
                    Log.d(TAGO, "onActivityResult: the user has cancelled the sign in request");
                }else{
                    Toast.makeText(UserLogin.this,"ERROR "+response.getError(), Toast.LENGTH_SHORT).show();
                    Log.e(TAGO,"onActivityResult: ",response.getError());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void checkVerfication() {   //onActivityResult will call this method
        if(user.isEmailVerified()){
            //EMAIL IS VERIFIED
            checkUserData();
            Toast.makeText(UserLogin.this,"Verified Email", Toast.LENGTH_SHORT).show();;
        }else{  //EMAIL IS NOT VERIFIED
            //verfication_email();  //disable now
            checkUserData();
            Toast.makeText(UserLogin.this,"Passing unverified Email", Toast.LENGTH_SHORT).show();;
        }
    }
    private String dUserUID = "NO";
    private void checkUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();
        if(dUserUID.equals("")){
            Toast.makeText(UserLogin.this,"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
            //Please Modify Database Auth READ WRITE Condition if its not connect to database
            Toast.makeText(UserLogin.this, "Checking Database", Toast.LENGTH_SHORT).show();;
            user_data_ref.collection("NORMAL_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                Intent intent = new Intent(UserLogin.this,UserProfile.class);
                                startActivity(intent);
                                //finish();

                            }else{
                                //User has no data saved
                                Toast.makeText(getApplicationContext(),"User Inforamtion 404", Toast.LENGTH_SHORT).show();;
                                Intent intent = new Intent(UserLogin.this, UserProfileEdit.class);
                                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
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

}