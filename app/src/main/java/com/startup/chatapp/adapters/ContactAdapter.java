package com.startup.chatapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.chatapp.R;
import com.startup.chatapp.model.ContactsModel;

import java.util.ArrayList;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private ArrayList<ContactsModel> data;
    private Context context;

    public ContactAdapter(ArrayList<ContactsModel> data, Context context) {
        this.data = data;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.contact_design, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ContactsModel contactsModel = data.get(position);

        holder.tv_name.setText(contactsModel.getContactName());
        holder.tv_num.setText(contactsModel.getContactNumber());


        /*Onclick*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactsModel.getContactNumber()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tv_name;
        TextView tv_num;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_num = itemView.findViewById(R.id.tv_number);

        }
    }

    /*Filter*/
    public void filter(ArrayList<ContactsModel> newList) {
        data = newList;
        notifyDataSetChanged();
    }
}
