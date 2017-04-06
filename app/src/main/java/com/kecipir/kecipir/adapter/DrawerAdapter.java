package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kecipir.kecipir.R;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.MenuDrawer;

import java.util.List;

/**
 * Created by Albani on 10/19/2015.
 */
public class DrawerAdapter  extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder>{

    List<MenuDrawer> data;
    private LayoutInflater inflater;
    private Context context;

    ClickListener clickListener;

    public DrawerAdapter(Context context, List<MenuDrawer> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public DrawerAdapter.DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.custom_drawer, parent,false);
        DrawerViewHolder holder = new DrawerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DrawerAdapter.DrawerViewHolder holder, int position) {
        MenuDrawer current = data.get(position);
        holder.imageView.setImageResource(current.icon);
        holder.textList.setText(current.title);
        if (current.badge == 0) {
            holder.badgeList.setVisibility(View.INVISIBLE);
        }
        else {
            holder.badgeList.setText("(" + current.badge + ")");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener (ClickListener clickListener){
        this.clickListener  = clickListener;
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textList, badgeList;
        ImageView imageView;
        public DrawerViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.image_drawer);
            textList = (TextView) itemView.findViewById(R.id.txt_drawer);
            badgeList = (TextView) itemView.findViewById(R.id.badge_drawer);
        }
        @Override
        public void onClick(View view) {
            if (clickListener!=null){
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}
