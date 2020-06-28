package com.startup.chatapp.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startup.chatapp.R;
import com.startup.chatapp.adapters.MessageAdapter;
import com.startup.chatapp.model.Person;
import com.startup.chatapp.model.RecentChatsModel;
import com.startup.chatapp.model.MessageModelClass;
import com.startup.chatapp.model.Upload;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    // Widgets
    EditText text;
    View backView;
    // Toolbar name and image
    TextView textView;
    ImageView imageView;
    // Recycler View
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    // Global variables
    public static String user1_number;
    public static String user2_number;
    String comingFrom;
    // to store...
    private String msgUid;
    String user2_uid;
    String push;
    String user1_uid;
    String title, token;
    String user2_name;

    String user1_pushid, user2_pushid;

    // ============== FOR BACK STACK ===================


    // arraylist
    List<MessageModelClass> msgList = new ArrayList<>();

    // models
    MessageModelClass messageModelClass;
    RecentChatsModel recentChatsModel;
    // coming from recentchats
    RecentChatsModel intentObj;
    private String fcmUrl = "https://fcm.googleapis.com/fcm/send";
    public static boolean flag = false;

    // onStart()...
    @Override
    protected void onStart() {
        super.onStart();
        checkIfRecentChatAlreadyExist();
        getUserToken(); // get user token for notifications ...
        flag = true; // flag for notifications ...
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false; // for notifications
    }

    public void getUserToken() {
        FirebaseDatabase.getInstance().getReference("Users").child(user1_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person value = dataSnapshot.getValue(Person.class);
                token = value.getToken();
                title = user1_number;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // onCreate...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // if coming from Contacts activity
        user1_uid = FirebaseAuth.getInstance().getUid();
        comingFrom = getIntent().getStringExtra("key");
        if (comingFrom.equals("ContactActivity")) {
            msgUid = getIntent().getStringExtra("msgUid");
            user2_uid = getIntent().getStringExtra("user2_uid");
            user2_number = getIntent().getStringExtra("user2_number");
            user2_name = getIntent().getStringExtra("user2_name");
        }

        // if coming from recent chats
        else {
            intentObj = (RecentChatsModel) getIntent().getSerializableExtra("object");
            msgUid = intentObj.getCombined_uid();
            user2_uid = intentObj.getUser2_uid();
            user2_number = intentObj.getPhone();
            user2_name = intentObj.getName();
        }


        user1_pushid = FirebaseDatabase.getInstance().getReference().push().getKey();
        user2_pushid = FirebaseDatabase.getInstance().getReference().push().getKey();


        // recyclerView
        messageAdapter = new MessageAdapter(this, msgList);
        recyclerView.setAdapter(messageAdapter);
        //
        user1_number = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        // Get all messages in onCreate();
        getLiveMessagesFromFirebaseDatabase();


        // Get image and name in chat

        FirebaseDatabase.getInstance().getReference("uploads").child(user2_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Upload upload = dataSnapshot.getValue(Upload.class);
                textView.setText(user2_name);
                Glide.with(ChatActivity.this).load(upload.getUrl()).centerCrop().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /*Send msgs*/
    public void sendMessage(View view) throws JSONException {
        String msgtext = text.getText().toString();
        if (msgtext.equals("")) {

        } else {

            MediaPlayer mediaPlayer = MediaPlayer.create(ChatActivity.this, R.raw.voicesend);
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
            //msg sending code
            // push id under each combined uid(will)
            push = FirebaseDatabase.getInstance().getReference().push().getKey();

            messageModelClass.setMsg(msgtext);
            messageModelClass.setTimestamp(System.currentTimeMillis() / 1000);
            messageModelClass.setMsgId(push);
            messageModelClass.setUid(user1_uid);
            FirebaseDatabase.getInstance().getReference().child("ChatSystem").child(msgUid).child(push).setValue(messageModelClass);
            text.setText("");
            // make recent chats
            makeRecentChats(msgtext);
            /*Sound when message sent...*/

            sendNotifications(title, msgtext, token);
        }
    }


    /*Getting chat messages from firebase*/
    public void getLiveMessagesFromFirebaseDatabase() {
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


    // InitViews...
    private void initViews() {
        text = findViewById(R.id.text_send);
        recyclerView = findViewById(R.id.msg_recyclerView);
        recyclerView.setHasFixedSize(true);
        messageModelClass = new MessageModelClass();
        recentChatsModel = new RecentChatsModel();
        backView = findViewById(R.id.back_press_img);
        textView = findViewById(R.id.ca_user_name);
        imageView = findViewById(R.id.ca_profile_image);

    }




    /*Recent Chats Integeration ========================================================================================================*/

    RecentChatsModel recentChatsModelTemp;
    List<RecentChatsModel> recentList = new ArrayList<>();


    // Getting user1 recent chats and compare for updating purpose (below...)

    // TODO ...
    public void checkIfRecentChatAlreadyExist() {
        FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").
                child(FirebaseAuth.getInstance().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                recentChatsModelTemp = dataSnapshot.getValue(RecentChatsModel.class);
                recentList.add(recentChatsModelTemp);
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


    // TODO ...
    public void makeRecentChats(String msgtext) {
        boolean flag = false;

        String push1 = null, push2 = null;
        for (int position = 0; position < recentList.size(); position++) {
            if (recentList.get(position).getUser2_uid().equals(user2_uid)) {
                push1 = recentList.get(position).getUser1_pushid();
                push2 = recentList.get(position).getUser2_pushid();
                flag = true;
            }
        }
        // if user is not in recent chats...
        if (flag == false) {

            // user 1 recent chat...

            recentChatsModel.setLastMsg(msgtext);
            recentChatsModel.setTimestamp(System.currentTimeMillis() / 1000);
            recentChatsModel.setPhone(user2_number);

            recentChatsModel.setCombined_uid(msgUid);
            recentChatsModel.setUser1_pushid(user1_pushid);
            recentChatsModel.setUser2_pushid(user2_pushid);

            recentChatsModel.setUser1_uid(user1_uid);
            recentChatsModel.setUser2_uid(user2_uid);

            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(user1_uid).child(user1_pushid).setValue(recentChatsModel);


            // user 2 recent chat...

            recentChatsModel.setUser1_pushid(user2_pushid);
            recentChatsModel.setUser2_pushid(user1_pushid);
            recentChatsModel.setPhone(user1_number);
            recentChatsModel.setUser1_uid(user2_uid);
            recentChatsModel.setUser2_uid(user1_uid);
            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(user2_uid).child(user2_pushid).setValue(recentChatsModel);

        }
        // if user is already in recent chats...
        else {
            // user 1 recent chat

            recentChatsModel.setLastMsg(msgtext);
            recentChatsModel.setTimestamp(System.currentTimeMillis() / 1000);
            recentChatsModel.setPhone(user2_number);

            recentChatsModel.setCombined_uid(msgUid);
            recentChatsModel.setUser1_pushid(push1);
            recentChatsModel.setUser2_pushid(push2);

            recentChatsModel.setUser1_uid(user1_uid);
            recentChatsModel.setUser2_uid(user2_uid);
            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(user1_uid).child(push1).setValue(recentChatsModel);

            // user 2 recent chat

            recentChatsModel.setUser1_pushid(push2);
            recentChatsModel.setUser2_pushid(push1);
            recentChatsModel.setPhone(user1_number);
            recentChatsModel.setUser1_uid(user2_uid);
            recentChatsModel.setUser2_uid(user1_uid);
            FirebaseDatabase.getInstance().getReference().child("RecentChatsModel").child(user2_uid).child(push2).setValue(recentChatsModel);

        }
    }


    // When user(FROM CONTACTS Fragment) back press directly goes to Chats Fragment
    @Override
    public void onBackPressed() {
        setResult(9, getIntent());
        finish();
    }



    /*   ==========   Send Notification when send message   ==========*/

    public void sendNotifications(String title, String body, String token) throws JSONException {
        JSONObject jsonObject1 = new JSONObject(); // wrapper
        JSONObject jsonObject2 = new JSONObject(); // notification
        JSONObject jsonObject3 = new JSONObject(); // data


        jsonObject2.put("body", body); // notification
        jsonObject2.put("title", title);

        jsonObject3.put("number", user1_number); // data


        jsonObject1.put("to", token);
        jsonObject1.put("collapse_key", "type_a"); // wrapper 4...
        jsonObject1.put("notification", jsonObject2);
        jsonObject1.put("data", jsonObject3);


        RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, fcmUrl, jsonObject1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=AAAAbgd3ido:APA91bFklishbUBTll337_U3wP6vf75-Uv0wy5tacUinaPQBAdwgpJ1m3bCIKlTvP2lvKsNahQD2IatMZHnUt7meQFoxFNYMlOPJAQ7Dj-Zs6uIkReefiuz5LPIm9jPfSp0FNr_1A4jP");
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        queue.add(jsonObjectRequest);
        jsonObjectRequest.setShouldCache(false);
    }

    /* =========== show text and image ============== */

    // todo ...


}
