package com.konik.hatherkache.Service.Map;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.konik.hatherkache.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.konik.hatherkache.Service.utill.Constants.MAPVIEW_BUNDLE_KEY;

public class MAP_Fragment extends Fragment implements
        OnMapReadyCallback, View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener
        {
    private static final String TAG = "UserListFragment";
    private MapView mMapView;
    private ArrayList<MAP_UserLocationModel> mUserLocations = new ArrayList<>();
    private MAP_UserLocationModel mUserPosition; //Present User Location Data
    private LatLngBounds mMapBoundary;  //Boundary for Camera View

    private RelativeLayout mMapContainer; //for showing full view of map

    public static MAP_Fragment newInstance() {  //For Map Activity class, we call this fregment by this instance
        return new MAP_Fragment();
    }

    private GoogleMap mGoogleMap;
    ////////DIRECTION API
    private GeoApiContext  mGeoApiContext = null;
    private String dsOrderUID = "NIX";
    private String dsRiderUID = "NIL";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Collecting Data From Map Activity
        if (getArguments() != null) {
            //Toast.makeText(getActivity(),"Toast Collection", Toast.LENGTH_LONG).show();
            //error1 mUserList = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
            mUserLocations= getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
            dsOrderUID = getArguments().getString("dsOrderUID");
            dsRiderUID = getArguments().getString("dsRiderUID");   // equal to NIl or RiderUID , if NIL we will find nearest rider.
            Log.e(TAG, "onResult: Fragment Open. GetIntent dsOrderUID = "+ dsOrderUID+" RiderUID "+ dsRiderUID );
            int s = mUserLocations.size();
            Toast.makeText(getActivity(), "Total xArray Size"+s, Toast.LENGTH_SHORT).show();
        }
        geocoder = new Geocoder(getActivity());//for long click
    }
    private TextView mMapInfoText ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment_mapview, container, false);
        mMapInfoText = view.findViewById(R.id.main_map_info_text);
        mMapView = view.findViewById(R.id.user_list_map);
        mMapContainer = view.findViewById(R.id.map_container); // for showing full view of map
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);

        initGoogleMap(savedInstanceState);  //Initializing Google Map
        setUserPosition();
        return view;
    }
    //Method Two    //Method one is removed because its initialize recycler view
    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        //Direction API
        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key)).build();
        }
    }
    //Method Two Part B Major
    private void setUserPosition() {
        Log.e(TAG, "onResult: setUserPosition() : Start Method");
        int s = mUserLocations.size();
        Toast.makeText(getActivity(), "Total Array Size"+s, Toast.LENGTH_SHORT).show();
        for (MAP_UserLocationModel userLocation : mUserLocations) {  //Searching ALl the User Location
            if (userLocation.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                mUserPosition = userLocation;   //for the user who viewing it.
                Toast.makeText(getActivity(), "MATCHED USER", Toast.LENGTH_SHORT).show();

            }else{
                //Toast.makeText(getActivity(), "Please Login", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG, "onResult: mUserLocations.name = "+userLocation.getName()+"  UID = "+userLocation.getUid()+"  Type"+userLocation.getAvater());
        }
    }
    //METHOD Four A
    private MAP_UserLocationModel shopLoctionModel;
    private void checkUserList(){
        for(MAP_UserLocationModel userLocation: mUserLocations){
            try {
                String dsMarkerType  = userLocation.getAvater();//Human or Shop
                String dsRiderUIDx = userLocation.getUid();


                if(dsMarkerType.equals("FoodDelivery")  || dsMarkerType.equals("GroceryShopping") ){
                    dgShopGeoPoint = userLocation.getGeoPoint();
                    OrderTypeFoodorGrocery = true;
                    shopLoctionModel = userLocation;
                }

            }catch (NullPointerException e){
                Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
            }
        }
        Log.e(TAG, "onResult: checkUserList(): RiderUID "+ dsRiderUID );
        if(dsRiderUID.equals("NIL") && OrderTypeFoodorGrocery == true){
            Log.e(TAG, "onResult: checkUserList(): Searching Nearest Rider");
            if(dgShopGeoPoint != null)
                calculateDistanceFromShops(dgShopGeoPoint);
        }else{
            Log.e(TAG, "onResult: checkUserList(): Rider All ready set");
            addMapMarkers();
        }

    }
        //Method Five
        int i = 0;
        CollectionReference userLocationReference;
        private GeoPoint dgShopGeoPoint = null;
        ArrayList<Map_Distance_Model> ar = new ArrayList<Map_Distance_Model>();
        private boolean NearestRiderNotDeclared = false;
        private boolean OrderTypeFoodorGrocery = false;
        //Method Five
        private void calculateDistanceFromShops(GeoPoint ShopGeoPoint){     //DISABLE
            getComapreUserLocationWithShop(ShopGeoPoint);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //do background staff
                    delay();
                }
            });
            //getComapreUserLocationWithShop(ShopGeoPoint);
            Log.d(TAG, "calculateDirections: calculating directions.");

        }
        //Method Six
        private void getComapreUserLocationWithShop(GeoPoint ShopGeoPoint) {
            userLocationReference = FirebaseFirestore.getInstance()
                    .collection("HatherKacheApp").document("Location")
                    .collection("Riders");
            userLocationReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.isEmpty()){

                    }else{
                        int queryDocumentSize = queryDocumentSnapshots.size();
                        i = 0;
                        Log.d(TAG, "onResult: getComapreUserLocationWithShop() queryDocumentSize "+queryDocumentSize);
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                i++;
                                GeoPoint user_geopoint = documentSnapshot.getGeoPoint("geoPoint");
                                String riderName = documentSnapshot.getString("name");
                                String riderUID = documentSnapshot.getId();
                                com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                                        ShopGeoPoint.getLatitude(),
                                        ShopGeoPoint.getLongitude()
                                );
                                DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
                                directions.alternatives(false);

                                directions.origin(
                                        new com.google.maps.model.LatLng(
                                                user_geopoint.getLatitude(),
                                                user_geopoint.getLongitude()
                                        )
                                );
                                Log.d(TAG, "calculateDirections: destination: " + destination.toString());
                                directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                                    @Override
                                    public void onResult(DirectionsResult result) {
                                        String dsRiderUID = riderUID;
                                        String dsRiderName = riderName;
                                        long dlRiderDuration = result.routes[0].legs[0].duration.inSeconds;
                                        long dlRiderDistance = result.routes[0].legs[0].distance.inMeters;
                                        Log.d(TAG, "onGetUser");
                                        Log.d(TAG, "\nonResult: Name " + riderName + " no: "+i);
                                        Log.d(TAG, "onResult: duration: " + dlRiderDuration);
                                        Log.d(TAG, "onResult: distance: " + dlRiderDistance);
                                        //Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                                        //Log.d(TAG, "onResult: routes: " + result.routes[0].toString());


                                        ar.add(new Map_Distance_Model(dlRiderDuration, dlRiderDistance, dsRiderUID, dsRiderName));

                                    }

                                    @Override
                                    public void onFailure(Throwable e) {
                                        Log.e(TAG, "onFailure: " + e.getMessage() );

                                    }
                                });
                            }
                        }

                    }

                }
            });
        }

        //METHOD SEVEN, Realtime User  Location Data Retrive code
        private Handler mHandler = new Handler();
            private Runnable mRunnable;
            private static final int LOCATION_UPDATE_INTERVAL = 3000;
        private Handler mHander = new Handler();
        private static final int LOCATION_UPDATE_AFTER = 3000;
        public void delay(){
            Runnable mRunnable ;
            mHandler.postDelayed(mRunnable = new Runnable() {
                @Override
                public void run() {
                    resultShortestDistanceOfRiderFromShop();
                    //mHandler.postDelayed(mRunnable, LOCATION_UPDATE_AFTER);
                }
            }, LOCATION_UPDATE_AFTER);
        }
        //Method Eight a
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private void resultShortestDistanceOfRiderFromShop(){
            if(ar.size()>0){
                Collections.sort(ar, new SortbyDistance());
                Collections.sort(ar, new SortbyDuration());
                String LowestDistanceRiderUID = ar.get(0).getUser_uid() ;
                String LowestDistanceRiderName= ar.get(0).getUser_name() ;
                Log.d(TAG, "onResult: Lowest Distance Name: " + LowestDistanceRiderName);
                Log.d(TAG, "onResult: Lowest Distance UID: " + LowestDistanceRiderUID);
                //Log.d(TAG, "onResult: Array Size: " + String.valueOf(array_size));
                Log.d(TAG, "onResult: Setting RiderUId on Server; name.");
                /*PB_OrderDetails pb_orderDetailsObject = new PB_OrderDetails();
                pb_orderDetailsObject.setRiderUID(LowestDistanceRiderUID);*/
                if(!dsOrderUID.equals("NO")){
                    db.collection("HatherKacheApp").document("Sylhet")
                            .collection("Orders").document(dsOrderUID).update("uid_rider",LowestDistanceRiderUID).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("UserListFragment", "onResult: set shortest distance RiderUID on server " + LowestDistanceRiderUID);
                            getUserLocation(LowestDistanceRiderUID, "Riders");
                        }
                    });
                }else
                    Log.d("UserListFragment", "onResult: dsOrderUID not found " + LowestDistanceRiderUID);

            }else{
                Log.d(TAG, "onResult: Array Size: Zero");
            }
        }
        //Method Nine A
        private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        private void getUserLocation(String dsUserUID, String dsLevel2_Name){
            DocumentReference locationsRef = mDb
                    .collection("HatherKacheApp").document("Location")
                    .collection(dsLevel2_Name).document(dsUserUID);
            Log.d(TAG, "onResult: getUserLocation() ");
            locationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        if(task.getResult().toObject(MAP_UserLocationModel.class) != null){
                            //Toast.makeText(getApplicationContext(),"User Found ",Toast.LENGTH_SHORT).show();
                            mUserLocations.add(task.getResult().toObject(MAP_UserLocationModel.class));
                            Log.d(TAG, "onResult: Nearest Rider Location added to model class");
                            addMapMarkers();
                        }else{
                            Log.d(TAG, "onResult: Nearest Rider Location Not Found");
                        }
                    }else{
                        Log.d(TAG, "onResult: Nearest Rider Location Not Foundxx");
                    }
                }
            });

        }
        //Method Ten a
        private ClusterManager<MAP_ClusterMarkerModel> mClusterManager;
        private MAP_MyClusterManagerRenderer mClusterManagerRenderer;
        private ArrayList<MAP_ClusterMarkerModel> mClusterMarkers = new ArrayList<>();

        //Method Ten a
        private void addMapMarkers(){

            if(mGoogleMap != null){

                if(mClusterManager == null){
                    mClusterManager = new ClusterManager<MAP_ClusterMarkerModel>(getActivity().getApplicationContext(), mGoogleMap);
                }
                if(mClusterManagerRenderer == null){
                    mClusterManagerRenderer = new MAP_MyClusterManagerRenderer(
                            getActivity(),
                            mGoogleMap,
                            mClusterManager
                    );
                    mClusterManager.setRenderer(mClusterManagerRenderer);
                }
                mGoogleMap.setOnInfoWindowClickListener(this);

                for(MAP_UserLocationModel userLocation: mUserLocations){

                    Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeoPoint().toString());
                    try{
                        int avatar;
                        String snippet = "";
                        if(userLocation.getUid().equals(FirebaseAuth.getInstance().getUid()) && userLocation.getAvater().equals("Riders") ){
                            snippet = "This is you";
                            avatar = R.drawable.abrider; // set the default avatar
                        }else if(userLocation.getUid().equals(FirebaseAuth.getInstance().getUid())){
                            snippet = "This is you";
                            avatar = R.drawable.auser; // set the default avatar
                        }else if(userLocation.getAvater().equals("FoodDelivery") ){
                            snippet = "Determine route to Restaurant"  + "?";
                            avatar = R.drawable.aresturent; // set the resturent icon
                        }else if(userLocation.getAvater().equals("GroceryShopping") ){
                            snippet = "Determine route to GroceryShop"  + "?";
                            avatar = R.drawable.agraocery; // set the resturent icon //ERROR change icon
                        }else if(userLocation.getAvater().equals("Riders") ){
                            snippet = "Determine route to Rider"  + "?";
                            avatar = R.drawable.abrider; // set the default avatar
                        }else{
                            snippet = "Determine route to Him"  + "?";
                            avatar = R.drawable.auser; // set the default avatar
                        }
                        String avater_type = userLocation.getAvater();

    /*                    /////Code for Distance Calculator Start
                String dsMarkerType  = userLocation.getAvater();//Human or Shop
                if(dsMarkerType.equals("FoodDelivery")){
                    dgShopGeoPoint = userLocation.getGeoPoint();
                    OrderTypeFoodorGrocery = true;
                }
                if(dsMarkerType.equals("Riders")){
                    NearestRiderDeclared = true;
                }


                /////Code for Distance Calculator End*/

                /*try{
                    avatar = Integer.parseInt(userLocation.getAvater());
                }catch (NumberFormatException e){
                    Log.d(TAG, "addMapMarkers: no avatar for " + ", setting default.");
                }*/
                        MAP_ClusterMarkerModel newClusterMarker = new MAP_ClusterMarkerModel(
                                new LatLng(userLocation.getGeoPoint().getLatitude(), userLocation.getGeoPoint().getLongitude()),
                                userLocation.getName(),
                                snippet,
                                avater_type,
                                avatar,
                                userLocation.getUid()
                        );
                        mClusterManager.addItem(newClusterMarker);
                        mClusterMarkers.add(newClusterMarker);

                    }catch (NullPointerException e){
                        Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                    }

                }
                mClusterManager.cluster();
                setCameraView();
            }

        }
    //Method Eleven a //Video NO 10
    private void setCameraView() {
        boolean mUserPostionNull = false;
        if(mUserPosition == null){
            mUserPostionNull = true;
            //Toast.makeText(getActivity(),"Present User Postion Null", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Present User Maybe Shop Owner", Toast.LENGTH_LONG).show();
            if(shopLoctionModel != null){

                mUserPosition = shopLoctionModel;
                Toast.makeText(getActivity(),"Zooming to Shop", Toast.LENGTH_LONG).show();
            }
        }

        if(mUserPosition != null){
            double bottomBoundary = mUserPosition.getGeoPoint().getLatitude() - .005;   // Set a boundary to start
            double leftBoundary = mUserPosition.getGeoPoint().getLongitude() - .005;
            double topBoundary = mUserPosition.getGeoPoint().getLatitude() + .005;
            double rightBoundary = mUserPosition.getGeoPoint().getLongitude() + .005;

            mMapBoundary = new LatLngBounds(    //Map View Camera Setting
                    new LatLng(bottomBoundary, leftBoundary),
                    new LatLng(topBoundary, rightBoundary)
            );

            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 10));
                }
            });
            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 10));
            /*if(mUserPostionNull == true){
                mUserPosition = null;
                mUserPostionNull = false;
            }*/
        }

    }
    //Method Resume Eleven
    private void startUserLocationsRunnable(){ //It will call every 3 second to retrive location data of all user
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    //METHOD Resume Tweleve
    private void retrieveUserLocations(){
        Log.d(TAG, "retrieveUserLocations: retrieving location of all users.");

        try{
            for(final MAP_ClusterMarkerModel clusterMarker: mClusterMarkers){
                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                        .collection("HatherKacheApp").document("Location")
                        .collection(clusterMarker.getAvater()).document(clusterMarker.getUserUID());
                Log.e(TAG, "retrieveUserLocations: type: " + clusterMarker.getAvater());
                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            final MAP_UserLocationModel updatedUserLocation = task.getResult().toObject(MAP_UserLocationModel.class);

                            // update the location
                            for (int i = 0; i < mClusterMarkers.size(); i++) {
                                try {
                                    if (mClusterMarkers.get(i).getUserUID().equals(updatedUserLocation.getUid())) {

                                        LatLng updatedLatLng = new LatLng(
                                                updatedUserLocation.getGeoPoint().getLatitude(),
                                                updatedUserLocation.getGeoPoint().getLongitude()
                                        );

                                        mClusterMarkers.get(i).setPosition(updatedLatLng);
                                        mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                                    }


                                } catch (NullPointerException e) {
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                }
                            }
                        }
                    }
                });
            }
        }catch (IllegalStateException e){
            Log.e(TAG, "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.getMessage() );
        }

    }
    //METHOD Thirteen, Onstop, onDestroy
    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }






    //Collect Data From Activity
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override   //Method Three //from Implements
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //map.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
        map.setMyLocationEnabled(true);   //removing my blue icon on map
        mGoogleMap = map;   //showing my live location to google mapview

        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this); ///long pressing on map, go to the implemetns
        mGoogleMap.setOnMarkerDragListener(this);
        mGoogleMap.setOnMarkerClickListener(this);

        //addMapMarkers();
        checkUserList();
        //setCameraView();
    }



    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        startUserLocationsRunnable(); // update user locations every 'LOCATION_UPDATE_INTERVAL'
    }
    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        ///stopLocationUpdates(); // stop updating user locations
        super.onPause();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Expnading the view
    //Method Eight
    private void expandMapAnimation(){

        MAPViewWeightAnimationWrapper mapAnimationWrapper = new MAPViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);//showing map view fully

        /*//hiding recyclerview
        MAPViewWeightAnimationWrapper recyclerAnimationWrapper = new MAPViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);
        recyclerAnimation.start();*/

        mapAnimation.start();
    }
    //METHOD NINE
    private void contractMapAnimation(){
        //smalling map view
        MAPViewWeightAnimationWrapper mapAnimationWrapper = new MAPViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);
        //expanding recycler view
        /*MAPViewWeightAnimationWrapper recyclerAnimationWrapper = new MAPViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);
        recyclerAnimation.start();*/

        mapAnimation.start();

    }
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;
    @Override
    public void onClick(View v) {   //from implements
        switch (v.getId()){
            case R.id.btn_full_screen_map:{

                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }

        }
    }

    //METHOD TEN
    @Override   //Implements ,GoogleMap.OnInfoWindowClickListener
    public void onInfoWindowClick(Marker marker) {  //marker is the our destination
        if(marker.getSnippet().equals("This is you")){
            marker.hideInfoWindow();
        }
        else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            calculateDirections(marker);

                            //resultShortestDistanceOfRiderFromShop();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    //METHOD ELEVEN
    //DIRECTION API
    private void calculateDirections(Marker marker){

        if(mUserPosition != null){
            Log.d(TAG, "calculateDirections: calculating directions.");
            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    marker.getPosition().latitude,
                    marker.getPosition().longitude
            );
            DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

            directions.alternatives(true);
            directions.origin(
                    new com.google.maps.model.LatLng(
                            mUserPosition.getGeoPoint().getLatitude(),  //its user postion, not the destination
                            mUserPosition.getGeoPoint().getLongitude()
                    )
            );
            Log.d(TAG, "calculateDirections: destination: " + destination.toString());
            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                    Log.d(TAG, "onResult: duration: " + result.routes[0].legs[0].duration);
                    Log.d(TAG, "onResult: distance: " + result.routes[0].legs[0].distance);
                    Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                    mMapInfoText.setVisibility(View.VISIBLE);
                    mMapInfoText.setText("Minimum Duration: "+result.routes[0].legs[0].duration+"   Distance: "+result.routes[0].legs[0].distance);
                    addPolylinesToMap(result);
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "onFailure: " + e.getMessage() );

                }
            });
        }else{
            Log.d(TAG, "calculateDirections: calculating directions failed because mUserPosition = null.");
        }

    }








            //Methow Tweleve
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolyLinesData.size() > 0){
                    for(Map_PolylineData_Model polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorGray500));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new Map_PolylineData_Model(polyline, route.legs[0]));
                }
            }
        });
    }
    private Geocoder geocoder;
    private boolean markerCreated = false;
    @Override   //for long pressing on map
    public void onMapLongClick(LatLng latLng) {

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                if(markerCreated == false){
                    markerCreated = true;
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng).title(streetAddress).draggable(true));
                }else{
                    Toast.makeText(getActivity(),"All ready Marker Created", Toast.LENGTH_LONG).show();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
        //for marker moving
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.setTitle("");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                marker.setTitle("X");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
               //GeoPoint mpGeo = new GeoPoint(latLng.latitude,latLng.longitude) ;
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String streetAddress = address.getAddressLine(0);
                        marker.setTitle(streetAddress);
                        String word = address.getLocality();
                        Toast.makeText(getActivity(), "w " + word, Toast.LENGTH_SHORT).show();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onMarkerClick(Marker marker) {
                return  false;
            }

            private ArrayList<Map_PolylineData_Model> mPolyLinesData = new ArrayList<>();
            @Override
            public void onPolylineClick(Polyline polyline) {
                for(Map_PolylineData_Model polylineData: mPolyLinesData){
                    Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
                    if(polyline.getId().equals(polylineData.getPolyline().getId())){
                        polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorSweetBue));
                        polylineData.getPolyline().setZIndex(1);
                    }
                    else{
                        polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorGray500));
                        polylineData.getPolyline().setZIndex(0);
                    }
                }
            }
        }
