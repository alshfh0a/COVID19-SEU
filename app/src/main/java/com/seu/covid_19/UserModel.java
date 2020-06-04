package com.seu.covid_19;

public class UserModel {
    public String UserGvID;
    public String UserPhone;
    public boolean Risk;


    public UserModel(String UserGvID, String UserPhone, boolean Risk){
        this.UserGvID = UserGvID;
        this.UserPhone = UserPhone;
        this.Risk = Risk;
    }

}
