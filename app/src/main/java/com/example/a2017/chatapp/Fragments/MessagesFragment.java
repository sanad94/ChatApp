package com.example.a2017.chatapp.Fragments;

import android.annotation.TargetApi;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.a2017.chatapp.Models.ChatRoom;
import com.example.a2017.chatapp.Models.MessageOverNetwork;
import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RecyclerAdapters.MessagesAdapter;
import com.example.a2017.chatapp.RetrofitApi.ApiClientRetrofit;
import com.example.a2017.chatapp.RetrofitApi.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.Utils.Preferences;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 05/02/2017.
 */

public class MessagesFragment extends Fragment
{
    private final static String FROM_PHONE_NUMBER = "from_phone_Number";
    public static RecyclerView recyclerView_message_list ;
    public static MessagesAdapter messagesAdapter;
    private ArrayList<Messages> messages;
    private ChatRoom room;
    public static String fromPhoneNumber;
    private String myPhoneNumber;
    private Realm realm ;
    private EditText messageEditText;
    private Button send;
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
        setToolbarTitleToContact();
        disableBottomNavigationView();
        sendButtonOnClick();
        configureRecyclerView();
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
    }

    private void configureRecyclerView()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messages,myPhoneNumber);
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
                String tempMessage = messageEditText.getText().toString();
                if(!tempMessage.matches(""))
                {
                    //"06-feb-2018 06:74 pm"
                   /* Calendar c = Calendar.getInstance();
                    SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
                    String time = dateformat.format(c.getTime());*/
                    String time ="";
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
    }

}
