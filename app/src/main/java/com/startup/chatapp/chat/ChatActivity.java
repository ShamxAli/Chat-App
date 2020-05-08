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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.startup.chatapp.R;
import com.startup.chatapp.adapters.MessageAdapter;
import com.startup.chatapp.model.RecentChatsModel;
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
    String otheruid;
    RecentChatsModel recentChatsModel;
    String number;
    boolean flag;
    RecentChatsModel intentObj;

    @Override
    protected void onStart() {
        super.onStart();
        checkIfAlreadyExits();
        flag=false;
    }
    String opt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        text=findViewById(R.id.text_send);
        messageModelClass=new MessageModelClass();
        recentChatsModel =new RecentChatsModel();
        uid=FirebaseAuth.getInstance().getUid();
        opt=getIntent().getStringExtra("opt");
        if(opt.equals("ContactActivity")) {
            msgUid = getIntent().getStringExtra("msgUid");
            otheruid = getIntent().getStringExtra("otheruid");
            number = getIntent().getStringExtra("number");
        }else{
            intentObj= (RecentChatsModel) getIntent().getSerializableExtra("obj");
            msgUid=intentObj.getCombined_uid();
            otheruid=intentObj.getUser2_uid();
            number=intentObj.getPhone();
        }
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

    RecentChatsModel recentChatsModelAlreadyExists;
    List<RecentChatsModel> recentList =new ArrayList<>();
    public void checkIfAlreadyExits(){
        FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(FirebaseAuth.getInstance().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recentChatsModelAlreadyExists = dataSnapshot.getValue(RecentChatsModel.class);
                recentList.add(recentChatsModelAlreadyExists);
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
        for (int i = 0; i < recentList.size(); i++) {
           if(recentList.get(i).getUser2_uid().equals(otheruid)){
               push1= recentList.get(i).getUser1_pushid();
               push2= recentList.get(i).getUser2_pushid();
               flag=true;
           }
        }
        if (flag == false) {
            // create individual list
            Log.d("LOLLL", "makeChatBox: "+msgUid);

            recentChatsModel.setUser1_pushid(mypush);
            recentChatsModel.setUser2_pushid(otherpush);
            recentChatsModel.setUser2_uid(otheruid);
            recentChatsModel.setTimestamp(System.currentTimeMillis()/1000);
            recentChatsModel.setLastMsg(msgtext);
            recentChatsModel.setPhone(number);
            recentChatsModel.setCombined_uid(msgUid);
            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(uid).child(mypush).setValue(recentChatsModel);

            recentChatsModel.setCombined_uid(msgUid);
            recentChatsModel.setUser1_pushid(otherpush);
            recentChatsModel.setUser2_pushid(mypush);
            recentChatsModel.setPhone(mynumber);
            recentChatsModel.setUser2_uid(uid);
            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(otheruid).child(otherpush).setValue(recentChatsModel);

        } else {

            recentChatsModel.setCombined_uid(msgUid);
            recentChatsModel.setUser1_pushid(push1);
            recentChatsModel.setUser2_pushid(push2);
            recentChatsModel.setUser2_uid(otheruid);
            recentChatsModel.setTimestamp(System.currentTimeMillis()/1000);
            recentChatsModel.setLastMsg(msgtext);
            recentChatsModel.setPhone(number);
            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(uid).child(push1).setValue(recentChatsModel);
            recentChatsModel.setCombined_uid(otheruid);
            recentChatsModel.setUser1_pushid(push2);
            recentChatsModel.setUser2_pushid(push1);
            recentChatsModel.setPhone(mynumber);
            recentChatsModel.setUser2_uid(uid);
            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(otheruid).child(push2).setValue(recentChatsModel);

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
