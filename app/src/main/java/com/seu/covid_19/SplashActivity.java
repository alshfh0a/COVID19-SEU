package com.seu.covid_19;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread myThread = new Thread() {

            @Override
            public void run() {
                try
                {
                        /// just a view to make our app locks better
                        sleep(2000);

                        /// calling preferences
                        Prefrences preference = new Prefrences(getApplicationContext()) ;

                        /// if the preferences is empty, as code if the (boolean) loginStatue if false
                        if (!preference.readLoginStatue())
                        {
                            ///start Singup activity
                            Intent myIntent = new Intent(getApplicationContext(), userSignupActivity.class);
                            startActivity(myIntent);
                            finish();
                        }

                        /// if the preferences in NOT null, as code if the (boolean) loginStatue if true
                        else
                        {
                             ///start Map activity
                             Intent myIntent = new Intent(getApplicationContext(), userActivity.class);
                             startActivity(myIntent);
                             finish();
                        }

                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}


