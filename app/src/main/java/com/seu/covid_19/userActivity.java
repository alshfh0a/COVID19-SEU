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
import android.view.View;
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


public class userActivity extends Activity  {
    /// Map resource
    LocationManager locationManager;
    LocationListener locationListener;
    Location previousLocation;

    /// parameters for location
    private static final long MIN_DISTANCE_FOR_UPDATES = 1; // meters (here one meter)
    private static final long MIN_TIME_BW_UPDATES = 60000; // milliseconds (here one minute)


    /// layout
    TextView viewLocation, status;

    /// used in layout and FireBase, almost everywhere :D
    double latitude, longitude;

    /// the FireBase reference
    FirebaseDatabase DB;
    DatabaseReference refLocation,refUserUpdateLocation,userLocations;
    List<ReportModel> userList = new ArrayList<ReportModel>();
    List<ReportModel> otherUsers = new ArrayList<ReportModel>();
    int cashConfirm ;


    /// user info
    String GovernmentID, Phone;

    /// here is the parameters
    final double distanceTOothers = 20; // Maximum distance to others in meters
    final long timeTOreport = 1209600;  // Maximum time to fetch locations in Seconds = 14 days
    final long timeINlocation = 900;    // Maximum time in the same location in Seconds;
    final int highAlarm = 3; // how many case to reach the high alarms
    final int medAlarm = 2;  // how many case to reach the med  alarms
    final int lowAlarm = 1;  // how many case to reach the low  alarms
    final int noAlarm = 0;   // how many case to reach the  no  alarms

    /// below parameters for update Location if the user walking(80 meters/ one minute{MIN_TIME_BW_UPDATES})
    final int MinDistance = 1; // Minimum the distance to update the data
    final int MaxDistance = 80;// Maximum the distance to update the data



    /// methods for verify (checkgranted) & (onRequestPermissionsResult) & (verifyAllPermissions)
    public boolean checkgranted(boolean granted)
    {
        return granted;
    }

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
    ///                          FROM HERE WE START THE APP                            ///
    //////////////////////////////////////////////////////////////////////////////////////




    /// here (onCreate) inside the FireBase Location (path)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        viewLocation = (TextView) findViewById(R.id.textGetLocation);
        status = (TextView) findViewById(R.id.status);

        /// get the location
        getMyLocation();


        /// to get the user  info
        SharedPreferences result = getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE);
        GovernmentID = result.getString("GOV_ID", "err ID");
        Phone = result.getString("PHONE", "err phone");


        //////////////////////////////////////////////////////////////////////////////////////////
        ///////                     from here FireBase configuration                       ///////
        //////////////////////////////////////////////////////////////////////////////////////////


        /* FireBase reference retriever**/
        DB = FirebaseDatabase.getInstance();
        refLocation = DB.getReference("COVID-19");
        refUserUpdateLocation = refLocation.child(GovernmentID).child("locations");
        userLocations = refLocation.child(GovernmentID).child("locations");

        /// to get the user location history
        userLocations.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot s : dataSnapshot.getChildren())
                    {
                        /// we add each Snap data of the current user to [userList]
                        ReportModel user =s.getValue(ReportModel.class);
                        userList.add(user);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        /// get the other location history
        refLocation.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot s : dataSnapshot.getChildren())
                    {
                        /// here we create a temporary [user] to execute some Ifs
                        UserModel fetchedUser = s.getValue(UserModel.class);
                        boolean userStatus = fetchedUser.Risk;
                        String GovID = fetchedUser.UserGvID;
                        DataSnapshot locationsDataSnapshot =s.child("locations");

                        /// Two important Ifs
                        // (one: if the user status is Confirmed) && (two: if the user NOT the current user)
                        if (userStatus && !GovID.equals(GovernmentID))
                        {
                            for (DataSnapshot locationsSnapshot :locationsDataSnapshot.getChildren())
                            {
                                /// we add each Snap data of the other users to [otherUsers] list
                                ReportModel fetchedLocation =locationsSnapshot.getValue(ReportModel.class);
                                otherUsers.add(fetchedLocation);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

    }


    public void getMyLocation()
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                /// use to view
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                viewLocation.setText(latitude+ "\n"+longitude);

                /// use in compare location
                maxUpdate(location);
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

    /// compare between info of the user and confirmed users
    public int fetchAndCheck()
    {
        long TimeStamp = System.currentTimeMillis()/1000 ;
        /// to check if the user list has value
        if (userList.size() > 0)
        {
            /// to create temporary user Location (from the current user fetched data) to be use in compare.
            for ( int i =0; i<userList.size() ;i++)
            {
                ReportModel userTemp = userList.get(i);

                /// to check if the  others user list has value (note we are not fetching all the users ONLY the confirmed cases)
                if (otherUsers.size() > 0)
                {
                    /// to create temporary user Location (from the other users fetched data) to be use in compare.
                    for (int x = 0; x<otherUsers.size();x++)
                    {
                        ReportModel othersTemp = otherUsers.get(x);

                        /////////////////////////////////////////////////////////////////////////////////////
                        ////   from here we start comparing  between the created temporary users data    ////
                        /////////////////////////////////////////////////////////////////////////////////////

                        //// first if the user TimedStamp is less then [ timeTOreport ] which is here 14 days
                        if ((othersTemp.time - TimeStamp)< timeTOreport)
                        {
                            /// if the distance is less then [distanceTOothers] which is 20 meters
                            if (distance(userTemp.latitude, userTemp.longitude, othersTemp.latitude, othersTemp.longitude)<distanceTOothers)
                            {
                                /// if the user and the confirmed user are in the [above if] which is the distance and the time is less than [] which is 15 mins.
                                if (userTemp.time - othersTemp.time < timeINlocation &&
                                        userTemp.time - othersTemp.time >  -timeINlocation)

                                /// here we add 1 int to cashConfirm { which means current user has been in the Mix time in Mix distance with confirmed case}
                                { cashConfirm++; }

                            }
                        }
                    }
                }
            }
        }
        return cashConfirm;
    }


    /// to get the distance between two points (the user and the retrieved one)
    double Distance ;
    public double distance(Double userLatitude, Double userLongitude,Double otherLatitude, Double otherLongitude)
    {
        Location userLocation = new Location("userLocation");
        userLocation.setLatitude(userLatitude);
        userLocation.setLongitude(userLongitude);
        Location fetchedLocation = new Location("fetchedLocation");
        fetchedLocation.setLatitude(otherLatitude);
        fetchedLocation.setLongitude(otherLongitude);

        Distance = userLocation.distanceTo(fetchedLocation);
        return Distance;
    }


    /// to Max Distance to update the user location
    public void maxUpdate (Location location)
    {
        /// if there is no location before
        if(previousLocation == null)
        {
            previousLocation = location;
            updateUserLocation();
        }
        else
        {
            /// 1< userLocation > 80
            if (previousLocation.distanceTo(location)> MinDistance &&
                previousLocation.distanceTo(location)< MaxDistance)
            {
                previousLocation = location;
                updateUserLocation();
            }
            else
            {
                previousLocation = location;
            }

        }






    }


    /// update the user location
    public void updateUserLocation()
    {
        long TimeStamp = System.currentTimeMillis()/1000 ;
        ReportModel updateLocation = new ReportModel(TimeStamp,latitude,longitude);
        String StringTimeStamp = TimeStamp +"";
        refUserUpdateLocation.child(StringTimeStamp).setValue(updateLocation);
    }

    /// button to create view and call method [ fetchAndCheck] which gives the value of checked the user status.
    public void getStatus(View view)
    {
        fetchAndCheck();
        if(checkgranted(true))
        {
            int cashed = cashConfirm;
            if (cashed == noAlarm) {
                status.setText("you are in No risk");
            }
            if (cashed == lowAlarm) {
                status.setText("you are in Low risk");
            }
            if (cashed == medAlarm) {
                status.setText("you are in Med risk");
            }
            if (cashed >= highAlarm) {
                status.setText("you are in high risk");
            }

        }
    }
}