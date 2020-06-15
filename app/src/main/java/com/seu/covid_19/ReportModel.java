package com.seu.covid_19;


class ReportModel {
    public String userID;
    public long time;
    public double latitude;
    public double longitude;
    public boolean confirmed;

    public ReportModel() {
    }

    public ReportModel(String userID, long time, double latitude, double longitude, boolean confirmed) {
        this.time = time;
        this.confirmed = confirmed;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userID = userID;
    }

}