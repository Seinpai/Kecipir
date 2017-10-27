package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.CodMemberList;
import com.kecipir.kecipir.data.DepositList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kecipir-Dev on 09/12/2016.
 */

public class CodMemberAdapter   extends RecyclerView.Adapter<CodMemberAdapter.CodMemberViewHolder> {

    List<CodMemberList> data;
    private LayoutInflater inflater;
    private Context context;

    ClickListener clickListener;

    public CodMemberAdapter(Context context, List<CodMemberList> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public CodMemberAdapter.CodMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_codlist, parent, false);
        CodMemberAdapter.CodMemberViewHolder holder = new CodMemberAdapter.CodMemberViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CodMemberAdapter.CodMemberViewHolder holder, int position) {

        final CodMemberList current = data.get(position);
        holder.nodeposit.setText("No Nota : " + current.getNo_nota());
        holder.nama.setText("Nama member : " + current.getNama());
        holder.ket.setText("Keterangan : " + current.getKet());
        holder.amount.setText("Jumlah : " + current.getTotal());
        holder.tgl.setText("Status : " + current.getTanggal());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
//                    bayarCOD(current.getNo_nota(), current.getId_host(), current.getTotal());
                    Toast.makeText(context, current.getNo_nota(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private void confirmFinish(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Transaksi Berhasil")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    class CodMemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nodeposit;
        TextView tgl;
        TextView amount;
        TextView ket;
        TextView nama;
        CheckBox checkBox;

        public CodMemberViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nodeposit = (TextView) itemView.findViewById(R.id.no_nota);
            tgl = (TextView) itemView.findViewById(R.id.tgl_cod);
            amount = (TextView) itemView.findViewById(R.id.jumlah_total);
            ket = (TextView) itemView.findViewById(R.id.ket);
            nama = (TextView) itemView.findViewById(R.id.nama);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}