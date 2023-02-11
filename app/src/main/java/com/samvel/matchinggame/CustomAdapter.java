package com.samvel.matchinggame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList user_id, user_username, user_email, user_password, user_bestscore;

    CustomAdapter (Context context, ArrayList user_id, ArrayList user_username, ArrayList user_email, ArrayList user_password, ArrayList user_bestscore){
        this.context = context;
        this.user_id = user_id;
        this.user_username = user_username;
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_bestscore = user_bestscore;
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        holder.user_id_txt.setText(String.valueOf(user_id.get(position)));
        holder.user_username_txt.setText(String.valueOf(user_username.get(position)));
        holder.user_email_txt.setText(String.valueOf(user_email.get(position)));
        holder.user_password_txt.setText(String.valueOf(user_password.get(position)));
        holder.user_bestscore_txt.setText(String.valueOf(user_bestscore.get(position)));
    }

    @Override
    public int getItemCount() {
        return user_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_id_txt, user_username_txt, user_email_txt, user_password_txt, user_bestscore_txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_id_txt = itemView.findViewById(R.id.book_id_txt);
            user_username_txt = itemView.findViewById(R.id.book_title_txt);
            user_email_txt = itemView.findViewById(R.id.book_author_txt);
            user_password_txt = itemView.findViewById(R.id.book_password_txt);
            user_bestscore_txt = itemView.findViewById(R.id.book_pages_txt);
        }
    }
}
