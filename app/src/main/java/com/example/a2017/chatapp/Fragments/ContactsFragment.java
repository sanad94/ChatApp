package com.example.a2017.chatapp.Fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.a2017.chatapp.Models.MyContacts;
import com.example.a2017.chatapp.RecyclerAdapters.ContactsAdapter;
import com.example.a2017.chatapp.RecyclerTools.ContactsDividerItemDecoration;
import com.example.a2017.chatapp.RecyclerTools.ContactsRecyclerTouchListner;
import com.example.a2017.chatapp.RecyclerTools.IclickListner;
import com.example.a2017.chatapp.Services.ContactService;
import com.example.a2017.chatapp.Utils.Preferences;
import com.example.a2017.chatapp.R;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by 2017 on 28/01/2017.
 */

public class ContactsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
    private final static String FROM_PHONE_NUMBER = "from_phone_Number";
    private static int CONTACT_PERMISSION_CODE = 123;
    private RecyclerView recyclerView_contact;
    private ContactsAdapter contactsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isFirstRun;
    private Realm realm;
    private ArrayList<MyContacts> contactsList;
    private ArrayList<MyContacts> tempContactsList;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        isFirstRun = Preferences.isFirstRun(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.contact_main,container,false);
        realm = Realm.getDefaultInstance();
        recyclerView_contact = (RecyclerView) view.findViewById(R.id.recyclerview_contact);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contact_refresh);
        manager = getFragmentManager();
        checkFirstRun();
        configureRecyclerView();
        configureRequestPermissions();
        return view;
   }

    @Override
    public void onStart()
    {
        super.onStart();
        configureSwipeRefreshLayout();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == CONTACT_PERMISSION_CODE)
        {
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(),"Permission granted now you can read the contact_list", Toast.LENGTH_LONG).show();
                configureSwipeRefreshLayout();
            }
            else
            {
                Toast.makeText(getContext(),"Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ContactService.ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.activity_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        configureSearchView(searchView);
    }

    private void configureSearchView(SearchView searchView)
    {
        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tempContactsList = contactsList;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                contactsAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                contactsAdapter.getFilter().filter(newText);
                contactsList = contactsAdapter.getContacts();
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                contactsList = tempContactsList;
                contactsAdapter.setContacts(contactsList);
                contactsAdapter.notifyDataSetChanged();
                tempContactsList = null;
                return false;
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean isDone = intent.getExtras().getBoolean("done");
            if(isDone)
            {
                getFromRealm();
            }
            else
            {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(),getResources().getText(R.string.internet_connection),Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void getFromRealm()
    {
        RealmQuery<MyContacts> query = realm.where(MyContacts.class);
        RealmResults<MyContacts> realmResults =query.findAll();
        contactsList = new ArrayList<>(realmResults);
        contactsAdapter.setContacts(contactsList);
        contactsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
    private void configureRequestPermissions()
    {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
            {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
            }
        }

    }

    private void checkFirstRun()
    {
        if(isFirstRun)
        {
            Preferences.setFirstRun(false,getContext());
        }
    }

    private boolean isPermissionsDenied()
    {
        boolean denied = false;
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            if(!isFirstRun)
            {
                Toast.makeText(getContext(), "go to setting and allow the contact_list permission", Toast.LENGTH_SHORT).show();
                Log.d("firstRun", "false");
            }

            denied = true;
        }
        return denied;
    }
    private void configureSwipeRefreshLayout()
    {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run()
            {
                if (!isPermissionsDenied())
                {
                    if (isFirstRun)
                    {
                        fireContactService();
                    }
                    else
                    {
                        getFromRealm();
                    }
                }
            }
        });
    }

     private void configureRecyclerView()
    {
        contactsAdapter = new ContactsAdapter(new ArrayList<MyContacts>());
        recyclerView_contact.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_contact.setItemAnimator(new DefaultItemAnimator());
        recyclerView_contact.setAdapter(contactsAdapter);
        recyclerView_contact.addItemDecoration(new ContactsDividerItemDecoration(getContext()));
        recyclerView_contact.addOnItemTouchListener(new ContactsRecyclerTouchListner(getContext(), recyclerView_contact, new IclickListner()
        {
            @Override
            public void onClick(View view, int position)
            {
                goToMessageFragment(position);
            }

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));
    }

    private void fireContactService()
    {
        Intent serviceIntent = new Intent(getContext(), ContactService.class);
        getActivity().startService(serviceIntent);
    }

    private Bundle setArgument(int position)
    {
        MyContacts myContact = contactsList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(FROM_PHONE_NUMBER,myContact.getPhoneNumber());
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

    @Override
    public void onRefresh()
    {
        if(!isPermissionsDenied())
        {
            fireContactService();
        }
    }
}
