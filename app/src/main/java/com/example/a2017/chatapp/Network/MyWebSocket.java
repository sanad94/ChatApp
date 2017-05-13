package com.example.a2017.chatapp.Network;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by 2017 on 13/05/2017.
 */

public class MyWebSocket extends WebSocketListener
{
    public static final int NORMAL_CLOSURE_STATUS = 1000;
    private IhandleWebSocket handleMessage;

    public MyWebSocket(IhandleWebSocket handleMessage)
    {
        this.handleMessage = handleMessage;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response)
    {
        handleMessage.OnOpen(webSocket,response);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text)
    {
        handleMessage.OnMessage(webSocket,text);
        Log.d( "onMessage: ",text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes)
    {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason)
    {
        handleMessage.onClosing(webSocket,code,reason);
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response)
    {

        t.printStackTrace();
    }


    }

