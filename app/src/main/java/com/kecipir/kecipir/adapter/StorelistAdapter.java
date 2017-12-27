package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.SessionManager;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.MenuDrawer;
import com.kecipir.kecipir.data.ModelClickListener;
import com.kecipir.kecipir.data.StoreList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albani on 10/20/2015.
 */
public class StorelistAdapter extends RecyclerView.Adapter<StorelistAdapter.StorelistViewHolder>{

    List<StoreList> data;
    private LayoutInflater inflater;
    private Context context;

    ModelClickListener clickListener;

    SessionManager sessionManager;

    public StorelistAdapter(Context context, List<StoreList> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public StorelistAdapter.StorelistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.custom_newstorelist, parent,false);
        StorelistViewHolder holder = new StorelistViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(StorelistAdapter.StorelistViewHolder holder, int position) {
        StoreList current = data.get(position);
        sessionManager = new SessionManager(context);
        holder.namaSayuran.setText(current.getNama());
        if (sessionManager.isLoggedIn()){
            holder.stock.setText("Sisa : \n"+current.getStock());
        }
        else {
            holder.stock.setText(" \n ");
        }
        holder.satuan.setText(""+current.getSatuan());
        holder.harga.setText("Rp. "+current.getHarga());
        holder.grade.setText("Grade "+current.getGrade());
        if (current.getGrade().equalsIgnoreCase("A")){
            holder.grade.setBackgroundColor(Color.parseColor("#70bc52"));
        }
        else if (current.getGrade().equalsIgnoreCase("B")){
            holder.grade.setBackgroundColor(Color.parseColor("#337ab7"));
        }
        else{
            holder.grade.setBackgroundColor(Color.parseColor("#ff9913"));
        }

        holder.petani.setText(current.getPetani());
        Glide.with(context).load(current.getImage()).centerCrop().into(holder.imageSayuran);
        if (current.isSale()){
            holder.imageSale.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageSale.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener (ModelClickListener clickListener){
        this.clickListener  = clickListener;
    }


    public void setFilter(List<StoreList> storeLists) {
        data = new ArrayList<>();
        data.addAll(storeLists);
        notifyDataSetChanged();
    }

    class StorelistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView namaSayuran;
        TextView tglPanen;
        TextView stock;
        TextView harga;
        TextView satuan;
        TextView grade;
        TextView petani;
        ImageView imageSayuran;
        ImageView imageSale;
        public StorelistViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            namaSayuran = (TextView) itemView.findViewById(R.id.storelist_nama_sayuran);
            satuan = (TextView) itemView.findViewById(R.id.storelist_satuan);
            stock = (TextView) itemView.findViewById(R.id.storelist_stock);
            harga = (TextView) itemView.findViewById(R.id.storelist_harga);
            imageSayuran = (ImageView) itemView.findViewById(R.id.storelist_image);
            grade = (TextView) itemView.findViewById(R.id.storelist_grade);
            petani = (TextView) itemView.findViewById(R.id.storelist_petani);
            imageSale = (ImageView) itemView.findViewById(R.id.image_sale);
        }
        @Override
        public void onClick(View view) {
            if (clickListener!=null){
                clickListener.itemClicked(data, view, getLayoutPosition());
            }
        }
    }
}
