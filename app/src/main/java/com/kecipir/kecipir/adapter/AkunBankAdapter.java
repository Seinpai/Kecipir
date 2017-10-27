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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.data.AkunBankList;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.DepositList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kecipir-Dev on 27/05/2016.
 */
public class AkunBankAdapter  extends RecyclerView.Adapter<AkunBankAdapter.AkunBankViewHolder> {

    List<AkunBankList> data;
    private LayoutInflater inflater;
    private Context context;

    ClickListener clickListener;

    public AkunBankAdapter(Context context, List<AkunBankList> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public AkunBankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_akunbanklist, parent, false);
        AkunBankViewHolder holder = new AkunBankViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AkunBankViewHolder holder, int position) {

        final AkunBankList current = data.get(position);
        holder.namaRekening.setText(current.getNamaRekening());
        holder.namaBank.setText(current.getNamaBank());
        holder.noRekening.setText(current.getNoRekening());
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

    class AkunBankViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView namaRekening;
        TextView noRekening;
        TextView namaBank;

        public AkunBankViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            namaRekening = (TextView) itemView.findViewById(R.id.nama_rekening_listbank);
            noRekening = (TextView) itemView.findViewById(R.id.no_rekening_listbank);
            namaBank = (TextView) itemView.findViewById(R.id.nama_bank_listbank);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}