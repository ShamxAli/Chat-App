package com.startup.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.startup.chatapp.adapters.RecentAdapter;
import com.startup.chatapp.chat.ChatActivity;
import com.startup.chatapp.model.ContactsModel;
import com.startup.chatapp.model.RecentChatsModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;


public class RecentChats extends AppCompatActivity implements RecentAdapter.OnItemClick {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    RecentAdapter recentAdapter;
    Context context;
    TextView tvFirstInfo;

    ArrayList<ContactsModel> arrayList = new ArrayList<>();
    ArrayList<RecentChatsModel> recentChatsArrayList = new ArrayList<>();
    ValueEventListener mListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);
        context = this;
        initViews();
        Log.d("TAGTAG", "onCreate: called");

        // Read contacts
        readContacts();


        // Offline capability...
        FirebaseDatabase.getInstance().getReference("RecentChatsModel").
                child(FirebaseAuth.getInstance().
                        getCurrentUser().getUid())
                .keepSynced(true);


        // Observing chats with LISTENER...
        observingRecentChats();


        //----------------------------------------------------------------------------------
        // REGISTER LISTENER
        FirebaseDatabase.getInstance().getReference("RecentChatsModel").
                child(FirebaseAuth.getInstance().
                        getCurrentUser().getUid()).addValueEventListener(mListener);


        LinkedHashSet<RecentChatsModel> hashSet = new LinkedHashSet<>(recentChatsArrayList);
        recentChatsArrayList.clear();
        recentChatsArrayList = new ArrayList<>(hashSet);
        bakeRecyclerView(recentChatsArrayList);
    }


    private void observingRecentChats() {
        // VALUE EVENT LISTENER----------------------------------------------------------------------------------
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAGTAG", "onDataChange: added called");
                // Clearing the list..
                recentChatsArrayList.clear();
                if (dataSnapshot != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
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
                    recentAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


    // UNREGISTER LISTENER
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference("RecentChatsModel").removeEventListener(mListener);
    }


    /* Single Item click listener... GO TO CHAT ACTIVITY*/
    @Override
    public void ItemClick(int position) {
        RecentChatsModel recentChatsModel = recentChatsArrayList.get(position);
        Intent intent = new Intent(RecentChats.this, ChatActivity.class);
        intent.putExtra("object", recentChatsModel);
        intent.putExtra("key", "RecentActivity");
        startActivity(intent);
    }


    // initViews
    private void initViews() {
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerview_recent);
        tvFirstInfo = findViewById(R.id.tv_first_info);
    }

    // Fab icon click...
    public void fabClick(View view) {
        Intent intent = new Intent(RecentChats.this, MainActivity.class);
        startActivity(intent);
    }

    // bake recyclerView
    private void bakeRecyclerView(ArrayList<RecentChatsModel> recentChatsModels) {
        recentAdapter = new RecentAdapter(context, recentChatsModels, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recentAdapter);
    }

    /*Read Contacts =======================*/

    public void readContacts() {

        // Permission checking...
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            isAllowed();
//        }


        // Reading...
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
    // *****


    /*Permissions ============================*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void isAllowed() {
        if (ContextCompat.checkSelfPermission(RecentChats.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

    }

    // Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
        }

    }
    // *****


}
