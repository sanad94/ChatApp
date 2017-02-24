package com.example.a2017.chatapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a2017.chatapp.R;
import com.example.a2017.chatapp.Utils.Preferences;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by 2017 on 15/02/2017.
 */

public class ImageViewerFragment extends Fragment
{
    private final static String FROM_PHONE_NUMBER = "from_phone_Number";
    private  String fromPhoneNumber;
    private SimpleDraweeView image;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getArgument();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.image_viewer,container,false);
        image = (SimpleDraweeView) view .findViewById(R.id.contactImageView);
        getImage();
        return view;
    }

    private void getArgument()
    {
        fromPhoneNumber = getArguments().getString(FROM_PHONE_NUMBER);

    }

    private void getImage()
    {
       image.setImageURI("http://10.0.0.8:8080/ChatService/getImage/"+fromPhoneNumber);
    }
}
