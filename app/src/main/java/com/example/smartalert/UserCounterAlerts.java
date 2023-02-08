package com.example.smartalert;

public class UserCounterAlerts {

    public int count;
    public String time;
    public String dangerType;
    public UserCounterAlerts(){

    }
    public UserCounterAlerts( int count, String time, String dangerType){

        this.count = count;
        this.time = time;
        this.dangerType = dangerType;
    }
}
