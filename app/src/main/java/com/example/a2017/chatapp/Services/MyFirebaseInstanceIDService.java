package com.example.a2017.chatapp.Services;

import android.util.Log;

import com.example.a2017.chatapp.Models.UserToken;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RetrofitApi.ApiClientRetrofit;
import com.example.a2017.chatapp.RetrofitApi.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.Utils.Preferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 04/02/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    private String token;
    private String myPhoneNumber;
    private boolean isLogin;

    @Override
    public void onTokenRefresh()
    {
        token = FirebaseInstanceId.getInstance().getToken();
        Preferences.setMyToken(token,getBaseContext());
        isLogin = Preferences.isLogin(getBaseContext());
        if(isLogin)
        {
            myPhoneNumber = Preferences.getMyPhoneNumber(getBaseContext());
            sendTokenToServer(token,myPhoneNumber);

        }
    }

    private void sendTokenToServer(String token,final String myPhoneNumber)
    {
        UserToken mytoken = new UserToken(myPhoneNumber,token);
        ApiInterfaceRetrofit apiClientRetrofit= ApiClientRetrofit.getClient().create(ApiInterfaceRetrofit.class);
        Call<UserToken> sendToken = apiClientRetrofit.updateMyToken(mytoken);
        sendToken.enqueue(new Callback<UserToken>()
        {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response)
            {

            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t)
            {

            }
        });
    }

}
