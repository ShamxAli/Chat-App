package com.startup.chatapp.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.firebase.auth.FirebaseAuth;
import com.startup.chatapp.R;
import com.startup.chatapp.model.MessageModelClass;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    public static final long FADE_DURATION = 2000;
    private Context context;
    private List<MessageModelClass> msgList;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private String uid;

    public MessageAdapter(Context context, List<MessageModelClass> msgList) {
        this.msgList = msgList;
        this.context = context;
        uid = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public int getItemViewType(int position) {
        MessageModelClass message = msgList.get(position);

        if (message.getUid().equals(uid)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_message_layout, parent, false);
            return new MessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reciever_message_layout, parent, false);
            return new MessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModelClass messageModelClass = msgList.get(position);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(messageModelClass.getTimestamp() * 1000L);
//        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss aa", cal).toString();
        String date = DateFormat.format("hh:mm aa", cal).toString();

        holder.msg.setText(messageModelClass.getMsg());
        holder.time.setText(date);

    }


    @Override
    public int getItemCount() {
        return msgList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView time, msg;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.showmsg);
            time = itemView.findViewById(R.id.datetime);
        }
    }


}
