package com.example.a2017.chatapp.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import com.example.a2017.chatapp.Activitys.MainActivity;
import com.example.a2017.chatapp.Fragments.ChatRoomsFragment;
import com.example.a2017.chatapp.Fragments.MessagesFragment;
import com.example.a2017.chatapp.Models.ChatRoom;
import com.example.a2017.chatapp.Models.MessageOverNetwork;
import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.Network.ApiClientRetrofit;
import com.example.a2017.chatapp.Network.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RecyclerAdapters.ChatRoomsAdapter;
import com.example.a2017.chatapp.RecyclerAdapters.MessagesAdapter;
import com.example.a2017.chatapp.Utils.Preferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 04/02/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private Realm realm;
    private ChatRoom room;
    private Handler handler;
    private String uuid;
    private int status;
    private String phoneNumber,message,time;
    private boolean isInChatRoom;
    private boolean isInbackground;
    private boolean isLogin;
    private boolean isFirstRun;
    String myPhoneNumber ;
    public static final String ACTION ="com.example.a2017.chatapp.services.MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
         myPhoneNumber = Preferences.getMyPhoneNumber(getBaseContext());
         isLogin = Preferences.isLogin(getBaseContext());
        isLogin = Preferences.isFirstRun(getBaseContext());
        try
        {
            realm = Realm.getDefaultInstance();
            phoneNumber = remoteMessage.getData().get("fromPhoneNumber");
            message = remoteMessage.getData().get("message");
            time = remoteMessage.getData().get("time");
            uuid = remoteMessage.getData().get("uuid");
            status = Integer.valueOf(remoteMessage.getData().get("status"));
            isInChatRoom = Preferences.isInChatRoom(getBaseContext());
            isInbackground = Preferences.isInbackground(getBaseContext());
            Messages obj = realm.where(Messages.class).equalTo("uuid", uuid).findFirst();
            if(obj==null)
            {
                addToRealm();
                final MessageOverNetwork messageToSend = new MessageOverNetwork(myPhoneNumber,phoneNumber,time,message,uuid,MessageOverNetwork.DELIVERED);
                sendMessageToserver(messageToSend);
            }
            else if(status > obj.getStatus())
            {
                updateMessage(obj,status,message);
            }

            if(!isInbackground)
            {
                if(!isInChatRoom)
                {
                    updateRoomListUi();
                    sedSoundNotification();
                }
                else
                {
                    if(MessagesFragment.fromPhoneNumber.equals(phoneNumber))
                    {
                        updateRoomMessageUi(status,uuid);
                    }
                    else
                    {
                        sedSoundNotification();
                    }
                }

            }
            else if(isLogin && !isFirstRun)
            {
                sendFullNotification(remoteMessage);
            }
        }
        catch (Exception e)
        {
            if (isInbackground)
                sendFullNotification(remoteMessage);
        }



    }

    private void sedSoundNotification()
    {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSound(notificationSound);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification.build());
    }
    private void sendFullNotification(RemoteMessage remoteMessage)
    {
        String phoneNumber = remoteMessage.getData().get("fromPhoneNumber");
        String message = remoteMessage.getData().get("message");
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(phoneNumber)
                .setSound(notificationSound)
                .setContentText(message);
        Intent intentNotificatio = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent =PendingIntent.getActivity(this,0,intentNotificatio,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification.build());
    }

    private void addToRealm()
    {

        realm.executeTransaction(new Realm.Transaction()
        {
            Messages messageObject = new Messages(message,time,false,phoneNumber,uuid,Messages.DELIVERED);
            RealmList<Messages> messagesList;

            @Override
            public void execute(Realm realm)
            {
                if (realm.where(ChatRoom.class).equalTo("phoneNumber", phoneNumber).count() == 0)
                {
                    messagesList =new RealmList<>();
                    messagesList.add(messageObject);
                    room = new ChatRoom(messagesList,phoneNumber,time);
                }
                else
                {
                    RealmQuery<ChatRoom> chatRoomRealmQuery = realm.where(ChatRoom.class);
                    room = chatRoomRealmQuery.equalTo("phoneNumber",phoneNumber).findFirst();
                    messagesList=room.getMessages();
                    messagesList.add(messageObject);
                }
                realm.copyToRealmOrUpdate(room);
            }
        });
    }

    private void updateRoomListUi()
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction()
                {
                    @Override
                    public void execute(Realm realm)
                    {
                        ChatRoomsAdapter chatRoomsAdapter = ChatRoomsFragment.chatRoomsAdapter;
                        ArrayList<ChatRoom> chatRoomArrayList = chatRoomsAdapter.getChatRooms();
                        RealmQuery<ChatRoom> chatRoomRealmQuery = realm.where(ChatRoom.class);
                        ChatRoom room= chatRoomRealmQuery.equalTo("phoneNumber",phoneNumber).findFirst();
                        int itemIndex = chatRoomArrayList.indexOf(room);
                        if(itemIndex>-1)
                        {
                            chatRoomArrayList.remove(itemIndex);
                        }
                        chatRoomArrayList.add(0,room);
                        chatRoomsAdapter.setChatRooms(chatRoomArrayList);
                        chatRoomsAdapter.notifyItemRemoved(itemIndex);
                        chatRoomsAdapter.notifyItemInserted(0);
                    }
                });
            }
        };
        handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }

    private void updateRoomMessageUi(final int status,final String uuid)
    {
        boolean isInserted = false;

        if(status == Messages.TOSERVER)
        {
            isInserted = true;
        }
        Intent intentValue = new Intent(ACTION);
        intentValue.putExtra("isInserted",isInserted);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentValue);
    }

    private void updateMessage(final Messages message, final int status ,final String msg)
    {
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                final Messages messageToSave = new Messages(msg,message.getTime(),message.isRead(),message.getFromPhoneNumber(),message.getUuid(),status);
                realm.copyToRealmOrUpdate(messageToSave);
            }
        });
    }

    private void sendMessageToserver(final MessageOverNetwork messageToSend)
    {
        ApiInterfaceRetrofit apiClientRetrofit = ApiClientRetrofit.getClient().create(ApiInterfaceRetrofit.class);
        Call<MessageOverNetwork> sendMessage = apiClientRetrofit.sendMessage(messageToSend);
        sendMessage.enqueue(new Callback<MessageOverNetwork>()
        {
            @Override
            public void onResponse(Call<MessageOverNetwork> call, Response<MessageOverNetwork> response)
            {

            }

            @Override
            public void onFailure(Call<MessageOverNetwork> call, Throwable t)
            {

            }
        });
    }
}

