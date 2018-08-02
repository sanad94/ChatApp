package com.example.a2017.chatapp.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 2017 on 06/02/2017.
 */
public class MessageOverNetwork
{
    public static int TOSERVER = 1 ;
    public static int SENT = 2 ;
    public static int DELIVERED = 3 ;
    public static int READ = 4 ;

    @SerializedName("fromPhoneNumber")
    private String fromPhoneNumber;
    @SerializedName("toPhoneNumber")
    private String toPhoneNumber;
    @SerializedName("time")
    private String time ;
    @SerializedName("message")
    private String message;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("status")
    private int status;

    public MessageOverNetwork()
    {
    }

    public MessageOverNetwork(String fromPhoneNumber, String toPhoneNumber, String time, String message, String uuid,int status) {
        this.fromPhoneNumber = fromPhoneNumber;
        this.toPhoneNumber = toPhoneNumber;
        this.time = time;
        this.message = message;
        this.uuid = uuid;
        this.status = status;
    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber)
    {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToPhoneNumber()
    {
        return toPhoneNumber;
    }

    public void setToPhoneNumber(String toPhoneNumber)
    {
        this.toPhoneNumber = toPhoneNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
