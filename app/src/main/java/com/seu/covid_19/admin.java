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
import com.google.firebase.database.ValueEventListener;

public class admin extends Activity {

    /// user and password for admin from Firebase
    public static String adminUser;
    public static String adminPassword ;


    /// message and ID & Phone fixed digit
    public static final String Userwrong = "wrong User";
    public static final String PasswordWrong ="wrong Password";

    /// for the layout
    Button btnAdmin; EditText TxAdminUser, TxAdminPassword;

    //admin info
    String  AdminUser,AdminPassword;
    AdminModel adminModel;

    /// the FireBase reference
    FirebaseDatabase DB;
    DatabaseReference refAdmin;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_signup);


        /// view layout
        btnAdmin = (Button) findViewById(R.id.btn_admin);
        TxAdminUser = (EditText) findViewById(R.id.TxAdminUser);
        TxAdminPassword = (EditText) findViewById(R.id.TxAdminPassword);

        /// FireBase reference retriever
        DB = FirebaseDatabase.getInstance();
        refAdmin = DB.getReference("ADMIN");


        /// on click on SignUp button
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AdminUser = TxAdminUser.getText().toString();
                AdminPassword = TxAdminPassword.getText().toString();
                adminModel = new AdminModel();
                adminModel.AdminUser = AdminUser;
                adminModel.AdminPassword = AdminPassword;

                firebaseAdmin(adminModel);
            }
        });
    }

    /// fetching the Info from the Firebase
    public void firebaseAdmin(final AdminModel adminModel)
    {
        refAdmin.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            { refAdmin.child(AdminUser).setValue(adminModel); }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

}

