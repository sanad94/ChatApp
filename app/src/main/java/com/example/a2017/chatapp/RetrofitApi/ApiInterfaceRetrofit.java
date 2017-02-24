package com.example.a2017.chatapp.RetrofitApi;

import com.example.a2017.chatapp.Models.ImageByte;
import com.example.a2017.chatapp.Models.MessageOverNetwork;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.Models.UserToken;

import java.util.LinkedList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by 2017 on 04/02/2017.
 */

public interface ApiInterfaceRetrofit
{

    @POST("UsersTokens")
    Call<UserToken> submitMyToken(@Body UserToken mytoken);

    @POST("UpdateUsersTokens")
    Call<UserToken> updateMyToken(@Body UserToken mytoken);

    @POST("PushMessage")
    Call<MessageOverNetwork> sendMessage (@Body MessageOverNetwork mymessage);

    @POST("SendContactList")
    Call<LinkedList<MyContacts>> sendContact (@Body LinkedList<MyContacts> myContact);

    @GET("DeleteUser/{phoneNumber}")
    Call<Void> deactivateAccount (@Path("phoneNumber") String myPhoneNumber);

    @POST("settImage/{phoneNumber}")
    Call<Void> settImage( @Path("phoneNumber") String phoneNumber , @Body ImageByte imageByte );


}
