package com.example.a2017.chatapp.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.a2017.chatapp.Models.ChatRoom;
import com.example.a2017.chatapp.Models.MessageOverNetwork;
import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.Models.OnlineModel;
import com.example.a2017.chatapp.Network.IhandleWebSocket;
import com.example.a2017.chatapp.Network.SingleWebSocket;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RecyclerAdapters.MessagesAdapter;
import com.example.a2017.chatapp.Network.ApiClientRetrofit;
import com.example.a2017.chatapp.Network.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.Services.ImageService;
import com.example.a2017.chatapp.Utils.Preferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 05/02/2017.
 */

public class MessagesFragment extends Fragment
{
    private final static String FROM_PHONE_NUMBER = "from_phone_Number";
    private static int IMAGE_REQUEST_CODE = 101;
    public static RecyclerView recyclerView_message_list ;
    public static MessagesAdapter messagesAdapter;
    private ArrayList<Messages> messages;
    private ChatRoom room;
    public static String fromPhoneNumber;
    private String myPhoneNumber;
    private Realm realm ;
    private EditText messageEditText;
    private Button send;
    private Button sendImage;
    private int fragment_container_padding_bottom;
    private View bottomNavigationView;
    private View fragment_container;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        realm= Realm.getDefaultInstance();
        getArgument();
        Preferences.setIsInChatRoom(true,getContext());
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
       View view = inflater.inflate(R.layout.chat_room_message_main,container,false);
        recyclerView_message_list= (RecyclerView) view.findViewById(R.id.recycler_messages);
        messageEditText = (EditText) view.findViewById(R.id.message);
        send = (Button) view.findViewById(R.id.btn_send);
        sendImage = (Button) view.findViewById(R.id.btn_img);
        setToolbarTitleToContact();
        disableBottomNavigationView();
        sendButtonOnClick();
        sendImageButtonOnclick();
        configureRecyclerView();
        setAdjustResize();
        getContactOnline();
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getFromRealm();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Preferences.setIsInChatRoom(false,getContext());
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        enableBottomNavigationView();
        setToolbarTitleToAppName();
        setAdjustPan();
    }

    private void getContactOnline()
    {
        final Gson gson = new Gson();
        OnlineModel onlineModel = new OnlineModel();
        onlineModel.setService("IsConnected");
        onlineModel.setFromPhoneNumber(myPhoneNumber);
        onlineModel.setToPhoneNumber(fromPhoneNumber);
        SingleWebSocket.getSocket().send(gson.toJson(onlineModel));
        SingleWebSocket.setIhandleWebSocket(new IhandleWebSocket()
        {
            @Override
            public void OnMessage(WebSocket socket, String text)
            {
                OnlineModel online = gson.fromJson(text,OnlineModel.class);
                if(online==null)
                {
                    return;
                }
                String service = online.getService();
                if(service.equals("IsConnected"))
                {
                    if(online.getStatus().equals("online"))
                    {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getContext().getString(R.string.online));
                        Log.d("getcontact",text);
                    }
                    else
                    {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getContext().getString(R.string.last_seen)+online.getStatus());
                    }
                }
                else if(service.equals("OffLine"))
                {
                    if(online.getFromPhoneNumber().equals(fromPhoneNumber))
                    {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getContext().getString(R.string.last_seen)+text.replace("DisConnect:",""));
                    }
                }
            }

            @Override
            public void OnOpen(WebSocket webSocket, okhttp3.Response response)
            {

            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason)
            {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==IMAGE_REQUEST_CODE && data!=null)
        {
            Uri imageUri = data.getData();
            Intent imageServiceIntent = new Intent(getContext(),ImageService.class);
            imageServiceIntent.putExtra("check",false);
            imageServiceIntent.putExtra("imageUri",imageUri.toString());
            imageServiceIntent.putExtra("myPhoneNumber",myPhoneNumber);
            imageServiceIntent.putExtra("toPhoneNumber",fromPhoneNumber);
            getActivity().startService(imageServiceIntent);
/*            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
            String time = dateformat.format(c.getTime());*/
            String time = "";
            String tempMessage ="ImageMessage:";
            tempMessage =  tempMessage + imageUri.toString();
            final Messages messageToSave = new Messages(myPhoneNumber,true,time,tempMessage);
            final MessageOverNetwork messageToSend = new MessageOverNetwork(fromPhoneNumber,myPhoneNumber,tempMessage,time);
            messages.add(messageToSave);
            messagesAdapter.setMessages(messages);
            messagesAdapter.notifyDataSetChanged();
            saveToRelm(messageToSave);
            recyclerView_message_list.scrollToPosition(messages.size()-1);
        }
    }

    private void configureRecyclerView()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messages,myPhoneNumber,fromPhoneNumber);
        recyclerView_message_list.setLayoutManager(layoutManager);
        recyclerView_message_list.setItemAnimator(new DefaultItemAnimator());
        recyclerView_message_list.setAdapter(messagesAdapter);
    }

    private void sendButtonOnClick()
    {
        send.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v)
            {
                String tempMessage ="";
                tempMessage =  messageEditText.getText().toString();
                if(!tempMessage.matches(""))
                {
                    //"06-feb-2018 06:74 pm"
//                    Calendar c = Calendar.getInstance();
//                    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
//                    String time = dateformat.format(c.getTime());
                    String time ="";
                    tempMessage ="TextMessage:" + tempMessage;
                    final Messages messageToSave = new Messages(myPhoneNumber,true,time,tempMessage);
                    final MessageOverNetwork messageToSend = new MessageOverNetwork(fromPhoneNumber,myPhoneNumber,tempMessage,time);
                    messages.add(messageToSave);
                    messagesAdapter.setMessages(messages);
                    messagesAdapter.notifyDataSetChanged();
                    messageEditText.setText("");
                    saveToRelm(messageToSave);
                    sendMessageToserver(messageToSend);
                    recyclerView_message_list.scrollToPosition(messages.size()-1);
                }
            }
        });
    }

    private void sendImageButtonOnclick()
    {
        sendImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    // intent.setType("image/jpg");
                    intent.setType("*/*");
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
            }
        });
    }
    private void getArgument()
    {
         fromPhoneNumber = getArguments().getString(FROM_PHONE_NUMBER);
         myPhoneNumber = Preferences.getMyPhoneNumber(getContext());
    }

    private void getFromRealm()
    {
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                RealmQuery<ChatRoom> chatRoomRealmQuery = realm.where(ChatRoom.class);
                room = chatRoomRealmQuery.equalTo("phoneNumber",fromPhoneNumber).findFirst();
                if(room==null)
                {
                    return;
                }
                messages=new ArrayList<>(room.getMessages());
                notifyDataSetChanged(messages);
            }
        });
    }

    private void saveToRelm(final Messages messageToSave)
    {
        if(room==null)
        {
            addChatRoomToRealm();
            return;
        }
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                room.getMessages().add(messageToSave);
                room.setDate(messageToSave.getTime());
                realm.copyToRealmOrUpdate(room);
            }
        });

    }
    private void notifyDataSetChanged(ArrayList<Messages> messages)
    {
        messagesAdapter.setMessages(messages);
        messagesAdapter.notifyDataSetChanged();
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

    private void disableBottomNavigationView()
    {
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        fragment_container = getActivity().findViewById(R.id.fragment_container);
        fragment_container_padding_bottom = fragment_container.getPaddingBottom();
        fragment_container.setPadding(0,0,0,0);
    }

    private void enableBottomNavigationView()
    {
        bottomNavigationView.setVisibility(View.VISIBLE);
        fragment_container.setPadding(0,0,0,fragment_container_padding_bottom);
    }

    private void addChatRoomToRealm()
    {
        RealmList<Messages> messageList = new RealmList<>();
        messageList.addAll(messages);
        room =new ChatRoom(messageList,fromPhoneNumber,messageList.get(messageList.size()-1).getTime());
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                realm.copyToRealmOrUpdate(room);
            }
        });
    }

    private void setToolbarTitleToContact()
    {
        realm.executeTransactionAsync(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                MyContacts contact=realm.where(MyContacts.class).equalTo("phoneNumber",fromPhoneNumber).findFirst();
                if(contact!=null)
                {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(contact.getName());
                    return;
                }
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(fromPhoneNumber);
            }
        });
    }

    private void setToolbarTitleToAppName()
    {
        String appName = getResources().getString(R.string.app_name);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(appName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
    }

    private void setAdjustPan()
    {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void setAdjustResize()
    {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

}
