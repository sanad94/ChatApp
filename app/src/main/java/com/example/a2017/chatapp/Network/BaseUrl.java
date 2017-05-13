package com.example.a2017.chatapp.Network;

/**
 * Created by 2017 on 25/02/2017.
 */

public class BaseUrl
{
    private static final String IP_ADDRESS = "10.0.0.9";
    public static final String BASE_URL_SERVER = "http://"+IP_ADDRESS+":8080/ChatService/";
    public static final String BASE_URL_IMAGE = "http://"+IP_ADDRESS+":8080/ChatService/getImage/";
    public static final String BASE_URL_ROOM_IMAGE = "http://"+IP_ADDRESS+":8080/ChatService/getRoomImage/";
    public static final String BASE_URL_WEB_SOCKET = "ws://"+IP_ADDRESS+":8080/echo";

}