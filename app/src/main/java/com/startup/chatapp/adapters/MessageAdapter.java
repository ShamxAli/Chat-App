package com.startup.chatapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.startup.chatapp.R;
import com.startup.chatapp.model.MessageModelClass;


import java.io.IOException;
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
    private static final int VIEW_IMG_SEND=3;
    private static final int VIEW_IMG_REC=4;
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
            if(message.getType()==1){
                return VIEW_IMG_SEND;
            }else {
                return VIEW_TYPE_MESSAGE_SENT;
            }
        } else {
            // If some other user sent the message
            if(message.getType()==1){
                return VIEW_IMG_REC;
            }else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
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
        }else if(viewType== VIEW_IMG_REC){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reciever_image_layout, parent, false);
            return new MessageViewHolder(view);
        }else if(viewType==VIEW_IMG_SEND){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_image_layout, parent, false);
            return new MessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        MessageModelClass messageModelClass = msgList.get(position);
        if(messageModelClass.getType()==0) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(messageModelClass.getTimestamp() * 1000L);
//        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss aa", cal).toString();
            String date = DateFormat.format("hh:mm aa", cal).toString();

            holder.msg.setText(messageModelClass.getMsg());
            holder.time.setText(date);
        }else if(messageModelClass.getType()==1){
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(messageModelClass.getTimestamp() * 1000L);
//        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss aa", cal).toString();
            String date = DateFormat.format("hh:mm aa", cal).toString();
            Glide.with(context)
                    .asBitmap()
                    .load(messageModelClass.getMsg())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Bitmap resized = ThumbnailUtils.extractThumbnail(resource, 250, 250);
                            Log.d("imagesize", "onResourceReady: "+resized.getByteCount());
                            holder.imageView.setImageBitmap(resized);
                        }
                    });
            holder.time.setText(date);
        }



    }



    @Override
    public int getItemCount() {
        return msgList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView time, msg;
        ImageView imageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.showmsg);
            time = itemView.findViewById(R.id.datetime);
            imageView= itemView.findViewById(R.id.image);
        }
    }


}
