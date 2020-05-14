package com.startup.chatapp.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE = 1001;
    public static final String TAG = "TAK";
    private ImageView imageView;
    private TextView name, number;
    private DatabaseReference mRef;
    private ValueEventListener mListener;
    private StorageReference mStorageRef;
    private Upload upload;


    public AccountFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_account, container, false);


        imageView = view.findViewById(R.id.account_img);
        name = view.findViewById(R.id.account_name);
        number = view.findViewById(R.id.account_number);
        imageView.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");


        mRef = FirebaseDatabase.getInstance().getReference("uploads");

        mRef.keepSynced(true); // offline name and number
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    upload = snapshot.getValue(Upload.class);
                    upload.setKey(snapshot.getKey());
                    name.setText(upload.getName());
                    number.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    Glide.with(getActivity()).load(upload.getUrl()).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRef.addValueEventListener(mListener);


        return view;
    }


    // Update image....
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.account_img) {
            chooseImage();
        }
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            final Uri uri = data.getData();

            if (uri != null) {

                StorageReference fileRef = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(uri));

                fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getActivity(), "uploaded", Toast.LENGTH_SHORT).show();
                        Glide.with(getActivity()).load(uri).into(imageView);

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
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {

        ContentResolver cR = getActivity().getContentResolver();

        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


}
