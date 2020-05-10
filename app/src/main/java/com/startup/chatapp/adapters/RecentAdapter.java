package com.startup.chatapp.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.chatapp.R;
import com.startup.chatapp.model.RecentChatsModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    Context context;
    private List<RecentChatsModel> recentChatList;
    private OnItemClick onItemClick;

    public RecentAdapter(Context context, List<RecentChatsModel> recentChatList, OnItemClick onItemClick) {
        this.context = context;
        this.recentChatList = recentChatList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_view_item, parent, false);
        return new RecentViewHolder(view,onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        RecentChatsModel recentChatsModel = recentChatList.get(position);
        // timestamp---
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(recentChatsModel.getTimestamp() * 1000L);
        String date = DateFormat.format("hh:mm aa", cal).toString();


        holder.name.setText(recentChatsModel.getName());
        holder.lastmsg.setText(recentChatsModel.getLastMsg());
        holder.timestamp.setText(date);
    }

    @Override
    public int getItemCount() {
        return recentChatList.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name,lastmsg,timestamp;
        OnItemClick onItemClick;


        public RecentViewHolder(@NonNull View itemView,OnItemClick onItemClick) {
            super(itemView);
            name=itemView.findViewById(R.id.tv_name);
            lastmsg=itemView.findViewById(R.id.tvLastMsg);
            timestamp=itemView.findViewById(R.id.tvTimeStamp);
            this.onItemClick=onItemClick;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemClick.ItemClick(getAdapterPosition());
        }
    }

    // onClick Interface
    public interface OnItemClick{
        void ItemClick(int position);
    }
}
