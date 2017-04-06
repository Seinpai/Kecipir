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
import com.kecipir.kecipir.data.MenuCategory;
import com.kecipir.kecipir.data.MenuDrawer;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Albani on 10/21/2015.
 */
public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.MenuCategoryViewHolder>{

    List<MenuCategory> data;
    private LayoutInflater inflater;
    private Context context;

    ClickListener clickListener;

    public MenuCategoryAdapter(Context context, List<MenuCategory> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MenuCategoryAdapter.MenuCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.custom_menucategory, parent,false);
        MenuCategoryViewHolder holder = new MenuCategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MenuCategoryAdapter.MenuCategoryViewHolder holder, int position) {
        MenuCategory current = data.get(position);
        holder.textList.setText(current.title);
        holder.imageList.setImageResource(current.icon);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener (ClickListener clickListener){
        this.clickListener  = clickListener;
    }

    class MenuCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textList;
        ImageView imageList;

        public MenuCategoryViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            textList = (TextView) itemView.findViewById(R.id.txt_category);
            imageList = (ImageView) itemView.findViewById(R.id.image_drawer);

        }
        @Override
        public void onClick(View view) {
            if (clickListener!=null){
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}
