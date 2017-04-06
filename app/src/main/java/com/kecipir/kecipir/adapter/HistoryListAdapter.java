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
import com.kecipir.kecipir.data.HistoryList;
import com.kecipir.kecipir.data.StoreList;

import java.util.List;

/**
 * Created by Albani on 11/20/2015.
 */
public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryListViewHolder>{

    List<HistoryList> data;
    private LayoutInflater inflater;
    private Context context;

    ClickListener clickListener;

    public HistoryListAdapter(Context context, List<HistoryList> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public HistoryListAdapter.HistoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.custom_historylist, parent,false);
        HistoryListViewHolder holder = new HistoryListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HistoryListAdapter.HistoryListViewHolder holder, int position) {
        HistoryList current = data.get(position);
        holder.nota.setText("No Nota : "+current.getNota());
        holder.nama.setText("Nama    : "+current.getNamaMember());
//        holder.qty.setText("Total Barang : "+current.getQty());
        holder.tglorder.setText("Tanggal Pesan :"+current.getTanggal());
        holder.tglpanen.setText("Tanggal Panen :"+current.getTgl_panen());
        holder.total.setText("Total Belanja : Rp. "+current.getTotalPembelian());
        holder.stsBayar.setText("Pembayaran : "+current.getStsBayar());
        holder.status.setText("Status Belanja : COMPLETE");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener (ClickListener clickListener){
        this.clickListener  = clickListener;
    }

    class HistoryListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nota;
        TextView nama;
        TextView tglpanen;
        TextView tglorder;
        TextView status;
        TextView qty;
        TextView total;
        TextView stsBayar;

        public HistoryListViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            nota = (TextView) itemView.findViewById(R.id.nota_historylist);
            nama = (TextView) itemView.findViewById(R.id.nama_historylist);
            tglpanen = (TextView) itemView.findViewById(R.id.tglpanen_historylist);
            tglorder = (TextView) itemView.findViewById(R.id.tglorder_historylist);
            status = (TextView) itemView.findViewById(R.id.status_historylist);
            total = (TextView) itemView.findViewById(R.id.totalharga_historylist);
            stsBayar = (TextView) itemView.findViewById(R.id.stsbayar_historylist);
        }
        @Override
        public void onClick(View view) {
            if (clickListener!=null){
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}
