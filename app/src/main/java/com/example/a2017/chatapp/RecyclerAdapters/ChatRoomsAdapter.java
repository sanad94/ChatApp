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
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by 2017 on 04/02/2017.
 */

public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> implements Filterable
{
    private ArrayList<ChatRoom> chatRooms;
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
         this.chatRooms=chatRooms;
         realm=Realm.getDefaultInstance();
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
        Messages lastMesage = new  Messages();
        final ChatRoom chatRoom = chatRooms.get(position);
        RealmList<Messages> messages = chatRoom.getMessages();
        for (int i = 0 ; i<messages.size();i++)
        {
            Messages message = messages.get(i);
            lastMesage=message;
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
            chatRoom.setDate(lastMesage.getTime());
            contact = realm.where(MyContacts.class).equalTo("phoneNumber",chatRoom.getPhoneNumber()).findFirst();
            realm.copyToRealmOrUpdate(chatRoom);
            realm.commitTransaction();
        }
        holder.lastMessage.setText(lastMesage.getMessage());
        holder.time.setText(lastMesage.getTime());
        if(contact!=null)
        {
            holder.name.setText(contact.getName());
            holder.image.setImageURI("http://10.0.0.8:8080/ChatService/getImage/"+contact.getPhoneNumber());
            return;
        }
        holder.image.setImageURI("http://10.0.0.8:8080/ChatService/getImage/"+chatRoom.getPhoneNumber());
        holder.name.setText(chatRoom.getPhoneNumber());
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
            filter = new ChatRoomsFilter(this,chatRooms);
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
