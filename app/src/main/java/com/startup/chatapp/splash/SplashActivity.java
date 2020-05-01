package com.startup.chatapp.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.startup.chatapp.MainActivity;
import com.startup.chatapp.R;
import com.startup.chatapp.RecentChats;
import com.startup.chatapp.phoneauthentication.PhoneLoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    startActivity(new Intent(SplashActivity.this, PhoneLoginActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this, RecentChats.class));
                    finish();
                }
            }
        },3000);


    }
}
