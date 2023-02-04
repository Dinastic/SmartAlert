package com.example.smartalert;

public class UserCounterAlerts {
    public String city;
    public int count;
    public String time;
    public String dangerType;
    public UserCounterAlerts(){

    }
    public UserCounterAlerts(String city, int count, String time, String dangerType){
        this.city = city;
        this.count = count;
        this.time = time;
        this.dangerType = dangerType;
    }
}
