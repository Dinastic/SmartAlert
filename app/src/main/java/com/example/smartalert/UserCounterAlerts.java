package com.example.smartalert;

public class UserCounterAlerts {
    public String address;
    public int count;
    public String time;
    public String dangerType;
    public UserCounterAlerts(){

    }
    public UserCounterAlerts(String address, int count, String time, String dangerType){
        this.address = address;
        this.count = count;
        this.time = time;
        this.dangerType = dangerType;
    }
}
