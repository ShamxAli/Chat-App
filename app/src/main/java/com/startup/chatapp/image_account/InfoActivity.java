package com.startup.chatapp.image_account;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.startup.chatapp.HomeActivity;
import com.startup.chatapp.R;
import com.startup.chatapp.model.Upload;

public class InfoActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1001;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    Uri uri;
    ProgressBar progressBar;
    EditText editText;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        editText = findViewById(R.id.et);
        mDatabase = FirebaseDatabase.getInstance();
        progressBar = findViewById(R.id.pb);
        imageView = findViewById(R.id.img);

        mStorage = FirebaseStorage.getInstance();
        mRef = mDatabase.getReference("uploads");
        mStorageRef = mStorage.getReference("uploads");
    }


    public void imageClick(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            showProgress();
            uri = data.getData();
            if (uri != null) {

                StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + "jpg");

                storageReference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                showToast("Upload successful");
                                hideProgress();
                                Glide.with(InfoActivity.this).load(uri).into(imageView);


                                // Now store to the database

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();

                                while (!urlTask.isSuccessful()) ;

                                Uri downloadUrl = urlTask.getResult();
                                Log.d("TAGTAG", "onSuccess: url is : " + downloadUrl);

                                Upload upload = new Upload(editText.getText().toString().trim(), downloadUrl.toString());

                                String uploadId = mRef.push().getKey();
                                mRef.child(uploadId).setValue(upload);
                            }


                        });

            }


        }


    }

    public void nextClick(View view) {
        Intent intent = new Intent(InfoActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
