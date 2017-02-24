package com.example.a2017.chatapp.RecyclerAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by 2017 on 04/02/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>
{
    private ArrayList<Messages> messages ;
    private String fromPhoneNumber;
    private int isMe = 101;
    private Realm realm;
    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView message , time ;
        public ViewHolder(View itemView)
        {
            super(itemView);
            message= (TextView) itemView.findViewById(R.id.message);
            time= (TextView) itemView.findViewById(R.id.time);
        }
    }

   public MessagesAdapter(ArrayList<Messages> messages, String fromPhoneNumber)
    {
        this.messages=messages;
        this.fromPhoneNumber=fromPhoneNumber;
        realm=Realm.getDefaultInstance();
    }
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemview = null;
        if (viewType==isMe)
        {
            itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_to,parent,false);
            return  new ViewHolder(itemview);
        }
        itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_from,parent,false);
        return  new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
       final Messages message = messages.get(position);
        if(!message.isRead())
        {
            realm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    message.setRead(true);
                }
            });
        }
        SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
        try
        {
            Date date = dateformat.parse(message.getTime());
            String time = timeformat.format(date);
            holder.time.setText(time);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        holder.message.setText(message.getMessage());

    }

    @Override
    public int getItemCount()
    {
     return messages.size();
    }

    @Override
    public int getItemViewType(int position)
    {

     if(messages.get(position).getFromPhoneNumber().equals(fromPhoneNumber))
     {
      return isMe;
     }

        return position;
    }

    public void setMessages(ArrayList<Messages> messages)
    {
        this.messages = messages;
    }

    public ArrayList<Messages> getMessages()
    {
        return messages;
    }
}
