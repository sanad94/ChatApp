package com.example.a2017.chatapp.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 2017 on 06/02/2017.
 */
public class MessageOverNetwork
{
    @SerializedName("fromPhoneNumber")
    private String fromPhoneNumber;
    @SerializedName("toPhoneNumber")
    private String toPhoneNumber;
    @SerializedName("time")
    private String time ;
    @SerializedName("message")
    private String message;

    public MessageOverNetwork()
    {
    }

    public MessageOverNetwork(String toPhoneNumber,String fromPhoneNumber, String message, String time)
    {
        this.toPhoneNumber = toPhoneNumber;
        this.message = message;
        this.time = time;
        this.fromPhoneNumber = fromPhoneNumber;
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
}
