package com.example.a2017.chatapp.Network;

import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by 2017 on 13/05/2017.
 */

public class MyWebSocket extends okhttp3.WebSocketListener
{
    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response)
    {
        // super.onOpen(webSocket, response);

//        webSocket.send("Hello, it's sanad !");
//        webSocket.send("What's up ?");
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
       // super.onClosing(webSocket, code, reason);
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {

        t.printStackTrace();
    }

    private static final int NORMAL_CLOSURE_STATUS = 1000;


    }

