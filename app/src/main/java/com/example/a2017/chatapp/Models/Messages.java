package com.example.a2017.chatapp.Models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 2017 on 04/02/2017.
 */
public class Messages extends RealmObject
{
    public static int TOSERVER = 1 ;
    public static int SENT = 2 ;
    public static int DELIVERED = 3 ;
    public static int READ = 4 ;

    private String message;
    private String time;
    private boolean isRead;
    private String fromPhoneNumber;
    @PrimaryKey
    private String uuid;
    private int status;

    public Messages()
    {
    }

    public Messages(String message, String time, boolean isRead, String fromPhoneNumber, String uuid, int status)
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
