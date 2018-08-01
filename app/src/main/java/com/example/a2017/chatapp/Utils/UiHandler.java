package com.example.a2017.chatapp.Utils;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

public class UiHandler
{
    final static Handler handler = new Handler(Looper.getMainLooper());

    public static void runOnUIThread(Runnable run)
    {
        handler.post(run);
    }
}
