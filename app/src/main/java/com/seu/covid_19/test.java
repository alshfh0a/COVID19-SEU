package com.seu.covid_19;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class test extends Activity  {
    /// Map resource
    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;

    /// parameters for location
    private static final long MIN_DISTANCE_FOR_UPDATES = 1; // meters (here one meter)
    private static final long MIN_TIME_BW_UPDATES = 1000; // milliseconds (here one second)


    /// layout
    TextView text1, text2, text3, text4;

    /// used in layout and FireBase, almost everywhere :D
    double latitude;
    double longitude;

    /// the FireBase reference
    FirebaseDatabase DB;
    DatabaseReference refLocation,refUserUpdateLocation;


    /// user info
    String GovernmentID, Phone;

    /// here is the parameters
    final double disBpo = 20; // in meters
    final long fechedSeco = 1209600; // in Seconds = 14 days
    final int highAlarm = 3; // how many case to reach the high alarms
    final int medAlarm = 2;  // how many case to reach the med  alarms
    final int lowAlarm = 1;  // how many case to reach the low  alarms
    final int noAlarm = 0;   // how many case to reach the  no  alarms



    /// methods for verify (checkgranted) & (onRequestPermissionsResult) & (verifyAllPermissions)
    public boolean checkgranted(boolean granted) { return granted; }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 100)
        {
            if (!verifyAllPermissions(grantResults))
            {Toast.makeText(getApplicationContext(),"No sufficient permissions", Toast.LENGTH_LONG).show(); }
            else
            {
                getMyLocation();
                checkgranted(true);
            }
        }
        else
        {super.onRequestPermissionsResult(requestCode, permissions, grantResults); }
    }

    public boolean verifyAllPermissions(int[] grantResults)
    {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    //////////////////////////////////////////////////////////////////////////////////////
    /// ########        ######        ########       ##########
    /// ###            ##    ##       ##    ##       ##  ##  ##







    /// here (onCreate) inside the FireBase Location (path)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text4);

        /// get the location
        getMyLocation();


        /// to get the user  info
        SharedPreferences result = getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE);
        GovernmentID = result.getString("GOV_ID", "err ID");
        Phone = result.getString("PHONE", "err phone");


        /// FireBase reference retriever
        /// FireBase retriever reports
        /// main important (if) statement
        DB = FirebaseDatabase.getInstance();
        refLocation = DB.getReference("COVID-19");
        refUserUpdateLocation = refLocation.child(GovernmentID).child("locations");


        if(checkgranted(true))
        {
            int cashed = fechAndCheck();
            text2.setText(cashed+"");

            if (cashed == noAlarm) {
                text1.setText("you are in No risk");
            }
            if (cashed == lowAlarm) {
                text1.setText("you are in Low risk");
            }
            if (cashed == medAlarm) {
                text1.setText("you are in Med risk");
            }
            if (cashed >= highAlarm) {
                text1.setText("you are in high risk");
            }


        }



    }

    /// to get the distance between two points (the user and the retrieved one)
    double Distance ;
    public double distance(Double userLatitude, Double userLongitude,Double otherLatitude, Double otherLongitude)
    {
        Location userLocation = new Location("userLocation");
        userLocation.setLatitude(userLatitude);
        userLocation.setLongitude(userLongitude);
        Location fechedLocation = new Location("fechedLocation");
        fechedLocation.setLatitude(otherLatitude);
        fechedLocation.setLongitude(otherLongitude);

        Distance = userLocation.distanceTo(fechedLocation);
        return Distance;
    }



    public void getMyLocation()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                long TimeStamp = System.currentTimeMillis()/1000 ;
                currentLocation = location;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                ReportModel updateLocation = new ReportModel(TimeStamp,latitude,longitude);
                String StringTimeStamp = TimeStamp +"";
                refUserUpdateLocation.child(StringTimeStamp).setValue(updateLocation);

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

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_FOR_UPDATES,locationListener);
            }
            else
            {
                requestPermissions(new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},100);
            }
        }
    }

    int cashConfirm;

    ArrayList<ReportModel> userList = new ArrayList<ReportModel>();
    ArrayList<ReportModel> otherUsers = new ArrayList<ReportModel>();
    public int fechAndCheck()
    {

            /// to get the user location history
            DatabaseReference userLocations = refLocation.child(GovernmentID).child("locations");
            userLocations.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        for (DataSnapshot s : dataSnapshot.getChildren())
                        {
                            ReportModel user =s.getValue(ReportModel.class);
                            userList.add(user);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {            }
            });

            /// get the other location history
            refLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        for (DataSnapshot s : dataSnapshot.getChildren())
                        {
                            UserModel fechedUser = s.getValue(UserModel.class);
                            String GovID = fechedUser.UserGvID;
                            boolean userStatus = fechedUser.Risk;
                            DataSnapshot locationsDataSnapshot =s.child("locations");
                            if (userStatus && !GovID.equals(GovernmentID))
                            {
                                for (DataSnapshot locationsSnapshot :locationsDataSnapshot.getChildren())
                                {
                                    ReportModel fechecLocation =locationsSnapshot.getValue(ReportModel.class);
                                    otherUsers.add(fechecLocation);

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {            }

            });

            /// compare between info of the user and confirmed users
            if (userList != null)
            {
                for ( int i =0;i<userList.size();i++)
                {
                    ReportModel userTemp = userList.get(i);
                    text3.setText(userTemp.time+"");
                    if (otherUsers!= null)
                    {
                        for (int x = 0; x<otherUsers.size();x++)
                        {
                            ReportModel othersTemp = otherUsers.get(x);
                            text4.setText(othersTemp.time+"");
                            if (userTemp.time - othersTemp.time <fechedSeco)
                            {
                                if (distance(userTemp.latitude, userTemp.longitude, othersTemp.latitude, othersTemp.longitude)<disBpo) { cashConfirm++; }
                            }
                        }
                    }
                }
            }

        return cashConfirm;
    }


}