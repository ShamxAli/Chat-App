package com.startup.chatapp.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.provider.ContactsContract;
import android.util.Log;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.startup.chatapp.HomeActivity;
import com.startup.chatapp.R;
import com.startup.chatapp.adapters.RecentAdapter;
import com.startup.chatapp.chat.ChatActivity;
import com.startup.chatapp.model.ContactsModel;
import com.startup.chatapp.model.MessageModelClass;
import com.startup.chatapp.model.RecentChatsModel;
import com.startup.chatapp.model.Upload;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.startup.chatapp.image_account.InfoActivity.REQUEST_CODE;


public class ChatFragment extends Fragment implements RecentAdapter.OnItemClick, View.OnClickListener {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    RecentAdapter recentAdapter;
    Context context;
    TextView tvFirstInfo;
    ArrayList<ContactsModel> arrayList = new ArrayList<>();
    ArrayList<RecentChatsModel> recentChatsArrayList = new ArrayList<>();
    ValueEventListener mListener;


    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        fab = view.findViewById(R.id.fab);

        recyclerView = view.findViewById(R.id.recyclerview_recent);
        tvFirstInfo = view.findViewById(R.id.tv_first_info);
        fab.setOnClickListener(this);


        // Read contacts
        readContacts();


        // Offline capability...
//        FirebaseDatabase.getInstance().getReference("RecentChatsModel").
//                child(FirebaseAuth.getInstance().
//                        getCurrentUser().getUid())
//                .keepSynced(true);


        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Observing chats with LISTENER...
        observingRecentChats();

        //----------------------------------------------------------------------------------
        // REGISTER LISTENER
        FirebaseDatabase.getInstance().getReference("RecentChatsModel").
                child(FirebaseAuth.getInstance().
                        getCurrentUser().getUid()).addValueEventListener(mListener);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        FirebaseDatabase.getInstance().getReference("RecentChatsModel").
                child(FirebaseAuth.getInstance().
                        getCurrentUser().getUid()).removeEventListener(mListener);
    }

    private void observingRecentChats() {
        // VALUE EVENT LISTENER----------------------------------------------------------------------------------
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("fuck", "fuck 000");
                // Clearing the list..
                recentChatsArrayList.clear();
                recyclerView.setVisibility(View.VISIBLE);

                // ...
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    RecentChatsModel recentChatsModel = child.getValue(RecentChatsModel.class);

                    // comparing number numbers

                    for (ContactsModel contactsModel : arrayList) {
                        if (recentChatsModel.getPhone().equals(contactsModel.getContactNumber())) {
                            recentChatsModel.setName(contactsModel.getContactName());
                            recentChatsArrayList.add(recentChatsModel);
                            tvFirstInfo.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                getImgUrl();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


    /* Single Item click listener... GO TO CHAT ACTIVITY*/
    @Override
    public void ItemClick(int position) {
        RecentChatsModel recentChatsModel = recentChatsArrayList.get(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("object", recentChatsModel);
        intent.putExtra("key", "RecentActivity");
        startActivity(intent);
    }


    // bake recyclerView
    private void bakeRecyclerView(ArrayList<RecentChatsModel> recentChatsModels) {
        context = getActivity();
        recentAdapter = new RecentAdapter(context, recentChatsModels, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recentAdapter);
    }


    /*Read Contacts =======================*/

    public void readContacts() {

        // Permission checking...
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            isAllowed();
//        }.


        // Reading...
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);


        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            /*Remove Duplication with Regexp*/

            number = number.replaceAll("\\s", "");
            number = number.replaceAll("-", "");
            number = number.replaceAll("\\(", "");
            number = number.replaceAll("\\)", "");


            if (number.substring(0, 1).contains("0")) {
                number = number.substring(1);
                number = "+92" + number;
            } else if (number.substring(0, 1).contains("3")) {
                number = "+92" + number;
            }
            if (number.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {

            } else {
                arrayList.add(new ContactsModel(name, number, ""));
            }
        }



        /*Remove Duplication & Check in logcat*/
        Log.d("duplicate", "readContacts: " + arrayList.size());

        LinkedHashSet<ContactsModel> hashSet = new LinkedHashSet<>(arrayList);

        arrayList = new ArrayList<>(hashSet);

        Log.d("duplicate", "readContacts: " + arrayList.size());

    }

    List<Upload> uploadList = new ArrayList<>();

    // *****
    public void getImgUrl() {
        uploadList.clear();
        FirebaseDatabase.getInstance().getReference().child("uploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("fuckkkk", "onDataChange: fuck");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Upload upload = child.getValue(Upload.class);
                    upload.setUid(child.getKey());
                    uploadList.add(upload);
                }

                Log.d("TAGLOL", "onDataChange: kndknk");
                for (int i = 0; i < uploadList.size(); i++) {
                    Log.d("TAGLOL", "onDataChange: " + uploadList.get(i).getUid());
                    for (int j = 0; j < recentChatsArrayList.size(); j++) {

                        if (recentChatsArrayList.get(j).getUser2_uid().equals(uploadList.get(i).getUid())) {
                            recentChatsArrayList.get(j).setImg_url(uploadList.get(i).getUrl());
                            Log.d("TAGLOL", "onDataChange: " + recentChatsArrayList.get(j).getName() + recentChatsArrayList.get(j).getImg_url());
                        }
                    }
                }
                LinkedHashSet<RecentChatsModel> hashSet = new LinkedHashSet<>(recentChatsArrayList);
                recentChatsArrayList.clear();
                recentChatsArrayList = new ArrayList<>(hashSet);
                Log.d("TAGTAG", "onActivityCreated: " + recentChatsArrayList.get(0).getImg_url());
                bakeRecyclerView(recentChatsArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    boolean f = true;
    /*Permissions ============================*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void isAllowed() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

    }

    // Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
        }

    }

    // fab onClick
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            ((HomeActivity) getActivity()).setCurrentItem(1, true);
        }
    }
    // *****
}
