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
import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RecyclerAdapters.ChatRoomsAdapter;
import com.example.a2017.chatapp.RecyclerAdapters.MessagesAdapter;
import com.example.a2017.chatapp.Utils.Preferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import android.os.Handler;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

/**
 * Created by 2017 on 04/02/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private Realm realm;
    private ChatRoom room;
    private Runnable runnable;
    private Handler handler;
    private String phoneNumber,message,time;
    private boolean isInChatRoom;
    private boolean isInbackground;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        try
        {
            realm = Realm.getDefaultInstance();
            phoneNumber = remoteMessage.getData().get("fromPhoneNumber");
            message = remoteMessage.getData().get("message");
            time = remoteMessage.getData().get("time");
            isInChatRoom = Preferences.isInChatRoom(getBaseContext());
            isInbackground = Preferences.isInbackground(getBaseContext());
            addToRealm();
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
                        updateRoomMessageUi();
                    }
                    else
                    {
                        sedSoundNotification();
                    }
                }

            }
            else
            {
                sendFullNotification(remoteMessage);
            }
        }
        catch (Exception e)
        {
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
            Messages messageObject = new Messages(phoneNumber,false,time,message);
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
        runnable = new Runnable()
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

    private void updateRoomMessageUi()
    {
        runnable = new Runnable()
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
                        RealmQuery<ChatRoom> chatRoomRealmQuery = realm.where(ChatRoom.class);
                        ChatRoom room= chatRoomRealmQuery.equalTo("phoneNumber",phoneNumber).findFirst();
                        ArrayList<Messages> messagesArrayList =new ArrayList<>(room.getMessages());
                        MessagesAdapter messagesAdapter =MessagesFragment.messagesAdapter;
                        messagesAdapter.setMessages(messagesArrayList);
                        messagesAdapter.notifyItemInserted(messagesArrayList.size()-1);
                        MessagesFragment.recyclerView_message_list.scrollToPosition(messagesArrayList.size()-1);
                    }
                });
            }
        };
        handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}

