package com.seu.covid_19;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.os.SystemClock.sleep;


public class test extends FragmentActivity implements View.OnClickListener {
    TextView text1, text2, text3, text4;
    long fechedTimeStamp = 1592160366;
    long timestamp = System.currentTimeMillis()/1000 ;
    final double fechedSeco = 1.2096 * 1000000;

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
    DatabaseReference query1;
    ArrayList array;



    /// user info
    String GovernmentID,Phone;

    /// report info
    ReportModel reportModel;


    AlertDialog alertDialog;



    int Risk ;

    final double disBpo = 20; //meters
    final int highAlarm = 3;
    final int medAlarm = 2;
    final int lowAlarm = 1;
    final int noAlarm = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text4);

        /// get the location
        try{
            if(ActivityCompat.checkSelfPermission(this, mPermission)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String []{mPermission}, REQUEST_CODE_PERMISSION);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        gps= new GPSTracker(test.this);

        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }



        /// to get the user  info
        SharedPreferences result = getSharedPreferences("LOGIN_FILE", Context.MODE_PRIVATE);
        GovernmentID = result.getString("GOV_ID","err ID");
        Phone = result.getString("PHONE", "err phone");



        /// FireBase reference retriever
        /// FireBase retriever reports
        /// main important (if) statement

        DatabaseReference reportType;
        reportType = FirebaseDatabase.getInstance().getReference("UserLocationUpdate");
        reportType.push();
        DB = FirebaseDatabase.getInstance();
        refUser = DB.getReference("UserInfo");
        refReport = DB.getReference("UserLocationUpdate").push();
        refReport.setValue(array);

        query1 = DB.getReference("UserLocationUpdate");
        reportType.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int fuck = 0;
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot s : dataSnapshot.getChildren()){
                        ReportModel reportModel = s.getValue(ReportModel.class);
                            boolean fechedConfirmed = reportModel.confirmed;
                            double fechedLatitude = reportModel.latitude;
                            double fechedLongitude = reportModel.longitude;
                            long fechedTimeStamp = reportModel.time;
                               text1.setText(fechedConfirmed +"");
                               text2.setText(fechedTimeStamp+"");
                               ///text3.setText(fuck.toString);


                            setDistance(fechedLatitude,fechedLongitude);

                            if (fechedConfirmed == true)
                            {
                                if ((timestamp - fechedTimeStamp) <= (fechedSeco) )
                                {
                                    if (getDistance()< disBpo)
                                    {
                                        fuck ++;
                                    }
                                }
                            }


                    }


                }
                if (fuck == noAlarm) {
                    text4.setText("you are in No risk");
                }
                if (fuck == lowAlarm) {
                    text4.setText("you are in Low risk");
                }
                if (fuck == medAlarm) {
                    text4.setText("you are in Med risk");
                }
                if (fuck >= highAlarm) {
                    text4.setText("you are in high risk");
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }

        });





    }




        public void reportButton(View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(test.this);
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
