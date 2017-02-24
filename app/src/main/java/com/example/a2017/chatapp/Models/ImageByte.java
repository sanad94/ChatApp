package com.example.a2017.chatapp.Models;

/**
 * Created by 2017 on 16/02/2017.
 */
public class ImageByte
{
    private byte [] image ;

    public ImageByte()
    {
    }

    public ImageByte(byte[] image)
    {
        this.image = image;
    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }
}
