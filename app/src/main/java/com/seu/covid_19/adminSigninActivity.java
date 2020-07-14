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

import java.util.ArrayList;
import java.util.List;


public class adminSigninActivity extends Activity {

    /// user and password for admin from Firebase
    List<AdminModel> adminList = new ArrayList<AdminModel>();


    /// message and ID & Phone fixed digit
    public static final String Err = "User or Password wrong";

    /// for the layout
    EditText TxAdminUser, TxAdminPassword;

    //admin info (inserted)
    String  AdminUser,AdminPassword;

    /// the FireBase reference
    FirebaseDatabase DB;
    DatabaseReference refAdmin;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_signup);

        /// view layout
        TxAdminUser = (EditText) findViewById(R.id.TxAdminUser);
        TxAdminPassword = (EditText) findViewById(R.id.TxAdminPassword);

        /// FireBase reference retriever
        DB = FirebaseDatabase.getInstance();
        refAdmin = DB.getReference("ADMIN");
        refAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    for (DataSnapshot s : snapshot.getChildren())
                    {
                        /// we add each Snap data of the Firebase to [adminList]
                        AdminModel adminTemp =s.getValue(AdminModel.class);
                        adminList.add(adminTemp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /// on click on SignUp button
    public void AdminButton(View view)
    {
        AdminUser = TxAdminUser.getText().toString();
        AdminPassword = TxAdminPassword.getText().toString();
        {
            dataInserted = adminCheckUp(AdminUser, AdminPassword);

            if (dataInserted)
            {
                /// to start (adminActivity)
                Intent myIntent = new Intent(getApplicationContext(), adminActivity.class);
                startActivity(myIntent);
                finish();
            }
            if (!dataInserted)
            {
                Toast.makeText(this, Err, Toast.LENGTH_LONG).show();
            }
        }


    }

    /// here is the method to check the inserted info
    boolean dataInserted;
    public boolean adminCheckUp(String AdminUser,String AdminPassword) {
        boolean AdminUs = false;
        boolean AdminPas = false;
        AdminModel adminTemp;
        String adminUser = AdminUser.toLowerCase();

        /// check if bot User & Password are correct
        if (!AdminUs || !AdminPas)
        {
            for (int i=0 ; i< adminList.size();i++)
            {
                adminTemp = adminList.get(i);
                if (adminUser.equals(adminTemp.AdminUser))
                { AdminUs = true; } /// ID correct
                if (AdminPassword.equals(adminTemp.AdminPassword))
                { AdminPas = true; } /// Password Correct
            }
        }

        if (AdminUs && AdminPas) { dataInserted = true; } /// both correct
        return dataInserted;
    }
}




