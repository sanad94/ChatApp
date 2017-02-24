package com.example.a2017.chatapp.Activitys;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.a2017.chatapp.Fragments.ChatRoomsFragment;
import com.example.a2017.chatapp.Fragments.ContactsFragment;
import com.example.a2017.chatapp.Fragments.SettingsFragment;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.Utils.Preferences;

public class MainActivity extends AppCompatActivity
{
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setSupportActionBar(toolbar);
        goToChatRoomListFragment();
        backStackFragment();
        setOnNavigationItemSelectedListener();
        setItemCheckedNavigationView();

    }

    private void backStackFragment()
    {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            onBackPressed();
                        }
                    });
                }
                else
                {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
    }

    private void goToChatRoomListFragment()
    {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        ChatRoomsFragment chatRoomsFragment = new ChatRoomsFragment();
        transaction.replace(R.id.fragment_container, chatRoomsFragment,"CHAT_ROOMS_FRAGMENT");
        transaction.commit();
    }

    private void setItemCheckedNavigationView()
    {
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
    }

    private void setOnNavigationItemSelectedListener()
    {
     bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
     {
         @Override
         public boolean onNavigationItemSelected(@NonNull MenuItem item)
         {
             manager = getSupportFragmentManager();
             switch (item.getItemId())
             {
                 case R.id.action_contacts :
                     transaction = manager.beginTransaction();
                     ContactsFragment contactsFragment = new ContactsFragment();
                     transaction.replace(R.id.fragment_container, contactsFragment,"CONTACTS_FRAGMENT");
                     transaction.commit();
                     break;
                 case R.id.action_chat :
                     transaction = manager.beginTransaction();
                     ChatRoomsFragment chatRoomsFragment = new ChatRoomsFragment();
                     transaction.replace(R.id.fragment_container, chatRoomsFragment,"CHAT_ROOMS_FRAGMENT");
                     transaction.commit();
                     break;
                 case R.id.action_settings :
                     transaction = manager.beginTransaction();
                     SettingsFragment settingsFragment = new SettingsFragment();
                     transaction.replace(R.id.fragment_container, settingsFragment,"SETTINGS_FRAGMENT");
                     transaction.commit();
                     break;

             }
             return true;
         }
     });
    }

    private void setStatusBarToTransparent()
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            toolbar.setTitleMarginEnd(getStatusBarHeight());
        }
    }

    private int getStatusBarHeight()
    {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight= contentViewTop - statusBarHeight;
        return titleBarHeight;
    }

        @Override
    protected void onPause()
    {
        super.onPause();
        Preferences.setisInbackground(true,this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Preferences.setisInbackground(false,this);
    }
}
