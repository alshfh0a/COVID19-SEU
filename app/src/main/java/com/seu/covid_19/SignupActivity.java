package com.seu.covid_19;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.prefs.Preferences;


public class SignupActivity extends Activity {
    Prefrences preference;
    Button btnLogin;
    EditText TxGovID, TxPhone;
    String GovernmentID, Phone;
    DataSnapshot dataSnapshot;
    DatabaseReference userID = FirebaseDatabase.getInstance().getReference("UserInfo");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        preference = new Prefrences(getApplicationContext());

        btnLogin = (Button) findViewById(R.id.btn_login);
        TxGovID = (EditText) findViewById(R.id.TxGovID);
        TxPhone = (EditText) findViewById(R.id.TxPhone);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /// method to start (checkup), which is to upload the user info if not exist.
                addintoDB();
                /// submitID();
                /// to update the Login data and not showing Signup activity again.
                preference.SaveGovID(GovernmentID);
                preference.SavePhone(Phone);
                preference.writeLoginStatue(true);

                /// to start (MapActivity)
                Intent myIntent = new Intent(getApplicationContext(), MapActivity.class);

                startActivity(myIntent);

                finish();
        }});}

        /// to upload User information to FireBase
    public void addintoDB ()
    {
        UserModel UserInfo = new UserModel(TxGovID.getText().toString(),TxPhone.getText().toString(),false );
        FirebaseDatabase.getInstance().getReference("COVID-19").child("UserInfo").push()
                .setValue(UserInfo);
    }


    public void submitID(){
        String ID = TxGovID.getText().toString();
        Intent I = new Intent(this, MapActivity.class);
        I.putExtra("ID", ID);
        startActivity(I);
    }


    public void checkup()
    {
        if (dataSnapshot != null){
        for (DataSnapshot s : dataSnapshot.getChildren()){
        UserModel report = s.getValue(UserModel.class);
        String GovID = report.UserGvID;
        String Phone = report.UserPhone;

            if (s != null)
            {
                if (TxGovID.getText().toString() != GovID)
                { addintoDB ();}
                if (TxPhone.getText().toString() != Phone)
                { addintoDB ();}}
        }
    }}

}