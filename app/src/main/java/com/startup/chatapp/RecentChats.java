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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    ArrayList<ContactsModel> arrayList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        fab = findViewById(R.id.fab);
        recyclerView=findViewById(R.id.recyclerview_recent);
        context=this;

        readContacts();
    }

    public void fabClick(View view) {
        Intent intent = new Intent(RecentChats.this , MainActivity.class);
        startActivity(intent);
    }


    private void isAllowed() {

        if (ContextCompat.checkSelfPermission(RecentChats.this, Manifest.permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(RecentChats.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            askForPermission();
        }
    }

    /*Ask if not allowed*/
    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, 1);

        }
    }

    /*Permission Results*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 & grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
        }

    }
    private void bakeRecyclerView(ArrayList<RecentChatsModel> recentChatsModels) {

        recentAdapter = new RecentAdapter(context, recentChatsModels,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recentAdapter);
    }


    /*Read Contacts ======================================================================================================*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void readContacts() {

        isAllowed();

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
            if(number.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){

            }else {
                arrayList.add(new ContactsModel(name, number, ""));
            }
        }



        /*Remove Duplication & Check in logcat*/
        Log.d("duplicate", "readContacts: " + arrayList.size());

        LinkedHashSet<ContactsModel> hashSet = new LinkedHashSet<>(arrayList);

        arrayList = new ArrayList<>(hashSet);

        Log.d("duplicate", "readContacts: " + arrayList.size());

        getAllUsersFromFirebase();
    }


    ArrayList<RecentChatsModel> recentChatsModels = new ArrayList<>();

    public void getAllUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference("RecentChatsModel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    for(DataSnapshot grandchild: child.getChildren()) {
                        RecentChatsModel recentChatsModel = grandchild.getValue(RecentChatsModel.class);
                        Log.d("MyError", "onDataChange: " + recentChatsModel.getPhone());
                        // comparing phone numbers
                        for (ContactsModel contactsModel : arrayList) {
                            if (recentChatsModel.getPhone().equals(contactsModel.getContactNumber())) {
                                recentChatsModel.setName(contactsModel.getContactName());
                                recentChatsModels.add(recentChatsModel);
                            }
                        }
                    }
                }

                LinkedHashSet<RecentChatsModel> hashSet = new LinkedHashSet<>(recentChatsModels);
                recentChatsModels.clear();
                recentChatsModels = new ArrayList<>(hashSet);

                bakeRecyclerView(recentChatsModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void ItemClick(int position) {
        RecentChatsModel recentChatsModel = recentChatsModels.get(position);
        Intent intent=new Intent(RecentChats.this, ChatActivity.class);
        intent.putExtra("obj", recentChatsModel);
        intent.putExtra("opt","RecentActivity");
        startActivity(intent);
    }
}
