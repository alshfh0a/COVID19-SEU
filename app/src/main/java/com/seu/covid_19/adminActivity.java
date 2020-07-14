package com.seu.covid_19;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class adminActivity extends AppCompatActivity
{
    /// message and ID & Phone fixed digit
    public static final String IDwrong = "wrong ID";
    final int ID_DIGIT = 10;

    /// layout
    Button btnSearch,exit; EditText searchID;
    AlertDialog alertDialog;
    ListView listView;
    AdapterAdapter adapter;
    ArrayList<UserModel> arrayOfUsers;

    ///// current user location
    List<ReportModel> userList = new ArrayList<ReportModel>();

    //// other users info
    List<UserModel> otherUsersInfo = new ArrayList<UserModel>();
    List<ReportModel> otherUsers = new ArrayList<ReportModel>();

    /// class to view the ID & Phone

    /// the FireBase reference
    String GovernmentID;
    FirebaseDatabase DB;
    DatabaseReference refLocation;

    /// here is the parameters
    final double distanceTOothers = 20; // Maximum distance to others in meters
    final long timeTOreport = 1209600;  // Maximum time to fetch locations in Seconds = 14 days
    final long timeINlocation = 900;    // Maximum time in the same location in Seconds;


    /// here (onCreate) inside the FireBase Location (path)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_search);

        /// view layout
        btnSearch = (Button) findViewById(R.id.btn_search);
        searchID = (EditText) findViewById(R.id.TxSearchID);
        listView = (ListView) findViewById(R.id.admin_listView);


        // Construct the data source
        arrayOfUsers = new ArrayList<UserModel>();

        // Create the adapter to convert the array to views
        adapter = new AdapterAdapter(this, arrayOfUsers);

        // Attach the adapter to a ListView
        listView.setAdapter(adapter);

        /// FireBase reference retriever
        DB = FirebaseDatabase.getInstance();
        refLocation = DB.getReference("COVID-19");


    }

    public void btnexit(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(adminActivity.this);
        builder.setTitle("are you sure ?");
        builder.setPositiveButton("SURE", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent myIntent = new Intent(getApplicationContext(), menuActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }


    /// here is the method to check the inserted info
    boolean dataInserted;
    public boolean dataInserted(String GovernmentID) {
        boolean GovCh=false;

        /// checkup the ID
        if (!GovCh) {
            if (GovernmentID.length() == ID_DIGIT && GovernmentID.matches("[0-9]+")) { GovCh = true; } /// ID correct
            else { Toast.makeText(this, IDwrong, Toast.LENGTH_LONG).show(); } /// ID wrong
        }
        if (GovCh ){ dataInserted = true; } /// correct

        return dataInserted;
    }

    /// from here the admin  upload the user status
    public void btnSearch(View view)
    {
        GovernmentID = searchID.getText().toString();
        if (dataInserted(GovernmentID))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(adminActivity.this);
            builder.setTitle("is he");

            // clear all arrays
            arrayOfUsers.clear();
            userList.clear();
            otherUsers.clear();
            otherUsersInfo.clear();
            adapter.clear();
            workOut();


            builder.setPositiveButton("confirmed case", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    UserModel user = new UserModel();
                    user.UserGvID = GovernmentID;
                    user.Risk = true;
                    updateUserInfo(user);
                    {
                        fetchAndCheck();
                    }

                }
            });
            builder.setNegativeButton("healthy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    UserModel user = new UserModel();
                    user.UserGvID = GovernmentID;
                    user.Risk = false;
                    updateUserInfo(user);
                }

            });
            alertDialog = builder.create();
            alertDialog.show();
        }

    }

    public void workOut()
    {
        refLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot s : dataSnapshot.getChildren())
                    {
                        /// here we create a temporary [user] to execute some Ifs
                        UserModel fetchedUser = s.getValue(UserModel.class);
                        String GovID = fetchedUser.UserGvID;
                        DataSnapshot locationsDataSnapshot =s.child("locations");

                        /// Two important Ifs
                        // ( if the user NOT the current user) others
                        if (!GovID.equals(GovernmentID))
                        {
                            for (DataSnapshot locationsSnapshot :locationsDataSnapshot.getChildren())
                            {
                                /// we add each Snap data of the other users to [otherUsers] list
                                ReportModel fetchedLocation =locationsSnapshot.getValue(ReportModel.class);
                                otherUsers.add(fetchedLocation);
                                otherUsersInfo.add(fetchedUser);
                            }
                        }
                        // ( if the user is the current user) user
                        if (GovID.equals(GovernmentID))
                        {
                            for (DataSnapshot locationsSnapshot :locationsDataSnapshot.getChildren())
                            {
                                /// we add each Snap data of the current user to [userList]
                                ReportModel user =locationsSnapshot.getValue(ReportModel.class);
                                userList.add(user);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    /// to updata the user status
    public void updateUserInfo(UserModel userModel)
    {
        refLocation.child(GovernmentID).child("Risk").setValue(userModel.Risk).
        addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(adminActivity.this,
                        "You reported successfully",
                        Toast.LENGTH_LONG).show();

            }});
    }

    /// compare between info of the user and confirmed users
    public void fetchAndCheck()
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
                        UserModel userInfoTemp =otherUsersInfo.get(x);

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

                                /// here we add User info to adapter { which means current user has been in the Mix time in Mix distance with confirmed case}
                                {
                                    adapter.add(userInfoTemp);
                                }
                            }
                        }
                    }
                }
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
        Location fetchedLocation = new Location("fetchedLocation");
        fetchedLocation.setLatitude(otherLatitude);
        fetchedLocation.setLongitude(otherLongitude);

        Distance = userLocation.distanceTo(fetchedLocation);
        return Distance;
    }


}
