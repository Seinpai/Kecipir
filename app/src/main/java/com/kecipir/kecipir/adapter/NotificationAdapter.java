package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.HistoryDetail;
import com.kecipir.kecipir.data.Notification;

import java.util.List;

/**
 * Created by Kecipir-Dev on 10/10/2016.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    List<Notification> data;
    private LayoutInflater inflater;
    private Context context;

    String nama;
    ClickListener clickListener;


    public NotificationAdapter(Context context, List<Notification> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_notification, parent, false);
        NotificationAdapter.NotificationViewHolder holder = new NotificationAdapter.NotificationViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.NotificationViewHolder holder, int position) {
        Notification current = data.get(position);

        holder.txtTitle.setText(current.getTitle());
        holder.txtPostDate.setText(current.getPostDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTitle, txtPostDate;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtPostDate = (TextView) itemView.findViewById(R.id.txt_postdate);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}
