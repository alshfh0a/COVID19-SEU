package com.seu.covid_19;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.SystemClock;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static java.lang.Integer.parseInt;


public class MapActivity extends FragmentActivity implements View.OnClickListener {

        /// Map resource
        String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        private static final int REQUEST_CODE_PERMISSION = 2;
        GPSTracker gps;
        TextView textLocation;

        double latitude;
        double longitude;


        /// the FireBase reference
        FirebaseDatabase DB;
        DatabaseReference refUser;
        DatabaseReference refReport;
        Query query1;

        /// user info
        String GovernmentID,Phone;


        AlertDialog alertDialog;
        long timestamp = System.currentTimeMillis()/1000;



        SimpleDateFormat tt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");




        int Risk ;
        final double disBpo = 20;
        final double fechedSeco = 1.2096*1000000000;
        final int highAlarm = 3;
        final int medAlarm = 2;
        final int lowAlarm = 1;
        final int noAlarm = 0;




        /// here (onCreate) inside the FireBase Location (path)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.map_layout);

            try{
                if(ActivityCompat.checkSelfPermission(this, mPermission)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String []{mPermission}, REQUEST_CODE_PERMISSION);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            gps= new GPSTracker(MapActivity.this);
            textLocation=(TextView)findViewById(R.id.textGetLocation);
            if(gps.canGetLocation()){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                textLocation.setText(latitude+ " "+ longitude);
            }else{
                gps.showSettingsAlert();
            }


            /// to get the user  info
            SharedPreferences result = getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE);
            GovernmentID = result.getString("GOV_ID","err ID");
            Phone = result.getString("PHONE", "err phone");


            /// FireBase reference retriever
            DB = FirebaseDatabase.getInstance();
            refUser = DB.getReference("UserInfo");
            refReport = DB.getReference("UserLocationUpdate");

            query1 = refReport.orderByChild("confirmed").equalTo(true);

        }




        /// FireBase retriever
        /// main important (if) for confirmed cases


    @Override
    protected void onStart() {

        /// FireBase retriever Reports
        super.onStart();
        refReport.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {

                    for (DataSnapshot s : dataSnapshot.getChildren())
                    {
                        //ReportModel report = s.getValue(ReportModel.class);
                        boolean fechedConfirmed = false;
                        double fechedLatitude = 0;
                        double fechedLongitude = 0;
                        long fechedTimeStamp = 0L;

                        if (s.getKey().equals("confirmed"))
                        {fechedConfirmed = Boolean.valueOf(s.toString());}
                        //if (s.getKey().equals("time"))
                        //{fechedTimeStamp = Long.valueOf(s.toString());}
                        //if (s.getKey().equals("latitude"))
                        //{fechedLatitude = Double.valueOf(s.toString());}
                       // if (s.getKey().equals("longitude"))
                       // {fechedLongitude = Double.valueOf(s.toString());}

                        setDistance(fechedLatitude,fechedLongitude);

                        if (fechedConfirmed)
                        {Risk++;
                            if ((timestamp - fechedTimeStamp) <= (fechedSeco/1000) )
                            {
                                if (getDistance()> disBpo)
                                {

                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });
        if (Risk >= highAlarm) {
            Toast.makeText(this, "you are in high risk", Toast.LENGTH_LONG).show();
        }
        if (Risk >= medAlarm) {
            Toast.makeText(this, "you are in Med risk", Toast.LENGTH_LONG).show();
        }
        if (Risk >= lowAlarm) {
            Toast.makeText(this, "you are in Low risk", Toast.LENGTH_LONG).show();
        }
        if (Risk <= noAlarm) {
            Toast.makeText(this, "you are in NO risk", Toast.LENGTH_LONG).show();
        }

    }




        public void reportButton(View view){
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
        public void updateUserlocation(ReportModel Userlocation){
            refReport.setValue(Userlocation);
        }


        /// to get the distance between two points (the user and the retrieved one)
        double Distance ;
        public void setDistance(Double fechedLatitude, Double fechedLongitude){
             Location userLocation = new Location("userLocation");
             userLocation.setLatitude(latitude);
             userLocation.setLongitude(longitude);
             Location fechedLocation = new Location("fechedLocation");
             fechedLocation.setLatitude(fechedLatitude);
             fechedLocation.setLongitude(fechedLongitude);

             Distance = userLocation.distanceTo(fechedLocation);
        }

        public double getDistance() { return Distance; }




        public void updateUserInfo(UserModel userModel)
        { refUser.child(GovernmentID).setValue(userModel);}





        @Override
        public void onClick(View view) {    }


}
