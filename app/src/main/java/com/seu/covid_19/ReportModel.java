package com.seu.covid_19;


class ReportModel {
    public long time;
    public double latitude;
    public double longitude;

    public ReportModel() {
    }

    public ReportModel(long time, double latitude, double longitude)
    {
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}