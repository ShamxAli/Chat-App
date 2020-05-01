package com.startup.chatapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.startup.chatapp.R;
import com.startup.chatapp.adapters.MessageAdapter;
import com.startup.chatapp.model.MessageModelClass;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String msgUid;
    EditText text;
    MessageModelClass messageModelClass;
    String push;
    String uid;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<MessageModelClass> msgList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        text=findViewById(R.id.text_send);
        messageModelClass=new MessageModelClass();
        uid=FirebaseAuth.getInstance().getUid();
        msgUid=getIntent().getStringExtra("msgUid");
        recyclerView=findViewById(R.id.msg_recyclerView);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(manager);
        messageAdapter = new MessageAdapter(this, msgList);
        recyclerView.setAdapter(messageAdapter);
        getLiveMessagesFromFirebaseDatabase();
    }

    public void sendMessage(View view) {
        String msgtext=text.getText().toString();
        if (msgtext.equals("")){

        }else{
            push= FirebaseDatabase.getInstance().getReference().push().getKey();
            Log.d("lolll", "sendMessage: "+push);
            messageModelClass.setMsg(msgtext);
            messageModelClass.setTimestamp(System.currentTimeMillis()/1000);
            messageModelClass.setMsgId(push);
            messageModelClass.setUid(uid);
            Log.d("lolll", "sendMessage: "+messageModelClass.getUid()+" "+messageModelClass.getTimestamp()+" "+messageModelClass.getMsgId()+messageModelClass.getTimestamp()+" "+push);
            FirebaseDatabase.getInstance().getReference().child("ChatSystem").child(msgUid).child(push).setValue(messageModelClass);
            text.setText("");
        }
    }

    public void getLiveMessagesFromFirebaseDatabase(){
        FirebaseDatabase.getInstance().getReference().child("ChatSystem").child(msgUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("msg").getValue() != null) {
                        MessageModelClass message = dataSnapshot.getValue(MessageModelClass.class);
                        msgList.add(message);
                        recyclerView.scrollToPosition(msgList.size() - 1);
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
