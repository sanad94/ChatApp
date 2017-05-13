package com.example.a2017.chatapp.RecyclerAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RecyclerTools.ContactsFilter;
import com.example.a2017.chatapp.Network.BaseUrl;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by 2017 on 28/01/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements Filterable
{
    private ArrayList<MyContacts> contacts;
    private ContactsFilter filter;

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView contactName;
        SimpleDraweeView image ;

        public ViewHolder(View itemView)
        {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            image = (SimpleDraweeView) itemView.findViewById(R.id.contactImage);
        }
    }

    public ContactsAdapter(ArrayList<MyContacts> contacts)
    {
        this.contacts = contacts;
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list, parent, false);
        return new ContactsAdapter.ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position)
    {
        MyContacts contact = contacts.get(position);
        holder.contactName.setText(contact.getName());
        holder.image.setImageURI(BaseUrl.BASE_URL_IMAGE+contact.getPhoneNumber());
        holder.image.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
    }

    @Override
    public int getItemCount()
    {
        return contacts.size();
    }

    public void setContacts(ArrayList<MyContacts> contacts)
    {
        this.contacts = contacts;
    }

    public ArrayList<MyContacts> getContacts()
    {
        return contacts;
    }

    @Override
    public Filter getFilter()
    {
        if(filter == null )
        {
            filter = new ContactsFilter(this,contacts);
        }

        return filter;
    }
}
