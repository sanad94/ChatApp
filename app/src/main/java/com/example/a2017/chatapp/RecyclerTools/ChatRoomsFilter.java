package com.example.a2017.chatapp.RecyclerTools;

import android.widget.Filter;
import com.example.a2017.chatapp.Models.ChatRoom;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.RecyclerAdapters.ChatRoomsAdapter;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by 2017 on 20/02/2017.
 */

public  class ChatRoomsFilter extends Filter
{
    private  ChatRoomsAdapter adapter;
    private HashMap<String,ChatRoom> originalMap;
    private  ArrayList<ChatRoom> filteredList;

    public ChatRoomsFilter(ChatRoomsAdapter adapter, HashMap<String,ChatRoom> originalMap)
    {
        super();
        this.adapter = adapter;
        this.originalMap = originalMap;
        this.filteredList = new ArrayList<>();
    }

    @Override
    protected  FilterResults performFiltering(CharSequence constraint)
    {
        // here i can't perform filtering because this method trigger another thread
        return null;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results)
    {
        //https://github.com/realm/realm-java/issues/646
        //http://stackoverflow.com/questions/30162431/filtering-realm-object-in-android
        // solve the issue for realm object access from other thread
        filteredList.clear();
        filterListByName(constraint);
        adapter.setChatRooms(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void filterListByPhoneNumber(CharSequence constraint)
    {
        if (constraint.length() == 0)
        {
            filteredList.addAll(originalMap.values());
        }
        else
        {
            String filterPattern = constraint.toString().toLowerCase().trim();
            if(filterPattern.length()>= 3 && filterPattern.length() <= 6)
            {
                filterPattern = filterPattern.substring(0, 3) + "-" ;
            }
            else if(filterPattern.length()>= 6 && filterPattern.length() <= 10)
            {
                filterPattern = filterPattern.substring(0, 3) + "-" + filterPattern.substring(3, 6) + "-" + filterPattern.substring(6, filterPattern.length());

            }
            for (String key : originalMap.keySet())
            {

                if (originalMap.get(key).getPhoneNumber().trim().contains(filterPattern))
                {
                    ChatRoom chatRoom = originalMap.get(key);
                    filteredList.add(chatRoom);
                }
            }
        }
    }

    private void filterListByName(CharSequence constraint)
    {
        if (constraint.length() == 0)
        {
            filteredList.addAll(originalMap.values());
        }
        else
        {
            String filterPattern = constraint.toString().toLowerCase().trim();
            for (String key : originalMap.keySet())
            {

                if (key.trim().contains(filterPattern))
                {
                    ChatRoom chatRoom = originalMap.get(key);
                    filteredList.add(chatRoom);
                }
            }
        }

        if(filteredList.size() == 0)
        {
            filterListByPhoneNumber(constraint);
        }
    }
}
