package com.konik.hatherkache.Service.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.konik.hatherkache.R;
import com.konik.hatherkache.View.Ui.Level_C_Add;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.konik.hatherkache.Service.utill.Constants.MAPVIEW_BUNDLE_KEY;

public class MAP_SetLocation_Fragment extends Fragment implements OnMapReadyCallback,View.OnClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {
    private TextView mMapInfoText;
    private MapView mMapView;
    private RelativeLayout mMapContainer; //for showing full view of map
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;  //Boundary for Camera View
    private FusedLocationProviderClient mFusedLocationClient;   //by this we can find user last recent location
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Collecting Data From Map Activity
        if (getArguments() != null) {
            //Toast.makeText(getActivity(),"Toast Collection", Toast.LENGTH_LONG).show();
            //error1 mUserList = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
            //mUserLocations= getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
            Toast.makeText(getActivity(), "Total xArray Size", Toast.LENGTH_SHORT).show();
        }
        geocoder = new Geocoder(getActivity());//for long click
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment_set_mapview, container, false);
        mMapInfoText = view.findViewById(R.id.map_info_text);
        mMapView = view.findViewById(R.id.user_list_map);
        mMapContainer = view.findViewById(R.id.map_container); // for showing full view of map
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());   //for last recent location

        initGoogleMap(savedInstanceState);  //Initializing Google Map
        //setUserPosition();
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

    }

    @Override
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
        mGoogleMap.setOnMapLongClickListener(this); ///long pressing on map, go to the implemetns
        mGoogleMap.setOnMarkerDragListener(this);
        //for camera zooming
        getLastKnownLocation();

    }
    private void setCameraView(GeoPoint myGeo) {
        if(myGeo == null){
            Toast.makeText(getActivity(),"Present User Postion Null", Toast.LENGTH_LONG).show();
        }else{
            double bottomBoundary = myGeo.getLatitude() - .005;   // Set a boundary to start
            double leftBoundary = myGeo.getLongitude() - .005;
            double topBoundary = myGeo.getLatitude() + .005;
            double rightBoundary = myGeo.getLongitude() + .005;

            mMapBoundary = new LatLngBounds(    //Map View Camera Setting
                    new LatLng(bottomBoundary, leftBoundary),
                    new LatLng(topBoundary, rightBoundary)
            );

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
        }

    }
    private void getLastKnownLocation() {   //Method Eight
        Log.d(TAG, "getLastKnownLocation: called.");
        //Permission Check for safety
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Collect Last Recent Location
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if(location == null){
                        Toast.makeText(getActivity(),"Location null",Toast.LENGTH_LONG).show();
                    }else{
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d("TAG","GetLastLocation 1 "+geoPoint.getLatitude());
                        Log.d("TAG","GetLastLocation 2 "+geoPoint.getLongitude());

                        setCameraView(geoPoint);
                    }

                }else{
                    Toast.makeText(getActivity(),"Fail to Collect Recent Location",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public static MAP_SetLocation_Fragment newInstance() {  //For Map Activity class, we call this fregment by this instance
        return new MAP_SetLocation_Fragment();
    }
    @Override
    public void onClick(View v) {   //map extended click

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
    }
    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();

    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();

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

    private Geocoder geocoder;
    private boolean markerCreated = false;
    @Override   //for long pressing on map
    public void onMapLongClick(LatLng latLng) {

        try {

            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if(addresses.size() > 0){
                if(markerCreated == false){
                    Address address = addresses.get(0);
                    String streetAddress = address.getAddressLine(0);
                    mMapInfoText.setText("Lattitudex: "+latLng.latitude+"\nLongitude: "+latLng.longitude+"\nArea: "+streetAddress);
                    markerCreated = true;
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng).title(streetAddress).draggable(true));

                    GeoPoint myGeo = new GeoPoint(latLng.latitude, latLng.longitude);
                    Level_C_Add activity = (Level_C_Add) getActivity();
                    activity.setGeoPoint(myGeo,streetAddress);
                }else{
                    Toast.makeText(getActivity(),"All ready Marker Created", Toast.LENGTH_LONG).show();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng = marker.getPosition();
        mMapInfoText.setText("Lattitude: "+latLng.latitude+"\nLongitude: "+latLng.longitude);
        //GeoPoint mpGeo = new GeoPoint(latLng.latitude,latLng.longitude) ;
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMapInfoText.setText("Lattitude: "+latLng.latitude+"\nLongitude: "+latLng.longitude+"\nArea: "+streetAddress);
                marker.setTitle(streetAddress);
                String word = address.getLocality();
                Toast.makeText(getActivity(), "w " + word, Toast.LENGTH_SHORT).show();

                GeoPoint myGeo = new GeoPoint(latLng.latitude, latLng.longitude);
                Level_C_Add activity = (Level_C_Add) getActivity();
                activity.setGeoPoint(myGeo,streetAddress);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
