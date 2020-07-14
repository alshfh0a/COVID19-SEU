package com.seu.covid_19;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class menuActivity extends Activity {

    /// layout
    Button btnUser, btnAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        /// view layout
        btnUser  = (Button) findViewById(R.id.btn_userActivity);
        btnAdmin = (Button) findViewById(R.id.btn_adminActivity);

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /// to start (adminActivity)
                Intent myIntent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /// to start (adminActivity)
                Intent myIntent = new Intent(getApplicationContext(), adminSigninActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

    }
}
