package com.example.smartalert;

public class Alert {
    public String comment, time, address, dangerType;
    public Alert(){

    }
    public Alert(String comment, String time, String address, String dangerType){
        this.comment = comment;
        this.time = time;
        this.address = address;
        this.dangerType = dangerType;
    }
}
