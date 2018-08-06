package com.example.a2017.chatapp.Network;

/**
 * Created by 2017 on 25/02/2017.
 */

public class BaseUrl
{
/*//local;
    private static final String IP_ADDRESS = "10.0.0.14";
    public static final String BASE_URL_SERVER = "http://"+IP_ADDRESS+":9090/ChatService/";
    public static final String BASE_URL_IMAGE = "http://"+IP_ADDRESS+":9090/ChatService/getImage/";
    public static final String BASE_URL_ROOM_IMAGE = "http://"+IP_ADDRESS+":9090/ChatService/getRoomImage/";
    public static final String BASE_URL_Online_SOCKET = "ws://"+IP_ADDRESS+":9090/OnlineSocket";
    public static final String BASE_URL_TYPING_SOCKET = "ws://"+IP_ADDRESS+":9090/TypingSocket";*/

//gcp
private static final String IP_ADDRESS = "104.196.211.123";
    public static final String BASE_URL_SERVER = "http://"+IP_ADDRESS+":8080/ChatServer-1.0-SNAPSHOT/ChatService/";
    public static final String BASE_URL_IMAGE = "http://"+IP_ADDRESS+":8080/ChatServer-1.0-SNAPSHOT/ChatService/getImage/";
    public static final String BASE_URL_ROOM_IMAGE = "http://"+IP_ADDRESS+":8080/ChatServer-1.0-SNAPSHOT/ChatService/getRoomImage/";
    public static final String BASE_URL_Online_SOCKET = "ws://"+IP_ADDRESS+":8080/ChatServer-1.0-SNAPSHOT/OnlineSocket";
    public static final String BASE_URL_TYPING_SOCKET = "ws://"+IP_ADDRESS+":8080/ChatServer-1.0-SNAPSHOT/TypingSocket";

}
