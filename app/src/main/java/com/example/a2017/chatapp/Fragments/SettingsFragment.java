package com.example.a2017.chatapp.Fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.a2017.chatapp.Activitys.Login;
import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.RetrofitApi.ApiClientRetrofit;
import com.example.a2017.chatapp.RetrofitApi.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.Services.ImageService;
import com.example.a2017.chatapp.Utils.Preferences;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.jorgecastilloprz.FABProgressCircle;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 16/02/2017.
 */

public class SettingsFragment extends Fragment
{
    private SimpleDraweeView image;
    private static int IMAGE_REQUEST_CODE = 101;
    private static int READ_PERMISSION_CODE = 123;
    private String myPhoneNumber;
    private String myImageUri;
    private Realm realm;
    private Toolbar toolbar;
    private String myfullName;
    private TextView textInfo;
    public static FABProgressCircle fabProgressCircle;
    private Intent imageServiceIntent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableActivityToolbar();
        myPhoneNumber = Preferences.getMyPhoneNumber(getContext());
        myfullName = Preferences.getMyFullName(getContext());
        myImageUri = Preferences.getMyImageUri(getContext());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.settings_fragment,container,false);
        fabProgressCircle = (FABProgressCircle) view.findViewById(R.id.fabProgressCircle);
        image = (SimpleDraweeView) view.findViewById(R.id.myImageView);
        textInfo = (TextView) view.findViewById(R.id.info_text);
        toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        realm = Realm.getDefaultInstance();
        configureRequestPermissions();
        setImageUri();
        imageOnclick();
        enableSettingsToolbar();
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        textInfo.setText(myPhoneNumber);
        IntentFilter intentFilter = new IntentFilter(ImageService.ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,intentFilter);
        imageServiceIntent = new Intent(getContext(),ImageService.class);
        imageServiceIntent.putExtra("check",true);
        getActivity().startService(imageServiceIntent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean isDone = intent.getExtras().getBoolean("done");
            boolean isRunning = intent.getExtras().getBoolean("running");
            if(isRunning)
            {
                fabProgressCircle.show();
            }
            if(isDone)
            {
                fabProgressCircle.beginFinalAnimation();
            }
            else
            {
                fabProgressCircle.hide();
            }
        }
    };

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        enableActivityToolbar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.settings_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_deactivateAccount)
        {
            deactivate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deactivate()
    {
        ApiInterfaceRetrofit retrofit = ApiClientRetrofit.getClient().create(ApiInterfaceRetrofit.class);
        Call<Void> deactivate = retrofit.deactivateAccount(myPhoneNumber);
        deactivate.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                Log.d("onResponse: ",String.valueOf(response.code()));
                if(response.code()>=200 && response.code()<300)
                {
                    Preferences.setLogin(false,getContext());
                    Preferences.setMyPhoneNumber(null,getContext());
                    Intent intent = new Intent(getActivity(),Login.class);
                    startActivity(intent);
                    getActivity().finish();
                    realm.executeTransactionAsync(new Realm.Transaction()
                    {
                        @Override
                        public void execute(Realm realm)
                        {
                            realm.deleteAll();
                             Preferences.setMyImageUri("",getContext());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {

            }
        });
    }

    public void setImageUri()
    {
       image.setImageURI(myImageUri);
    }

    public void imageOnclick()
    {
        image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                   // intent.setType("image/jpg");
                    intent.setType("*/*");
                    startActivityForResult(intent, IMAGE_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==IMAGE_REQUEST_CODE && data!=null)
        {
            Uri imageUri = data.getData();
            image.setImageURI(imageUri);
            imageServiceIntent = new Intent(getContext(),ImageService.class);
            imageServiceIntent.putExtra("check",false);
            imageServiceIntent.putExtra("imageUri",imageUri.toString());
            imageServiceIntent.putExtra("myPhoneNumber",myPhoneNumber);
            getActivity().startService(imageServiceIntent);
            fabProgressCircle.show();
        }
    }

    private void configureRequestPermissions()
    {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
                }
            }
        }

    }

    private void disableActivityToolbar()
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    private void enableActivityToolbar()
    {
        Toolbar activityToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity) getActivity()).setSupportActionBar(activityToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void enableSettingsToolbar()
    {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(myfullName);
    }


}
