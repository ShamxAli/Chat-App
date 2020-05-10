package com.startup.chatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.chatapp.R;
import com.startup.chatapp.model.ContactsModel;

import java.util.ArrayList;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private ArrayList<ContactsModel> data;
    private Context context;
    private ItemOnClickListener itemOnClickListener;

    public ContactAdapter(ArrayList<ContactsModel> data, Context context, ItemOnClickListener itemOnClickListener) {
        this.data = data;
        this.context = context;
        this.itemOnClickListener = itemOnClickListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.contact_design, parent, false);
        return new MyViewHolder(view, itemOnClickListener);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ContactsModel contactsModel = data.get(position);

        holder.tv_name.setText(contactsModel.getContactName());
        holder.tv_num.setText(contactsModel.getContactNumber());



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tv_name;
        TextView tv_num;
        ImageView imageView;
        ItemOnClickListener itemOnClickListener;

        MyViewHolder(@NonNull View itemView, ItemOnClickListener itemOnClickListener) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_num = itemView.findViewById(R.id.tv_number);
            imageView = itemView.findViewById(R.id.contacts_imageView);
            this.itemOnClickListener = itemOnClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemOnClickListener.onItemClick(getAdapterPosition());
        }
    }

    /*Filter*/
    public void filter(ArrayList<ContactsModel> newList) {
        data = newList;
        notifyDataSetChanged();
    }

    public interface ItemOnClickListener {
        public void onItemClick(int position);
    }
}
