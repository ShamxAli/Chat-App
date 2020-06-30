package com.startup.chatapp.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
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

    String msgUid;
    static ChatActivity contex;


    public static void setContext(Context context) {
        contex = (ChatActivity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_send);

        imageView = findViewById(R.id.imagesend);


        mStorageRef = FirebaseStorage.getInstance().getReference("u_image");
        uri = getIntent().getStringExtra("img");

        imageView.setImageURI(Uri.parse(uri));
        msgUid = getIntent().getStringExtra("msgUid");
    }


    public void btnSendImage(View view) {
        imageUpload();
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
                        contex.makeRecentChats("Photo");
                        try {
                            contex.sendNotifications(ChatActivity.user1_number, contex.getTitl(), contex.getToken());
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