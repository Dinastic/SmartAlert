package com.example.smartalert;

public class UserCounterAlerts {

    public int count;
    public String time;
    public String dangerType;
    public String city;
    public UserCounterAlerts(){

    }
    public UserCounterAlerts( int count, String time, String dangerType, String city){

        this.count = count;
        this.time = time;
        this.dangerType = dangerType;
        this.city = city;
    }
}
