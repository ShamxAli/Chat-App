package com.startup.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecentChats extends AppCompatActivity {
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        fab = findViewById(R.id.fab);
    }

    public void fabClick(View view) {
        Intent intent = new Intent(RecentChats.this , MainActivity.class);
        startActivity(intent);
    }
}
