package com.startup.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ZoomedImage extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_image);
        imageView = findViewById(R.id.img);

        String extra = getIntent().getStringExtra("imgs");
        Glide.with(this).load(extra).into(imageView);


    }
}
