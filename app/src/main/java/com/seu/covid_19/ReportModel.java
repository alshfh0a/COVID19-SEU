package com.seu.covid_19;

import java.text.SimpleDateFormat;

public class ReportModel {

    public String userID;
    public long time;
    public double latitude;
    public double longitude;
    public boolean confirmed;

    private ReportModel(){}

    public ReportModel(String userID,long time,double latitude,double longitude,boolean confirmed){
        this.time=time;
        this.confirmed=confirmed;
        this.longitude=longitude;
        this.latitude=latitude;
        this.userID=userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}