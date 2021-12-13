
//@startuml
package com.konik.hatherkache.View.Ui;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.konik.hatherkache.Service.Map.MAP_LocationService;
import com.konik.hatherkache.Service.Map.MAP_UserLocationModel;
import com.konik.hatherkache.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.konik.hatherkache.Service.utill.Constants.ERROR_DIALOG_REQUEST;
import static com.konik.hatherkache.Service.utill.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.konik.hatherkache.Service.utill.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {
    private String first_Space = "";
    private String last_Space = "";
    private Button mLoginBtn;
    private LinearLayout mLinearGroceryShoping, mLinearFoodDelivery, mLinearHomeServices;


    //FirebaseAUTH
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final int recording_time = 5;
    private Geocoder geocoder; //getting area address name
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ///VARIABLES
    public String dUserType = "NO";
    public String getdUserType() {
        Log.d("MAP", "onResult: Get dUserType "+dUserType);
        return dUserType;
    }

    public void setdUserType(String dUserType) {
        Log.d("MAP", "onResult: Set dUserType "+dUserType);
        this.dUserType = dUserType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize
        mLoginBtn = (Button)findViewById(R.id.login_btn);
        mLinearGroceryShoping = (LinearLayout)findViewById(R.id.grocery_shoping_linear);
        mLinearFoodDelivery = (LinearLayout)findViewById(R.id.food_delivery_linear);
        mLinearHomeServices = (LinearLayout)findViewById(R.id.home_services_shoping_linear);
        //Slider Code
        ImageSlider imageSlider = (ImageSlider) findViewById(R.id.image_slider);
        List <SlideModel> imageList = new ArrayList<>();
        List <String> mSlidePhotoURl = new ArrayList<>();
        mSlidePhotoURl.add("https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FADMIN%2FSlides%2Fslide4.jpg?alt=media&token=218d09f5-6837-4ee1-8399-8df61530fa98");
        mSlidePhotoURl.add("https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FADMIN%2FSlides%2Fslide2.jpg?alt=media&token=a5762f49-1f3b-40ea-b0af-6574b6291bcf");
        //mSlidePhotoURl.add("https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FADMIN%2FSlides%2Fslide3.jpeg?alt=media&token=1fda34a3-0d28-4e0f-be54-17e5d3e75546");

        mSlidePhotoURl.add("https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FADMIN%2FSlides%2Fslide5.jpg?alt=media&token=135749d1-f669-4999-9032-07297a066ba7");
        mSlidePhotoURl.add("https://firebasestorage.googleapis.com/v0/b/hather-kache.appspot.com/o/HatherKacheApp%2FADMIN%2FSlides%2Fslide7.jpg?alt=media&token=6001438f-7c14-455f-a443-af91a5731727");

        int diSlideTotalPic = mSlidePhotoURl.size();
        for(int i = 0; i<diSlideTotalPic; i++){
            imageList.add(new SlideModel(mSlidePhotoURl.get(i), first_Space+" "+last_Space,  ScaleTypes.FIT));
        }
        imageSlider.setImageList(imageList,ScaleTypes.CENTER_CROP);
        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                String word = imageList.get(i).getTitle();
                Toast.makeText(MainActivity.this, "Pic no "+word,
                        Toast.LENGTH_LONG).show();
            }
        });
        ///////AUTHO CHECK
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d("MAP", "onResult: mAuth Logged In");
                    dUserName = user.getDisplayName();
                    dUserUID = FirebaseAuth.getInstance().getUid();
                    db.collection("HatherKacheApp").document("REGISTER")
                            .collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                dUserType = documentSnapshot.getString("userType");
                                dUserType = dUserType +"s";
                                setdUserType(dUserType);
                            }else{

                                Toast.makeText(getApplicationContext(),"Add Profile information first",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            }

                        }
                    });
                    mLoginBtn.setText("My Profile");
                }else{
                    Log.d("MAP", "onResult: mAuth Null");
                    Toast.makeText(getApplicationContext(),"Login Please", Toast.LENGTH_SHORT).show();;
                    dUserUID = "NO";
                    dUserType = "NO";
                    mLoginBtn.setText("Login");
                }
            }
        };
        mLinearGroceryShoping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!dUserType.equals("NoUserData")){
                    Intent intent = new Intent(getApplicationContext(), Level_C.class);
                    intent.putExtra("Level1_Name", "Sylhet");
                    intent.putExtra("Level2_Name", "GroceryShopping");
                    startActivity(intent);
                /*}else{
                    Toast.makeText(getApplicationContext(),"Add Profile information first",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,UserProfile.class);
                    startActivity(intent);
                }*/

            }
        });
        mLinearFoodDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!dUserType.equals("NoUserData")){
                    Intent intent = new Intent(getApplicationContext(),Level_C.class);
                    intent.putExtra("Level1_Name", "Sylhet");
                    intent.putExtra("Level2_Name", "FoodDelivery");
                    startActivity(intent);
                /*}else{
                    Toast.makeText(getApplicationContext(),"Add Profile information first",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,UserProfile.class);
                    startActivity(intent);
                }*/
            }
        });
        mLinearHomeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!dUserType.equals("NoUserData")){
                    Intent intent = new Intent(getApplicationContext(),Level_C.class);
                    intent.putExtra("Level1_Name", "Sylhet");
                    intent.putExtra("Level2_Name", "HomeServices");
                    startActivity(intent);
                /*}else{
                    Toast.makeText(getApplicationContext(),"Add Profile information first",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,UserProfile.class);
                    startActivity(intent);
                }*/
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserLogin.class);
                startActivity(intent);
            }
        });

        ///GOOGLE MAP CODE
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);   //for last recent location
        geocoder = new Geocoder(MainActivity.this);//for long click

    }
    //Google Map Code
    //Method One to Seven will Check the Permission of GPS and Realtime Location
    private String dUserUID = "NO", dUserName = "NO";

    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;   //by this we can find user last recent location
    private MAP_UserLocationModel mUserLocation;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    //google maps code start
    //METHOD ELEVEN
    private void startLocationService(){    //Connected with LocationService.class
        if(user != null){   //check logged in or not
            if(!isLocationServiceRunning()){  //Sent Update location data in every four minutes
                Intent serviceIntent = new Intent(this, MAP_LocationService.class);
                //this.startService(serviceIntent);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    //if we dont call foreground service then
                    // when the app will go to the background, it will stop
                    MainActivity.this.startForegroundService(serviceIntent);
                }else{
                    startService(serviceIntent);
                }
            }
        }

    }
    //Method TEN
    private boolean isLocationServiceRunning() {    //Connected with LocationService.class
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.konik.hatherkache.Service.Map.MAP_LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }
    private boolean dbUserLocationAllReadyExistsOnServer = false;
    private void saveUserLocation(){    //Method Nine
        if(dUserType.equals("NO") || dUserUID.equals("NO"))
                getUserData();
        else if(TextUtils.isEmpty(dUserType))
                getUserData();
        else if(TextUtils.isEmpty(dUserUID))
                getUserData();
        else if(mUserLocation != null && !dUserUID.equals("NO") && !dUserType.equals("NO") && !TextUtils.isEmpty(dUserType)){
            DocumentReference locationRef = mDb
                    .collection("HatherKacheApp").document("Location")
                    .collection(dUserType).document(dUserUID);
            Log.d("MAP", "onResult: saveUserLocation()");
            if(dbUserLocationAllReadyExistsOnServer == false){

                mUserLocation.setAvater(dUserType);
                locationRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String dsUserName = documentSnapshot.getString("name");

                            GeoPoint dgGepPoint = mUserLocation.getGeoPoint();
                            FieldValue ddDate =  FieldValue.serverTimestamp();
                            dsStreetAddress = mUserLocation.getAddress();
                            Map<String, Object> data = new HashMap<>();
                            data.put("geoPoint", dgGepPoint);
                            data.put("timestamp", ddDate);
                            data.put("address", dsStreetAddress);
                            //String dsAddress = documentSnapshot.getString("address");
                            String dsAddress = dsStreetAddress;




                            Log.d("MAP", "onResult: locationRef Update Exists dsUserName"+dsUserName);
                            if(dsAddress.equals("NX") ){
                                data.put("address", mUserLocation.getAddress());
                            }
                            if(dsUserName.equals("NX") ){
                                if(user!=null){
                                    dsUserName = user.getDisplayName();
                                    data.put("name", dsUserName);
                                }else{
                                    data.put("name", "NY");
                                }
                            }
                            if (TextUtils.isEmpty(dsAddress)){
                                dbUserLocationAllReadyExistsOnServer = false;
                                if(user!=null){
                                    dsUserName = user.getDisplayName();
                                    data.put("name", dsUserName);
                                }else{
                                    data.put("name", "NY");
                                }
                                data.put("address", mUserLocation.getAddress());
                                data.put("avater", mUserLocation.getAvater());
                                data.put("uid", mUserLocation.getUid());
                                data.put("view", mUserLocation.getView());

                            }else{
                                dbUserLocationAllReadyExistsOnServer = true;

                            }
                            locationRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "saveUserLocation:");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "saveUserLocation failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Log.d("MAP", "onResult: locationRef not Exists");
                            Toast.makeText(getApplicationContext(), "saveUserLocation creating", Toast.LENGTH_LONG).show();
                            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "saveUserLocation: \ninserted user location into database." +
                                                "\n latitude: " + mUserLocation.getGeoPoint().getLatitude() +
                                                "\n longitude: " + mUserLocation.getGeoPoint().getLongitude());
                                    }
                                }
                            });
                        }
                    }
                });
            }else{
                Log.d("MAP", "onResult: locationRef Information Exists");
                GeoPoint dgGepPoint = mUserLocation.getGeoPoint();
                FieldValue ddDate =  FieldValue.serverTimestamp();

                Map<String, Object> data = new HashMap<>();
                data.put("geoPoint", dgGepPoint);
                data.put("timestamp", ddDate);
                data.put("address", dsStreetAddress);

                locationRef.set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "saveUserLocation failed", Toast.LENGTH_LONG).show();
                    }
                });
            }


        }else if(dUserUID.equals("NO")){
            Log.d("MAP", "onResult: saveUserLocation() dUserUID = NO");
            Toast.makeText(getApplicationContext(),"Please Login",Toast.LENGTH_LONG).show();
        }else if(mUserLocation == null ){
            Log.d("MAP", "onResult: saveUserLocation() mUserLocationNull");
            Toast.makeText(getApplicationContext(),"Location 404",Toast.LENGTH_LONG).show();
        }else{
            Log.d("MAP", "onResult: saveUserLocation() ERROR");
        }
    }

    private void getUserData() {
        Log.d("MAP", "onResult: dUserType Retrive "+dUserType);
        if(FirebaseAuth.getInstance().getUid() != null){
            dUserUID = FirebaseAuth.getInstance().getUid();
            db.collection("HatherKacheApp").document("REGISTER")
                    .collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        dUserType = documentSnapshot.getString("userType");
                        dUserType = dUserType +"s";
                        Log.d("MAP", "onResult: dUserType Found "+dUserType);
                        saveUserLocation();
                    }else{
                        Log.d("MAP", "onResult: dUserType NOT Found ");
                    }
                }
            });
        }else{
            Log.d("MAP", "onResult: dUserType Retrive Failed ");
        }
    }
    private String dsStreetAddress = "NO";
    private void getLastKnownLocation() {   //Method Eight
        Log.d(TAG, "onResult: getLastKnownLocation");
        //Permission Check for safety
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Collect Last Recent Location

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if(location == null){
                        Log.d(TAG, "onResult: getLastKnownLocation = Location null");
                        Toast.makeText(getApplicationContext(),"Location null",Toast.LENGTH_LONG).show();
                    }else{
                        Log.d(TAG, "onResult: getLastKnownLocation = Location Found");
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d("TAG","GetLastLocation 1 "+geoPoint.getLatitude());
                        Log.d("TAG","GetLastLocation 2 "+geoPoint.getLongitude());
                        String streetAddress = "NO";
                        try {
                            List<Address> addresses = geocoder.getFromLocation(geoPoint.getLatitude(),geoPoint.getLongitude(), 1);
                            if(addresses.size() > 0){
                                    Address address = addresses.get(0);
                                    streetAddress = address.getAddressLine(0);
                                    if(TextUtils.isEmpty(streetAddress)){
                                        streetAddress =  "NO";
                                    }
                                dsStreetAddress = streetAddress;
                            }
                            Log.d(TAG, "onResult: getLastKnownLocation streetAddress ="+streetAddress);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(mUserLocation == null) {
                            Log.d(TAG, "onResult: getLastKnownLocation = mUserLocation null");

                            mUserLocation = new MAP_UserLocationModel();
                            mUserLocation.setGeoPoint(geoPoint);
                            mUserLocation.setTimestamp(null);   //Its Auto Matically add time staamp from server
                            mUserLocation.setUid(dUserUID);
                            mUserLocation.setView(0);
                            mUserLocation.setAvater("Users");
                            mUserLocation.setName(dUserName);
                            mUserLocation.setAddress(streetAddress);
                            saveUserLocation();
                        }else{
                            Log.d(TAG, "onResult: getLastKnownLocation = mUserLocation Found");
                            mUserLocation.setGeoPoint(geoPoint);
                            mUserLocation.setTimestamp(null);
                            mUserLocation.setUid(dUserUID);
                            mUserLocation.setView(0);
                            mUserLocation.setAvater("Users");
                            mUserLocation.setName(dUserName);
                            mUserLocation.setAddress(streetAddress);
                            saveUserLocation();
                        }
                        startLocationService();
                    }

                }else{
                    Log.d(TAG, "mFusedLocationClient Failled to collect recent location");
                    Toast.makeText(getApplicationContext(),"Fail to Collect Recent Location",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    //first call -> isServicesOk -> isMapsEnabled -> buildAlertMessageNoGPS -> OnActivityResult - >
    private boolean checkMapServices(){     //Method One
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }
    //Method One isServiceOK
    public boolean isServicesOK(){  //Method Two
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public boolean isMapsEnabled(){ //Method Three
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    private void buildAlertMessageNoGps() { //Method Four
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {  //Method Five
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //getChatrooms();
            //getUserDetails();
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override   //Method Six
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override   //Method Seven
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    //getChatrooms();
                    //getUserDetails();
                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }
    //MAP CORE CODE ENDS

        //ACTIVITY LIFECYCLE
        @Override
        protected void onDestroy() {
            super.onDestroy();

        }

    @Override
    protected void onResume() {
        super.onResume();
        dbUserLocationAllReadyExistsOnServer = false;
        if(checkMapServices()){     //CLASS WORKING START ONE
            if(mLocationPermissionGranted){
                //getChatrooms();
                //getUserDetails();
                //startLocationService();
                getLastKnownLocation();
            }
            else{
                getLocationPermission();
            }
        }
    }
    //AUTH
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
//@enduml