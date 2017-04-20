package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kecipir.kecipir.R;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.CodMemberList;
import com.kecipir.kecipir.data.DataMember;

import java.util.List;

/**
 * Created by Kecipir-Dev on 29/12/2016.
 */

public class DataMemberAdapter    extends RecyclerView.Adapter<DataMemberAdapter.DataMemberViewHolder> {

    List<DataMember> data;
    private LayoutInflater inflater;
    private Context context;

    ClickListener clickListener;

    public DataMemberAdapter(Context context, List<DataMember> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public DataMemberAdapter.DataMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_memberlist, parent, false);
        DataMemberAdapter.DataMemberViewHolder holder = new DataMemberAdapter.DataMemberViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataMemberAdapter.DataMemberViewHolder holder, int position) {

        final DataMember current = data.get(position);
        if (current.getNama().length() >14) {
            holder.txtNama.setText(current.getNama().substring(0, 13) + "..");
        }
        else{
            holder.txtNama.setText(current.getNama());
        }
        holder.txtAlamat.setText(current.getAlamat());
        holder.txtTelp.setText(current.getNoTelp());
        holder.txtEmail.setText(current.getEmail());
        holder.txtTglDaftar.setText(current.getTglDaftar());
        holder.txtBelanja.setText("Jumlah Belanja : "+current.getJmlBelanja());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class DataMemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtNama;
        TextView txtTglDaftar;
        TextView txtEmail;
        TextView txtAlamat;
        TextView txtTelp;
        TextView txtBelanja;

        public DataMemberViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtNama = (TextView) itemView.findViewById(R.id.txt_nama);
            txtTglDaftar = (TextView) itemView.findViewById(R.id.txt_tgldaftar);
            txtEmail = (TextView) itemView.findViewById(R.id.txt_email);
            txtAlamat = (TextView) itemView.findViewById(R.id.txt_alamat);
            txtTelp = (TextView) itemView.findViewById(R.id.txt_telp);
            txtBelanja = (TextView) itemView.findViewById(R.id.txt_jmlbelanja);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}
