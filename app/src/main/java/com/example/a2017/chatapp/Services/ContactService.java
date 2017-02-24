package com.example.a2017.chatapp.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.RetrofitApi.ApiClientRetrofit;
import com.example.a2017.chatapp.RetrofitApi.ApiInterfaceRetrofit;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 12/02/2017.
 */

public class ContactService extends IntentService
{
    private LinkedList<MyContacts> myContacts;
    private Realm realm;
    public static final String ACTION ="com.example.a2017.chatapp.services.contactService";

    public ContactService()
    {
        super("ContactService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        readContact();
    }

    private void readContact()
    {
        Query q = Contacts.getQuery();
        List<Contact> contacts = q.find();
        myContacts = new LinkedList<>();
        for (Contact c : contacts)
        {
            MyContacts myContact ;
            for (int i=0 ; i<c.getPhoneNumbers().size() ; i++)
            {
                String phomeNumber = c.getPhoneNumbers().get(i).getNumber();
                if(phomeNumber.length() == 10)
                {
                    phomeNumber = phomeNumber.substring(0, 3) + "-" + phomeNumber.substring(3, 6) + "-" + phomeNumber.substring(6, phomeNumber.length());
                }
                else if(phomeNumber.length() == 12)
                {
                    if(phomeNumber.charAt(3)!='-')
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
                myContact = new MyContacts();
                myContact.setName(c.getDisplayName());
                myContact.setPhoneNumber(phomeNumber);
                myContacts.add(myContact);

            }

        }

        sendContactToserver(myContacts);
    }

    private void sendContactToserver(final LinkedList<MyContacts> contacts)
    {
        ApiInterfaceRetrofit apiClient = ApiClientRetrofit.getClient().create(ApiInterfaceRetrofit.class);
        Call<LinkedList<MyContacts>> contactList = apiClient.sendContact(contacts);
        contactList.enqueue(new Callback<LinkedList<MyContacts>>() {
            @Override
            public void onResponse(Call<LinkedList<MyContacts>> call, Response<LinkedList<MyContacts>> response)
            {
                if(response.code()==200 || response.code()==204)
                {
                    final LinkedList<MyContacts> contacts = response.body();
                    addContactToRealm(contacts);
                }
            }

            @Override
            public void onFailure(Call<LinkedList<MyContacts>> call, Throwable t)
            {

            }
        });
    }

    private void addContactToRealm(final LinkedList<MyContacts> contacts)
    {
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                realm.copyToRealmOrUpdate(contacts);
            }
        });
        Intent intentValue = new Intent(ACTION);
        intentValue.putExtra("done",true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentValue);
    }
}
