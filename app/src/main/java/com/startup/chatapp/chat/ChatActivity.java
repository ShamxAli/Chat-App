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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.startup.chatapp.R;
import com.startup.chatapp.adapters.MessageAdapter;
import com.startup.chatapp.model.IndChatList;
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
    String otheruid,myuid;
    IndChatList indChatList;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        text=findViewById(R.id.text_send);
        messageModelClass=new MessageModelClass();
        indChatList=new IndChatList();
        uid=FirebaseAuth.getInstance().getUid();
        msgUid=getIntent().getStringExtra("msgUid");
        otheruid=getIntent().getStringExtra("otheruid");
        myuid=getIntent().getStringExtra("myuid");
        number=getIntent().getStringExtra("number");
        mypush=FirebaseDatabase.getInstance().getReference().push().getKey();
        otherpush=FirebaseDatabase.getInstance().getReference().push().getKey();
        recyclerView=findViewById(R.id.msg_recyclerView);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(manager);
        messageAdapter = new MessageAdapter(this, msgList);
        recyclerView.setAdapter(messageAdapter);
        getNumberFromFirebase();
        getLiveMessagesFromFirebaseDatabase();
    }


    String mypush,otherpush;

    public void sendMessage(View view) {
        String msgtext=text.getText().toString();
        if (msgtext.equals("")){

        }else{
            //msg sending code
            push= FirebaseDatabase.getInstance().getReference().push().getKey();
            Log.d("lolll", "sendMessage: "+push);
            messageModelClass.setMsg(msgtext);
            messageModelClass.setTimestamp(System.currentTimeMillis()/1000);
            messageModelClass.setMsgId(push);
            messageModelClass.setUid(uid);
            Log.d("lolll", "sendMessage: "+messageModelClass.getUid()+" "+messageModelClass.getTimestamp()+" "+messageModelClass.getMsgId()+messageModelClass.getTimestamp()+" "+push);
            FirebaseDatabase.getInstance().getReference().child("ChatSystem").child(msgUid).child(push).setValue(messageModelClass);
            text.setText("");

            // create individual list
            indChatList.setMsguid(msgUid);
            indChatList.setMypushid(mypush);
            indChatList.setOtherpushid(otherpush);
            indChatList.setTimestamp(System.currentTimeMillis()/1000);
            indChatList.setLastmsg(msgtext);
            indChatList.setPhone(number);

            FirebaseDatabase.getInstance().getReference().child("IndChatList").child(uid).child(mypush).setValue(indChatList);

            indChatList.setMsguid(otheruid);
            indChatList.setMypushid(otherpush);
            indChatList.setOtherpushid(mypush);
            indChatList.setPhone(mynumber);

            FirebaseDatabase.getInstance().getReference().child("IndChatList").child(otheruid).child(mypush).setValue(indChatList);

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

    String mynumber;
    public void getNumberFromFirebase(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        mynumber=user.getPhoneNumber();
    }

}
