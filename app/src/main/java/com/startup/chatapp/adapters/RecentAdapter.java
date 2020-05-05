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
import com.startup.chatapp.model.IndChatList;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    Context context;
    List<IndChatList> indChatListList;
    OnItemClick onItemClick;

    public RecentAdapter(Context context, List<IndChatList> indChatListList, OnItemClick onItemClick) {
        this.context = context;
        this.indChatListList = indChatListList;
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
        IndChatList indChatList = indChatListList.get(position);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(indChatList.getTimestamp() * 1000L);
        String date = DateFormat.format("hh:mm aa", cal).toString();

        holder.name.setText(indChatList.getName());
        holder.lastmsg.setText(indChatList.getLastmsg());
        holder.timestamp.setText(date);
    }

    @Override
    public int getItemCount() {
        return indChatListList.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name,lastmsg,timestamp;
        OnItemClick onItemClick;


        public RecentViewHolder(@NonNull View itemView,OnItemClick onItemClick) {
            super(itemView);
            name=itemView.findViewById(R.id.tvName);
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

    public interface OnItemClick{
        void ItemClick(int position);
    }
}
