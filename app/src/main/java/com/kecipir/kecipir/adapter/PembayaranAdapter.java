package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kecipir.kecipir.HistoryActivity;
import com.kecipir.kecipir.MainActivity;
import com.kecipir.kecipir.PembayaranActivity;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.HistoryList;
import com.kecipir.kecipir.data.PembayaranList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kecipir-Dev on 04/04/2016.
 */
public class PembayaranAdapter extends RecyclerView.Adapter<PembayaranAdapter.PembayaranListViewHolder> {

    List<PembayaranList> data;
    private LayoutInflater inflater;
    private Context context;

    ClickListener clickListener;

    public PembayaranAdapter(Context context, List<PembayaranList> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public PembayaranListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.custom_pembayaranlist, parent,false);
        PembayaranListViewHolder holder = new PembayaranListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PembayaranListViewHolder holder, int position) {

        final PembayaranList current = data.get(position);
        holder.nota.setText("No Nota : "+current.getNoNota());
        holder.nama.setText("Nama    : "+current.getNama());
        holder.tglorder.setText("Tanggal Pesan :"+current.getTglTransaksi());
        holder.tglpanen.setText("Tanggal Panen :"+current.getTglPanen());
        holder.total.setText("Total Belanja : Rp. "+current.getTotal());
        holder.payment.setText("Pembayaran : "+current.getPayment());


//        holder.btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                konfirmasi(current.getNoNota(), current.getNama(), current.getPayment(), current.getTotal());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener (ClickListener clickListener){
        this.clickListener  = clickListener;
    }

    private void konfirmasi(final String no_nota, final String nama, final String tujuan, final String total) {

        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "View chckout Response: " + response.toString());
                try {
                    if (response.equalsIgnoreCase("")) {
//                        frameBuffer.setVisibility(View.INVISIBLE);
                    }
                    else {

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error){
//                            frameBuffer.setVisibility(View.INVISIBLE);
                            confirmFinish(jsonObject.getString("message"));
                        }
                        else {
                            Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    retry();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "View Checkout Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                frameBuffer.setVisibility(View.INVISIBLE);
//                retry();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("tag", "confirm_payment");
                params.put("no_nota", no_nota);
                params.put("tujuan", tujuan);
                params.put("nama", nama);
                params.put("total", total);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","green","web-indonesia");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    class PembayaranListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nota;
        TextView nama;
        TextView tglpanen;
        TextView tglorder;
        TextView payment;
        TextView total;
//        Button btnKonfirmasi;

        public PembayaranListViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            nota = (TextView) itemView.findViewById(R.id.nota_pembayaranlist);
            nama = (TextView) itemView.findViewById(R.id.nama_pembayaranlist);
            tglpanen = (TextView) itemView.findViewById(R.id.tglpanen_pembayaranlist);
            tglorder = (TextView) itemView.findViewById(R.id.tglorder_pembayaranlist);
            payment = (TextView) itemView.findViewById(R.id.payment_pembayaranlist);
            total = (TextView) itemView.findViewById(R.id.total_pembayaranlist);
//            btnKonfirmasi = (Button) itemView.findViewById(R.id.btn_konfirmasi_pembayaran);

        }
        @Override
        public void onClick(View view) {
            if (clickListener!=null){
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }
    }
}
