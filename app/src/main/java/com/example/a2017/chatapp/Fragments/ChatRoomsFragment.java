package com.example.a2017.chatapp.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.a2017.chatapp.Models.ChatRoom;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RecyclerAdapters.ChatRoomsAdapter;
import com.example.a2017.chatapp.RecyclerTools.ChatRoomsDividerItemDecoration;
import com.example.a2017.chatapp.RecyclerTools.ChatRoomsRecyclerTouchListner;
import com.example.a2017.chatapp.RecyclerTools.IclickListner;
import com.example.a2017.chatapp.Utils.Preferences;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by 2017 on 04/02/2017.
 */

public class ChatRoomsFragment extends Fragment
{
    private final static String FROM_PHONE_NUMBER = "from_phone_Number";
    private FloatingActionButton fab ;
    private RecyclerView recyclerView_chat_list ;
    public static ChatRoomsAdapter chatRoomsAdapter;
    private Realm realm;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private ArrayList<ChatRoom> chatRoomList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Preferences.setIsInChatRoom(false,getContext());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.chat_rooms_main,container,false);
        recyclerView_chat_list = (RecyclerView) view.findViewById(R.id.recycler_chat_list);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        realm = Realm.getDefaultInstance();
        manager = getFragmentManager();
        fabOnclick();
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.activity_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {

                chatRoomsAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                chatRoomsAdapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    private void configureRecyclerView()
    {
        final ArrayList<ChatRoom> defaultchatRoomList = new ArrayList<>();
        chatRoomsAdapter =  new ChatRoomsAdapter(defaultchatRoomList);
        recyclerView_chat_list.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_chat_list.setItemAnimator(new DefaultItemAnimator());
        recyclerView_chat_list.addItemDecoration(new ChatRoomsDividerItemDecoration(getContext()));
        recyclerView_chat_list.addOnItemTouchListener(new ChatRoomsRecyclerTouchListner(getContext(), recyclerView_chat_list, new IclickListner()
        {
            @Override
            public  void onClick(View view, final int position)
            {
                if(view.getId() == R.id.roomImage)
                {
                    goToimageViewerFragment(position);
                    return;
                }
                goToMessageFragment(position);
            }

            @Override
            public void onLongClick(View view, int position)
            {
            }
        }));
        recyclerView_chat_list.setAdapter(chatRoomsAdapter);
    }

    private void fabOnclick()
    {
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createAlertDialog();
            }
        });
    }

    private void createAlertDialog()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        input.setHint("phone number");
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String number = input.getText().toString();
                if(!number.matches("") && number.length() == 10)
                {
                    number = number.substring(0, 3) + "-" + number.substring(3, 6) + "-" + number.substring(6, number.length());
                    goToMessageFragment(number);
                }

            }
        });
        alertDialog.show();
    }

    private void getFromRealm()
    {
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                RealmQuery<ChatRoom> chatRoomRealmQuery = realm.where(ChatRoom.class);
                RealmResults<ChatRoom> chatRoomRealmResults = chatRoomRealmQuery.findAll().sort("date",Sort.DESCENDING);
                chatRoomList = new ArrayList<>(chatRoomRealmResults);
                notifyDataSetChanged(chatRoomList);
            }
        });
    }

    private void notifyDataSetChanged(ArrayList<ChatRoom> chatRoomList)
    {
        chatRoomsAdapter.setChatRooms(chatRoomList);
        chatRoomsAdapter.notifyDataSetChanged();
    }

    private Bundle setArgument(int position)
    {
        ChatRoom room = chatRoomList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(FROM_PHONE_NUMBER,room.getPhoneNumber());
        return bundle;
    }

    private Bundle setArgument(String phoneNumber)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FROM_PHONE_NUMBER,phoneNumber);
        return bundle;
    }

    private void goToMessageFragment(int position)
    {
        transaction = manager.beginTransaction();
        MessagesFragment messagesFragment = new MessagesFragment();
        messagesFragment.setArguments(setArgument(position));
        transaction.replace(R.id.fragment_container, messagesFragment,"MESSAGES_FRAGMENT");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goToimageViewerFragment(int position)
    {
        transaction = manager.beginTransaction();
        ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
        imageViewerFragment.setArguments(setArgument(position));
        transaction.replace(R.id.fragment_container, imageViewerFragment,"IMAGE_VIEWER_FRAGMENT");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goToMessageFragment(String phoneNumber)
    {
        transaction = manager.beginTransaction();
        MessagesFragment messagesFragment = new MessagesFragment();
        messagesFragment.setArguments(setArgument(phoneNumber));
        transaction.replace(R.id.fragment_container, messagesFragment,"MESSAGES_FRAGMENT");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
