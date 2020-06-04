package com.seu.covid_19;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefrences {

    SharedPreferences sharedPreferences;
    Context context;

    public Prefrences(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LOGIN_FILE",Context.MODE_PRIVATE);

    }

    public void writeLoginStatue(boolean statue)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LOGIN_STATUE",statue);
        editor.commit();
    }

    public boolean readLoginStatue()
    {
        boolean statue;
        statue = sharedPreferences.getBoolean("LOGIN_STATUE",false);
        return statue;
    }

    public void SaveGovID(String GovID)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("GOV_ID",GovID);
        editor.commit();
    }

    public String readGovID()
    {
        String GovID;
        GovID = sharedPreferences.getString("GOV_ID",null);
        return GovID;
    }

    public void SavePhone(String Phone)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PHONE",Phone);
        editor.commit();
    }

    public String readPhone()
    {
        String Phone;
        Phone = sharedPreferences.getString("PHONE",null);
        return Phone;
    }



}
