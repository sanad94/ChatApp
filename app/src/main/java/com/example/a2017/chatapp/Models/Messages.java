package com.example.a2017.chatapp.Models;

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
    public Messages()
    {
    }

    public Messages(String fromPhoneNumber, boolean isRead, String time, String message)
    {
        this.fromPhoneNumber = fromPhoneNumber;
        this.isRead = isRead;
        this.time = time;
        this.message = message;
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

    @Override
    public String toString() {
        return super.toString();
    }
}
