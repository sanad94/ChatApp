package com.example.a2017.chatapp.RecyclerTools;

import android.text.TextUtils;
import android.widget.Filter;
import com.example.a2017.chatapp.Models.ChatRoom;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.RecyclerAdapters.ChatRoomsAdapter;
import com.example.a2017.chatapp.RecyclerAdapters.ContactsAdapter;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by 2017 on 20/02/2017.
 */

public  class ContactsFilter extends Filter
{
    private ContactsAdapter adapter;
    private ArrayList<MyContacts> originalList;
    private  ArrayList<MyContacts> filteredList;

    public ContactsFilter(ContactsAdapter adapter, ArrayList<MyContacts> originalList)
    {
        super();
        this.adapter = adapter;
        this.originalList = new ArrayList<>(originalList);
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
        adapter.setContacts(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void filterListByPhoneNumber(CharSequence constraint)
    {
        if (constraint.length() == 0)
        {
            filteredList.addAll(originalList);
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
            for (int i = 0 ; i < originalList.size() ; i++ )
            {
                MyContacts contacts = originalList.get(i);
                if (contacts.getPhoneNumber().contains(filterPattern))
                {
                    filteredList.add(contacts);
                }
            }
        }
    }

    private void filterListByName(CharSequence constraint)
    {
        if (constraint.length() == 0)
        {
            filteredList.addAll(originalList);
        }
        else
        {
            String filterPattern = constraint.toString().toLowerCase().trim();
            for (int i = 0 ; i < originalList.size() ; i++ )
            {
                MyContacts contacts = originalList.get(i);
                if (contacts.getName().toLowerCase().trim().contains(filterPattern))
                {
                    filteredList.add(contacts);
                }
            }
        }

        if(filteredList.size()== 0)
        {
            filterListByPhoneNumber(constraint);
        }
    }
}
