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

    public static MyWebSocket getInstance(IhandleWebSocket handleSocket)
    {
        if(listener==null)
        {
            client = new OkHttpClient();
             request = new Request.Builder().url(BaseUrl.BASE_URL_WEB_SOCKET).build();
             listener = new MyWebSocket(handleSocket);
             socket = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
        }
        return listener;
    }

    public static WebSocket getSocket()
    {
        return socket;
    }
}
