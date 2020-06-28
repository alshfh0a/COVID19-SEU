package com.seu.covid_19;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class adminSigninActivity extends Activity {

    /// user and password for admin
    public static final String adminUser = "admin";
    public static final String adminPassword = "admin";


    /// message and ID & Phone fixed digit
    public static final String Userwrong = "wrong User";
    public static final String PasswordWrong ="wrong Password";

    /// for the layout
    Button btnAdmin; EditText TxAdminUser, TxAdminPassword;

    //admin info
    String  AdminUser,AdminPassword;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_signup);


        /// view layout
        btnAdmin = (Button) findViewById(R.id.btn_admin);
        TxAdminUser = (EditText) findViewById(R.id.TxAdminUser);
        TxAdminPassword = (EditText) findViewById(R.id.TxAdminPassword);


        /// on click on SignUp button
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdminUser = TxAdminUser.getText().toString();
                AdminPassword = TxAdminPassword.getText().toString();

                dataInserted = adminCheckUp(AdminUser, AdminPassword);

                if (dataInserted)
                {
                    /// to start (adminActivity)
                    Intent myIntent = new Intent(getApplicationContext(), adminActivity.class);
                    startActivity(myIntent);
                    finish();
                }

            }
        });
    }

    /// here is the method to check the inserted info
    boolean dataInserted;
    public boolean adminCheckUp(String AdminUser,String AdminPassword) {
        boolean AdminUs = false;
        boolean AdminPas = false;

        /// check if bot User & Password are correct
        if (!AdminUs || !AdminPas) /// both wrong
        {
            /// checkup the User
            if (!AdminUs)
            {
                if (AdminUser.equals(adminUser)) { AdminUs = true; } /// ID correct
                else { Toast.makeText(this, Userwrong, Toast.LENGTH_LONG).show(); } /// ID wrong
            }

            /// check up the Password
            if (!AdminPas)
            {
                if (AdminPassword.equals(adminPassword)) { AdminPas = true; }  /// phone correct
                else { Toast.makeText(this, PasswordWrong, Toast.LENGTH_LONG).show(); } /// Password wrong
            }
        }
        if (AdminUs && AdminPas) { dataInserted = true; } /// both correct

        return dataInserted;
    }
}




