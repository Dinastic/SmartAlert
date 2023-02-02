package com.example.smartalert;

public class Alert {
    public String comment, time, addresss, dangerType;

    public Alert(){

    }
    public Alert(String comment, String time, String addresss, String dangerType){
        this.comment = comment;
        this.time = time;
        this.addresss = addresss;
        this.dangerType = dangerType;
    }
}
