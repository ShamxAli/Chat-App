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
    boolean flag;

    @Override
    protected void onStart() {
        super.onStart();
        checkIfAlreadyExits();
        flag=false;
    }

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
            makeChatBox(msgtext);
        }
    }

    IndChatList indChatListAlreadyExists;
    List<IndChatList> indChatListList=new ArrayList<>();
    public void checkIfAlreadyExits(){
        FirebaseDatabase.getInstance().getReference().child("IndChatList").child(FirebaseAuth.getInstance().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                indChatListAlreadyExists = dataSnapshot.getValue(IndChatList.class);
                indChatListList.add(indChatListAlreadyExists);
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

    public void makeChatBox(String msgtext) {
        boolean flag = false;
        String push1 = null,push2=null;
        for (int i = 0; i < indChatListList.size(); i++) {
           if(indChatListList.get(i).getOtheruid().equals(otheruid)){
               push1=indChatListList.get(i).getMypushid();
               push2=indChatListList.get(i).getOtherpushid();
               flag=true;
           }
        }
        if (flag == false) {
            // create individual list
            indChatList.setMsguid(msgUid);
            indChatList.setMypushid(mypush);
            indChatList.setOtherpushid(otherpush);
            indChatList.setOtheruid(otheruid);
            indChatList.setTimestamp(System.currentTimeMillis()/1000);
            indChatList.setLastmsg(msgtext);
            indChatList.setPhone(number);

            FirebaseDatabase.getInstance().getReference().child("IndChatList").child(uid).child(mypush).setValue(indChatList);

            indChatList.setMsguid(otheruid);
            indChatList.setMypushid(otherpush);
            indChatList.setOtherpushid(mypush);
            indChatList.setPhone(mynumber);
            indChatList.setOtheruid(uid);
            FirebaseDatabase.getInstance().getReference().child("IndChatList").child(otheruid).child(otherpush).setValue(indChatList);

        } else {

            indChatList.setMsguid(msgUid);
            indChatList.setMypushid(push1);
            indChatList.setOtherpushid(push2);
            indChatList.setOtheruid(otheruid);
            indChatList.setTimestamp(System.currentTimeMillis()/1000);
            indChatList.setLastmsg(msgtext);
            indChatList.setPhone(number);
            FirebaseDatabase.getInstance().getReference().child("IndChatList").child(uid).child(push1).setValue(indChatList);
            indChatList.setMsguid(otheruid);
            indChatList.setMypushid(push2);
            indChatList.setOtherpushid(push1);
            indChatList.setPhone(mynumber);
            indChatList.setOtheruid(uid);
            FirebaseDatabase.getInstance().getReference().child("IndChatList").child(otheruid).child(push2).setValue(indChatList);

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
