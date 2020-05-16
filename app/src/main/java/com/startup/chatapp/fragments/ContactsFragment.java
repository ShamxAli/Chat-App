package com.startup.chatapp.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.media.Session2Command;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startup.chatapp.HomeActivity;
import com.startup.chatapp.R;
import com.startup.chatapp.adapters.ContactAdapter;
import com.startup.chatapp.chat.ChatActivity;
import com.startup.chatapp.model.ContactsModel;
import com.startup.chatapp.model.Person;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ContactsFragment extends Fragment implements ContactAdapter.ItemOnClickListener {

    /*Variable Initialization ====== */
    RecyclerView recyclerView;

    ContactAdapter contactAdapter;
    ArrayList<ContactsModel> arrayList = new ArrayList<>();


    public ContactsFragment() {

    }

    // views init...
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        readContacts();

        // enable offline capability...
        FirebaseDatabase.getInstance().getReference("Users").keepSynced(true);
        return view;
    }




    /*Read Contacts ======================================================================================================*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void readContacts() {


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
                    // comparing number numbers
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


    /*Bake RecyclerView==============================================================*/
    private void bakeRecyclerView(ArrayList<ContactsModel> contactsModels) {

        contactAdapter = new ContactAdapter(contactsModels, getActivity(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
//        if (msgUid.equals("Error")) {
//            Toast.makeText(context, "You connot msg yourself", Toast.LENGTH_SHORT).show();
//        } else {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("msgUid", msgUid);
        intent.putExtra("user2_uid", user2_uid);
        intent.putExtra("user2_number", contactList.get(position).getContactNumber());
        intent.putExtra("key", "ContactActivity");

        // === For activity result ===
        startActivityForResult(intent, 2002);
//        }
    }



    // --- When user comes back directly goes to Chats Fragment ---
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2002 && resultCode == 9) {
            ((HomeActivity) getActivity()).setCurrentItem(0, false);
        }
    }
}
