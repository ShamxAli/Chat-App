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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startup.chatapp.adapters.ContactAdapter;
import com.startup.chatapp.chat.ChatActivity;
import com.startup.chatapp.model.ContactsModel;
import com.startup.chatapp.model.Person;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity implements ContactAdapter.ItemOnClickListener {

    /*Variable Initialization ====== */
    TextView textView;
    RecyclerView recyclerView;
    EditText editText;
    Context context;

    ContactAdapter contactAdapter;
    ArrayList<ContactsModel> arrayList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        /*Run All Operations*/

        initViews();

        readContacts();

        searchMethod();


        /*Show number of contacts*/
        textView.setText("Number of contacts " + arrayList.size());
        FirebaseDatabase.getInstance().getReference("Users").keepSynced(true);


    }


    /*Permission Code = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = */


    private void isAllowed() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
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

        /*
            if index[0] == 0
                remove 0;
                concat num with +92
            else if index[0] == "3"
                concat num with +92
        */
            if (number.substring(0, 1).contains("0")) {
                number = number.substring(1);
                number = "+92" + number;
            } else if (number.substring(0, 1).contains("3")) {
                number = "+92" + number;
            }
            arrayList.add(new ContactsModel(name, number,""));
        }



        /*Remove Duplication & Check in logcat*/
        Log.d("duplicate", "readContacts: " + arrayList.size());

        LinkedHashSet<ContactsModel> hashSet = new LinkedHashSet<>(arrayList);

        arrayList = new ArrayList<>(hashSet);

        Log.d("duplicate", "readContacts: " + arrayList.size());

        getAllUsersFromFirebase();
    }


    ArrayList<ContactsModel> contactList = new ArrayList<>();

    public void getAllUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Person person = child.getValue(Person.class);
                    // comparing phone numbers
                    for (ContactsModel contactsModel : arrayList) {
                        if (person.getPhoneNumber().equals(contactsModel.getContactNumber())) {
                            contactList.add(new ContactsModel(contactsModel.getContactName(), contactsModel.getContactNumber(),person.getUid()));
                        }
                    }
                }

                LinkedHashSet<ContactsModel> hashSet = new LinkedHashSet<>(contactList);
                contactList.clear();
                contactList = new ArrayList<>(hashSet);

                bakeRecyclerView(contactList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Searching Method======================================================================================*/
    public void searchMethod() {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());
            }
        });
    }


    /*Searching filter====================*/
    public void filter(String text) {

        // New ArrayList...
        ArrayList<ContactsModel> filteredList = new ArrayList<>();

        for (ContactsModel items : arrayList) {
            if (items.getContactName().toLowerCase().contains(text.toLowerCase()) ||
                    items.getContactNumber().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(items);
            }
        }

        // passing filtered list to adapter
        contactAdapter.filter(filteredList);

    }


    /*Views Initialization===========================================================*/
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        textView = findViewById(R.id.tv_numOfContacts);
        editText = findViewById(R.id.edit_search);
    }


    /*Bake RecyclerView==============================================================*/
    private void bakeRecyclerView(ArrayList<ContactsModel> contactsModels) {

        contactAdapter = new ContactAdapter(contactsModels, context,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);
    }


    String p_user, c_user;

    public String getChatUid(int postion) {
        c_user = FirebaseAuth.getInstance().getUid();
        p_user = contactList.get(postion).getUid();
        return setOnetoOneChat(c_user, p_user);
    }

    public String setOnetoOneChat(String uid1, String uid2) {
        char f1;
        char f2;
        int cf1, cf2;
        int length1 = uid1.length();
        int length2 = uid2.length();
        if (length1 < length2) {
            return uid1 + uid2;
        } else if (length1 == length2) {
            for (int i = 0; i < uid1.length(); i++) {
                f1 = uid1.charAt(i);
                f2 = uid2.charAt(i);
                cf1 = (int) f1;
                cf2 = (int) f2;
                if (cf1 < cf2) {
                    return uid1 + uid2;
                } else if (cf1 > cf2) {
                    return uid2 + uid1;
                } else {

                }
            }
        } else {
            return uid2 + uid1;
        }
        return "Error";
    }


    @Override
    public void onItemClick(int position) {
        String msgUid=getChatUid(position);
        if(msgUid.equals("Error")){
            Toast.makeText(context, "You connot msg yourself", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent=new Intent(this, ChatActivity.class);
            intent.putExtra("msgUid",msgUid);
            startActivity(intent);
        }
    }
}
