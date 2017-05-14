package com.example.a2017.chatapp.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 2017 on 14/05/2017.
 */
public class ImageMessageOverNetwork
{
    @SerializedName("fromPhoneNumber")
    private String fromPhoneNumber;
    @SerializedName("toPhoneNumber")
    private String toPhoneNumber;
    @SerializedName("time")
    private String time ;
    @SerializedName("imageByte")
    private byte [] imageByte;

    public ImageMessageOverNetwork(String fromPhoneNumber, String toPhoneNumber, String time, byte[] imageByte) {
        this.fromPhoneNumber = fromPhoneNumber;
        this.toPhoneNumber = toPhoneNumber;
        this.time = time;
        this.imageByte = imageByte;
    }

    public ImageMessageOverNetwork() {
    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public String getToPhoneNumber() {
        return toPhoneNumber;
    }

    public void setToPhoneNumber(String toPhoneNumber) {
        this.toPhoneNumber = toPhoneNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(byte[] imageByte) {
        this.imageByte = imageByte;
    }
}
