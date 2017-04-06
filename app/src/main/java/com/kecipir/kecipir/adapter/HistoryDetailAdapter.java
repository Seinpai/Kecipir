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

import java.util.List;

/**
 * Created by Albani on 9/17/2015.
 */
public class HistoryDetailAdapter extends RecyclerView.Adapter<HistoryDetailAdapter.HistoryDetailViewHolder> {

    List<HistoryDetail> data;
    private LayoutInflater inflater;
    private Context context;

    String nama;
    ClickListener clickListener;


    public HistoryDetailAdapter(Context context, List<HistoryDetail> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public HistoryDetailAdapter.HistoryDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_dialog_history, parent, false);
        HistoryDetailViewHolder holder = new HistoryDetailViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HistoryDetailAdapter.HistoryDetailViewHolder holder, int position) {
        HistoryDetail current = data.get(position);

        Glide.with(context).load(current.getFoto()).override(150, 150).into(holder.foto);
        holder.textNamaBarang.setText(current.getNamaBarang());
        holder.textNamaPetani.setText(current.getPetani());
        holder.textGrade.setText("Grade : " + current.getGrade());
        holder.textSatuan.setText("/"+current.getSatuan());
        holder.textQuantity.setText("(" + current.getQuantity()+")");
        holder.textHarga.setText("Rp. " + current.getHarga());
        holder.textTotal.setText("Rp. " + current.getTotal());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class HistoryDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView foto;
        TextView textNamaBarang, textNamaPetani, textGrade, textSatuan, textQuantity, textHarga, textTotal;

        public HistoryDetailViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            foto = (ImageView) itemView.findViewById(R.id.history_detailfoto);
            textNamaBarang = (TextView) itemView.findViewById(R.id.history_detailnamabarang);
            textNamaPetani = (TextView) itemView.findViewById(R.id.history_detailpetani);
            textGrade = (TextView) itemView.findViewById(R.id.history_detailgrade);
            textSatuan = (TextView) itemView.findViewById(R.id.history_detailsatuan);
            textQuantity = (TextView) itemView.findViewById(R.id.history_detailquantity);
            textHarga = (TextView) itemView.findViewById(R.id.history_detailharga);
            textTotal = (TextView) itemView.findViewById(R.id.history_detailtotal);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}
