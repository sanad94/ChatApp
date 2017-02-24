package com.example.a2017.chatapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 2017 on 04/02/2017.
 */

public class Preferences
{
    private static final String SHARED_PREFERENCES_FILE_NAME = "chatAppPreferences";
    private static final String MY_IMAGE_URI = "myImageURi";
    private static final String LOGIN = "login";
    private static final String MY_PHONE_NUMBER = "myPhoneNumber";
    private static final String MY_FULL_NAMEE = "myFullName";
    private static final String FIRST_RUN = "firstRun";
    private static final String TOKEN ="token";
    private static final String CHAT_ROOM = "chatRoom";
    private static final String BACKGROUND = "background";

    public static boolean isLogin(Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(LOGIN,false);
    }

    public static void setLogin(boolean isLogin,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(LOGIN,isLogin).apply();
    }

    public static boolean isFirstRun(Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(FIRST_RUN,false);
    }

    public static void setFirstRun(boolean isLogin,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(FIRST_RUN,isLogin).apply();
    }

    public static String getMyImageUri(Context context )
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getString(MY_IMAGE_URI,null);
    }

    public static void setMyImageUri(String imageURi ,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putString(MY_IMAGE_URI,imageURi).apply();
    }

    public static String getMyPhoneNumber(Context context )
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getString(MY_PHONE_NUMBER,null);
    }

    public static void setMyPhoneNumber(String phoneNumber ,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putString(MY_PHONE_NUMBER,phoneNumber).apply();
    }

    public static String getMyFullName(Context context )
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getString(MY_FULL_NAMEE,null);
    }

    public static void setMyFullName(String phoneNumber ,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putString(MY_FULL_NAMEE,phoneNumber).apply();
    }

    public static String getMyToken(Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getString(TOKEN,null);
    }

    public static void setMyToken(String token,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putString(TOKEN,token).apply();
    }

    public static boolean isInChatRoom(Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(CHAT_ROOM,false);
    }

    public static void setIsInChatRoom(boolean isInChatRoom,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(CHAT_ROOM,isInChatRoom).apply();
    }

    public static boolean isInbackground(Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(BACKGROUND,false);
    }

    public static void setisInbackground(boolean isInbackground,Context context)
    {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(BACKGROUND,isInbackground).apply();
    }

    private static SharedPreferences getPreferences(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME,context.MODE_PRIVATE);
        return preferences;
    }
}
