package com.example.a2017.chatapp.Activitys;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.a2017.chatapp.Models.UserToken;
import com.example.a2017.chatapp.RetrofitApi.ApiClientRetrofit;
import com.example.a2017.chatapp.RetrofitApi.ApiInterfaceRetrofit;
import com.example.a2017.chatapp.Utils.Preferences;
import com.example.a2017.chatapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity
{
    private boolean isLogin;
    private EditText phoneNumber;
    private EditText fullName;
    private Button enter;
    private String myPhoneNumber;
    private String myfullName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        enter= (Button) findViewById(R.id.btn_enter);
        phoneNumber= (EditText) findViewById(R.id.phone_number);
        fullName= (EditText) findViewById(R.id.full_name);
        isLogin= Preferences.isLogin(this);
       if (isLogin)
        {
            goToNextActivity();
            Preferences.setFirstRun(true,this);
        }
        else
        {
            enterOnclick();

        }
    }

    private void goToNextActivity()
    {
        Intent intent =new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void enterOnclick()
    {
        enter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myPhoneNumber = phoneNumber.getText().toString();
                myfullName = fullName.getText().toString();
                if(!myPhoneNumber.matches("") && myPhoneNumber.length() == 10 && !myfullName.matches(""))
                {
                    myPhoneNumber = myPhoneNumber.substring(0, 3) + "-" + myPhoneNumber.substring(3, 6) + "-" + myPhoneNumber.substring(6, myPhoneNumber.length());
                    String token = Preferences.getMyToken(getBaseContext());
                    sendTokenToServer(token,myPhoneNumber);
                }
                else
                {
                    phoneNumber.setError(getResources().getString(R.string.login_validation));
                    fullName.setError(getResources().getString(R.string.login_validation));
                }
            }
        });
    }

    private void sendTokenToServer(String token,final String myPhoneNumber)
    {
        UserToken mytoken = new UserToken(myPhoneNumber,token);
        ApiInterfaceRetrofit apiClientRetrofit= ApiClientRetrofit.getClient().create(ApiInterfaceRetrofit.class);
        Call<UserToken> sendToken = apiClientRetrofit.submitMyToken(mytoken);
        sendToken.enqueue(new Callback<UserToken>()
        {
            @Override
            public void onResponse(Call<UserToken> call, Response<UserToken> response)
            {
                if(response.code()==200 || response.code() == 204)
                {
                    Preferences.setMyPhoneNumber(myPhoneNumber,getBaseContext());
                    Preferences.setMyFullName(myfullName,getBaseContext());
                    Preferences.setLogin(true,getBaseContext());
                    goToNextActivity();
                }
                else
                {
                    phoneNumber.setError(getResources().getString(R.string.server_validation));
                }
            }

            @Override
            public void onFailure(Call<UserToken> call, Throwable t)
            {

            }
        });
    }
}
