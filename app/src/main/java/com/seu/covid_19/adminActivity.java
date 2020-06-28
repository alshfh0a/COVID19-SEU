package com.seu.covid_19;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class adminActivity extends FragmentActivity
{

    /// layuot
    Button btnSearch; EditText searchID;
    AlertDialog alertDialog;


    /// the FireBase reference
    String ID;
    FirebaseDatabase DB;
    DatabaseReference refUser;


    /// here (onCreate) inside the FireBase Location (path)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_search);

        /// view layout
        btnSearch = (Button) findViewById(R.id.btn_search);
        searchID = (EditText) findViewById(R.id.TxSearchID);

        /// FireBase reference retriever
        DB = FirebaseDatabase.getInstance();
        refUser = DB.getReference("COVID-19");
    }

    /// from here the admin  upload the user status
    public void btnSearch(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(adminActivity.this);
        builder.setTitle("is he");
        ID = searchID.getText().toString();

        builder.setPositiveButton("confirmed case", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UserModel user = new UserModel();
                user.UserGvID = ID;
                user.Risk = true;
                updateUserInfo(user);

            }
        });
        builder.setNegativeButton("healthy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UserModel user = new UserModel();
                user.UserGvID = ID;
                user.Risk = false;
                updateUserInfo(user);
            }

        });
        alertDialog = builder.create();
        alertDialog.show();

    }


    /// to updata the user status
    public void updateUserInfo(UserModel userModel)
    {
        refUser.child(ID).child("Risk").setValue(userModel.Risk);
    }


}
