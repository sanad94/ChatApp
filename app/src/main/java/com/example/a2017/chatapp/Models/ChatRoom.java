package com.example.a2017.chatapp.Models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by 2017 on 04/02/2017.
 */
public class ChatRoom extends RealmObject
{
    @PrimaryKey
    private String phoneNumber;
    private RealmList<Messages> messages;
    private String date;

    public ChatRoom()
    {
    }

    public ChatRoom(RealmList<Messages> messages,String phoneNumber,String date)
    {
        this.messages = messages;
        this.phoneNumber = phoneNumber;
        this.date = date;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public RealmList<Messages> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<Messages> messages)
    {
        this.messages = messages;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
