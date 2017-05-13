package com.example.a2017.chatapp.Network;

import com.example.a2017.chatapp.Models.ImageByte;
import com.example.a2017.chatapp.Models.MessageOverNetwork;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.Models.UserToken;
import java.util.ArrayList;
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
    Call<ArrayList<MyContacts>> sendContact (@Body ArrayList<MyContacts> myContact);

    @GET("DeleteUser/{phoneNumber}")
    Call<Void> deactivateAccount (@Path("phoneNumber") String myPhoneNumber);

    @POST("settImage/{phoneNumber}")
    Call<Void> settImage( @Path("phoneNumber") String phoneNumber , @Body ImageByte imageByte );

    @POST("sendImageMessage/{fromPhoneNumber}/{toPhoneNumber}/{time}")
    Call<Void> sendImageMessage( @Path("fromPhoneNumber") String fromPhoneNumber ,@Path("toPhoneNumber") String toPhoneNumber ,@Path("time") String time , @Body ImageByte imageByte );


}
