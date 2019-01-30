package com.wayforlife.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.wayforlife.Activities.HomeActivity;
import com.wayforlife.Common.CommonData;
import com.wayforlife.GlobalStateApplication;
import com.wayforlife.Models.LocationAddress;
import com.wayforlife.Models.Problem;
import com.wayforlife.Models.SerializeProblem;
import com.wayforlife.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeMapFragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    private MapView mapView;
    private Context context;
    private GoogleMap googleMap;
    private HomeActivity homeActivity;
    private Map<String,Problem> problemHashMap; //will store the problem
    private Map<String,String> problemKeyHashap; //will store key of the problem
    private Boolean flag=true;

    //Whenever Home button will be clicked then this fragment will be displayed


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        problemHashMap=new HashMap<>();
        problemKeyHashap=new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.home_map_fragment,container,false);


        homeActivity= (HomeActivity) getActivity();
        context=getContext();
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();//needed to get the map to display immediately

        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        return view;

    }

    //Below method will show all the problems on map and put marker onto that place.
    private void showAllTheProblemByMarker() {
        GlobalStateApplication.problemDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Problem problem=dataSnapshot.getValue(Problem.class);
                if(problem!=null) {
                    putMarkerOnMap(problem,dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void putMarkerOnMap(Problem problem,String key) {
        LocationAddress locationAddress=problem.getLocationAddress();
        MarkerOptions markerOptions=new MarkerOptions();
        LatLng latLng=new LatLng(Double.parseDouble(locationAddress.getLatitude()),
                Double.parseDouble(locationAddress.getLongitude()));
        markerOptions.position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(280f));
        Marker marker=googleMap.addMarker(markerOptions);
        problemHashMap.put(marker.getId(),problem); //storing the problem
        problemKeyHashap.put(marker.getId(),key); //storing the key of the problem.
        Log.i("key",problemKeyHashap.get(marker.getId())+" "+key);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();

        //When tha app open first time i want to animate the camera and after it i will directly move the camera on that place.
        if(flag && GlobalStateApplication.isFirstTimeMapIsShowing) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            GlobalStateApplication.isFirstTimeMapIsShowing = false;
            flag=false;
        }else if(flag){
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            flag=false;
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap=gMap;
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String id=marker.getId();
                SerializeProblem serializeProblem=new SerializeProblem();
                serializeProblem.setProblem(problemHashMap.get(id));
                Log.i("passing key",problemKeyHashap.get(id));
                homeActivity.addNewFragment(XyzProblemFragment.newInstance(serializeProblem,true,problemKeyHashap.get(id)),getString(R.string.xyzProblemFragmentTag));
                return true;
            }
        });

        showAllTheProblemByMarker();
        googleMap.setOnMapClickListener(this);

//      googleMap.setMyLocationEnabled(true);
//        LatLng sydney=new LatLng(20,78);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker title"));
//
//        CameraPosition cameraPosition=new CameraPosition.Builder().target(sydney).zoom(12).build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.i("MapReady","-------------------------------inside it");
        if(!hasPermission()){
            Log.i("Permission check","Inside has permission");
            getPermission();
        }else{
            Log.i("Inside","getuserlocation");
//            getUserLocation();
        }

    }

    //Whenever user will click on map then he will be redirected to XyzProblem fragment where he can report the problem.
    @Override
    public void onMapClick(LatLng latLng) {
        Problem problem=new Problem();
        problem.setLocationAddress(new LocationAddress(String.format("%.3f",latLng.latitude),
                String.format("%.3f",latLng.longitude),getAddressFromLocation(latLng.latitude,
                latLng.longitude)));
        problem.setDate(CommonData.getTodayDate());
        problem.setTime(CommonData.getCurrentTime());
        SerializeProblem serializeProblem=new SerializeProblem(problem);
        homeActivity.addNewFragment(XyzProblemFragment.newInstance(serializeProblem,
                false,""),"xyzProblemFragment");
    }




    //It will return the address from the longitude and latitude.
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.i("address",String.valueOf(addresses.size()));
            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                if(fetchedAddress.getAddressLine(0)!=null)
                strAddress.append(fetchedAddress.getAddressLine(0)).append(" ");
                if(fetchedAddress.getLocality()!=null)
                strAddress.append(fetchedAddress.getLocality()).append(" ");
                if(fetchedAddress.getAdminArea()!=null)
                strAddress.append(fetchedAddress.getAdminArea()).append(" ");
                if(fetchedAddress.getCountryName()!=null)
                strAddress.append(fetchedAddress.getCountryName()).append("\n");

                Log.i("strAddress",strAddress.toString());
                return strAddress.toString();

            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    //Checking whether user give permission or not
    private boolean hasPermission() {

        if (Build.VERSION.SDK_INT > 21) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    //Show permission dialog to user
    private void getPermission() {
        Log.i("get permission","inside get permission");
        ActivityCompat.requestPermissions(homeActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, CommonData.LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CommonData.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("value ","--------"+String.valueOf(hasPermission()));
                if (!hasPermission()) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CommonData.LOCATION_PERMISSION_REQUEST_CODE);
                }else{
//                    getUserLocation();
                }
            }
        }
    }

    public static HomeMapFragment newInstance() {
        Bundle args = new Bundle();
        HomeMapFragment fragment = new HomeMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        //againRequestLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
