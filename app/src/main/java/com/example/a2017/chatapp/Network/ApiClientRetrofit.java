package com.example.a2017.chatapp.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 2017 on 04/02/2017.
 */

public class ApiClientRetrofit
{
    private static Retrofit retrofit = null ;
    public static Retrofit getClient()
    {
        if(retrofit==null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BaseUrl.BASE_URL_SERVER).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit ;
    }

}