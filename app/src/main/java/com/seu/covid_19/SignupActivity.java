package com.seu.covid_19;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



public class SignupActivity extends Activity {

    /// for the shared preferences
    Prefrences preference;

    /// for the layout
    Button btnLogin; EditText TxGovID, TxPhone;

    /// the FireBase reference
    FirebaseDatabase DB;
    DatabaseReference refUser;
    Query isExist;


    /// for user info
    UserModel userInfo; String GovernmentID,Phone; boolean Risk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        /// FireBase reference retriever
        DB = FirebaseDatabase.getInstance();
        refUser = DB.getReference("UserInfo");
        isExist = refUser.orderByChild("UserInfo").equalTo(GovernmentID);

        /// view layout
        btnLogin = (Button) findViewById(R.id.btn_login);
        TxGovID = (EditText) findViewById(R.id.TxGovID);
        TxPhone = (EditText) findViewById(R.id.TxPhone);

        /// shared preferences
        preference = new Prefrences(getApplicationContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GovernmentID = TxGovID.getText().toString();
                Phone = TxPhone.getText().toString();
                Risk = false;



                /// method to start (checkup), which is to upload the user info if not exist.
                userInfo = new UserModel();
                userInfo.UserGvID = GovernmentID;
                userInfo.UserPhone = Phone;
                userInfo.Risk = Risk;

                /// checkup with Firebase users info
                checkup();


                /// to update the Login data and not showing Signup activity again.
                preference.SaveGovID(GovernmentID);
                preference.SavePhone(Phone);
                preference.writeLoginStatue(true);


                /// to start (MapActivity)
                Intent myIntent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(myIntent);
                finish();
        }});}



    public void checkup()
    {
        isExist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String ID = dataSnapshot.child(GovernmentID).child("UserGvID").getValue(String.class);
                    if (ID != null)
                    {
                        boolean IsRisk = dataSnapshot.child(GovernmentID).child("Risk").getValue(Boolean.class);
                        if (!IsRisk) {  updateUserInfo(userInfo); }
                        else {}
                    }
                    else {updateUserInfo(userInfo); }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void updateUserInfo(UserModel userModel){
        refUser.child(GovernmentID).setValue(userModel);
    }



}