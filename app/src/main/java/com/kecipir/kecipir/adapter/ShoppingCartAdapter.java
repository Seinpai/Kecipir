package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.SessionManager;
import com.kecipir.kecipir.ShoppingCartActivity;
import com.kecipir.kecipir.data.AppConfig;
import com.kecipir.kecipir.data.AppController;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.ShoppingCart;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Albani on 10/27/2015.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder>{

    List<ShoppingCart> data;
    private LayoutInflater inflater;
    private Context context;


    TextView jmlCart;
    ShoppingCart cart;

    ClickListener clickListener;

    public ShoppingCartAdapter(Context context, List<ShoppingCart> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ShoppingCartAdapter.ShoppingCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.custom_cart, parent,false);
        ShoppingCartViewHolder holder = new ShoppingCartViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShoppingCartAdapter.ShoppingCartViewHolder holder, int position) {
        ShoppingCart current = data.get(position);
        holder.namaBarangCart.setText(current.getNama_barang());
        holder.hargaBarangCart.setText(current.getHarga_jual());
        holder.hargaBarangCartAwal.setText(current.getKc_harga_asli());
        holder.tglPanenCart.setText(current.getTgl_panen());
        holder.petaniCart.setText(current.getNama_petani());
        holder.jmlCart.setText(current.getQuantity());

        Glide.with(context).load(current.getFoto()).into(holder.imgCart);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener (ClickListener clickListener){
        this.clickListener  = clickListener;
    }

    class ShoppingCartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imgCart;
        TextView namaBarangCart;
        TextView hargaBarangCart;
        TextView hargaBarangCartAwal;
        TextView tglPanenCart;
        TextView petaniCart;
        ImageView deleteCart;
        Button btnMin;
        Button btnPlus;
        TextView jmlCart;

        String id_user;
        String email;
        int tot_awal;
        int tot;
        int tot_awal2;
        int tot2;
        SessionManager sessionManager;

        ShoppingCart current;

        public ShoppingCartViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            imgCart = (ImageView) itemView.findViewById(R.id.img_cart);
            deleteCart = (ImageView) itemView.findViewById(R.id.delete_cart);
            namaBarangCart = (TextView) itemView.findViewById(R.id.namabarang_cart);
            hargaBarangCart = (TextView) itemView.findViewById(R.id.hargabarang_cart);
            hargaBarangCartAwal =(TextView) itemView.findViewById(R.id.hargabarangawal_cart);
            tglPanenCart = (TextView) itemView.findViewById(R.id.tglpanen_cart);
            petaniCart = (TextView) itemView.findViewById(R.id.petani_cart);
            btnPlus = (Button) itemView.findViewById(R.id.btn_pluscart);
            btnMin = (Button) itemView.findViewById(R.id.btn_mincart);
            jmlCart = (TextView) itemView.findViewById(R.id.jml_cart);

            sessionManager = new SessionManager(context);
            HashMap<String, String> user = sessionManager.getUser();
            id_user = user.get("uid");
            email = user.get("email");

            hargaBarangCartAwal = (TextView) itemView.findViewById(R.id.hargabarangawal_cart);
            hargaBarangCartAwal.setPaintFlags(hargaBarangCartAwal.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

            hargaBarangCartAwal.setVisibility(View.INVISIBLE);

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.equals(btnPlus)) {
                        int indicator = 1;
                        int jml_cart = Integer.valueOf(jmlCart.getText().toString());
                        jml_cart = jml_cart + indicator;
                        current = data.get(getLayoutPosition());
                        ((ShoppingCartActivity)context).setLoading();

                        int harga_satuan = Integer.parseInt(current.getHarga_jual());

                        updateCart(id_user, email, current.getId_wishlist(), current.getId_barang(), ""+jml_cart, harga_satuan+"", indicator, "add");
                    }
                }
            });

            btnMin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.equals(btnMin)) {
                        int indicator = -1;
                        int jml_cart = Integer.valueOf(jmlCart.getText().toString());
                        if (jml_cart <= 1){
                            Toast.makeText(context, "Jumlah tidak bisa 0", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            jml_cart = jml_cart  + indicator;
                            current = data.get(getLayoutPosition());
                            ((ShoppingCartActivity)context).setLoading();
                            int harga_satuan = - Integer.parseInt(current.getHarga_jual());

                            updateCart(id_user, email, current.getId_wishlist(), current.getId_barang(), ""+jml_cart, harga_satuan+"", indicator, "remove");

                        }
                    }
                }
            });

            deleteCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view.equals(deleteCart)){
                        new AlertDialog.Builder(context)
                                .setTitle("Hapus Wishlist")
                                .setMessage("Anda yakin mau hapus wishlist")
                                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        tot_awal = ((ShoppingCartActivity)context).getTotalHarga();
                                        tot_awal2 = ((ShoppingCartActivity)context).getTotalHargaAwal();
                                        ((ShoppingCartActivity)context).setLoading();
                                        current = data.get(getLayoutPosition());
                                        hapusWishlist(id_user, email, current.getId_barang());

                                    }
                                })
                                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();

                    }
                }
            });


        }

        @Override
        public void onClick(View view) {
            if (clickListener!=null){
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }

        private void updateCart(final String id_user, final String email, final String id_wishlist, final String id_barang, final String jml_cart, final String harga_satuan, final int indicator, final String param) {

            String tag_string_req = "req_login";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    AppConfig.URL_LOGIN, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d("TAG", "updatecart Response: " + response.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if(!jsonObject.getBoolean("error")){

                            int jml;
                            jml = Integer.parseInt(jml_cart);
                            jmlCart.setText(String.valueOf(jml_cart));
                            current = data.get(getLayoutPosition());
                            current.setQuantity(jml_cart);

                            tot_awal = ((ShoppingCartActivity)context).getTotalHarga();
                            tot_awal2 = ((ShoppingCartActivity)context).getTotalHargaAwal();


                            ((ShoppingCartActivity)context).dismissLoading();
                            int harga_satuan = Integer.parseInt(current.getHarga_jual());
                            tot = tot_awal + (Integer.parseInt(current.getHarga_jual())*indicator);
                            tot2 = tot_awal2 + (Integer.parseInt(current.getKc_harga_asli())*indicator);

                            if(tot > 0) {
                                ((ShoppingCartActivity)context).setTotalHarga("Rp. " + tot + "");
                                ((ShoppingCartActivity)context).setTotalHargaAwal(String.valueOf(tot2));
//                                current.setKc_harga_asli(String.valueOf(tot2));

                            }
                            else {
                                ((ShoppingCartActivity) context).setTotalHarga("Rp. 0");
//                                ((ShoppingCartActivity)context).setTotalHargaAwal("Rp. 0");
                            }

                        }
                        else {

                            Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            int jml = Integer.parseInt(jml_cart) - indicator;
                            jmlCart.setText(jml+"");
                            ((ShoppingCartActivity)context).dismissLoading();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ((ShoppingCartActivity)context).dismissLoading();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "updatecart Error: " + error.getMessage());
                    Toast.makeText(context,
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    ((ShoppingCartActivity)context).dismissLoading();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("tag", "updatecart");
                    params.put("id_barang_panen", id_barang);
                    params.put("id_user", id_user);
                    params.put("email", email);
                    params.put("id_wishlist", id_wishlist);
                    params.put("qty", jml_cart);
                    params.put("indicator", param);

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

            strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

        private void hapusWishlist(final String id_user, final String email, final String id_barang) {

            String tag_string_req = "req_login";

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    AppConfig.URL_LOGIN, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    data.remove(getLayoutPosition());
                    notifyDataSetChanged();
                    Log.d("TAG", "Login Response: " + response.toString());
                    ((ShoppingCartActivity)context).dismissLoading();
                    int qty = Integer.parseInt(jmlCart.getText().toString());
//                                        hapusWishlist(id_user, current.getId_barang());
                    tot = tot_awal - (Integer.parseInt(current.getHarga_jual())*qty);
                    tot2 = tot_awal2 - (Integer.parseInt(current.getKc_harga_asli())*qty);
                    if(data.size() > 0) {
                        ((ShoppingCartActivity)context).setTotalHarga("Rp. " + tot + "");
                        ((ShoppingCartActivity)context).setTotalHargaAwal(String.valueOf(tot2));
                    }
                    else {
                        ((ShoppingCartActivity) context).setTotalHarga("Rp. 0");
                        ((ShoppingCartActivity)context).setTotalHargaAwal(String.valueOf(tot2));
                    }

                    Log.d("HITUNG!!!", tot + " = "+tot_awal+" - "+Integer.parseInt(current.getJumlah_harga()));
                    Log.d("HITUNG!!!", tot2 + " = "+tot_awal2+" - "+Integer.parseInt(current.getKc_harga_asli()));
                    Toast.makeText(context, "Terhapus", Toast.LENGTH_SHORT).show();

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String jml_panen = jsonObject.getString("jml_tglpanen");
                        String jml_tagihan = jsonObject.getString("jml_tagihan");
                        if (Integer.parseInt(jml_panen) > 1){
                            ShoppingCartActivity activity = (ShoppingCartActivity )context;
                            activity.setBtnCheckout(false, "jml_tglpanen");

                        }
                        else if(Integer.parseInt(jml_panen)<= 1){
                            ShoppingCartActivity activity = (ShoppingCartActivity )context;
                            if (Integer.parseInt(jml_tagihan) > 0){
                                activity.setBtnCheckout(false, "jml_tagihan");
                            }
                            else{
                                activity.setBtnCheckout(true, "jml_tagihan");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "Login Error: " + error.getMessage());
                    Toast.makeText(context,
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    ((ShoppingCartActivity)context).dismissLoading();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("tag", "hapuswishlist");
                    params.put("id_barang", id_barang);
                    params.put("id_user", id_user);
                    params.put("email", email);

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

            strReq.setRetryPolicy(new DefaultRetryPolicy(AppConfig.TIMEOUT_NETWORK, AppConfig.RETRY_NETWORK, AppConfig.MULTI_NETWORK));
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

    }

}
