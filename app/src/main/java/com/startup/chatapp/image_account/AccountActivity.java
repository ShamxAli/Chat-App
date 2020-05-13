package com.startup.chatapp.image_account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.startup.chatapp.R;
import com.startup.chatapp.model.Upload;

public class AccountActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1001;
    public static final String TAG = "TAK";
    ImageView imageView;
    TextView name, number;
    DatabaseReference mRef;
    ValueEventListener mListener;
    StorageReference mStorageRef;
    Upload upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");


        mRef = FirebaseDatabase.getInstance().getReference("uploads");
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageView = findViewById(R.id.account_img);
                name = findViewById(R.id.account_name);
                number = findViewById(R.id.account_number);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    upload = snapshot.getValue(Upload.class);
                    upload.setKey(snapshot.getKey());
                    name.setText(upload.getName());
                    number.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    Glide.with(getApplicationContext()).load(upload.getUrl()).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRef.addValueEventListener(mListener);
    }

    // Update image....
    public void updateImage(View view) {
        chooseImage();

    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            final Uri uri = data.getData();

            if (uri != null) {

                StorageReference fileRef = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(uri));

                fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(AccountActivity.this, "uploaded", Toast.LENGTH_SHORT).show();
                        Glide.with(getApplicationContext()).load(uri).into(imageView);

                        delPrevImage();


                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();

                        String uploadId = upload.getKey();
                        String name = upload.getName();
                        Upload upload = new Upload(name, downloadUrl.toString());
                        mRef.child(uploadId).setValue(upload);
                    }

                });

            }


        }

    }

    private void delPrevImage() {
        FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AccountActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private String getFileExtension(Uri uri) {

        ContentResolver cR = getContentResolver();

        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


}
