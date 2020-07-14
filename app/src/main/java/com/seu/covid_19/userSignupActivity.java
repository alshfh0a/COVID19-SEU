package com.seu.covid_19;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



public class userSignupActivity extends Activity {

    /// message and ID & Phone fixed digit
    public static final String IDwrong = "wrong ID";
    public static final String PhoneWrong ="wrong phone";
    final int ID_DIGIT = 10;
    final int PHONE_DIGIT =10;

    /// for the shared preferences
    Prefrences preference;

    /// for the layout
    Button btnLogin; EditText TxGovID, TxPhone;

    /// the FireBase reference
    FirebaseDatabase DB;
    DatabaseReference refUser;

    /// for user info

    UserModel userInfo; String GovernmentID,Phone;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup);

        /// FireBase reference retriever
        DB = FirebaseDatabase.getInstance();
        refUser = DB.getReference("COVID-19");

        /// view layout
        btnLogin = (Button) findViewById(R.id.btn_login);
        TxGovID = (EditText) findViewById(R.id.TxGovID);
        TxPhone = (EditText) findViewById(R.id.TxPhone);

        /// shared preferences
        preference = new Prefrences(getApplicationContext());

        /// on click on SignUp button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                GovernmentID = TxGovID.getText().toString();
                Phone = TxPhone.getText().toString();


                dataInserted = dataInserted(GovernmentID,Phone);

                if(dataInserted)
                {
                    /// method to start (checkup), which is to upload the user info if not exist.
                    userInfo = new UserModel();
                    userInfo.UserGvID = GovernmentID;
                    userInfo.UserPhone = Phone;


                    /// checkup with FireBase users info
                    checkup(userInfo);

                    /// to update the Login data and not showing Signup activity again.
                    preference.SaveGovID(GovernmentID);
                    preference.SavePhone(Phone);
                    preference.writeLoginStatue(true);

                    /// to start (userctivity)
                    Intent myIntent = new Intent(getApplicationContext(), userActivity.class);
                    startActivity(myIntent);
                    finish();

                }

            }
        });
    }

    /// to update the user info to the FireBase
    UserModel userUP;
    public void checkup(UserModel user)
    {  userUP = user;
        refUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String ID = userUP.UserGvID;
                String phone = userUP.UserPhone;
                refUser.child(ID).child("UserGvID").setValue(ID);
                refUser.child(ID).child("UserPhone").setValue(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        }
        );
    }


    /// here is the method to check the inserted info
    boolean dataInserted;
    public boolean dataInserted(String GovernmentID,String Phone) {
        boolean GovCh=false;
        boolean PhoCH =false;

        /// check if bot ID & Phone are correct
        if (!GovCh || !PhoCH) /// both wrong
        {
            /// checkup the ID
            if (!GovCh) {
                if (GovernmentID.length() == ID_DIGIT && GovernmentID.matches("[0-9]+")) { GovCh = true; } /// ID correct
                else { Toast.makeText(this, IDwrong, Toast.LENGTH_LONG).show(); } /// ID wrong
            }

            /// check up the phone
            if (!PhoCH) {
                if (Phone.length() == PHONE_DIGIT && Phone.matches("[0-9]+")) { PhoCH = true; }  /// phone correct
                else { Toast.makeText(this, PhoneWrong, Toast.LENGTH_LONG).show(); } /// phone wrong
            }
        }
        if (GovCh && PhoCH){ dataInserted = true; } /// both correct

        return dataInserted;
    }




}