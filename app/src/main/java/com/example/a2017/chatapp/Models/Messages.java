package com.example.a2017.chatapp.Models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by 2017 on 04/02/2017.
 */
public class Messages extends RealmObject
{
    private String message;
    private String time;
    private boolean isRead;
    private String fromPhoneNumber;
    private String uuid;
    private String status;

    public Messages()
    {
    }

    public Messages(String message, String time, boolean isRead, String fromPhoneNumber, String uuid, String status)
    {
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.fromPhoneNumber = fromPhoneNumber;
        this.uuid = uuid;
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getFromPhoneNumber()
    {
        return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
