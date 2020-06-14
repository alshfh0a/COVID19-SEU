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
                try {
                    sleep(2000);
                    Prefrences preference = new Prefrences(getApplicationContext()) ;
                    if (!preference.readLoginStatue()) {
                        Intent myIntent = new Intent(getApplicationContext(), SignupActivity.class);
                        startActivity(myIntent);
                        finish();

                    }else {
                        Intent myIntent = new Intent(getApplicationContext(), SignupActivity.class);
                        startActivity(myIntent);
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}


