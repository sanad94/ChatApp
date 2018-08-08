package com.example.a2017.chatapp.RecyclerAdapters;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a2017.chatapp.Models.MessageOverNetwork;
import com.example.a2017.chatapp.Models.Messages;
import com.example.a2017.chatapp.Network.ApiClientRetrofit;
import com.example.a2017.chatapp.Network.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.Network.BaseUrl;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

import com.facebook.*;

import static java.security.AccessController.getContext;

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
        ProgressBar imageProgress  ;
        public ViewHolder(View itemView)
        {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.imageMessage);
            message= (TextView) itemView.findViewById(R.id.message);
            time= (TextView) itemView.findViewById(R.id.time);
            status = (SimpleDraweeView) itemView.findViewById(R.id.status);
            imageProgress = (ProgressBar) itemView.findViewById(R.id.imageUplod);
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
        if(isMeflag && message.getMessage().contains("ImageMessage") && message.getStatus() == Messages.TOSERVER)
        {
            String loclaPath = message.getMessage().replace("ImageMessage:", "");
            holder.image.setImageURI(loclaPath);
            holder.imageProgress.setVisibility(View.VISIBLE);
        }
        else if(message.getMessage().contains("ImageMessage"))
        {

            holder.image.setImageURI(BaseUrl.BASE_URL_ROOM_IMAGE+message.getMessage().replace("ImageMessage:", "")+"/"+fromPhoneNumber+"/"+toPhoneNumber);
            if(isMeflag && holder.imageProgress.getVisibility()==View.VISIBLE)
                holder.imageProgress.setVisibility(View.INVISIBLE);
        }
        else
        {
            if(holder.message==null)
                return;
            holder.message.setText(message.getMessage().replace("TextMessage:",""));
        }
        updateMessageStatus(holder,message);
        holder.setIsRecyclable(false);
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

        if(isMeflag && holder.status.getVisibility()==View.VISIBLE)
        {
            holder.status.setVisibility(View.INVISIBLE);
        }

        if(message.getUuid().equals(myLastMessage.getUuid()))
        {
            if(holder.status == null)
                return;
            if(holder.status.getVisibility() == View.INVISIBLE)
            {
                holder.status.setVisibility(View.VISIBLE);

            }
            if(message.getStatus()==Messages.SENT)
            {
                id = R.mipmap.baseline_done_white_18;
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(id).build();
                holder.status.setImageURI(imageRequest.getSourceUri());
            }
            else if(message.getStatus()==Messages.DELIVERED)
            {
                id = R.mipmap.baseline_done_all_white_18;
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(id).build();
                holder.status.setImageURI(imageRequest.getSourceUri());
            }
            else if(message.getStatus()==Messages.READ)
            {
                holder.status.setImageURI(BaseUrl.BASE_URL_IMAGE+fromPhoneNumber);
            }



        }

    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public String getToPhoneNumber() {
        return toPhoneNumber;
    }

    public void setToPhoneNumber(String toPhoneNumber) {
        this.toPhoneNumber = toPhoneNumber;
    }
}
