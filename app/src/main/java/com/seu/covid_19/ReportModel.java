package com.seu.covid_19;

import java.util.Date;

public class ReportModel {
    public String userID;
    public Date time;
    public double latitude;
    public double longitude;
    public boolean confirmed;

    public ReportModel(String userID, double latitude,double longitude, Date time, boolean confirmed){
        this.userID = userID;
        this.time = time;
        this.latitude=latitude;
        this.longitude=longitude;
        this.confirmed =confirmed;
    }



}