package com.seu.covid_19;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.ValueEventListener;


public class MapActivity extends FragmentActivity implements View.OnClickListener {

        /// Map resource
        String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        private static final int REQUEST_CODE_PERMISSION = 2;
        GPSTracker gps;

        /// layuot
        TextView textLocation,status;
        AlertDialog alertDialog;

        /// used in layour and Firebase, almost everwhere :D
        double latitude;
        double longitude;
        long timestamp = System.currentTimeMillis()/1000;

        /// the FireBase reference
        FirebaseDatabase DB;
        DatabaseReference refUser;
        DatabaseReference refReport;

        /// user info
        String GovernmentID,Phone;

        /// here is the parameters
        final double disBpo = 20; // in meters
        final long fechedSeco = 1209600; // in Seconds = 14 days
        final int highAlarm = 3; // how many case to reach the high alarms
        final int medAlarm = 2;  // how many case to reach the med  alarms
        final int lowAlarm = 1;  // how many case to reach the low  alarms
        final int noAlarm = 0;   // how many case to reach the  no  alarms

        /// here (onCreate) inside the FireBase Location (path)
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.map_layout);
            textLocation = (TextView)findViewById(R.id.textGetLocation);
            status = (TextView)findViewById(R.id.status);

            /// get the location
            try
            {
                if(ActivityCompat.checkSelfPermission(this, mPermission)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String []{mPermission}, REQUEST_CODE_PERMISSION);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            gps= new GPSTracker(MapActivity.this);

            if(gps.canGetLocation())
            {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                textLocation.setText(latitude+ " "+ longitude);
            }
            else
            {
                gps.showSettingsAlert();
            }


            /// to get the user  info
            SharedPreferences result = getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE);
            GovernmentID = result.getString("GOV_ID","err ID");
            Phone = result.getString("PHONE", "err phone");


            /// FireBase reference retriever
            /// FireBase retriever reports
            /// main important (if) statement
            DB = FirebaseDatabase.getInstance();
            refUser = DB.getReference("UserInfo");
            refReport = DB.getReference("UserLocationUpdate").push();

            DatabaseReference reportType;
            reportType = FirebaseDatabase.getInstance().getReference("UserLocationUpdate");
            reportType.push();
            reportType.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {  int Risk =0;
                            for (DataSnapshot s : dataSnapshot.getChildren()){
                                ReportModel reportModel = s.getValue(ReportModel.class);
                                boolean fechedConfirmed = reportModel.confirmed;
                                double fechedLatitude = reportModel.latitude;
                                double fechedLongitude = reportModel.longitude;
                                long fechedTimeStamp = reportModel.time;
                                setDistance(fechedLatitude,fechedLongitude);

                                if (fechedConfirmed)
                                {
                                    if ((timestamp - fechedTimeStamp) <= (fechedSeco) )
                                    {
                                        if (getDistance()< disBpo)
                                        {
                                            Risk++;
                                        }
                                    }
                                }
                            }
                        if (Risk == noAlarm) {
                            status.setText("you are in No risk");
                        }
                        if (Risk == lowAlarm) {
                            status.setText("you are in Low risk");
                        }
                        if (Risk == medAlarm) {
                            status.setText("you are in Med risk");
                        }
                        if (Risk >= highAlarm) {
                            status.setText("you are in high risk");
                        }

                    }
                    if(!dataSnapshot.exists()) {
                        status.setText("you are the first user or there is no data to retrieve");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {            }
            });


        }

        /// from here the user can upload the location the the user status
        public void reportButton(View view)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            builder.setTitle("Ara you confirmed case");

            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    UserModel user = new UserModel();
                    user.UserGvID = GovernmentID;
                    user.UserPhone = Phone;
                    user.Risk = true;
                    updateUserInfo(user);
                    ReportModel Userlocation= new ReportModel(GovernmentID,timestamp,latitude,longitude,true);
                    updateUserlocation(Userlocation);
					
                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ReportModel Userlocation= new ReportModel(GovernmentID,timestamp,latitude,longitude,false);
                    updateUserlocation(Userlocation);
                }

            });
            alertDialog = builder.create();
            alertDialog.show();

        }

         /// to upload the report file to Firebase
        public void updateUserlocation(ReportModel Userlocation)
        {
            refReport.setValue(Userlocation);
        }

        /// to get the distance between two points (the user and the retrieved one)
        double Distance ;
        public void setDistance(Double fechedLatitude, Double fechedLongitude)
        {
             Location userLocation = new Location("userLocation");
             userLocation.setLatitude(latitude);
             userLocation.setLongitude(longitude);
             Location fechedLocation = new Location("fechedLocation");
             fechedLocation.setLatitude(fechedLatitude);
             fechedLocation.setLongitude(fechedLongitude);

             Distance = userLocation.distanceTo(fechedLocation);
        }
        public double getDistance() { return Distance; }

        /// to updata the user status
        public void updateUserInfo(UserModel userModel)
        { refUser.child(GovernmentID).setValue(userModel);}

        @Override
        public void onClick(View view) {    }

}
