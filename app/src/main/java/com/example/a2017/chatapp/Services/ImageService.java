package com.example.a2017.chatapp.Services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.example.a2017.chatapp.Models.ImageByte;
import com.example.a2017.chatapp.RetrofitApi.ApiClientRetrofit;
import com.example.a2017.chatapp.RetrofitApi.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.Utils.Preferences;
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

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try
        {
            imageUri = Uri.parse(intent.getStringExtra("imageUri"));
            myPhoneNumber = intent.getStringExtra("myPhoneNumber");
            convertImageTobytes(imageUri);
            Log.d("onHandleIntent", "done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

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
            uploadImageToServer(imageByte,uri);
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

    public void uploadImageToServer(ImageByte imageByte, final Uri uri)
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
}
