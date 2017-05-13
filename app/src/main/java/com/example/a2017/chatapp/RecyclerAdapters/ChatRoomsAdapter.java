package com.example.a2017.chatapp.RecyclerAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.example.a2017.chatapp.Models.ChatRoom;
import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RecyclerTools.ChatRoomsFilter;
import com.example.a2017.chatapp.Network.BaseUrl;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.HashMap;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by 2017 on 04/02/2017.
 */

public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> implements Filterable
{
    private ArrayList<ChatRoom> chatRooms;
    private HashMap<String,ChatRoom> chatRoomMap;
    private Realm realm;
    private ChatRoomsFilter filter;

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,count,time ,lastMessage;
        SimpleDraweeView image ;
        public ViewHolder(View itemView)
        {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.roomImage);
            name = (TextView) itemView.findViewById(R.id.name);
            count= (TextView) itemView.findViewById(R.id.count);
            time= (TextView) itemView.findViewById(R.id.times);
            lastMessage= (TextView) itemView.findViewById(R.id.last_message);
        }
    }

    public ChatRoomsAdapter(ArrayList<ChatRoom> chatRooms)
     {
         this.chatRooms = chatRooms;
         this.chatRoomMap = new HashMap<>();
         realm = Realm.getDefaultInstance();

     }
    @Override
    public ChatRoomsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_list,parent,false);
        return  new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        int messageNotReaded = 0;
        MyContacts contact = null;
        Messages lastMessage = new  Messages();
        final ChatRoom chatRoom = chatRooms.get(position);
        RealmList<Messages> messages = chatRoom.getMessages();
        for (int i = 0 ; i<messages.size();i++)
        {
            Messages message = messages.get(i);
            lastMessage=message;
            if(!message.isRead())
            {
              messageNotReaded++;
            }
        }
        if(messageNotReaded>0)
        {
            holder.count.setText(String.valueOf(messageNotReaded));
            holder.count.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.count.setVisibility(View.GONE);
        }
        if(chatRoom!=null)
        {
            realm.beginTransaction();
            chatRoom.setDate(lastMessage.getTime());
            contact = realm.where(MyContacts.class).equalTo("phoneNumber",chatRoom.getPhoneNumber()).findFirst();
            realm.copyToRealmOrUpdate(chatRoom);
            realm.commitTransaction();
        }
        if(lastMessage.getMessage().contains("ImageMessage:"))
        {
            holder.lastMessage.setText(holder.image.getContext().getResources().getString(R.string.image));
        }
        else
        {
            holder.lastMessage.setText(lastMessage.getMessage().replace("TextMessage:",""));
        }
        holder.time.setText(lastMessage.getTime());
        if(contact!=null)
        {
            String contactName = contact.getName();
            String contactPhoneNumber = contact.getPhoneNumber();
            holder.name.setText(contactName);
            holder.image.setImageURI(BaseUrl.BASE_URL_IMAGE+contactPhoneNumber);
            chatRoomMap.put(contactName,chatRoom);
            return;
        }
        String roomPhoneNumber = chatRoom.getPhoneNumber();
        holder.image.setImageURI(BaseUrl.BASE_URL_IMAGE+roomPhoneNumber);
        holder.name.setText(chatRoom.getPhoneNumber());
        chatRoomMap.put(roomPhoneNumber,chatRoom);
    }

    @Override
    public int getItemCount()
    {
        return chatRooms.size();
    }

    @Override
    public Filter getFilter()
    {
        if(filter == null )
        {
            filter = new ChatRoomsFilter(this,chatRoomMap);
        }

        return filter;
    }

    public void setChatRooms(ArrayList<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public ArrayList<ChatRoom> getChatRooms()
    {
        return chatRooms;
    }
}
