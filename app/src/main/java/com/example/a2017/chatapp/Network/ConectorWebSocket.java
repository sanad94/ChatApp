package com.example.a2017.chatapp.Network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

/**
 * Created by 2017 on 13/05/2017.
 */

public class ConectorWebSocket
{
    private  MyWebSocket listener  ;
    private  WebSocket socket ;
    private   OkHttpClient client;
    private  Request request;
    private String url ;
    private  IhandleWebSocket previoushandlerSocket;

    public ConectorWebSocket(String url)
    {
        this.url = url ;
    }

    public void init(IhandleWebSocket handleSocket )
    {
            previoushandlerSocket = handleSocket;
            client = new OkHttpClient();
             request = new Request.Builder().url(url).build();
             listener = new MyWebSocket(handleSocket);
             socket = client.newWebSocket(request, listener);
            client.dispatcher().executorService().shutdown();
    }

    public  void setIhandleWebSocket(IhandleWebSocket handleSocket)
    {
        listener.setHandleMessage(handleSocket);
    }

    public  WebSocket getSocket()
    {
        return socket;
    }

    public void setPreviousIhandleWebSocket()
    {
        listener.setHandleMessage(previoushandlerSocket);
    }
}
