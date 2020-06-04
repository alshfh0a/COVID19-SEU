package com.seu.covid_19;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class MapActivity extends FragmentActivity implements
        OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

/// below two methods for verification the permissions
/// onRequestPermissionsResult  && verifyAllPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 100){
            if (!verifyAllPermissions(grantResults)) {
                Toast.makeText(getApplicationContext(),"No sufficient permissions",Toast.LENGTH_LONG).show();
            }else{
                getMyLocation();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public boolean verifyAllPermissions(int[] grantResults) {

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


        GoogleMap myMap;
        LocationManager myLocationManager;
        LocationListener myLocationListener;
        LatLng myOrigin;
        double latitude;
        double longitude;
        DatabaseReference reportType;
        Marker marker;
        DataSnapshot dataSnapshot;
        String ID;
        String confirmed = "Confirmed Case";


        /// here (onCreate) inside the FireBase Location (path)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.map_layout);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.myMap);
            mapFragment.getMapAsync(this);


            /// to get the user  GovernmentID
            ///getID();


            /// Fire base location
            ChildEventListener mChildEventListener;
            reportType = FirebaseDatabase.getInstance().getReference("COVID-19").child("UserLocationUpdate");
            reportType.push().setValue(marker);
        }



        /// here (onMapReady) the following:-
        /// Get user location (only request)
        /// FireBase retriever
        /// main important (if) for confirmed cases
        @Override
        public void onMapReady(GoogleMap googleMap) {
            myMap = googleMap;
            getMyLocation();
            updateUserlocation();
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
            reportType.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    /// FireBase retriever
                   /** for (DataSnapshot s : dataSnapshot.getChildren()){
                        ReportModel report = s.getValue(ReportModel.class);
                        LatLng location =new LatLng(report.latitude,report.longitude);
                       /// int confirmed = new int(report.confirmed);

                        if (report.confirmed){
                            myMap.addMarker(new MarkerOptions().position(location).title(confirmed))
                                    .setIcon(BitmapDescriptorFactory.
                                                        defaultMarker(BitmapDescriptorFactory.HUE_RED));}
                    }**/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {   }
            });

        }



        /// here (getMyLocation) to get the current location of the user with movements
        public void getMyLocation(){
            myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            myLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    myOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myOrigin,13));
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(String provider) { }

                @Override
                public void onProviderDisabled(String provider) {}
            };

            int currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                    myMap.setMyLocationEnabled(true);

                    myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,myLocationListener);

                }else{
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },100);
                }
            }
        }


        public void updateUserlocation(){
            Date currentTime = Calendar.getInstance().getTime();
            boolean confirmed = false;
            ReportModel Userlocation = new ReportModel(ID, latitude, longitude, currentTime, confirmed);
            FirebaseDatabase.getInstance().getReference("COVID-19").child("UserLocationUpdate").push()
                    .setValue(Userlocation);
        }

        public void getID(){
            Intent I = getIntent();
            ID = Objects.requireNonNull(I.getExtras()).getString("ID");
        }

        @Override
        public void onClick(View view) {    }

        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }

}
