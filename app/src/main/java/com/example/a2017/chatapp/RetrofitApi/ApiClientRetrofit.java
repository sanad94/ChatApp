package com.example.a2017.chatapp.RetrofitApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 2017 on 04/02/2017.
 */

public class ApiClientRetrofit
{
    private static final String BASE_URL = "http://10.0.0.8:8080/ChatService/";
    private static Retrofit retrofit = null ;
    public static Retrofit getClient()
    {
        if(retrofit==null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit ;
    }

}