package com.example.a2017.chatapp.RecyclerAdapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a2017.chatapp.Models.MessageOverNetwork;
import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.Network.ApiClientRetrofit;
import com.example.a2017.chatapp.Network.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.Network.BaseUrl;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 04/02/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>
{
    private ArrayList<Messages> messages ;
    private String fromPhoneNumber;
    private String toPhoneNumber;
    private Messages myLastMessage;
    private int imageMessage = 100;
    private int isMe_imageMessage = 101;
    private int isMe_textMessage = 102;
    private Realm realm;
    private boolean isMeflag = true ;
    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView message , time ;
        SimpleDraweeView image ;
        SimpleDraweeView status ;
        public ViewHolder(View itemView)
        {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.imageMessage);
            message= (TextView) itemView.findViewById(R.id.message);
            time= (TextView) itemView.findViewById(R.id.time);
            status = (SimpleDraweeView) itemView.findViewById(R.id.status);
        }
    }

   public MessagesAdapter(ArrayList<Messages> messages, String fromPhoneNumber ,String toPhoneNumber)
    {
        this.messages=messages;
        this.fromPhoneNumber=fromPhoneNumber;
        this.toPhoneNumber = toPhoneNumber;
        realm=Realm.getDefaultInstance();
    }
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemview = null;
        if (viewType==isMe_textMessage)
        {
            itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_to,parent,false);
            return  new ViewHolder(itemview);
        }
        else if(viewType==isMe_imageMessage)
        {
            itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_to,parent,false);
            return  new ViewHolder(itemview);
        }
        else if(viewType== imageMessage)
        {
            itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_from,parent,false);
            return  new ViewHolder(itemview);
        }
        itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_from,parent,false);
        return  new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
       final Messages message = messages.get(position);
        if(message==null)
            return;
        if(!message.isRead())
        {
            realm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    message.setRead(true);
                    message.setStatus(Messages.READ);
                }
            });
            final MessageOverNetwork messageToSend = new MessageOverNetwork(fromPhoneNumber,toPhoneNumber,message.getTime(),message.getMessage(),message.getUuid(),MessageOverNetwork.READ);
            sendMessageToserver(messageToSend);
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
           // e.printStackTrace();
        }
        if(message.getMessage().contains("ImageMessage:"))
        {
            if(messages.get(position).getFromPhoneNumber().equals(fromPhoneNumber))
            {
                holder.image.setImageURI(message.getMessage().replace("ImageMessage:", ""));
            }
            else
            {
                holder.image.setImageURI(BaseUrl.BASE_URL_ROOM_IMAGE+message.getMessage().replace("ImageMessage:", "")+"/"+fromPhoneNumber+"/"+toPhoneNumber);
            }
        }
        else
        {
            holder.message.setText(message.getMessage().replace("TextMessage:",""));
        }

        updateMessageStatus(holder,message);

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
         isMeflag = true;
      if(messages.get(position).getMessage().contains("ImageMessage:"))
      {
          return isMe_imageMessage;
      }
        return isMe_textMessage;
     }
     else
     {
         isMeflag = false;
         if(messages.get(position).getMessage().contains("ImageMessage:"))
         {
             return imageMessage;
         }
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

    public Messages getMyLastMessage() {
        return myLastMessage;
    }

    public void setMyLastMessage(Messages myLastMessage) {
        this.myLastMessage = myLastMessage;
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

    private void updateMessageStatus(ViewHolder holder, Messages message)
    {
        if(myLastMessage==null)
            return;

        int id = 0;
        ImageRequest imageRequest;
        if(message.getUuid().equals(myLastMessage.getUuid()))
        {
            if(holder.status.getVisibility() == View.INVISIBLE)
            {
                holder.status.setVisibility(View.VISIBLE);

            }
            if(message.getStatus()==Messages.SENT)
            {
                id = R.mipmap.baseline_done_white_18;
            }
            else if(message.getStatus()==Messages.DELIVERED)
            {
                id = R.mipmap.baseline_done_all_white_18;
            }
            else if(message.getStatus()==Messages.READ)
            {
              id = R.drawable.default_image;
            }

            imageRequest = ImageRequestBuilder.newBuilderWithResourceId(id).build();
            holder.status.setImageURI(imageRequest.getSourceUri());

        }

    }
}
