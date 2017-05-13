package com.example.a2017.chatapp.Network;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * Created by 2017 on 13/05/2017.
 */

public interface IhandleWebSocket
{
     void OnMessage(WebSocket socket , String text);
     void OnOpen(WebSocket webSocket, Response response);
     void onClosing(WebSocket webSocket, int code, String reason);
}
