package com.startup.chatapp.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.startup.chatapp.R;
import com.startup.chatapp.model.MessageModelClass;

import org.json.JSONException;

public class ImageSend extends AppCompatActivity {

    MessageModelClass messageModelClass;
    ImageView imageView;
    private StorageReference mStorageRef;
    String uri;
    Button button;
    String msgUid;
    ChatActivity chatActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_send);

        button = findViewById(R.id.imagesendbtn);

        imageView = findViewById(R.id.imagesend);

        chatActivity = new ChatActivity();

        mStorageRef = FirebaseStorage.getInstance().getReference("u_image");
        uri = getIntent().getStringExtra("img");

        imageView.setImageURI(Uri.parse(uri));
        msgUid = getIntent().getStringExtra("msgUid");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUpload();
            }
        });

    }


    private String getFileExtension(Uri uri) {

        ContentResolver cR = getApplicationContext().getContentResolver();

        MimeTypeMap mime = MimeTypeMap.getSingleton();


        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void imageUpload() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                StorageReference fileRef = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(Uri.parse(uri)));


                fileRef.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_SHORT).show();
//                Glide.with(getApplicationContext()).load(uri).into(imageView);

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        String push = FirebaseDatabase.getInstance().getReference().push().getKey();

                        messageModelClass = new MessageModelClass(downloadUrl.toString(), push, FirebaseAuth.getInstance().getUid(), System.currentTimeMillis() / 1000);
                        messageModelClass.setType(1);
                        FirebaseDatabase.getInstance().getReference().child("ChatSystem").child(msgUid).child(push).setValue(messageModelClass);
                        chatActivity.makeRecentChats("Photo");
                        try {
                            chatActivity.sendNotifications(ChatActivity.user1_number, chatActivity.getTitl(), chatActivity.getToken());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }).start();
        finish();
    }
}
