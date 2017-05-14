package com.example.a2017.chatapp.Network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * Created by 2017 on 13/05/2017.
 */

public class SingleWebSocket
{
    private static MyWebSocket listener  ;
    private static WebSocket socket ;
    private static  OkHttpClient client;
    private static Request request;
    public static int RECONECT_FLAG = 1;

    public static MyWebSocket getInstance(int flag,IhandleWebSocket handleSocket )
    {
        // using flag to reconnect to the socket
        if(listener==null ||flag == RECONECT_FLAG )
        {
            client = new OkHttpClient();
             request = new Request.Builder().url(BaseUrl.BASE_URL_WEB_SOCKET).build();
             listener = new MyWebSocket(handleSocket);
             socket = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
        }
        return listener;
    }

    public static void setIhandleWebSocket(IhandleWebSocket handleSocket)
    {
        listener.setHandleMessage(handleSocket);
    }
    public static WebSocket getSocket()
    {
        return socket;
    }
}
