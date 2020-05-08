package com.startup.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
        setTitle("Contacts");


        /*Run All Operations*/

        initViews();

        readContacts();

        searchMethod();


//        FirebaseDatabase.getInstance().getReference("Users").keepSynced(true);


    }


    /*Read Contacts ======================================================================================================*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void readContacts() {


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
            arrayList.add(new ContactsModel(name, number, ""));
        }



        /*Remove Duplication & Check in logcat*/
        Log.d("duplicate", "readContacts: " + arrayList.size());

        LinkedHashSet<ContactsModel> hashSet = new LinkedHashSet<>(arrayList);

        arrayList = new ArrayList<>(hashSet);

        Log.d("duplicate", "readContacts: " + arrayList.size());

        getAllUsersFromFirebase();
    }


    /*Compare firebase contacts with local contacts and add to list*/
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
                            contactList.add(new ContactsModel(contactsModel.getContactName(), contactsModel.getContactNumber(), person.getUid()));
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

        for (ContactsModel items : contactList) {
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
//        textView = findViewById(R.id.tv_numOfContacts);
        editText = findViewById(R.id.edit_search);
    }


    /*Bake RecyclerView==============================================================*/
    private void bakeRecyclerView(ArrayList<ContactsModel> contactsModels) {

        contactAdapter = new ContactAdapter(contactsModels, context, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);
    }


    /*Combine chat uId's...*/
    String user2_uid, user1_uid;

    public String getCombinedUid(int postion) {
        user1_uid = FirebaseAuth.getInstance().getUid();
        user2_uid = contactList.get(postion).getUid();
        return setOnetoOneChat(user1_uid, user2_uid);
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
        return "Error 1001";
    }


    /* Open Chat Activity -----*/
    @Override
    public void onItemClick(int position) {
        String msgUid = getCombinedUid(position);
        if (msgUid.equals("Error")) {
            Toast.makeText(context, "You connot msg yourself", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("msgUid", msgUid);
            intent.putExtra("user2_uid", user2_uid);
            intent.putExtra("user2_number", contactList.get(position).getContactNumber());
            intent.putExtra("key", "ContactActivity");
            startActivity(intent);
        }
    }
}
