package com.example.a2017.chatapp.Network;

import java.util.HashMap;

public class SocketsFactory
{
    //using flyweight pattern



    private static HashMap<String,ConectorWebSocket> socketMap = new HashMap<>();

    public static ConectorWebSocket getSocket(String name)
    {
        ConectorWebSocket socket = null;
        if(socketMap.containsKey(name))
        {
            return socketMap.get(name);
        }

        SocketEnum choise = SocketEnum.valueOf(name);
        switch (choise)
        {
            case TYPING:
            {
                socket = new ConectorWebSocket(BaseUrl.BASE_URL_TYPING_SOCKET);
                socketMap.put(name,socket);
                break;
            }

            case ONLINE:
            {
                socket = new ConectorWebSocket(BaseUrl.BASE_URL_Online_SOCKET);
                socketMap.put(name,socket);
                break;
            }

        }

        return  socket ;
    }
}
