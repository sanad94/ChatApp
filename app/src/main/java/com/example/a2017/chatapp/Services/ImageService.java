package com.example.a2017.chatapp.Services;

import android.app.IntentService;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.a2017.chatapp.Models.ImageByte;
import com.example.a2017.chatapp.Models.ImageMessageOverNetwork;
import com.example.a2017.chatapp.Network.ApiClientRetrofit;
import com.example.a2017.chatapp.Network.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.Utils.Preferences;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 2017 on 18/02/2017.
 */

public class ImageService extends IntentService
{
    private Uri imageUri;
    private String myPhoneNumber;
    private String toPhoneNumber;
    private String uuid;
    public static final String ACTION ="com.example.a2017.chatapp.services.imageService";
    private boolean isRunning = false;
    private boolean checkRunning = false;

    public ImageService()
    {
        super("ImageService");
    }

    @Override
    public void onDestroy()
    {
        isRunning = false ;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        checkRunning = intent.getExtras().getBoolean("check");
        if (checkRunning)
        {
            Intent intentValue = new Intent(ACTION);

            if (!isRunning)
            {
                intentValue.putExtra("running", false);
            }
            else
            {
                intentValue.putExtra("running", true);
            }
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intentValue);
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            imageUri = Uri.parse(intent.getStringExtra("imageUri"));
            myPhoneNumber = intent.getStringExtra("myPhoneNumber");
            toPhoneNumber = intent.getStringExtra("toPhoneNumber");
            uuid = intent.getStringExtra("uuid");
            convertImageTobytes(imageUri);
            Log.d("onHandleIntent", "done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void convertImageTobytes(Uri uri)
    {
        isRunning = true;
        try
        {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte [] buffer = new byte[bufferSize];
            int len = 0 ;
            while ((len = inputStream.read(buffer) )!=-1)
            {
                byteArrayOutputStream.write(buffer,0,len);
            }
            ImageByte imageByte = new ImageByte(byteArrayOutputStream.toByteArray());
            if(toPhoneNumber == null)
            {
                uploadMyImageToServer(imageByte,uri);
            }
            else
            {
                sendImageMessage(imageByte);
            }
            Log.d("convertImageTobytes","done");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void uploadMyImageToServer(ImageByte imageByte, final Uri uri)
    {
        ApiInterfaceRetrofit retrofit = ApiClientRetrofit.getClient().create(ApiInterfaceRetrofit.class);
        Call<Void> upload = retrofit.settImage(myPhoneNumber,imageByte);
        upload.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.code()==200 || response.code()==204)
                {
                    Log.d("uploadImageToServer","done");
                    Preferences.setMyImageUri(uri.toString(),getBaseContext());
                    Intent intentValue = new Intent(ACTION);
                    intentValue.putExtra("done",true);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intentValue);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Intent intentValue = new Intent(ACTION);
                intentValue.putExtra("done",false);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intentValue);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendImageMessage(ImageByte imageByte)
    {
        Gson gson = new Gson();
        Log.d("SendImageMessage",gson.toJson(imageByte));
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
        String time = dateformat.format(c.getTime());
     //   String time = "";
        ImageMessageOverNetwork message = new ImageMessageOverNetwork(myPhoneNumber,toPhoneNumber,time,imageByte.getImage(),uuid);
        ApiInterfaceRetrofit retrofit = ApiClientRetrofit.getClient().create(ApiInterfaceRetrofit.class);
        Call<Void> upload = retrofit.sendImageMessage(message);
        upload.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.code()==200 || response.code()==204)
                {
                    Log.d("SendImageMessage","done");
                    Intent intentValue = new Intent(ACTION);
                    intentValue.putExtra("done",true);
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intentValue);
                }
                else
                {
                    Log.d("SendImageMessage",String.valueOf(response.code()));
                    Log.d("SendImageMessage",myPhoneNumber);
                    Log.d("SendImageMessage",toPhoneNumber);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Log.d("SendImageMessage","onFailure");
                Intent intentValue = new Intent(ACTION);
                intentValue.putExtra("done",false);
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intentValue);
            }
        });
    }
}
